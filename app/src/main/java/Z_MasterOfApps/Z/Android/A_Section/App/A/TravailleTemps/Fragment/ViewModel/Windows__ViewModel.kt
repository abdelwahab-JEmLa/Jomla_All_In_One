package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel

import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.Model.K_TempTravaille
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.Model.Repository.K_TempTravailleRepository
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.Model.Repository.K_TempTravailleRepositoryImpl
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Extension.RecordingHandler
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Extension.TimeFormatUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Windows__ViewModel(
    val repository: K_TempTravailleRepository = K_TempTravailleRepositoryImpl()
) : ViewModel() {
    // Recording handler handles all recording-related functionality
    private val recordingHandler = RecordingHandler(repository, viewModelScope)

    val dateList get() = repository.modelDatas
    // Added state for Abdelwahab Le Gérant privileges
    private val _isAbdelwahabLeGerant = MutableStateFlow(false)
    val isAbdelwahabLeGerant: StateFlow<Boolean> = _isAbdelwahabLeGerant.asStateFlow()
    // Expose recording states from handler
    val isRecording = recordingHandler.isRecording
    val displayTime = recordingHandler.displayTime
    private val _currentDate = MutableStateFlow(TimeFormatUtils.getCurrentDate())

    init {
        recordingHandler.updateTotalWorkedTime()
        recordingHandler.setupRecordingStateListener()
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
        recordingHandler.updateTotalWorkedTime() // Update the time calculations after deletion
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
                recordingHandler.updateTotalWorkedTime()
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

                    recordingHandler.updateTotalWorkedTime()
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

    // Delegate recording functions to RecordingHandler
    fun toggleRecording() {
        recordingHandler.toggleRecording()
    }

    fun updateElapsedTime() {
        recordingHandler.updateElapsedTime()
    }

    fun getTodayRecord(): K_TempTravaille? {
        val currentDate = _currentDate.value
        return dateList.find { it.infosDeBase.dateInString == currentDate }
    }


    fun resetSessionTimer() {
        recordingHandler.resetSessionTimer()
    }

    fun onLifecycleResume() {
        recordingHandler.onLifecycleResume(getTodayRecord())
    }

    fun onRecordingStopped() {
        recordingHandler.onRecordingStopped(getTodayRecord())
    }
}
