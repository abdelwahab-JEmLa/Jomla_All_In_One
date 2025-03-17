package Z_CodePartageEntreApps.Model.K_TempTravaille

import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Modules.TimeFormatUtils
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

    // Added helper method to format time input
    private fun formatTimeInput(timeInput: String): String {
        return try {
            val parts = timeInput.split(".")
            if (parts.size == 2) {
                val hours = parts[0].toInt().toString().padStart(2, '0')
                val minutes = parts[1].padStart(2, '0')
                "$hours:$minutes"
            } else {
                "HH:mm"
            }
        } catch (e: Exception) {
            "HH:mm"
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
                val sanitizedRecordId = FirebaseUtils.sanitizeFirebaseKey(recordToUpdate!!.vid)

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

    // Implemented from ViewModel
    override fun addNewInterval(
        recordId: String?,
        intervalId: String?,
        startTime: String?
    ) {
        val currentDate = TimeFormatUtils.getCurrentDate()
        val currentTime = TimeFormatUtils.getCurrentTime()

        val existingRecord = if (recordId != null) {
            modelDatas.find { it.vid == recordId }
        } else {
            modelDatas.find { it.infosDeBase.dateInString == currentDate }
        }

        if (existingRecord != null) {
            val currentTimeFormatted = intervalId ?: currentTime.replace(":", "_")
            val newInterval = K_TempTravaille.IntervalesDeTravaille(
                vid = currentTimeFormatted
            )

            newInterval.tempDepart = startTime ?: currentTime
            newInterval.enCoureDEnregestrement = true
            newInterval.typeTemp = K_TempTravaille.IntervalesDeTravaille.TypeTemp.ACHAT
            newInterval.idBonDeCetteIntervale = System.currentTimeMillis()

            existingRecord.intervalesDeTravaille.add(newInterval)
            updateUnSeulData(existingRecord.vid)
        } else {
            val newRecord = K_TempTravaille(vid = currentDate)
            newRecord.infosDeBase.dateInString = currentDate

            val currentTimeFormatted = intervalId ?: currentTime.replace(":", "_")
            val newInterval = K_TempTravaille.IntervalesDeTravaille(vid = currentTimeFormatted)

            newInterval.tempDepart = startTime ?: currentTime
            newInterval.enCoureDEnregestrement = true
            newInterval.typeTemp = K_TempTravaille.IntervalesDeTravaille.TypeTemp.ACHAT
            newInterval.idBonDeCetteIntervale = System.currentTimeMillis()

            newRecord.intervalesDeTravaille.add(newInterval)
            modelDatas.add(newRecord)
            updateUnSeulData(newRecord.vid)
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
        if (recordId == null || intervalId == null) {
            return
        }

        val existingRecord = modelDatas.find { it.vid == recordId }

        if (existingRecord != null) {
            val existingInterval = existingRecord.intervalesDeTravaille.find { it.vid == intervalId }

            if (existingInterval != null) {
                // Update start time if provided
                if (startTime != null) {
                    val formattedStartTime = formatTimeInput(startTime)
                    existingInterval.tempDepart = formattedStartTime
                }

                // Update end time if provided
                if (endTime != null) {
                    val formattedEndTime = formatTimeInput(endTime)
                    existingInterval.temparrete = formattedEndTime

                    // If end time is set, the interval is no longer recording
                    if (formattedEndTime != "HH:mm") {
                        existingInterval.enCoureDEnregestrement = false
                    }
                }

                // Update type if provided
                if (typeTemp != null) {
                    existingInterval.typeTemp = typeTemp
                }

                updateUnSeulData(existingRecord.vid)
            } else {
                val recordingInterval = existingRecord.intervalesDeTravaille.find { it.enCoureDEnregestrement }
                if (recordingInterval != null) {
                    // Update start time if provided
                    if (startTime != null) {
                        val formattedStartTime = formatTimeInput(startTime)
                        recordingInterval.tempDepart = formattedStartTime
                    }

                    // Update end time if provided
                    if (endTime != null) {
                        val formattedEndTime = formatTimeInput(endTime)
                        recordingInterval.temparrete = formattedEndTime

                        // If end time is set, the interval is no longer recording
                        if (formattedEndTime != "HH:mm") {
                            recordingInterval.enCoureDEnregestrement = false
                        }
                    }

                    // Update type if provided
                    if (typeTemp != null) {
                        recordingInterval.typeTemp = typeTemp
                    }

                    updateUnSeulData(existingRecord.vid)
                }
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
        try {
            // Parse input format MM.DD to the format used in the app (yyyy/MM/dd)
            val currentYear = java.time.Year.now().value
            val parts = date.split(".")

            if (parts.size != 2) {
                // Handle invalid format
                return
            }

            val month = parts[0].padStart(2, '0')
            val day = parts[1].padStart(2, '0')

            // Format the date in the format expected by the app
            val formattedDate = "$currentYear/$month/$day"
            val recordId = formattedDate.replace("/", "_")

            // Check if the date already exists
            val existingRecord = modelDatas.find { it.vid == recordId }

            if (existingRecord == null) {
                // Create a new record if it doesn't exist
                val newRecord = K_TempTravaille(vid = recordId)
                newRecord.infosDeBase.dateInString = formattedDate

                val defaultInterval = K_TempTravaille.IntervalesDeTravaille(vid = "00_00")
                defaultInterval.tempDepart = "00:00"
                defaultInterval.temparrete = "00:00"
                defaultInterval.enCoureDEnregestrement = false
                defaultInterval.typeTemp = K_TempTravaille.IntervalesDeTravaille.TypeTemp.ENTRE_PAR_MAIN
                defaultInterval.idBonDeCetteIntervale = System.currentTimeMillis()

                // Add the interval to the new record
                newRecord.intervalesDeTravaille.add(defaultInterval)

                // Add the new record to the list
                modelDatas.add(newRecord)

                // Update the database
                updateUnSeulData(newRecord.vid)
            } else {
                // If record already exists, just update it to ensure UI refreshes
                updateUnSeulData(existingRecord.vid)
            }
        } catch (e: Exception) {
            // Handle any exceptions
            println("Error adding new day: ${e.message}")
        }
    }

    override fun stopDatabaseListener() {
        listener?.let {
            K_TempTravailleRepository.caReference.removeEventListener(it)
        }
        listener = null
    }
}
