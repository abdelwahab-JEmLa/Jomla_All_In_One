package Z_CodePartageEntreApps.Model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.net.InetSocketAddress
import java.net.Socket

interface K_TempTravailleRepository {
    var modelDatas: SnapshotStateList<K_TempTravaille>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<K_TempTravaille>, Flow<Float>>
    suspend fun updateDatas(datas: SnapshotStateList<K_TempTravaille>)
    fun stopDatabaseListener()
    fun checkConnectivityAndSync()
    fun deleteIntevaleDeTemp(intervalId:String)

    // Updated method signature to match the implementation in MockTempTravailleRepository
    fun updateUnSeulData(
        recordId: String? = null,
    )

    companion object {
        val caReference = Firebase.database.getReference("K_TempTravaille")
    }
}

class K_TempTravailleRepositoryImpl : K_TempTravailleRepository {
    override var modelDatas: SnapshotStateList<K_TempTravaille> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private var listener: ValueEventListener? = null
    private var isUpdating = false
    private var lastUpdateTimestamp = 0L
    private var lastConnectivityCheck = 0L
    private var lastConnectivityState = false
    private val DEBOUNCE_INTERVAL = 500L
    private val CONNECTIVITY_CHECK_INTERVAL = 10000L // 10 seconds
    private val CACHE_SIZE_BYTES = 100L * 1024L * 1024L // 100MB

    init {
        initializeFirebaseOfflineCapability()
        startDatabaseListener()
    }

    // Initialize Firebase offline capability
    private fun initializeFirebaseOfflineCapability() {
        try {
            // Enable disk persistence
            Firebase.database.setPersistenceEnabled(true)
            // Set cache size
            Firebase.database.setPersistenceCacheSizeBytes(CACHE_SIZE_BYTES)
            // Keep the reference synced
            K_TempTravailleRepository.caReference.keepSynced(true)
        } catch (e: Exception) {
            // Log error or handle exception
            println("Firebase initialization error: ${e.message}")
        }
    }

    // Sanitize Firebase key to avoid invalid characters
    private fun sanitizeFirebaseKey(key: String): String {
        return key.replace("/", "_")
            .replace(".", "_")
            .replace("#", "_")
            .replace("$", "_")
            .replace("[", "_")
            .replace("]", "_")
    }

    private fun startDatabaseListener() {
        stopDatabaseListener()

        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (isUpdating) return

                val currentTime = System.currentTimeMillis()
                if (currentTime - lastUpdateTimestamp < DEBOUNCE_INTERVAL) return
                lastUpdateTimestamp = currentTime

                try {
                    isUpdating = true
                    val totalItems = snapshot.childrenCount.toInt()

                    modelDatas.clear()
                    if (totalItems == 0) {
                        progressRepo.value = 1.0f
                        isUpdating = false
                        return
                    }

                    progressRepo.value = 0f
                    var processedItems = 0

                    for (dataSnapshot in snapshot.children) {
                        val tempTravaille = syncData(dataSnapshot = dataSnapshot) as K_TempTravaille

                        // Fixed the reference error by explicitly casting and checking
                        if (tempTravaille.intervalesDeTravaille.isNotEmpty()) {
                            modelDatas.add(tempTravaille)
                        }

                        processedItems++
                        progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                    }

                    modelDatas.sortBy { it.infosDeBase.dateInString }
                    progressRepo.value = 1.0f
                } catch (e: Exception) {
                    progressRepo.value = 0f
                    println("Error processing Firebase data: ${e.message}")
                } finally {
                    isUpdating = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressRepo.value = 0f
                println("Firebase database error: ${error.message}")
            }
        }

