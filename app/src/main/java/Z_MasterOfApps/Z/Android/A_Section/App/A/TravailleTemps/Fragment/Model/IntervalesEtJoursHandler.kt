package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.Model

import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Modules.TimeFormatUtils
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Utility class for handling interval and day operations for K_TempTravaille
 * Contains extracted functions from K_TempTravailleRepositoryImpl
 */
object IntervalesEtJoursHandler {

    /**
     * Format time input from decimal format to HH:mm format
     */
    fun formatTimeInput(timeInput: String): String {
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

    /**
     * Add a new work interval to an existing or new record
     */
    fun addNewInterval(
        modelDatas: SnapshotStateList<K_TempTravaille>,
        recordId: String? = null,
        intervalId: String? = null,
        startTime: String? = null,
        callback: (String) -> Unit
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
            callback(existingRecord.vid)
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
            callback(newRecord.vid)
        }
    }

    /**
     * Update an existing interval with new start/end times or type
     */
    fun updateExistingInterval(
        modelDatas: SnapshotStateList<K_TempTravaille>,
        recordId: String? = null,
        intervalId: String? = null,
        startTime: String? = null,
        endTime: String? = null,
        typeTemp: K_TempTravaille.IntervalesDeTravaille.TypeTemp? = null,
        callback: (String) -> Unit
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

                callback(existingRecord.vid)
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

                    callback(existingRecord.vid)
                }
            }
        }
    }

    /**
     * Add a new day to the repository with a default interval
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
                // Create a new record if it doesn't exist
                val newRecord = K_TempTravaille(vid = recordId)
                newRecord.infosDeBase.dateInString = formattedDate

                val defaultInterval = K_TempTravaille.IntervalesDeTravaille(vid = "00_00")
                defaultInterval.tempDepart = "00:00"
                defaultInterval.temparrete = "00:00"
                defaultInterval.enCoureDEnregestrement = false
                defaultInterval.typeTemp =
                    K_TempTravaille.IntervalesDeTravaille.TypeTemp.ENTRE_PAR_MAIN
                defaultInterval.idBonDeCetteIntervale = System.currentTimeMillis()

                // Add the interval to the new record
                newRecord.intervalesDeTravaille.add(defaultInterval)

                // Add the new record to the list
                modelDatas.add(newRecord)

                // Update the database
                callback(newRecord.vid)
            } else {
                // If record already exists, just update it to ensure UI refreshes
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

        // If found record to update
        if (recordToUpdate != null && callback != null) {
            callback(recordToUpdate!!.vid)
        }

        return recordToUpdate
    }
}
