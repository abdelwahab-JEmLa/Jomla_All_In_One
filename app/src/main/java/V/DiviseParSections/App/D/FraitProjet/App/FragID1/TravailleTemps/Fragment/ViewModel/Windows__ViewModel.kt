package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_4_PeriodeVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Test.C3_BonAchate
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.Extension.RecordingHandler
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.Extension.TimeFormatUtils
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepository
import Z_CodePartageEntreApps.Model.K_TempTravaille
import Z_CodePartageEntreApps.Model.K_TempTravailleRepository.Repository.K_TempTravailleRepository
import Z_CodePartageEntreApps.Model.K_TempTravailleRepository.Repository.K_TempTravailleRepositoryImpl
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class UiState(
    val bonAchatList: List<C3_BonAchate> = emptyList(),

    val activeBonAchat: C3_BonAchate? = null,
    val activePeriodeVent: _1_4_PeriodeVent? = _1_4_PeriodeVent(vid = 7L),
    val isRecording: Boolean = false,
    val displayTime: String = "00:00:00",
    val currentDate: String = "",
    val isAbdelwahabLeGerant: Boolean = true,
    val editingInterval: K_TempTravaille.IntervalesDeTravaille? = null,
    val nombreClientsAvecCible: Int = 0,
    val totalWorkedTime: String = "00:00:00"
)

