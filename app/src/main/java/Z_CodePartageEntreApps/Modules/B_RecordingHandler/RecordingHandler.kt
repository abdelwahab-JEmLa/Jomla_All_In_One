package Z_CodePartageEntreApps.Modules.B_RecordingHandler

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravaille
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravailleRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Interface for handling recording operations and time tracking
 */
interface IRecordingHandler {
    val isRecording: StateFlow<Boolean>
    val displayTime: StateFlow<String>

    fun toggleRecording(forceStop: Boolean = false)
    fun startRecordIfNot()

    fun startRecordingWithInterval(recordId: String, intervalId: String, startTime: String)
    fun setupRecordingStateListener()
    fun updateElapsedTime()
    fun calculateTotalWorkedTime(record: K_TempTravaille?, isCurrentlyRecording: Boolean): Long
    fun updateTotalWorkedTime()
    fun updateDisplayTime()
    fun resetSessionTimer()
    fun onLifecycleResume(todayRecord: K_TempTravaille?)
    fun onRecordingStopped(todayRecord: K_TempTravaille?)
    fun stopRecording()
}

class RecordingHandler(
    private val repository: K_TempTravailleRepository,
    private val coroutineScope: CoroutineScope
) : IRecordingHandler {

    private val _isRecording = MutableStateFlow(false)
    override val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _currentRecordId = MutableStateFlow<String?>(null)
    private val _currentIntervalId = MutableStateFlow<String?>(null)
    private val _currentStartTime = MutableStateFlow<String?>(null)
    private val _elapsedTimeInSeconds = MutableStateFlow(0)
    private val _totalWorkedSeconds = MutableStateFlow(0L)

    private val _displayTime = MutableStateFlow("00:00:00")
    override val displayTime: StateFlow<String> = _displayTime.asStateFlow()

    private val _currentElapsedSeconds = MutableStateFlow(0L)
    private val _lastUpdateTime = MutableStateFlow(System.currentTimeMillis())

    override fun toggleRecording(forceStop: Boolean) {
        if (_isRecording.value && !forceStop) {
            stopRecording()
        } else {
            startTimeInterval()
            updateRecordingState(true)
        }
    }

    override fun startRecordIfNot() {
        if (!_isRecording.value) {
            startTimeInterval()
            updateRecordingState(true)
        }
    }

    override fun stopRecording() {
        stopTimeInterval()
        updateRecordingState(false)
    }

    override fun startRecordingWithInterval(
        recordId: String,
        intervalId: String,
        startTime: String
    ) {
        _currentRecordId.value = recordId
        _currentIntervalId.value = intervalId
        _currentStartTime.value = startTime
        _currentElapsedSeconds.value = 0L
        _lastUpdateTime.value = System.currentTimeMillis()
        _elapsedTimeInSeconds.value = 0
        _isRecording.value = true
        updateRecordingState(true)
    }

    override fun setupRecordingStateListener() {
        K_TempTravailleRepository.caReference.child("_isRecording")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isRecordingValue = snapshot.getValue(Boolean::class.java) ?: false
                    if (isRecordingValue != _isRecording.value) {
                        _isRecording.value = isRecordingValue
                        if (isRecordingValue) {
                            val currentDateStr = TimeFormatUtils.getCurrentDate().replace("/", "_")
                            repository.modelDatas.find { it.vid == currentDateStr }
                                ?.intervalesDeTravaille?.find { it.enCoureDEnregestrement }
                                ?.let { interval ->
                                    _currentRecordId.value = currentDateStr
                                    _currentIntervalId.value = interval.vid
                                    _currentStartTime.value = interval.tempDepart
                                    _currentElapsedSeconds.value = 0L
                                    _lastUpdateTime.value = System.currentTimeMillis()
                                } ?: updateRecordingState(false)
                        } else {
                            _currentRecordId.value = null
                            _currentIntervalId.value = null
                            _currentStartTime.value = null
                            _elapsedTimeInSeconds.value = 0
                            _currentElapsedSeconds.value = 0L
                            updateTotalWorkedTime()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun updateRecordingState(isRecording: Boolean) {
        K_TempTravailleRepository.caReference.child("_isRecording").setValue(isRecording)
    }

    private fun startTimeInterval() {
        val currentDate = TimeFormatUtils.getCurrentDate().replace("/", "_")
        val currentTime = TimeFormatUtils.getCurrentTime()
        val timeId = currentTime.replace(":", "_")

        _currentRecordId.value = currentDate
        _currentIntervalId.value = timeId
        _currentStartTime.value = currentTime
        _currentElapsedSeconds.value = 0L
        _lastUpdateTime.value = System.currentTimeMillis()

        coroutineScope.launch {
            repository.addNewInterval(currentDate, timeId, currentTime)
            _isRecording.value = true
            _elapsedTimeInSeconds.value = 0
        }
    }

    private fun stopTimeInterval() {
        val currentDate = TimeFormatUtils.getCurrentDate().replace("/", "_")
        val currentTime = TimeFormatUtils.getCurrentTime()

        coroutineScope.launch {
            repository.updateExistingInterval(
                currentDate,
                _currentIntervalId.value ?: currentTime.replace(":", "_"),
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

    override fun updateElapsedTime() {
        if (_isRecording.value) {
            _elapsedTimeInSeconds.value += 1
            val now = System.currentTimeMillis()
            _currentElapsedSeconds.value += (now - _lastUpdateTime.value) / 1000
            _lastUpdateTime.value = now
            if (_elapsedTimeInSeconds.value % 5 == 0) {
                updateTotalWorkedTime()
            } else {
                updateDisplayTime()
            }
        }
    }

    override fun calculateTotalWorkedTime(
        record: K_TempTravaille?,
        isCurrentlyRecording: Boolean
    ): Long {
        if (record == null) return 0L
        var total = 0L
        record.intervalesDeTravaille.forEach { interval ->
            if (!interval.enCoureDEnregestrement) {
                val duration = K_TempTravaille.calculateDurationMinutes(
                    interval.tempDepart,
                    interval.temparrete
                )
                if (duration > 0) {
                    total += duration * 60L
                }
            } else if (isCurrentlyRecording && interval.vid == _currentIntervalId.value) {
                val startMinutes = interval.tempDepart.split(":").let {
                    if (it.size >= 2) it[0].toInt() * 60 + it[1].toInt() else 0
                }
                val currentMinutes = TimeFormatUtils.getCurrentTime().split(":").let {
                    if (it.size >= 2) it[0].toInt() * 60 + it[1].toInt() else 0
                }
                val duration = if (currentMinutes < startMinutes) {
                    (24 * 60 - startMinutes) + currentMinutes
                } else {
                    currentMinutes - startMinutes
                }
                total += duration * 60L
            }
        }
        return total
    }

    override fun updateTotalWorkedTime() {
        val todayRecord = repository.modelDatas.find {
            it.infosDeBase.dateInString == TimeFormatUtils.getCurrentDate()
        }
        _totalWorkedSeconds.value = calculateTotalWorkedTime(todayRecord, _isRecording.value)
        updateDisplayTime()
    }

    override fun updateDisplayTime() {
        val totalTimeSeconds = _totalWorkedSeconds.value + if (_isRecording.value) {
            _elapsedTimeInSeconds.value.toLong()
        } else {
            0
        }
        _displayTime.value = TimeFormatUtils.formatSecondsToTime(totalTimeSeconds)
    }

    override fun resetSessionTimer() {
        _currentElapsedSeconds.value = 0L
        _lastUpdateTime.value = System.currentTimeMillis()
    }

    override fun onLifecycleResume(todayRecord: K_TempTravaille?) {
        _totalWorkedSeconds.value = calculateTotalWorkedTime(todayRecord, _isRecording.value)
    }

    override fun onRecordingStopped(todayRecord: K_TempTravaille?) {
        if (_currentElapsedSeconds.value > 0) {
            _currentElapsedSeconds.value = 0
            _totalWorkedSeconds.value = calculateTotalWorkedTime(todayRecord, false)
        }
    }
}
