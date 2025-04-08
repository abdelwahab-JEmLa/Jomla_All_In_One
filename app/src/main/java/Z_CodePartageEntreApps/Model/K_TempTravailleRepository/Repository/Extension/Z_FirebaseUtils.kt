package Z_CodePartageEntreApps.Model.K_TempTravailleRepository.Repository.Extension

import Z_CodePartageEntreApps.Model.K_TempTravaille
import Z_CodePartageEntreApps.Model.K_TempTravailleRepository.Repository.K_TempTravailleRepository
import Z_CodePartageEntreApps.Model.K_TempTravailleRepository.Repository.K_TempTravailleRepositoryImpl
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.Flow
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Utility class for Firebase operations related to K_TempTravaille
 * Contains extracted functions from K_TempTravailleRepositoryImpl
 */
object Z_FirebaseUtils {
    private val CACHE_SIZE_BYTES = 100L * 1024L * 1024L // 100MB
    private val CONNECTIVITY_CHECK_INTERVAL = 10000L // 10 seconds
    private val DEBOUNCE_INTERVAL = 500L

    private var lastConnectivityCheck = 0L
    private var lastConnectivityState = false

    /**
     * Initialize Firebase offline capability
     */

    /**
     * Sanitize Firebase key to avoid invalid characters
     */
    fun sanitizeFirebaseKey(key: String): String {
        return key.replace("/", "_")
            .replace(".", "_")
            .replace("#", "_")
            .replace("$", "_")
            .replace("[", "_")
            .replace("]", "_")
    }

    /**
     * Start database listener for K_TempTravaille
     */
    fun startDatabaseListener(
        repository: K_TempTravailleRepositoryImpl,
        onValueEventListenerCreated: (ValueEventListener) -> Unit
    ) {
        // Create the value event listener
        val listener = createValueEventListener(repository)

        // Set the listener
        listener?.let {
            K_TempTravailleRepository.caReference.addValueEventListener(it)
            onValueEventListenerCreated(it)
        }
    }

    /**
     * Create value event listener for Firebase database changes
     * Extracted from K_TempTravailleRepositoryImpl
     */
    private fun createValueEventListener(repository: K_TempTravailleRepositoryImpl): ValueEventListener {
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

                    for (dataSnapshot in snapshot.children) {
                        val tempTravaille = repository.syncData(dataSnapshot = dataSnapshot) as K_TempTravaille

                        // Fixed the reference error by explicitly casting and checking
                        if (tempTravaille.intervalesDeTravaille.isNotEmpty()) {
                            repository.modelDatas.add(tempTravaille)
                        }

                        processedItems++
                        repository.progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                    }

                    repository.modelDatas.sortBy { it.infosDeBase.dateInString }
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
     * Extracted from K_TempTravailleRepositoryImpl
     */
    suspend fun onDataBaseChangeListnerAndLoad(repository: K_TempTravailleRepositoryImpl): Pair<List<K_TempTravaille>, Flow<Float>> {
        checkConnectivityAndSync(repository)
        // The listener setup is already handled by checkConnectivityAndSync or init
        return Pair(repository.modelDatas.toList(), repository.progressRepo)
    }

    /**
     * Check connectivity and sync if state has changed
     */
    fun checkConnectivityAndSync(repository: K_TempTravailleRepositoryImpl) {
        val currentTime = System.currentTimeMillis()

        // Only checkADD_1_4_PeriodeVent every CONNECTIVITY_CHECK_INTERVAL milliseconds
        if (currentTime - lastConnectivityCheck < CONNECTIVITY_CHECK_INTERVAL) {
            return
        }

        lastConnectivityCheck = currentTime

        // Check current connectivity
        val isConnected = try {
            val socket = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)
            socket.connect(socketAddress, 3000) // 3 seconds timeout
            socket.close()
            true
        } catch (e: Exception) {
            false
        }

        // If connectivity state has changed
        if (isConnected != lastConnectivityState) {
            lastConnectivityState = isConnected

            if (isConnected) {

                K_TempTravailleRepository.caReference.keepSynced(false)
                K_TempTravailleRepository.caReference.keepSynced(true)

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
}

