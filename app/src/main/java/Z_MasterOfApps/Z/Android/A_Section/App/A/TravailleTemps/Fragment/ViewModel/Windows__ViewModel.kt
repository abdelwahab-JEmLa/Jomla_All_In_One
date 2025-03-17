package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel

import Z_CodePartageEntreApps.Model.K_TempTravaille
import Z_CodePartageEntreApps.Model.K_TempTravailleRepository
import Z_CodePartageEntreApps.Model.K_TempTravailleRepositoryImpl
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Modules.TimeFormatUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Windows__ViewModel(
    val repository: K_TempTravailleRepository = K_TempTravailleRepositoryImpl()
) : ViewModel() {

    val dateList get() = repository.modelDatas

    // State tracking
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _currentRecordId = MutableStateFlow<String?>(null)
    private val _currentIntervalId = MutableStateFlow<String?>(null)
    private val _currentStartTime = MutableStateFlow<String?>(null)
    private val _elapsedTimeInSeconds = MutableStateFlow(0)
    val elapsedTimeInSeconds: StateFlow<Int> = _elapsedTimeInSeconds.asStateFlow()
    private val _totalWorkedSeconds = MutableStateFlow(0L)
    val totalWorkedSeconds: StateFlow<Long> = _totalWorkedSeconds.asStateFlow()
    private val _displayTime = MutableStateFlow("00:00:00")
    val displayTime: StateFlow<String> = _displayTime.asStateFlow()
    private val _currentElapsedSeconds = MutableStateFlow(0L)
    val currentElapsedSeconds: StateFlow<Long> = _currentElapsedSeconds.asStateFlow()
    private val _lastUpdateTime = MutableStateFlow(System.currentTimeMillis())
    val lastUpdateTime: StateFlow<Long> = _lastUpdateTime.asStateFlow()
    private val _currentDate = MutableStateFlow(TimeFormatUtils.getCurrentDate())
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    init {
        updateTotalWorkedTime()
    }

    fun ajoutJour(date: String): Unit {
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
            val existingRecord = dateList.find { it.vid == recordId }

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
                dateList.add(newRecord)

                // Update the database
                repository.updateUnSeulData(newRecord.vid)
            } else {
                // If record already exists, just update it to ensure UI refreshes
                repository.updateUnSeulData(existingRecord.vid)
            }
        } catch (e: Exception) {
            // Handle any exceptions
            println("Error adding new day: ${e.message}")
        }
    }

    fun deleteIntervaleTemp(intervalId: String) {
        // Find the interval
        var recordId: String? = null

        // Find which record contains this interval
        dateList.forEach { record ->
            val hasInterval = record.intervalesDeTravaille.any { it.vid == intervalId }
            if (hasInterval) {
                recordId = record.vid
                return@forEach
            }
        }

        // If found the record containing this interval
        if (recordId != null) {
            repository.deleteIntevaleDeTemp(intervalId)
            updateTotalWorkedTime() // Update the time calculations after deletion
        }
    }

    fun updatePareMain(recordId: String, startTime: String? = null, endTime: String? = null) {
        // Find the existing record
        val existingRecord = dateList.find { it.vid == recordId }

        if (existingRecord != null) {
            // If we have an active interval, update it
            val activeInterval = existingRecord.intervalesDeTravaille.find { it.enCoureDEnregestrement }

            if (activeInterval != null) {
                // Update start time if provided
                if (startTime != null) {
                    val formattedStartTime = formatTimeInput(startTime)
                    activeInterval.tempDepart = formattedStartTime
                }

                // Update end time if provided
                if (endTime != null) {
                    val formattedEndTime = formatTimeInput(endTime)
                    activeInterval.temparrete = formattedEndTime

                    // If end time is set, the interval is no longer recording
                    if (formattedEndTime != "HH:mm") {
                        activeInterval.enCoureDEnregestrement = false
                        _isRecording.value = false
                    }
                }

                // Update the data in the repository
                repository.updateUnSeulData(existingRecord.vid)

                // Update the total worked time to reflect the changes
                updateTotalWorkedTime()
            } else {
                // If no active interval, create a new one if both start and end time are provided
                if (startTime != null && endTime != null) {
                    val formattedStartTime = formatTimeInput(startTime)
                    val formattedEndTime = formatTimeInput(endTime)

                    val currentTimeFormatted = formattedStartTime.replace(":", "_")
                    val newInterval = K_TempTravaille.IntervalesDeTravaille(vid = currentTimeFormatted)

                    newInterval.tempDepart = formattedStartTime
                    newInterval.temparrete = formattedEndTime
                    newInterval.enCoureDEnregestrement = false
                    newInterval.typeTemp = K_TempTravaille.IntervalesDeTravaille.TypeTemp.ENTRE_PAR_MAIN
                    newInterval.idBonDeCetteIntervale = System.currentTimeMillis()

                    existingRecord.intervalesDeTravaille.add(newInterval)
                    repository.updateUnSeulData(existingRecord.vid)

                    updateTotalWorkedTime()
                }
            }
        }
    }

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
    private fun startTimeInterval() {
        val currentDate = TimeFormatUtils.getCurrentDate()
        val currentDateStr = currentDate.replace("/", "_")
        val currentTime = TimeFormatUtils.getCurrentTime()
        val currentTimeStr = currentTime.replace(":", "_")

        _currentRecordId.value = currentDateStr
        _currentIntervalId.value = currentTimeStr
        _currentStartTime.value = currentTime
        _currentElapsedSeconds.value = 0L
        _lastUpdateTime.value = System.currentTimeMillis()

        viewModelScope.launch {
            addNewInterval(
                recordId = currentDateStr,
                intervalId = currentTimeStr,
                startTime = currentTime
            )
            _isRecording.value = true
            _elapsedTimeInSeconds.value = 0
        }
    }

    private fun stopTimeInterval() {
        val currentDate = TimeFormatUtils.getCurrentDate()
        val currentDateStr = currentDate.replace("/", "_")
        val currentTime = TimeFormatUtils.getCurrentTime()
        val currentTimeStr = currentTime.replace(":", "_")

        viewModelScope.launch {
            updateExistingInterval(
                recordId = currentDateStr,
                intervalId = currentTimeStr,
                endTime = currentTime
            )

            _isRecording.value = false
            _currentRecordId.value = null
            _currentIntervalId.value = null
            _currentStartTime.value = null
            _elapsedTimeInSeconds.value = 0
            _currentElapsedSeconds.value = 0L

            updateTotalWorkedTime()
        }
    }

    private fun addNewInterval(
        recordId: String? = null,
        intervalId: String? = null,
        startTime: String? = null
    ) {
        val currentDate = TimeFormatUtils.getCurrentDate()
        val currentTime = TimeFormatUtils.getCurrentTime()

        val existingRecord = if (recordId != null) {
            dateList.find { it.vid == recordId }
        } else {
            dateList.find { it.infosDeBase.dateInString == currentDate }
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
            repository.updateUnSeulData(existingRecord.vid)
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
            dateList.add(newRecord)
            repository.updateUnSeulData(newRecord.vid)
        }
    }

    fun updateExistingInterval(
        recordId: String? = null,
        intervalId: String? = null,
        endTime: String? = null
    ) {
        if (recordId == null || intervalId == null || endTime == null) {
            return
        }

        val existingRecord = dateList.find { it.vid == recordId }

        if (existingRecord != null) {
            val existingInterval = existingRecord.intervalesDeTravaille.find { it.vid == intervalId }

            if (existingInterval != null) {
                existingInterval.temparrete = endTime
                existingInterval.enCoureDEnregestrement = false

                repository.updateUnSeulData(existingRecord.vid)
            } else {
                val recordingInterval = existingRecord.intervalesDeTravaille.find { it.enCoureDEnregestrement }
                if (recordingInterval != null) {
                    recordingInterval.temparrete = endTime
                    recordingInterval.enCoureDEnregestrement = false

                    repository.updateUnSeulData(existingRecord.vid)
                }
            }
        }
    }



    fun toggleRecording() {
        if (_isRecording.value) {
            stopTimeInterval()
        } else {
            startTimeInterval()
        }
    }

    fun updateElapsedTime() {
        if (_isRecording.value) {
            _elapsedTimeInSeconds.value += 1

            val now = System.currentTimeMillis()
            val delta = (now - _lastUpdateTime.value) / 1000
            _currentElapsedSeconds.value += delta
            _lastUpdateTime.value = now

            if (_elapsedTimeInSeconds.value % 5 == 0) {
                updateTotalWorkedTime()
            } else {
                updateDisplayTime()
            }
        }
    }

    fun getTodayRecord(): K_TempTravaille? {
        val currentDate = _currentDate.value
        return dateList.find { it.infosDeBase.dateInString == currentDate }
    }

    fun calculateTotalWorkedTime(record: K_TempTravaille?, isCurrentlyRecording: Boolean): Long {
        var total = 0L

        if (record == null) {
            return 0L
        }

        record.intervalesDeTravaille.forEach { interval ->
            if (!interval.enCoureDEnregestrement) {
                val durationMinutes = K_TempTravaille.calculateDurationMinutes(
                    interval.tempDepart,
                    interval.temparrete
                )

                if (durationMinutes > 0) {
                    total += durationMinutes * 60L
                }
            } else if (isCurrentlyRecording && interval.vid == _currentIntervalId.value) {
                val startMinutes = interval.tempDepart.split(":").let {
                    if (it.size >= 2) it[0].toInt() * 60 + it[1].toInt() else 0
                }

                val currentTime = TimeFormatUtils.getCurrentTime()
                val currentMinutes = currentTime.split(":").let {
                    if (it.size >= 2) it[0].toInt() * 60 + it[1].toInt() else 0
                }

                val durationMinutes = if (currentMinutes < startMinutes) {
                    (24 * 60 - startMinutes) + currentMinutes
                } else {
                    currentMinutes - startMinutes
                }

                total += durationMinutes * 60L
            }
        }

        return total
    }

    fun updateTotalWorkedTime() {
        val todayRecord = getTodayRecord()
        _totalWorkedSeconds.value = calculateTotalWorkedTime(todayRecord, _isRecording.value)
        updateDisplayTime()
    }

    fun updateDisplayTime() {
        val totalTimeSeconds = _totalWorkedSeconds.value +
                if (_isRecording.value) _elapsedTimeInSeconds.value.toLong() else 0
        _displayTime.value = TimeFormatUtils.formatSecondsToTime(totalTimeSeconds)
    }

    fun resetSessionTimer() {
        _currentElapsedSeconds.value = 0L
        _lastUpdateTime.value = System.currentTimeMillis()
    }

    fun onLifecycleResume() {
        _totalWorkedSeconds.value = calculateTotalWorkedTime(getTodayRecord(), _isRecording.value)
    }

    fun onRecordingStopped() {
        if (_currentElapsedSeconds.value > 0) {
            _currentElapsedSeconds.value = 0
            _totalWorkedSeconds.value = calculateTotalWorkedTime(getTodayRecord(), false)
        }
    }
}
