package Z_CodePartageEntreApps.Model.K_TempTravailleRepository.Repository

import Z_CodePartageEntreApps.Model.K_TempTravaille
import Z_CodePartageEntreApps.Model.K_TempTravailleRepository.Repository.Extension.IntervalesEtJoursHandler
import Z_CodePartageEntreApps.Model.K_TempTravailleRepository.Repository.Extension.TimeFormatUtils
import Z_CodePartageEntreApps.Model.K_TempTravailleRepository.Repository.Extension.Z_FirebaseUtils
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class K_TempTravailleRepositoryImpl :
    K_TempTravailleRepository {
    override var modelDatas: SnapshotStateList<K_TempTravaille> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private var listener: ValueEventListener? = null
    internal var isUpdating = false
    internal var lastUpdateTimestamp = 0L


    init {
        Z_FirebaseUtils.initializeFirebaseOfflineCapability()
      //  startDatabaseListener()
        progressRepo.value = 1.0f

    }

    private fun startDatabaseListener() {
        stopDatabaseListener()
        Z_FirebaseUtils.startDatabaseListener(this) { newListener ->
            listener = newListener
        }
    }

    internal fun restartDatabaseListener() {
        startDatabaseListener()
    }
    // In K_TempTravailleRepository.kt - Update the interface

    // In K_TempTravailleRepositoryImpl.kt - Modify the implementation
    override fun ajouteRecodeAvecIntervaleDAchat(
        clientId: Long,
        typeTemp:  K_TempTravaille.IntervalesDeTravaille.TypeTemp
    ): K_TempTravaille? {
        val currentDate = TimeFormatUtils.getCurrentDate()
        val currentDateStr = currentDate.replace("/", "_")
        val currentTime = TimeFormatUtils.getCurrentTime()
        val currentTimeFormatted = currentTime.replace(":", "_")
        var createdRecord: K_TempTravaille? = null

        // Check if the date already exists
        val existingRecord = modelDatas.find { it.vid == currentDateStr }

        if (existingRecord != null) {
            // Add new interval with ACHAT type
            IntervalesEtJoursHandler.addNewInterval(
                modelDatas = modelDatas,
                recordId = existingRecord.vid,
                intervalId = currentTimeFormatted,
                startTime = currentTime
            ) { recordId ->
                // Find the newly created interval
                val record = modelDatas.find { it.vid == recordId }
                val interval = record?.intervalesDeTravaille?.find {
                    it.vid == currentTimeFormatted && it.enCoureDEnregestrement
                }

                // Set client ID
                interval?.idClientSiAchat = clientId
                interval?.typeTemp = typeTemp

                // Update database
                updateOnPasseData(record)
                createdRecord = record
            }
        } else {
            // First create the day record
            IntervalesEtJoursHandler.ajoutJour(
                modelDatas = modelDatas,
                date = currentDate.split("/").let { "${it[1]}.${it[2]}" } // Convert from yyyy/MM/dd to MM.dd
            ) { recordId ->
                // Now add the interval
                val record = modelDatas.find { it.vid == recordId }
                if (record != null) {
                    IntervalesEtJoursHandler.addNewInterval(
                        modelDatas = modelDatas,
                        recordId = recordId,
                        intervalId = currentTimeFormatted,
                        startTime = currentTime
                    ) { updatedRecordId ->
                        // Find the newly created interval
                        val updatedRecord = modelDatas.find { it.vid == updatedRecordId }
                        val interval = updatedRecord?.intervalesDeTravaille?.find {
                            it.vid == currentTimeFormatted && it.enCoureDEnregestrement
                        }

                        // Set client ID and type
                        interval?.idClientSiAchat = clientId
                        interval?.typeTemp = typeTemp

                        // Update database
                        updateOnPasseData(updatedRecord)
                        createdRecord = updatedRecord
                    }
                }
            }
        }

        return createdRecord
    }
    // Check connectivity and sync if state has changed
    override fun checkConnectivityAndSync() {
        Z_FirebaseUtils.checkConnectivityAndSync(this)
    }
    override fun updateOnPasseData(record: K_TempTravaille?) {
        if (record != null) {
            // Find the index of the record in the modelDatas list
            val recordIndex = modelDatas.indexOfFirst { it.vid == record.vid }

            if (recordIndex != -1) {
                // Update the record in the modelDatas list
                modelDatas.removeAt(recordIndex)
                modelDatas.add(recordIndex, record)

                // Update the Firebase database with the updated record
                try {
                    // Check connectivity before trying to update Firebase
                    checkConnectivityAndSync()

                    // Update Firebase database with the updated record
                    updateDataUnSeulDataInFirebase(record)
                } catch (e: Exception) {
                    // Log the error or handle it appropriately
                    println("Firebase update failed in updateOnPasseData: ${e.message}")
                }
            }
        }
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
                val sanitizedRecordId = Z_FirebaseUtils.sanitizeFirebaseKey(recordId)

                // Sanitize the interval ID
                val sanitizedIntervalId = Z_FirebaseUtils.sanitizeFirebaseKey(intervalId)

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
            updateOnPasseData(updatedRecordId)
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
            val sanitizedKey = Z_FirebaseUtils.sanitizeFirebaseKey(tempTravaille.vid)

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
    // extract au Z_FirebaseUtils
        dataSnapshot: DataSnapshot? = null,
        tempTravaille: K_TempTravaille? = null
    ): Any {
        // Case 1: Parse from Firebase to K_TempTravaille
        if (dataSnapshot != null && tempTravaille == null) {
            val newTempTravaille = K_TempTravaille(vid = dataSnapshot.key ?: "unknown")

            // Parse infosDeBase
            val infosDeBaseSnapshot = dataSnapshot.child("infosDeBase")
            newTempTravaille.infosDeBase.dateInString = infosDeBaseSnapshot.child("dateInString").getValue(String::class.java) ?: newTempTravaille.vid
            newTempTravaille.infosDeBase.paye = infosDeBaseSnapshot.child("paye").getValue(Boolean::class.java) ?: false

            // Parse intervals
            val intervalsSnapshot = dataSnapshot.child("intervalesDeTravaille")
            for (intervalSnapshot in intervalsSnapshot.children) {
                val interval =
                     K_TempTravaille.IntervalesDeTravaille(vid = intervalSnapshot.key ?: "00_00")

                try {
                    val typeStr = intervalSnapshot.child("typeTemp").getValue(String::class.java) ?: "DEPLACEMENT"
                    interval.typeTemp =  K_TempTravaille.IntervalesDeTravaille.TypeTemp.valueOf(typeStr)
                } catch (e: Exception) {
                    interval.typeTemp =  K_TempTravaille.IntervalesDeTravaille.TypeTemp.DEPLACEMENT
                }

                interval.tempDepart = intervalSnapshot.child("tempDepart").getValue(String::class.java) ?: "HH:mm"
                interval.temparrete = intervalSnapshot.child("temparrete").getValue(String::class.java) ?: "HH:mm"
                interval.idClientSiAchat = intervalSnapshot.child("idBonDeCetteIntervale").getValue(Long::class.java) ?: 0L

                // Make sure we properly read the recording state
                interval.enCoureDEnregestrement = intervalSnapshot.child("enCoureDEnregestrement").getValue(Boolean::class.java) ?: false

                newTempTravaille.intervalesDeTravaille.add(interval)
            }

            return newTempTravaille
        }
        // Case 2: Convert K_TempTravaille to Firebase data
        else if (tempTravaille != null && dataSnapshot == null) {
            val result = mutableMapOf<String, Any>()

            val infosDeBase = mutableMapOf<String, Any>()
            infosDeBase["dateInString"] = tempTravaille.infosDeBase.dateInString
            result["infosDeBase"] = infosDeBase
            infosDeBase["paye"] = tempTravaille.infosDeBase.paye
            result["infosDeBase"] = infosDeBase

            // Add intervalesDeTravaille
            val intervalesDeTravaille = mutableMapOf<String, Any>()
            tempTravaille.intervalesDeTravaille.forEach { interval ->
                val intervalData = mapOf(
                    "typeTemp" to interval.typeTemp.name,
                    "tempDepart" to interval.tempDepart,
                    "temparrete" to interval.temparrete,
                    "idBonDeCetteIntervale" to interval.idClientSiAchat,
                    "enCoureDEnregestrement" to interval.enCoureDEnregestrement
                )

                // Make sure the interval ID is also sanitized
                val sanitizedIntervalId = Z_FirebaseUtils.sanitizeFirebaseKey(interval.vid)
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
        return Z_FirebaseUtils.onDataBaseChangeListnerAndLoad(this)
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
                val sanitizedKey = Z_FirebaseUtils.sanitizeFirebaseKey(tempTravaille.vid)

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