        listener?.let {
            K_TempTravailleRepository.caReference.addValueEventListener(it)
        }
    }

    // Check connectivity and sync if state has changed
    override fun checkConnectivityAndSync() {
        val currentTime = System.currentTimeMillis()

        // Only check every CONNECTIVITY_CHECK_INTERVAL milliseconds
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
                // We've reconnected, force sync with server
                K_TempTravailleRepository.caReference.keepSynced(false)
                K_TempTravailleRepository.caReference.keepSynced(true)

                // Make sure we're online
                Firebase.database.goOnline()

                // Restart listener to get fresh data
                startDatabaseListener()
            } else {
                // We've gone offline, make sure we're in offline mode
                Firebase.database.goOffline()
            }
        }
    }
    override fun deleteIntevaleDeTemp(intervalId: String) {
        // Locate the record containing this interval
        var recordToUpdate: K_TempTravaille? = null

        modelDatas.forEach { record ->
            val intervalIndex = record.intervalesDeTravaille.indexOfFirst { it.vid == intervalId }
            if (intervalIndex != -1) {
                // Remove locally
                record.intervalesDeTravaille.removeAt(intervalIndex)
                recordToUpdate = record
                return@forEach
            }
        }

        // If found record to update
        if (recordToUpdate != null) {
            try {
                // Check connectivity before trying to update Firebase
                checkConnectivityAndSync()

                // Sanitize the record ID
                val sanitizedRecordId = sanitizeFirebaseKey(recordToUpdate!!.vid)

                // Sanitize the interval ID
                val sanitizedIntervalId = sanitizeFirebaseKey(intervalId)

                // Create reference to the interval
                val intervalRef = K_TempTravailleRepository.caReference
                    .child(sanitizedRecordId)
                    .child("intervalesDeTravaille")
                    .child(sanitizedIntervalId)

                // Remove the interval from Firebase
                intervalRef.removeValue()
                    .addOnSuccessListener {
                        println("Successfully deleted interval $intervalId from record ${recordToUpdate!!.vid}")
                    }
                    .addOnFailureListener { e ->
                        println("Failed to delete interval from Firebase: ${e.message}")
                    }

                // Update local record
                updateUnSeulData(recordToUpdate!!.vid)
            } catch (e: Exception) {
                println("Error deleting interval: ${e.message}")
            }
        }
    }

    override fun updateUnSeulData(
        recordId: String?,
    ) {
        if (recordId != null) {
            val recordIndex = modelDatas.indexOfFirst { it.vid == recordId }
            if (recordIndex != -1) {
                val record = modelDatas[recordIndex]
                println("Updating record ${record.vid} with ${record.intervalesDeTravaille.size} intervals")

                // Log intervals for debugging
                record.intervalesDeTravaille.forEachIndexed { index, interval ->
                    println("Interval $index: ID=${interval.vid}, Start=${interval.tempDepart}, End=${interval.temparrete}, Recording=${interval.enCoureDEnregestrement}")
                }

                // Update local data
                modelDatas.removeAt(recordIndex)
                modelDatas.add(recordIndex, record)

                try {
                    // Check connectivity before trying to update Firebase
                    checkConnectivityAndSync()

                    // Call the Firebase update method with proper error handling
                    updateDataUnSeulDataInFirebase(record)
                } catch (e: Exception) {
                    // Log the error or handle it appropriately
                    println("Firebase update failed: ${e.message}")
                    // You might want to queue this update for retry later
                }
            }
        }
    }

    private fun updateDataUnSeulDataInFirebase(tempTravaille: K_TempTravaille) {
        try {
            // Use the unified function to convert the object to a Firebase-friendly format
            val firebaseData = syncData(tempTravaille = tempTravaille) as Map<String, Any>

            // Sanitize the key before using it in Firebase
            val sanitizedKey = sanitizeFirebaseKey(tempTravaille.vid)

            // Update the data in Firebase
            val dateRef = K_TempTravailleRepository.caReference.child(sanitizedKey)
            dateRef.updateChildren(firebaseData)
                .addOnSuccessListener {
                    println("Firebase update successful for record: ${tempTravaille.vid} with ${tempTravaille.intervalesDeTravaille.size} intervals")
                }
                .addOnFailureListener { e ->
                    println("Firebase update failed: ${e.message}")
                }
        } catch (e: Exception) {
            println("Failed to prepare data: ${e.message}")
        }
    }

    private fun syncData(
        dataSnapshot: DataSnapshot? = null,
        tempTravaille: K_TempTravaille? = null
    ): Any {
        // Case 1: Parse from Firebase to K_TempTravaille
        if (dataSnapshot != null && tempTravaille == null) {
            val newTempTravaille = K_TempTravaille(vid = dataSnapshot.key ?: "unknown")

            // Parse infosDeBase
            val infosDeBaseSnapshot = dataSnapshot.child("infosDeBase")
            newTempTravaille.infosDeBase.dateInString = infosDeBaseSnapshot.child("dateInString").getValue(String::class.java) ?: newTempTravaille.vid

            // Parse intervals
            val intervalsSnapshot = dataSnapshot.child("intervalesDeTravaille")
            for (intervalSnapshot in intervalsSnapshot.children) {
                val interval =
                    K_TempTravaille.IntervalesDeTravaille(vid = intervalSnapshot.key ?: "00_00")

                try {
                    val typeStr = intervalSnapshot.child("typeTemp").getValue(String::class.java) ?: "DEPLACEMENT"
                    interval.typeTemp = K_TempTravaille.IntervalesDeTravaille.TypeTemp.valueOf(typeStr)
                } catch (e: Exception) {
                    interval.typeTemp = K_TempTravaille.IntervalesDeTravaille.TypeTemp.DEPLACEMENT
                }

                interval.tempDepart = intervalSnapshot.child("tempDepart").getValue(String::class.java) ?: "HH:mm"
                interval.temparrete = intervalSnapshot.child("temparrete").getValue(String::class.java) ?: "HH:mm"
                interval.idBonDeCetteIntervale = intervalSnapshot.child("idBonDeCetteIntervale").getValue(Long::class.java) ?: 0L

                // Make sure we properly read the recording state
                interval.enCoureDEnregestrement = intervalSnapshot.child("enCoureDEnregestrement").getValue(Boolean::class.java) ?: false

                // No longer skip empty intervals - this could cause data loss
                newTempTravaille.intervalesDeTravaille.add(interval)
            }

            return newTempTravaille
        }
        // Case 2: Convert K_TempTravaille to Firebase data
        else if (tempTravaille != null && dataSnapshot == null) {
            val result = mutableMapOf<String, Any>()

            // Add infosDeBase
            val infosDeBase = mutableMapOf<String, Any>()
            infosDeBase["dateInString"] = tempTravaille.infosDeBase.dateInString
            result["infosDeBase"] = infosDeBase

            // Add intervalesDeTravaille
            val intervalesDeTravaille = mutableMapOf<String, Any>()
            tempTravaille.intervalesDeTravaille.forEach { interval ->
                val intervalData = mapOf(
                    "typeTemp" to interval.typeTemp.name,
                    "tempDepart" to interval.tempDepart,
                    "temparrete" to interval.temparrete,
                    "idBonDeCetteIntervale" to interval.idBonDeCetteIntervale,
                    "enCoureDEnregestrement" to interval.enCoureDEnregestrement
                )

                // Make sure the interval ID is also sanitized
                val sanitizedIntervalId = sanitizeFirebaseKey(interval.vid)
                intervalesDeTravaille[sanitizedIntervalId] = intervalData
            }
            result["intervalesDeTravaille"] = intervalesDeTravaille

            return result
        }
        // Handle invalid input
        else {
            throw IllegalArgumentException("Invalid parameters for syncData: either dataSnapshot or tempTravaille must be provided")
        }
    }

    override suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<K_TempTravaille>, Flow<Float>> {
        checkConnectivityAndSync()
        // The listener setup is already handled by checkConnectivityAndSync or init
        return Pair(modelDatas.toList(), progressRepo)
    }

    override suspend fun updateDatas(datas: SnapshotStateList<K_TempTravaille>) {
        if (isUpdating) return

        try {
            isUpdating = true
            progressRepo.value = 0f

            val totalItems = datas.size
            var processedItems = 0

            stopDatabaseListener()

            // Check connectivity before trying to update
            checkConnectivityAndSync()

            datas.forEach { tempTravaille ->
                // Use the unified function to convert the object to a Firebase-friendly format
                val firebaseData = syncData(tempTravaille = tempTravaille) as Map<String, Any>

                // Sanitize the key before using it in Firebase
                val sanitizedKey = sanitizeFirebaseKey(tempTravaille.vid)

                // Update the data in Firebase
                val dateRef = K_TempTravailleRepository.caReference.child(sanitizedKey)
                dateRef.updateChildren(firebaseData)

                processedItems++
                progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
            }

            modelDatas.clear()
            modelDatas.addAll(datas)
            modelDatas.sortBy { it.infosDeBase.dateInString }
            progressRepo.value = 1.0f
        } catch (e: Exception) {
            progressRepo.value = 0f
            println("Failed to update data batch: ${e.message}")
        } finally {
            isUpdating = false
            startDatabaseListener() // Restart the database listener
        }
    }

    override fun stopDatabaseListener() {
        listener?.let {
            K_TempTravailleRepository.caReference.removeEventListener(it)
        }
        listener = null
    }
}
