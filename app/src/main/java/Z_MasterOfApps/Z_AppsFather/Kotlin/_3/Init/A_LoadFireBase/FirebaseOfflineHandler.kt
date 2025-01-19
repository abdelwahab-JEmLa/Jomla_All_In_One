package Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.A_LoadFireBase

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

object FirebaseOfflineHandler {
    private const val TIMEOUT_MS = 2000L
    private var isInitialized = false

    fun initializeFirebase(app: FirebaseApp) {
        if (!isInitialized) {
            try {
                FirebaseDatabase.getInstance(app).apply {
                    setPersistenceEnabled(true)
                    setPersistenceCacheSizeBytes(100L * 1024L * 1024L) // 100MB
                }
                isInitialized = true
                Log.i("Firebase", "Firebase initialized successfully")
            } catch (e: Exception) {
                Log.e("Firebase", "Failed to initialize Firebase", e)
                throw e
            }
        }
    }

    suspend fun loadData(
        ref: DatabaseReference,
        viewModel: ViewModelInitApp? = null
    ): DataSnapshot? {
        try {
            if (!isInitialized) {
                Log.e("Firebase", "Firebase not initialized. Call initializeFirebase first.")
                return null
            }

            ref.keepSynced(true)

            // Simplified connection check using get()
            val isOnline = try {
                withTimeoutOrNull(TIMEOUT_MS) {
                    val testRef = ref.child("test_connection")
                    testRef.setValue(System.currentTimeMillis()).await()
                    testRef.removeValue().await()
                    true
                } ?: false
            } catch (e: Exception) {
                Log.w("Firebase", "Connection test failed, assuming offline mode", e)
                false
            }

            return when (isOnline) {
                false -> {
                    Log.i("Firebase", "Mode offline")
                    FirebaseDatabase.getInstance().goOffline()
                    val data = ref.get().await()
                    FirebaseDatabase.getInstance().goOnline()
                    data
                }
                true -> {
                    Log.i("Firebase", "Mode online")
                    val data = ref.get().await()

                    // Setup real-time listeners for all products
                    if (viewModel != null) {
                        setupProductListeners(viewModel)
                    }

                    data
                }
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Erreur chargement", e)
            return null
        }
    }

    private fun setupProductListeners(viewModel: ViewModelInitApp) {
        val scope = CoroutineScope(Dispatchers.IO)

        viewModel.modelAppsFather.produitsMainDataBase.forEach { produit ->
            Log.d("SetupListener", "Setting up listener for product ${produit.id}")

            _ModelAppsFather.produitsFireBaseRef.child(produit.id.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        scope.launch {
                            try {
                                val updatedProduct = LoadFromFirebaseProduits.parseProduct(snapshot)
                                if (updatedProduct != null) {
                                    val index = viewModel.modelAppsFather.produitsMainDataBase.indexOfFirst {
                                        it.id == updatedProduct.id
                                    }
                                    if (index != -1) {
                                        viewModel.modelAppsFather.produitsMainDataBase[index] = updatedProduct
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("SetupListener", "Error updating product", e)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("SetupListener", "Database error: ${error.message}")
                    }
                })
        }
    }

    inline fun <reified T> parseChild(
        path: String,
        snapshot: DataSnapshot,
        crossinline onSuccess: (List<T>) -> Unit
    ) {
        try {
            snapshot.child(path)
                .getValue(object : GenericTypeIndicator<List<T>>() {})
                ?.let(onSuccess)
        } catch (e: Exception) {
            Log.e("Firebase", "Erreur parse: $path", e)
        }
    }
}
