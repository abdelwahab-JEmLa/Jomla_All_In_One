package Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.Repository

import Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.Repository.Extension.FirebaseUtilsSoldArticlesTabelle
import Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.Repository.Extension.SyncDataUtils
import Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.SoldArticlesTabelle
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SoldArticlesTabelleRepositoryImpl :
    SoldArticlesTabelleRepository {
    override var modelDatas: SnapshotStateList<SoldArticlesTabelle> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private var listener: ValueEventListener? = null
    internal var isUpdating = false
    internal var lastUpdateTimestamp = 0L
    private var isFirebaseInitialized = false

    init {
        // Initialize Firebase offline capability with add_New callback for completion
        initializeFirebase {
            // Only start the database listener after Firebase is initialized
            startDatabaseListener ()
        }

        // Update reposeteryLoading when progressRepo reaches 100%
        try {
            // Launch add_New coroutine for collectLatest
            CoroutineScope(Dispatchers.Main).launch {
                progressRepo.collectLatest { progress ->
                    if (progress >= 1.0f && isFirebaseInitialized) {
                    }
                }
            }
        } catch (e: Exception) {
            println("Error monitoring progress: ${e.message}")
        }
    }

    private fun initializeFirebase(onInitialized: () -> Unit) {
        try {
            // Use Dispatchers.IO for network operations
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    isFirebaseInitialized = true
                    // Switch to main thread for UI updates
                    CoroutineScope(Dispatchers.Main).launch {
                        onInitialized()
                    }
                } catch (e: Exception) {
                    println("Firebase initialization error: ${e.message}")
                    // Even on error, we'll mark as initialized to avoid blocking the app
                    isFirebaseInitialized = true
                    // Switch to main thread for UI updates
                    CoroutineScope(Dispatchers.Main).launch {
                        onInitialized()
                    }
                }
            }
        } catch (e: Exception) {
            println("Failed to launch Firebase initialization: ${e.message}")
            isFirebaseInitialized = true // Prevent blocking on error
            onInitialized()
        }
    }

    fun startDatabaseListener(onDatabaseListenerEnd: () -> Unit = {}) {
        stopDatabaseListener()
        FirebaseUtilsSoldArticlesTabelle.startDatabaseListener(this) { newListener ->
            listener = newListener
            onDatabaseListenerEnd()
        }
    }

    internal fun restartDatabaseListener() {
        startDatabaseListener()
    }

    override fun checkConnectivityAndSync() {
        FirebaseUtilsSoldArticlesTabelle.checkConnectivityAndSync(this)
    }

    override fun updateData(data: SoldArticlesTabelle?) {
        if (data == null) return

        // Find the index of the record in the modelDatas list
        val recordIndex = modelDatas.indexOfFirst { it.vid == data.vid }

        if (recordIndex != -1) {
            // Update the record in the modelDatas list
            modelDatas[recordIndex] = data

            try {
                // Check connectivity before trying to upsertLenceCommandeRepoGroupedProtoAvantJuin3 Firebase
                checkConnectivityAndSync()

                // Update Firebase database with the updated record
                firebaseUpdateData(data)
            } catch (e: Exception) {
                println("Firebase upsertLenceCommandeRepoGroupedProtoAvantJuin3 failed in updateUnSeulData: ${e.message}")
            }
        }
    }

    private fun firebaseUpdateData(data: SoldArticlesTabelle) {
        try {
            val firebaseData = SyncDataUtils.syncData(tempTravaille = data) as Map<String, Any>

            // Sanitize the key before using it in Firebase
            val sanitizedKey = FirebaseUtilsSoldArticlesTabelle.sanitizeFirebaseKey(data.vid.toString())

            // Update the data in Firebase
            SoldArticlesTabelleRepository.caReference.child(sanitizedKey).updateChildren(firebaseData)
                .addOnFailureListener { e -> println("Firebase upsertLenceCommandeRepoGroupedProtoAvantJuin3 failed: ${e.message}") }
        } catch (e: Exception) {
            println("Failed to prepare data: ${e.message}")
        }
    }

    override suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<SoldArticlesTabelle>, Flow<Float>> {
        return FirebaseUtilsSoldArticlesTabelle.onDataBaseChangeListnerAndLoad(this)
    }

    override suspend fun updateDatas(datas: SnapshotStateList<SoldArticlesTabelle>) {
        if (isUpdating) return

        try {
            isUpdating = true
            progressRepo.value = 0f

            val totalItems = datas.size
            var processedItems = 0

            stopDatabaseListener()

            // Check connectivity before trying to upsertLenceCommandeRepoGroupedProtoAvantJuin3
            checkConnectivityAndSync()

            datas.forEach { data ->
                val firebaseData = SyncDataUtils.syncData(data = data) as Map<String, Any>

                // Sanitize the key before using it in Firebase
                val sanitizedKey = FirebaseUtilsSoldArticlesTabelle.sanitizeFirebaseKey(data.vid.toString())

                // Update the data in Firebase
                SoldArticlesTabelleRepository.caReference.child(sanitizedKey).updateChildren(firebaseData)

                processedItems++
                progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
            }

            modelDatas.clear()
            modelDatas.addAll(datas)
            progressRepo.value = 1.0f
        } catch (e: Exception) {
            progressRepo.value = 0f
            println("Failed to upsertLenceCommandeRepoGroupedProtoAvantJuin3 data batch: ${e.message}")
        } finally {
            isUpdating = false
            startDatabaseListener() // Restart the database listener
        }
    }

    override fun stopDatabaseListener() {
        listener?.let {
            SoldArticlesTabelleRepository.caReference.removeEventListener(it)
        }
        listener = null
    }
}
