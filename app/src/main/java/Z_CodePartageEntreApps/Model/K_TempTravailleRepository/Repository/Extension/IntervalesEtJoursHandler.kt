package Z_CodePartageEntreApps.Model.K_TempTravailleRepository.Repository.Extension

import Z_CodePartageEntreApps.Model.K_TempTravaille
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Utility class for handling interval and day operations for K_TempTravaille
 * Contains extracted functions from K_TempTravailleRepositoryImpl
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
            newInterval.typeTemp = K_TempTravaille. IntervalesDeTravaille.TypeTemp.ACHAT
            newInterval.idClientSiAchat = System.currentTimeMillis()

            existingRecord.intervalesDeTravaille.add(newInterval)
            callback(existingRecord.vid)
        } else {
            val newRecord = K_TempTravaille(vid = currentDate)
            newRecord.infosDeBase.dateInString = currentDate

            val currentTimeFormatted = intervalId ?: currentTime.replace(":", "_")
            val newInterval =  K_TempTravaille.IntervalesDeTravaille(vid = currentTimeFormatted)

            newInterval.tempDepart = startTime ?: currentTime
            newInterval.enCoureDEnregestrement = true
            newInterval.typeTemp =  K_TempTravaille.IntervalesDeTravaille.TypeTemp.ACHAT
            newInterval.idClientSiAchat = System.currentTimeMillis()

            newRecord.intervalesDeTravaille.add(newInterval)
            modelDatas.add(newRecord)
            callback(newRecord.vid)
        }
    }

    /**
     * Update an existing interval with new start/end times or type
     * With enhanced logging and improved time formatting
     */

    fun updateExistingInterval(
        //enleve logs
        modelDatas: SnapshotStateList<K_TempTravaille>,
        recordId: String? = null,
        intervalId: String? = null,
        startTime: String? = null,
        endTime: String? = null,
        typeTemp:  K_TempTravaille.IntervalesDeTravaille.TypeTemp? = null,
        callback: (K_TempTravaille) -> Unit
    ) {
        println("DEBUG: updateExistingInterval called")
        println("DEBUG: Parameters - recordId: $recordId, intervalId: $intervalId")
        println("DEBUG: Parameters - startTime: $startTime, endTime: $endTime, typeTemp: $typeTemp")

        if (recordId == null || intervalId == null) {
            println("DEBUG: Early return - recordId or intervalId is null")
            return
        }

        val existingRecord = modelDatas.find { it.vid == recordId }

        if (existingRecord == null) {
            println("DEBUG: No record found with ID: $recordId")
            return
        }

        println("DEBUG: Found record with ID: ${existingRecord.vid}")
        println("DEBUG: Record has ${existingRecord.intervalesDeTravaille.size} intervals")

        val existingInterval = existingRecord.intervalesDeTravaille.find { it.vid == intervalId }

        if (existingInterval != null) {
            println("DEBUG: Found interval with ID: ${existingInterval.vid}")
            println("DEBUG: Current values - start: ${existingInterval.tempDepart}, end: ${existingInterval.temparrete}")
            println("DEBUG: Current recording state: ${existingInterval.enCoureDEnregestrement}")

            // Update start time if provided
            if (startTime != null) {
                val formattedStartTime = formatTimeInput(startTime)
                println("DEBUG: Updating start time from ${existingInterval.tempDepart} to $formattedStartTime")
                existingInterval.tempDepart = formattedStartTime
                println("DEBUG: After update, start time is: ${existingInterval.tempDepart}")
            }

            // Update end time if provided
            if (endTime != null) {
                // Use the actual endTime directly if it's already in the right format
                val formattedEndTime = formatTimeInput(endTime)
                println("DEBUG: Raw endTime: $endTime")
                println("DEBUG: Formatted endTime: $formattedEndTime")
                println("DEBUG: Updating end time from ${existingInterval.temparrete} to $formattedEndTime")

                // Perform the update
                existingInterval.temparrete = formattedEndTime

                // Verify the update happened
                println("DEBUG: After update, end time is: ${existingInterval.temparrete}")

                // If end time is set to a valid time, the interval is no longer recording
                if (formattedEndTime != "HH:mm") {
                    println("DEBUG: Setting recording state to false")
                    existingInterval.enCoureDEnregestrement = false
                    println("DEBUG: After update, recording state is: ${existingInterval.enCoureDEnregestrement}")
                }
            }

            // Update type if provided
            if (typeTemp != null) {
                println("DEBUG: Updating type from ${existingInterval.typeTemp} to $typeTemp")
                existingInterval.typeTemp = typeTemp
                println("DEBUG: After update, type is: ${existingInterval.typeTemp}")
            }

            println("DEBUG: Final interval state - start: ${existingInterval.tempDepart}, end: ${existingInterval.temparrete}")
            println("DEBUG: Final recording state: ${existingInterval.enCoureDEnregestrement}")

            println("DEBUG: Calling callback with updated record")
            callback(existingRecord)
        } else {
            println("DEBUG: No interval found with ID: $intervalId")
            println("DEBUG: Checking for any recording interval")

            val recordingInterval = existingRecord.intervalesDeTravaille.find { it.enCoureDEnregestrement }
            if (recordingInterval != null) {
                println("DEBUG: Found recording interval with ID: ${recordingInterval.vid}")
                println("DEBUG: Current values - start: ${recordingInterval.tempDepart}, end: ${recordingInterval.temparrete}")

                // Update start time if provided
                if (startTime != null) {
                    val formattedStartTime = formatTimeInput(startTime)
                    println("DEBUG: Updating start time from ${recordingInterval.tempDepart} to $formattedStartTime")
                    recordingInterval.tempDepart = formattedStartTime
                    println("DEBUG: After update, start time is: ${recordingInterval.tempDepart}")
                }

                // Update end time if provided
                if (endTime != null) {
                    // Use the actual endTime directly if it's already in the right format
                    val formattedEndTime = formatTimeInput(endTime)
                    println("DEBUG: Raw endTime: $endTime")
                    println("DEBUG: Formatted endTime: $formattedEndTime")
                    println("DEBUG: Updating end time from ${recordingInterval.temparrete} to $formattedEndTime")

                    // Perform the update
                    recordingInterval.temparrete = formattedEndTime

                    // Verify the update happened
                    println("DEBUG: After update, end time is: ${recordingInterval.temparrete}")

                    // If end time is set to a valid time, the interval is no longer recording
                    if (formattedEndTime != "HH:mm") {
                        println("DEBUG: Setting recording state to false")
                        recordingInterval.enCoureDEnregestrement = false
                        println("DEBUG: After update, recording state is: ${recordingInterval.enCoureDEnregestrement}")
                    }
                }

                // Update type if provided
                if (typeTemp != null) {
                    println("DEBUG: Updating type from ${recordingInterval.typeTemp} to $typeTemp")
                    recordingInterval.typeTemp = typeTemp
                    println("DEBUG: After update, type is: ${recordingInterval.typeTemp}")
                }

                println("DEBUG: Final interval state - start: ${recordingInterval.tempDepart}, end: ${recordingInterval.temparrete}")
                println("DEBUG: Final recording state: ${recordingInterval.enCoureDEnregestrement}")

                println("DEBUG: Calling callback with updated record")
                callback(existingRecord)
            } else {
                println("DEBUG: No recording interval found")
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
