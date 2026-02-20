package Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.Extension

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravaille
import EntreApps.Shared.Models.Components.Utilisateur
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Utility class for handling interval and day operations for K_TempTravaille
 * Contains BonAchatInfos_FragID3 functions from K_TempTravailleRepositoryImpl
 */
object IntervalesEtJoursHandler {

    /**
     * Format time input from various formats to HH:mm format
     * Enhanced to handle both decimal format (7.30) and time format (07:30)
     */
    fun formatTimeInput(timeInput: String): String {
        return try {
            // Check if already in HH:mm format
            if (timeInput.contains(":") && timeInput.matches("\\d{1,2}:\\d{2}".toRegex())) {
                // Already in correct format, ensure proper padding
                val parts = timeInput.split(":")
                val hours = parts[0].toInt().toString().padStart(2, '0')
                val minutes = parts[1].padStart(2, '0')
                return "$hours:$minutes"
            }

            // Try to parse as decimal format (e.g. 7.30)
            val parts = timeInput.split(".")
            if (parts.size == 2) {
                val hours = parts[0].toInt().toString().padStart(2, '0')
                val minutes = parts[1].padStart(2, '0')
                "$hours:$minutes"
            } else {
                // Try to parse as integer (e.g., 7)
                val hours = timeInput.toIntOrNull()?.toString()?.padStart(2, '0')
                if (hours != null) {
                    "$hours:00"
                } else {
                    "HH:mm"
                }
            }
        } catch (e: Exception) {
            println("DEBUG: Error formatting time '$timeInput': ${e.message}")
            "HH:mm"
        }
    }
    fun addNewInterval(
        modelDatas: SnapshotStateList<K_TempTravaille>,
        recordId: String?,
        intervalId: String?,
        startTime: String?,
        utilisateur: Utilisateur = Utilisateur.Abdelmoumen,
        onComplete: (String) -> Unit = {}
    ) {
        if (recordId == null) return

        val recordIndex = modelDatas.indexOfFirst { it.vid == recordId }
        if (recordIndex == -1) return

        val record = modelDatas[recordIndex]
        val currentTime = TimeFormatUtils.getCurrentTime()
        val newIntervalId = intervalId ?: currentTime.replace(":", "_")

        // Check if interval already exists
        val existingIntervalIndex = record.intervalesDeTravaille.indexOfFirst {
            it.vid == newIntervalId
        }

        if (existingIntervalIndex != -1) {
            // Update existing interval
            val existingInterval = record.intervalesDeTravaille[existingIntervalIndex]
            if (startTime != null) {
                existingInterval.tempDepart = startTime
            }
            existingInterval.utilisateur = utilisateur
            existingInterval.enCoureDEnregestrement = true
        } else {
            // Create new interval
            val newInterval = K_TempTravaille.IntervalesDeTravaille(vid = newIntervalId)
            newInterval.tempDepart = startTime ?: currentTime
            newInterval.utilisateur = utilisateur
            newInterval.enCoureDEnregestrement = true
            newInterval.temparrete = "HH:mm"

            record.intervalesDeTravaille.add(newInterval)
        }

        onComplete(recordId)
    }

    fun updateExistingInterval(
        modelDatas: SnapshotStateList<K_TempTravaille>,
        recordId: String?,
        intervalId: String?,
        startTime: String?,
        endTime: String?,
        typeTemp: K_TempTravaille.IntervalesDeTravaille.TypeTemp?,
        utilisateur: Utilisateur? = null,
        onComplete: (K_TempTravaille?) -> Unit = {}
    ) {
        if (recordId == null || intervalId == null) {
            onComplete(null)
            return
        }

        val record = modelDatas.find { it.vid == recordId }
        if (record == null) {
            onComplete(null)
            return
        }

        val interval = record.intervalesDeTravaille.find { it.vid == intervalId }
        if (interval == null) {
            onComplete(null)
            return
        }

        // Update interval properties
        if (startTime != null) {
            interval.tempDepart = startTime
        }

        if (endTime != null) {
            interval.temparrete = endTime
            interval.enCoureDEnregestrement = false
        }

        if (typeTemp != null) {
            interval.typeTemp = typeTemp
        }

        if (utilisateur != null) {
            interval.utilisateur = utilisateur
        }

        onComplete(record)
    }

    /**
     * Add add_New new day to the repository with add_New default interval
     */
    fun ajoutJour(
        modelDatas: SnapshotStateList<K_TempTravaille>,
        date: String,
        callback: (String) -> Unit
    ) {
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
                // Create add_New new record if it doesn't exist
                val newRecord = K_TempTravaille(vid = recordId)
                newRecord.infosDeBase.dateInString = formattedDate

                val defaultInterval =  K_TempTravaille.IntervalesDeTravaille(vid = "00_00")
                defaultInterval.tempDepart = "00:00"
                defaultInterval.temparrete = "00:00"
                defaultInterval.enCoureDEnregestrement = false
                defaultInterval.typeTemp =
                     K_TempTravaille.IntervalesDeTravaille.TypeTemp.ENTRE_PAR_MAIN
                defaultInterval.idClientSiAchat = System.currentTimeMillis()

                // Add the interval to the new record
                newRecord.intervalesDeTravaille.add(defaultInterval)

                // Add the new record to the list
                modelDatas.add(newRecord)

                // Update the database
                callback(newRecord.vid)
            } else {
                // If record already exists, just upsertLenceCommandeRepoGroupedProtoAvantJuin3 it to ensure UI refreshes
                callback(existingRecord.vid)
            }
        } catch (e: Exception) {
            // Handle any exceptions
            println("Error adding new day: ${e.message}")
        }
    }

    /**
     * Delete an interval by its ID
     */
    fun deleteIntervaleDeTemp(
        modelDatas: SnapshotStateList<K_TempTravaille>,
        intervalId: String,
        callback: ((String) -> Unit)? = null
    ): K_TempTravaille? {
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

        // If found record to upsertLenceCommandeRepoGroupedProtoAvantJuin3
        if (recordToUpdate != null && callback != null) {
            callback(recordToUpdate!!.vid)
        }

        return recordToUpdate
    }
}
