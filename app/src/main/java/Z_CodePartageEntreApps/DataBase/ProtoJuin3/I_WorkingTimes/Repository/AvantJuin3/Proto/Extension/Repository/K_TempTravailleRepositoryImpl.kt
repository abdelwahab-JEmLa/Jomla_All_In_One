package Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository

import EntreApps.Shared.Models.Utilisateur
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.Extension.IntervalesEtJoursHandler
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.Extension.TimeFormatUtils
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.Extension.Z_FirebaseUtils
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
        startDatabaseListener()
        progressRepo.value = 1.0f
    }

    override fun add_new_Temp(k_TempTravaille: K_TempTravaille) {
        println(">>> add_new_Temp called: recordId=${k_TempTravaille.vid}")

        try {
            // Check if record already exists
            val existingIndex = modelDatas.indexOfFirst { it.vid == k_TempTravaille.vid }

            if (existingIndex != -1) {
                // Update existing record
                modelDatas[existingIndex] = k_TempTravaille
                println(">>> Updated existing record: ${k_TempTravaille.vid}")
            } else {
                // Add new record
                modelDatas.add(k_TempTravaille)
                modelDatas.sortBy { it.infosDeBase.dateInString }
                println(">>> Added new record: ${k_TempTravaille.vid}")
            }

            // Sync to Firebase
            updateDataUnSeulDataInFirebase(k_TempTravaille)

            println(">>> Successfully added/updated record with ${k_TempTravaille.intervalesDeTravaille.size} intervals")
        } catch (e: Exception) {
            println(">>> ERROR in add_new_Temp: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun addNewIntervals_au_TempTravaille(
        k_TempTravaille: K_TempTravaille,
        intervalesDeTravaille: List<K_TempTravaille.IntervalesDeTravaille>
    ) {
        println(">>> addNewIntervals_au_TempTravaille called: recordId=${k_TempTravaille.vid}, intervals count=${intervalesDeTravaille.size}")

        try {
            // Find the record in modelDatas
            val recordIndex = modelDatas.indexOfFirst { it.vid == k_TempTravaille.vid }

            if (recordIndex != -1) {
                val record = modelDatas[recordIndex]

                // Delete all existing intervals first
                println(">>> Deleting ${record.intervalesDeTravaille.size} existing intervals")
                record.intervalesDeTravaille.clear()

                // Add all new intervals
                record.intervalesDeTravaille.addAll(intervalesDeTravaille)
                println(">>> Added ${intervalesDeTravaille.size} new intervals")

                // Update the record in modelDatas
                modelDatas[recordIndex] = record

                // Sync to Firebase
                updateUnSeulData(k_TempTravaille.vid)

                println(">>> Successfully replaced all intervals for record: ${k_TempTravaille.vid}")
                println(">>> Total intervals in record: ${record.intervalesDeTravaille.size}")
            } else {
                println(">>> ERROR: Record not found with ID: ${k_TempTravaille.vid}")
            }
        } catch (e: Exception) {
            println(">>> ERROR in addNewIntervals_au_TempTravaille: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun addNewIntervalForWalid(
        recordId: String?,
        intervalId: String?,
        startTime: String?
    ) {
        println(">>> addNewIntervalForWalid called: recordId=$recordId, intervalId=$intervalId, startTime=$startTime")

        IntervalesEtJoursHandler.addNewInterval(
            modelDatas = modelDatas,
            recordId = recordId,
            intervalId = intervalId,
            startTime = startTime,
            utilisateur = Utilisateur.Walid
        ) { updatedRecordId ->
            println(">>> Walid interval added, updating record: $updatedRecordId")

            // Verify the interval was added
            val record = modelDatas.find { it.vid == updatedRecordId }
            val walidIntervals = record?.intervalesDeTravaille?.filter {
                it.utilisateur == Utilisateur.Walid
            }
            println(">>> Walid intervals count: ${walidIntervals?.size}")
            walidIntervals?.forEach {
                println(">>>   - ID: ${it.vid}, Start: ${it.tempDepart}, End: ${it.temparrete}, Recording: ${it.enCoureDEnregestrement}")
            }

            updateUnSeulData(updatedRecordId)
        }
    }

    override fun updateExistingIntervalForWalid(
        recordId: String?,
        intervalId: String?,
        startTime: String?,
        endTime: String?,
        typeTemp: K_TempTravaille.IntervalesDeTravaille.TypeTemp?
    ) {
        println(">>> updateExistingIntervalForWalid called: recordId=$recordId, intervalId=$intervalId, startTime=$startTime, endTime=$endTime, typeTemp=$typeTemp")

        IntervalesEtJoursHandler.updateExistingInterval(
            modelDatas = modelDatas,
            recordId = recordId,
            intervalId = intervalId,
            startTime = startTime,
            endTime = endTime,
            typeTemp = typeTemp,
            utilisateur = Utilisateur.Walid
        ) { updatedRecord ->
            println(">>> Walid interval updated: ${updatedRecord?.vid}")

            if (updatedRecord != null) {
                val interval = updatedRecord.intervalesDeTravaille.find { it.vid == intervalId }
                println(">>>   Updated interval: ID=${interval?.vid}, Vendeur=${interval?.utilisateur}, Start=${interval?.tempDepart}, End=${interval?.temparrete}")
            }

            updateOnPasseData(updatedRecord)
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
            newTempTravaille.infosDeBase.paye = infosDeBaseSnapshot.child("paye").getValue(Boolean::class.java) ?: false

            // Parse intervals
            val intervalsSnapshot = dataSnapshot.child("intervalesDeTravaille")
            for (intervalSnapshot in intervalsSnapshot.children) {
                val interval = K_TempTravaille.IntervalesDeTravaille(vid = intervalSnapshot.key ?: "00_00")

                // Parse vendeur field
                try {
                    val vendeurStr = intervalSnapshot.child("vendeur").getValue(String::class.java) ?: "Abdelmoumen"
                    interval.utilisateur = Utilisateur.valueOf(vendeurStr)
                } catch (e: Exception) {
                    interval.utilisateur = Utilisateur.Abdelmoumen
                }

                try {
                    val typeStr = intervalSnapshot.child("typeTemp").getValue(String::class.java) ?: "DEPLACEMENT"
                    interval.typeTemp = K_TempTravaille.IntervalesDeTravaille.TypeTemp.valueOf(typeStr)
                } catch (e: Exception) {
                    interval.typeTemp = K_TempTravaille.IntervalesDeTravaille.TypeTemp.DEPLACEMENT
                }

                interval.tempDepart = intervalSnapshot.child("tempDepart").getValue(String::class.java) ?: "HH:mm"
                interval.temparrete = intervalSnapshot.child("temparrete").getValue(String::class.java) ?: "HH:mm"
                interval.idClientSiAchat = intervalSnapshot.child("idBonDeCetteIntervale").getValue(Long::class.java) ?: 0L
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
            infosDeBase["paye"] = tempTravaille.infosDeBase.paye
            result["infosDeBase"] = infosDeBase

            // Add intervalesDeTravaille with vendeur field
            val intervalesDeTravaille = mutableMapOf<String, Any>()
            tempTravaille.intervalesDeTravaille.forEach { interval ->
                val intervalData = mapOf(
                    "vendeur" to interval.utilisateur.name,
                    "typeTemp" to interval.typeTemp.name,
                    "tempDepart" to interval.tempDepart,
                    "temparrete" to interval.temparrete,
                    "idBonDeCetteIntervale" to interval.idClientSiAchat,
                    "enCoureDEnregestrement" to interval.enCoureDEnregestrement
                )

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

    private fun startDatabaseListener() {
        stopDatabaseListener()
        Z_FirebaseUtils.startDatabaseListener(this) { newListener ->
            listener = newListener
        }
    }

    internal fun restartDatabaseListener() {
        startDatabaseListener()
    }

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
                date = currentDate.split("/").let { "${it[1]}.${it[2]}" }
            ) { recordId ->
                // Now upsert the interval
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

    override fun checkConnectivityAndSync() {
        Z_FirebaseUtils.checkConnectivityAndSync(this)
    }

    override fun updateOnPasseData(record: K_TempTravaille?) {
        if (record != null) {
            val recordIndex = modelDatas.indexOfFirst { it.vid == record.vid }

            if (recordIndex != -1) {
                modelDatas.removeAt(recordIndex)
                modelDatas.add(recordIndex, record)

                try {
                    checkConnectivityAndSync()
                    updateDataUnSeulDataInFirebase(record)
                } catch (e: Exception) {
                    println("Firebase update failed in updateOnPasseData: ${e.message}")
                }
            }
        }
    }

    override fun deleteIntevaleDeTemp(intervalId: String) {
        val recordToUpdate = IntervalesEtJoursHandler.deleteIntervaleDeTemp(
            modelDatas = modelDatas,
            intervalId = intervalId
        ) { recordId ->
            try {
                checkConnectivityAndSync()

                val sanitizedRecordId = Z_FirebaseUtils.sanitizeFirebaseKey(recordId)
                val sanitizedIntervalId = Z_FirebaseUtils.sanitizeFirebaseKey(intervalId)

                val intervalRef = K_TempTravailleRepository.caReference
                    .child(sanitizedRecordId)
                    .child("intervalesDeTravaille")
                    .child(sanitizedIntervalId)

                intervalRef.removeValue()
                    .addOnSuccessListener {
                        println("Successfully deleted interval $intervalId from record $recordId")
                    }
                    .addOnFailureListener { e ->
                        println("Failed to delete interval from Firebase: ${e.message}")
                    }

                updateUnSeulData(recordId)
            } catch (e: Exception) {
                println("Error deleting interval: ${e.message}")
            }
        }
    }

    override fun addNewInterval(
        recordId: String?,
        intervalId: String?,
        startTime: String?
    ) {
        IntervalesEtJoursHandler.addNewInterval(
            modelDatas = modelDatas,
            recordId = recordId,
            intervalId = intervalId,
            startTime = startTime
        ) { updatedRecordId ->
            updateUnSeulData(updatedRecordId)
        }
    }

    override fun updateExistingInterval(
        recordId: String?,
        intervalId: String?,
        startTime: String?,
        endTime: String?,
        typeTemp: K_TempTravaille.IntervalesDeTravaille.TypeTemp?
    ) {
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

                record.intervalesDeTravaille.forEachIndexed { index, interval ->
                    println("Interval $index: ID=${interval.vid}, Start=${interval.tempDepart}, End=${interval.temparrete}, Recording=${interval.enCoureDEnregestrement}")
                }

                modelDatas.removeAt(recordIndex)
                modelDatas.add(recordIndex, record)

                try {
                    checkConnectivityAndSync()
                    updateDataUnSeulDataInFirebase(record)
                } catch (e: Exception) {
                    println("Firebase update failed: ${e.message}")
                }
            }
        }
    }

    private fun updateDataUnSeulDataInFirebase(tempTravaille: K_TempTravaille) {
        try {
            val firebaseData = syncData(tempTravaille = tempTravaille) as Map<String, Any>
            val sanitizedKey = Z_FirebaseUtils.sanitizeFirebaseKey(tempTravaille.vid)

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
            checkConnectivityAndSync()

            datas.forEach { tempTravaille ->
                val firebaseData = syncData(tempTravaille = tempTravaille) as Map<String, Any>
                val sanitizedKey = Z_FirebaseUtils.sanitizeFirebaseKey(tempTravaille.vid)

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
            startDatabaseListener()
        }
    }

    override fun ajoutJour(date: String) {
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
