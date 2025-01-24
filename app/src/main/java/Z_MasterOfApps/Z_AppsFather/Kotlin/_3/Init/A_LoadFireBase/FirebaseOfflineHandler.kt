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
    private const val TIMEOUT_MS = 5000L
    private var isInitialized = false

    fun initializeFirebase(app: FirebaseApp) {
        if (!isInitialized) {
            try {
                FirebaseDatabase.getInstance(app).apply {
                    setPersistenceEnabled(true)
                    setPersistenceCacheSizeBytes(100L * 1024L * 1024L)
                    Log.d("Firebase", "Persistence configured")
                }
                isInitialized = true
                Log.i("Firebase", "Firebase initialized successfully")
            } catch (e: Exception) {
                Log.e("Firebase", "Initialization failed", e)
                throw e
            }
        }
    }

    suspend fun loadData(
        ref: DatabaseReference,
        refClientsDataBase: DatabaseReference,
        viewModel: ViewModelInitApp? = null
    ): Pair<DataSnapshot?, DataSnapshot?> {
        if (!isInitialized) {
            Log.e("Firebase", "Firebase not initialized")
            return Pair(null, null)
        }

        return try {
            ref.keepSynced(true)
            refClientsDataBase.keepSynced(true)

            val isOnline = checkConnection(ref)

            if (isOnline) {
                Log.i("Firebase", "🟢 Online mode")
                handleOnlineOperations(ref, refClientsDataBase, viewModel)
            } else {
                Log.w("Firebase", "🔴 Offline mode")
                handleOfflineOperations(ref, refClientsDataBase)
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Critical load error", e)
            Pair(null, null)
        }
    }

    private suspend fun checkConnection(ref: DatabaseReference): Boolean {
        return try {
            withTimeoutOrNull(3000L) {
                val testRef = ref.child("connection_test").apply { keepSynced(false) }
                testRef.setValue(true).await()
                testRef.removeValue().await()
                true
            } ?: false
        } catch (e: Exception) {
            Log.w("Firebase", "Connection check failed: ${e.message}")
            false
        }
    }

    private suspend fun handleOnlineOperations(
        ref: DatabaseReference,
        refClientsDataBase: DatabaseReference,
        viewModel: ViewModelInitApp?
    ): Pair<DataSnapshot?, DataSnapshot?> {
        return try {
            val data = ref.get().await()
            val data2 = refClientsDataBase.get().await()
            viewModel?.let { setupRealtimeListeners(it) }
            Pair(data, data2)
        } catch (e: Exception) {
            Log.e("Firebase", "Online operation failed", e)
            Pair(null, null)
        }
    }

    private suspend fun handleOfflineOperations(
        ref: DatabaseReference,
        refClientsDataBase: DatabaseReference
    ): Pair<DataSnapshot?, DataSnapshot?> {
        return try {
            FirebaseDatabase.getInstance().goOffline()
            val data = withTimeoutOrNull(TIMEOUT_MS) { ref.get().await() }
            val data2 = withTimeoutOrNull(TIMEOUT_MS) { refClientsDataBase.get().await() }
            FirebaseDatabase.getInstance().goOnline()
            Pair(data, data2)
        } catch (e: Exception) {
            Log.e("Firebase", "Offline operation failed", e)
            Pair(null, null)
        }
    }

    private fun setupRealtimeListeners(viewModel: ViewModelInitApp) {
        val scope = CoroutineScope(Dispatchers.IO)

        _ModelAppsFather.produitsFireBaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scope.launch {
                    val products = snapshot.children.mapNotNull { LoadFromFirebaseProduits.parseProduct(it) }
                    viewModel.modelAppsFather.produitsMainDataBase.apply {
                        clear()
                        addAll(products)
                    }
                    Log.d("Firebase", "Real-time products updated: ${products.size} items")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Products listener cancelled: ${error.message}")
            }
        })
    }
    // À l'intérieur de l'objet FirebaseOfflineHandler
    inline fun <reified T> parseChild(
        path: String,
        snapshot: DataSnapshot,
        crossinline onSuccess: (List<T>) -> Unit
    ) {
        try {
            val list = snapshot.child(path)
                .getValue(object : GenericTypeIndicator<List<T>>() {})
                ?: emptyList()

            onSuccess(list)
        } catch (e: Exception) {
            Log.e("Firebase", "Parse error for path '$path'", e)
        }
    }
}
