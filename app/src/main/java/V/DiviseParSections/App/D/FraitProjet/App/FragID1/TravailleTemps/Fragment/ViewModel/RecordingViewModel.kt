package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_4_PeriodeVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Test.C3_BonAchate
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.Extension.IRecordingHandler
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
import kotlinx.coroutines.launch

data class UiState(
    val bonAchatList: List<C3_BonAchate> = emptyList(),

    val activePeriodeVent: _1_4_PeriodeVent? = _1_4_PeriodeVent(vid = 7L),
    val isRecording: Boolean = false,
    val displayTime: String = "00:00:00",
    val currentDate: String = "",
    val isAbdelwahabLeGerant: Boolean = true,
    val editingInterval: K_TempTravaille.IntervalesDeTravaille? = null,
    val nombreClientsAvecCible: Int = 0,
    val totalWorkedTime: String = "00:00:00"
)

class RecordingViewModel(
    val groupeRepositorysProtoAvJuin3: GroupeRepositorysProtoAvJuin3,
    val b_ClientDataBaseRepository: B_ClientDataBaseRepository,
    private val recordingHandler: IRecordingHandler ,
    val repository: K_TempTravailleRepository = K_TempTravailleRepositoryImpl()
) : ViewModel() {
    private val repos = groupeRepositorysProtoAvJuin3.repositorys_Model
    val bProto_ClientsDataBase = b_ClientDataBaseRepository.modelDatas
    val reposBonAchatList = groupeRepositorysProtoAvJuin3.repositorys_Model.c3_BonAchate_Repository

    val TAG = "RecordingViewModel"
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isAbdelwahabLeGerant = MutableStateFlow(true)
    val isAbdelwahabLeGerant: StateFlow<Boolean> = _isAbdelwahabLeGerant.asStateFlow()

    val dateList get() = repository.modelDatas
    val isRecording = recordingHandler.isRecording
    val displayTime = recordingHandler.displayTime

    private val _currentDate = MutableStateFlow(TimeFormatUtils.getCurrentDate())
    private val _editingInterval = MutableStateFlow<K_TempTravaille.IntervalesDeTravaille?>(null)
    val editingInterval = _editingInterval.asStateFlow()

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
        snapshotFlow { uiState.value.bonAchatList }.collect { list ->
            val uiState = uiState.value
            val bonAchatList = uiState.bonAchatList
            log(bonAchatList)
            if (bonAchatList.any {
                    it.parentVID_1_4_PeriodeVent == uiState.activePeriodeVent?.vid &&
                            it.etateActuellementEst == C3_BonAchate.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                })
                Log.i(TAG, "LencePrint")

            recordingHandler.toggleRecording()
        }
    }

    private suspend fun collectBonAchatRepoModel() {
        snapshotFlow { reposBonAchatList.modelDatasSnapList.toList() }.collect { list ->
            updateUiState { it.copy(bonAchatList = list) }
        }
    }

    init {
        viewModelScope.launch {
            collectBonAchatRepoModel()
            collectActiveVendeurId()
        }

        viewModelScope.launch {
            suitUiBonAchet()
        }

        recordingHandler.updateTotalWorkedTime()
        recordingHandler.setupRecordingStateListener()
    }

    private suspend fun collectActiveVendeurId() {
        groupeRepositorysProtoAvJuin3.repositorys_Model.activeReactiveIdDe_1_5_Vendeur.collect { activeVendeurId ->
            val activePeriodeVent = get_PeriodVentActive()
            updateUiState { currentState ->
                currentState.copy(
                    activePeriodeVent = activePeriodeVent,
                )
            }
        }
    }

    fun get_PeriodVentActive(
    ): _1_4_PeriodeVent? {
        val repositorysModel1 = groupeRepositorysProtoAvJuin3.repositorys_Model

        val ceComptVendeurInsertBonsAchatAuPeriodID_ComptPeriodActive =
            repositorysModel1.repository_1_5_Vendeur.modelDatasSnapList
                .find { it.vid == repositorysModel1.activeReactiveIdDe_1_5_Vendeur.value }
                ?.ceComptVendeurInsertBonsAchatAuPeriodID

        return repositorysModel1.repository_1_4_PeriodeVent.modelDatasSnapList.find {
            it.vid == ceComptVendeurInsertBonsAchatAuPeriodID_ComptPeriodActive
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

    fun stopRecording() = recordingHandler.stopRecording()
    fun updateElapsedTime() = recordingHandler.updateElapsedTime()
    fun getTodayRecord(): K_TempTravaille? =
        dateList.find { it.infosDeBase.dateInString == _currentDate.value }

    fun resetSessionTimer() = recordingHandler.resetSessionTimer()
    fun onLifecycleResume() = recordingHandler.onLifecycleResume(getTodayRecord())
    fun onRecordingStopped() = recordingHandler.onRecordingStopped(getTodayRecord())
}
