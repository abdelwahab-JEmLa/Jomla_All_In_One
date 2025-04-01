package Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.Repository.Extension

import Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.Repository.SoldArticlesTabelleRepository
import Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.Repository.SoldArticlesTabelleRepositoryImpl
import Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.SoldArticlesTabelle
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.Flow
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Utility class for Firebase operations related to SoldArticlesTabelle
 */
object FirebaseUtilsSoldArticlesTabelle {
    private const val CACHE_SIZE_BYTES = 100L * 1024L * 1024L // 100MB
    private const val CONNECTIVITY_CHECK_INTERVAL = 10000L // 10 seconds
    private const val DEBOUNCE_INTERVAL = 500L

    private var lastConnectivityCheck = 0L
    private var lastConnectivityState = false


    /**
     * Sanitize Firebase key to avoid invalid characters
     */
    fun sanitizeFirebaseKey(key: String): String {
        return key.replace(Regex("[/.#$\\[\\]]"), "_")
    }

    /**
     * Start database listener for SoldArticlesTabelle
     */
    fun startDatabaseListener(
        repository: SoldArticlesTabelleRepositoryImpl,
        onValueEventListenerCreated: (ValueEventListener) -> Unit
    ) {
        createValueEventListener(repository)?.let {
            SoldArticlesTabelleRepository.caReference.addValueEventListener(it)
            onValueEventListenerCreated(it)
        }
    }

    /**
     * Create value event listener for Firebase database changes
     */
    private fun createValueEventListener(repository: SoldArticlesTabelleRepositoryImpl): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (repository.isUpdating) return

                val currentTime = System.currentTimeMillis()
                if (currentTime - repository.lastUpdateTimestamp < DEBOUNCE_INTERVAL) return
                repository.lastUpdateTimestamp = currentTime

                try {
                    repository.isUpdating = true
                    val totalItems = snapshot.childrenCount.toInt()

                    repository.modelDatas.clear()
                    if (totalItems == 0) {
                        repository.progressRepo.value = 1.0f
                        repository.isUpdating = false
                        return
                    }

                    repository.progressRepo.value = 0f
                    var processedItems = 0

                    snapshot.children.forEach { dataSnapshot ->
                        val data = SyncDataUtils.syncData(dataSnapshot = dataSnapshot) as SoldArticlesTabelle

                        // Check that the data has a valid ID
                        if (data.vid > 0) {
                            repository.modelDatas.add(data)
                        }

                        processedItems++
                        repository.progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                    }

                    repository.progressRepo.value = 1.0f
                } catch (e: Exception) {
                    repository.progressRepo.value = 0f
                    println("Error processing Firebase data: ${e.message}")
                } finally {
                    repository.isUpdating = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                repository.progressRepo.value = 0f
                println("Firebase database error: ${error.message}")
            }
        }
    }

    /**
     * Handle database change listener and load data
     */
     fun onDataBaseChangeListnerAndLoad(repository: SoldArticlesTabelleRepositoryImpl): Pair<List<SoldArticlesTabelle>, Flow<Float>> {
        checkConnectivityAndSync(repository)
        return Pair(repository.modelDatas.toList(), repository.progressRepo)
    }

    /**
     * Check connectivity and sync if state has changed
     */
    fun checkConnectivityAndSync(repository: SoldArticlesTabelleRepositoryImpl) {
        val currentTime = System.currentTimeMillis()

        // Only check every CONNECTIVITY_CHECK_INTERVAL milliseconds
        if (currentTime - lastConnectivityCheck < CONNECTIVITY_CHECK_INTERVAL) return

        lastConnectivityCheck = currentTime

        // Check current connectivity
        val isConnected = checkInternetConnection()

        // If connectivity state has changed
        if (isConnected != lastConnectivityState) {
            lastConnectivityState = isConnected

            if (isConnected) {
                // Reset sync flag to force refresh
                SoldArticlesTabelleRepository.caReference.keepSynced(false)
                SoldArticlesTabelleRepository.caReference.keepSynced(true)

                // Make sure we're online
                Firebase.database.goOnline()

                // Restart listener to get fresh data
                repository.restartDatabaseListener()
            } else {
                // We've gone offline, make sure we're in offline mode
                Firebase.database.goOffline()
            }
        }
    }

    /**
     * Check internet connection
     */
    private fun checkInternetConnection(): Boolean {
        return try {
            Socket().apply {
                connect(InetSocketAddress("8.8.8.8", 53), 3000) // 3 seconds timeout
                close()
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