class Windows__ViewModel(
    val groupeRepositorysProtoAvJuin3: GroupeRepositorysProtoAvJuin3,
    val b_ClientDataBaseRepository: B_ClientDataBaseRepository,
    val repository: K_TempTravailleRepository = K_TempTravailleRepositoryImpl()
) : ViewModel() {
      val TAG ="Windows__ViewModel"
    val recordingHandler = RecordingHandler(repository, viewModelScope)
    val dateList get() = repository.modelDatas
    val isRecording = recordingHandler.isRecording
    val displayTime = recordingHandler.displayTime
    val bProto_ClientsDataBase = b_ClientDataBaseRepository.modelDatas

    private val _isAbdelwahabLeGerant = MutableStateFlow(true)
    val isAbdelwahabLeGerant: StateFlow<Boolean> = _isAbdelwahabLeGerant.asStateFlow()
    private val _currentDate = MutableStateFlow(TimeFormatUtils.getCurrentDate())
    private val _editingInterval = MutableStateFlow<K_TempTravaille.IntervalesDeTravaille?>(null)
    val editingInterval = _editingInterval.asStateFlow()
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val repos = groupeRepositorysProtoAvJuin3.repositorys_Model

    val reposBonAchatList = groupeRepositorysProtoAvJuin3.repositorys_Model.c3_BonAchate_Repository

    private fun log(list: List<C3_BonAchate>) {
        val map = list
            .map { bon ->
                val clientAcheteurID = bon.clientAcheteurID
                val cli = bProto_ClientsDataBase.find { it.id == clientAcheteurID }
                bon.vid to cli?.nom to (bon.etateActuellementEst.name to bon.parentVID_1_4_PeriodeVent)
            }
        Log.d(TAG, "$map")
    }

    suspend fun suitUiBonAchet(): Unit {
        snapshotFlow { uiState.value.bonAchatList}.collect { list ->
            val uiState = uiState.value
            val bonAchatList = uiState.bonAchatList
            log(bonAchatList)
            if (bonAchatList.any {
                    it.parentVID_1_4_PeriodeVent == uiState.activePeriodeVent?.vid &&
                            it.etateActuellementEst == C3_BonAchate.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                })
                Log.i(TAG, "LencePrint")
        }
    }

    init {
        viewModelScope.launch {
            snapshotFlow { reposBonAchatList.modelDatasSnapList.toList() }.collect { list ->
                updateUiState { it.copy(bonAchatList = list) }
            }
        }

        viewModelScope.launch {
            suitUiBonAchet()
        }

        recordingHandler.updateTotalWorkedTime()
        recordingHandler.setupRecordingStateListener()

        viewModelScope.launch {
            repos.activeVId_C3_BonAchate_Repository.collect { id ->
                val bon =
                    if (id > 0) repos.c3_BonAchate_Repository.modelDatasSnapList.firstOrNull { it.vid == id } else null
                updateUiState { it.copy(activeBonAchat = bon) }
            }
        }

        viewModelScope.launch {
            snapshotFlow { repos.c3_BonAchate_Repository.modelDatasSnapList.toList() }.collect { list ->
                handleBonAchatSelection(list)
            }
        }

        viewModelScope.launch {
            combine(isRecording, displayTime, editingInterval) { r, t, i -> Triple(r, t, i) }
                .collect { (r, t, i) ->
                    updateUiState {
                        it.copy(
                            isRecording = r,
                            displayTime = t,
                            editingInterval = i,
                            nombreClientsAvecCible = nombreClientAvecCibleCommeLastBonAchat(),
                            totalWorkedTime = t
                        )
                    }
                }
        }
    }

    private fun handleBonAchatSelection(list: List<C3_BonAchate>) {
        _uiState.value.activePeriodeVent?.let { pid ->
            val filtered = list.filter {
                it.parentVID_1_4_PeriodeVent == pid.vid &&
                        it.etateActuellementEst == C3_BonAchate.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            }

            when {
                filtered.isEmpty() -> {
                    if (repos.activeVId_C3_BonAchate_Repository.value != -1L) {
                        repos.activeVId_C3_BonAchate_Repository.value = -1L
                    } else {

                    }
                }

                else -> {
                    filtered.minByOrNull { it.timestamps }?.let { bon ->
                        if (repos.activeVId_C3_BonAchate_Repository.value != bon.vid && !isRecording.value) {
                            repos.activeVId_C3_BonAchate_Repository.value = bon.vid
                            handleRecordingLogic()
                        }
                    }
                }
            }
        }
    }

    fun get_PeriodVentActive(
        groupeRepositorysProtoAvJuin3: GroupeRepositorysProtoAvJuin3
    ): _1_4_PeriodeVent? {
        val repositorysModel1 = groupeRepositorysProtoAvJuin3.repositorys_Model

        val ceComptVendeurInsertBonsAchatAuPeriodID_ComptPeriodActive =
            repositorysModel1.repository_1_5_Vendeur.modelDatasSnapList
                .find { it.vid == groupeRepositorysProtoAvJuin3.repositorys_Model.activeIdDe_1_5_Vendeur }
                ?.ceComptVendeurInsertBonsAchatAuPeriodID

        return repositorysModel1.repository_1_4_PeriodeVent.modelDatasSnapList.find { it.vid == ceComptVendeurInsertBonsAchatAuPeriodID_ComptPeriodActive }
    }

    private fun handleRecordingLogic() {
        getTodayRecord()?.let { rec ->
            rec.intervalesDeTravaille.find { it.enCoureDEnregestrement }
                ?.let { interval ->
                    recordingHandler.startRecordingWithInterval(
                        rec.vid,
                        interval.vid,
                        interval.tempDepart
                    )
                } ?: toggleRecording()
        }
    }

    fun getLastTransaction(client: B_ClientDataBase): C3_BonAchate? =
        repos.c3_BonAchate_Repository.modelDatasSnapList
            .filter { it.clientAcheteurID == client.id }
            .maxByOrNull { it.timestamps }

    fun nombreClientAvecCibleCommeLastBonAchat(): Int =
        bProto_ClientsDataBase.count { getLastTransaction(it)?.etateActuellementEst == C3_BonAchate.EtateActuellementEst.Cible }

    private fun updateUiState(update: (UiState) -> UiState) {
        _uiState.value = update(_uiState.value)
    }

    fun toggleAbdelwahabLeGerant() {
        _isAbdelwahabLeGerant.value = !_isAbdelwahabLeGerant.value
    }

    fun editIntervaleTemp(interval: K_TempTravaille.IntervalesDeTravaille) {
        _editingInterval.value = interval
    }

    fun clearEditingInterval() {
        _editingInterval.value = null
    }

    fun ajoutJour(date: String) {
        repository.ajoutJour(date)
    }

    fun deleteIntervaleTemp(intervalId: String) {
        repository.deleteIntevaleDeTemp(intervalId)
        recordingHandler.updateTotalWorkedTime()
    }

    fun updatePareMain(
        recordId: String, startTime: String? = null, endTime: String? = null,
        typeTemp: K_TempTravaille.IntervalesDeTravaille.TypeTemp = K_TempTravaille.IntervalesDeTravaille.TypeTemp.ENTRE_PAR_MAIN
    ) {
        dateList.find { it.vid == recordId }?.let { record ->
            record.intervalesDeTravaille.find { it.enCoureDEnregestrement }?.let { interval ->
                repository.updateExistingInterval(
                    record.vid,
                    interval.vid,
                    startTime,
                    endTime,
                    typeTemp
                )
                recordingHandler.updateTotalWorkedTime()
            } ?: run {
                if (startTime != null && endTime != null) {
                    val timeId = TimeFormatUtils.getCurrentTime().replace(":", "_")
                    repository.addNewInterval(record.vid, timeId, startTime)
                    repository.updateExistingInterval(
                        record.vid,
                        timeId,
                        endTime = endTime,
                        typeTemp = typeTemp
                    )
                    recordingHandler.updateTotalWorkedTime()
                }
            }
        }
    }

    fun toggleRecording(forceStop: Boolean = false) = recordingHandler.toggleRecording(forceStop)
    fun stopRecording() = recordingHandler.stopRecording()
    fun updateElapsedTime() = recordingHandler.updateElapsedTime()
    fun getTodayRecord(): K_TempTravaille? =
        dateList.find { it.infosDeBase.dateInString == _currentDate.value }

    fun resetSessionTimer() = recordingHandler.resetSessionTimer()
    fun onLifecycleResume() = recordingHandler.onLifecycleResume(getTodayRecord())
    fun onRecordingStopped() = recordingHandler.onRecordingStopped(getTodayRecord())
}
