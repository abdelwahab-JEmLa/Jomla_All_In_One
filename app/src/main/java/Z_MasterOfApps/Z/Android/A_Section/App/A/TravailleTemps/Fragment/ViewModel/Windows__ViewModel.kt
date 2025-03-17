package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel

import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.Model.K_TempTravaille
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.Model.K_TempTravailleRepository
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.Model.K_TempTravailleRepositoryImpl
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Modules.TimeFormatUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Windows__ViewModel(
    val repository: K_TempTravailleRepository = K_TempTravailleRepositoryImpl()
) : ViewModel() {

    val dateList get() = repository.modelDatas

    // Added state for Abdelwahab Le Gérant privileges
    private val _isAbdelwahabLeGerant = MutableStateFlow(false)
    val isAbdelwahabLeGerant: StateFlow<Boolean> = _isAbdelwahabLeGerant.asStateFlow()

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
        setupRecordingStateListener()
    }

    private fun setupRecordingStateListener() {
        val recordingStateRef = K_TempTravailleRepository.caReference.child("_isRecording")
        recordingStateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isRecordingValue = snapshot.getValue(Boolean::class.java) ?: false
                if (isRecordingValue != _isRecording.value) {
                    _isRecording.value = isRecordingValue
                    if (isRecordingValue) {
                        // We're now recording but weren't before, so setup the recording state
                        val currentDate = TimeFormatUtils.getCurrentDate()
                        val currentDateStr = currentDate.replace("/", "_")
                        // Check if there's an active interval
                        val existingRecord = dateList.find { it.vid == currentDateStr }
                        val activeInterval = existingRecord?.intervalesDeTravaille?.find { it.enCoureDEnregestrement }
                        if (activeInterval != null) {
                            _currentRecordId.value = currentDateStr
                            _currentIntervalId.value = activeInterval.vid
                            _currentStartTime.value = activeInterval.tempDepart
                            _currentElapsedSeconds.value = 0L
                            _lastUpdateTime.value = System.currentTimeMillis()
                        } else {
                            // No active interval, strange state, reset recording
                            updateRecordingState(false)
                        }
                    } else {
                        // We're no longer recording
                        _currentRecordId.value = null
                        _currentIntervalId.value = null
                        _currentStartTime.value = null
                        _elapsedTimeInSeconds.value = 0
                        _currentElapsedSeconds.value = 0L
                        updateTotalWorkedTime()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error listening to recording state: ${error.message}")
            }
        })
    }

    // Add this method to update the recording state in Firebase
    private fun updateRecordingState(isRecording: Boolean) {
        val recordingStateRef = K_TempTravailleRepository.caReference.child("_isRecording")
        recordingStateRef.setValue(isRecording)
            .addOnSuccessListener {
                println("Recording state updated successfully")
            }
            .addOnFailureListener { e ->
                println("Failed to update recording state: ${e.message}")
            }
    }

    // Modify the toggleRecording method to use the new updateRecordingState method
    fun toggleRecording() {
        if (_isRecording.value) {
            stopTimeInterval()
            updateRecordingState(false)
        } else {
            startTimeInterval()
            updateRecordingState(true)
        }
    }

    // Function to toggle admin privileges
    fun toggleAbdelwahabLeGerant() {
        _isAbdelwahabLeGerant.value = !_isAbdelwahabLeGerant.value
    }

    // Add these state variables to Windows__ViewModel class
    private val _editingInterval = MutableStateFlow<K_TempTravaille.IntervalesDeTravaille?>(null)
    val editingInterval = _editingInterval.asStateFlow()

    // Add this function to Windows__ViewModel class
    fun editIntervaleTemp(interval: K_TempTravaille.IntervalesDeTravaille) {
        _editingInterval.value = interval
    }

    // Add this function to Windows__ViewModel class
    fun clearEditingInterval() {
        _editingInterval.value = null
    }

    fun ajoutJour(date: String) {
        repository.ajoutJour(date)
    }

    fun deleteIntervaleTemp(intervalId: String) {
        // Utiliser la méthode de repository directement
        repository.deleteIntevaleDeTemp(intervalId)
        updateTotalWorkedTime() // Update the time calculations after deletion
    }

    fun updatePareMain(
        recordId: String,
        startTime: String? = null,
        endTime: String? = null,
        typeTemp: K_TempTravaille.IntervalesDeTravaille.TypeTemp = K_TempTravaille.IntervalesDeTravaille.TypeTemp.ENTRE_PAR_MAIN
    ) {
        // Find the existing record
        val existingRecord = dateList.find { it.vid == recordId }

        if (existingRecord != null) {
            // If we have an active interval, update it
            val activeInterval = existingRecord.intervalesDeTravaille.find { it.enCoureDEnregestrement }

            if (activeInterval != null) {
                // Update the existing interval using repository
                repository.updateExistingInterval(
                    recordId = existingRecord.vid,
                    intervalId = activeInterval.vid,
                    startTime = startTime,
                    endTime = endTime,
                    typeTemp = typeTemp
                )

                // Update the total worked time to reflect the changes
                updateTotalWorkedTime()
            } else {
                // If no active interval, create a new one if both start and end time are provided
                if (startTime != null && endTime != null) {
                    val currentTime = TimeFormatUtils.getCurrentTime()
                    val currentTimeFormatted = currentTime.replace(":", "_")

                    // Add new interval with repository
                    repository.addNewInterval(
                        recordId = existingRecord.vid,
                        intervalId = currentTimeFormatted,
                        startTime = startTime
                    )

                    // Update the interval we just created
                    repository.updateExistingInterval(
                        recordId = existingRecord.vid,
                        intervalId = currentTimeFormatted,
                        endTime = endTime,
                        typeTemp = typeTemp
                    )

                    updateTotalWorkedTime()
                }
            }
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
            // Utiliser la méthode de repository pour ajouter un nouvel intervalle
            repository.addNewInterval(
                recordId = currentDateStr,
                intervalId = currentTimeStr,
                startTime = currentTime
            )
            _isRecording.value = true
            _elapsedTimeInSeconds.value = 0
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
    private fun stopTimeInterval() {
        val currentDate = TimeFormatUtils.getCurrentDate()
        val currentDateStr = currentDate.replace("/", "_")
        val currentTime = TimeFormatUtils.getCurrentTime()
        val currentTimeStr = currentTime.replace(":", "_")

        viewModelScope.launch {
            // Utiliser la méthode de repository pour mettre à jour l'intervalle existant
            val intervalId = _currentIntervalId.value ?: currentTimeStr
            repository.updateExistingInterval(
                recordId = currentDateStr,
                intervalId = intervalId,
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
