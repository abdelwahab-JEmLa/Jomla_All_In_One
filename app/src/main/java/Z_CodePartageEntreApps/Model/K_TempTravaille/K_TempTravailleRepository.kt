package Z_CodePartageEntreApps.Model.K_TempTravaille

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface K_TempTravailleRepository {
    var modelDatas: SnapshotStateList<K_TempTravaille>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<K_TempTravaille>, Flow<Float>>
    suspend fun updateDatas(datas: SnapshotStateList<K_TempTravaille>)
    fun stopDatabaseListener()
    fun checkConnectivityAndSync()
    fun deleteIntevaleDeTemp(intervalId: String)
    fun ajoutJour(date: String)

    // Updated method signature to match the implementation in MockTempTravailleRepository
    fun updateUnSeulData(
        recordId: String? = null,
    )

    // Added methods that were previously in ViewModel
    fun addNewInterval(
        recordId: String? = null,
        intervalId: String? = null,
        startTime: String? = null
    )

    fun updateExistingInterval(
        recordId: String? = null,
        intervalId: String? = null,
        startTime: String? = null,
        endTime: String? = null,
        typeTemp: K_TempTravaille.IntervalesDeTravaille.TypeTemp? = null
    )

    companion object {
        val caReference = Firebase.database.getReference("K_TempTravaille")
    }
}



class K_TempTravailleRepositoryImpl : K_TempTravailleRepository {
    override var modelDatas: SnapshotStateList<K_TempTravaille> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private var listener: ValueEventListener? = null
    internal var isUpdating = false
    internal var lastUpdateTimestamp = 0L

    init {
        FirebaseUtils.initializeFirebaseOfflineCapability()
        startDatabaseListener()
    }

    private fun startDatabaseListener() {
        stopDatabaseListener()
        FirebaseUtils.startDatabaseListener(this) { newListener ->
            listener = newListener
        }
    }

    internal fun restartDatabaseListener() {
        startDatabaseListener()
    }

    // Check connectivity and sync if state has changed
    override fun checkConnectivityAndSync() {
        FirebaseUtils.checkConnectivityAndSync(this)
    }

    override fun deleteIntevaleDeTemp(intervalId: String) {
        // Use the extracted handler to perform the deletion
        val recordToUpdate = IntervalesEtJoursHandler.deleteIntervaleDeTemp(
            modelDatas = modelDatas,
            intervalId = intervalId
        ) { recordId ->
            // Update Firebase after local changes
            try {
                // Check connectivity before trying to update Firebase
                checkConnectivityAndSync()

                // Sanitize the record ID
                val sanitizedRecordId = FirebaseUtils.sanitizeFirebaseKey(recordId)

                // Sanitize the interval ID
                val sanitizedIntervalId = FirebaseUtils.sanitizeFirebaseKey(intervalId)

                // Create reference to the interval
                val intervalRef = K_TempTravailleRepository.caReference
                    .child(sanitizedRecordId)
                    .child("intervalesDeTravaille")
                    .child(sanitizedIntervalId)

                // Remove the interval from Firebase
                intervalRef.removeValue()
                    .addOnSuccessListener {
                        println("Successfully deleted interval $intervalId from record $recordId")
                    }
                    .addOnFailureListener { e ->
                        println("Failed to delete interval from Firebase: ${e.message}")
                    }

                // Update local record
                updateUnSeulData(recordId)
            } catch (e: Exception) {
                println("Error deleting interval: ${e.message}")
            }
        }
    }

    // Implemented from ViewModel
    override fun addNewInterval(
        recordId: String?,
        intervalId: String?,
        startTime: String?
    ) {
        // Use the extracted handler to add a new interval
        IntervalesEtJoursHandler.addNewInterval(
            modelDatas = modelDatas,
            recordId = recordId,
            intervalId = intervalId,
            startTime = startTime
        ) { updatedRecordId ->
            updateUnSeulData(updatedRecordId)
        }
    }

    // Implemented from ViewModel
    override fun updateExistingInterval(
        recordId: String?,
        intervalId: String?,
        startTime: String?,
        endTime: String?,
        typeTemp: K_TempTravaille.IntervalesDeTravaille.TypeTemp?
    ) {
        // Use the extracted handler to update an existing interval
        IntervalesEtJoursHandler.updateExistingInterval(
            modelDatas = modelDatas,
            recordId = recordId,
            intervalId = intervalId,
            startTime = startTime,
            endTime = endTime,
            typeTemp = typeTemp
        ) { updatedRecordId ->
            updateUnSeulData(updatedRecordId)
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
            val sanitizedKey = FirebaseUtils.sanitizeFirebaseKey(tempTravaille.vid)

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

    internal fun syncData(
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
                val sanitizedIntervalId = FirebaseUtils.sanitizeFirebaseKey(interval.vid)
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
        return FirebaseUtils.onDataBaseChangeListnerAndLoad(this)
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
                val sanitizedKey = FirebaseUtils.sanitizeFirebaseKey(tempTravaille.vid)

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

    override fun ajoutJour(date: String) {
        // Use the extracted handler to add a new day
        IntervalesEtJoursHandler.ajoutJour(
            modelDatas = modelDatas,
            date = date
        ) { updatedRecordId ->
            updateUnSeulData(updatedRecordId)
        }
    }

    override fun stopDatabaseListener() {
        listener?.let {
            K_TempTravailleRepository.caReference.removeEventListener(it)
        }
        listener = null
    }
}
