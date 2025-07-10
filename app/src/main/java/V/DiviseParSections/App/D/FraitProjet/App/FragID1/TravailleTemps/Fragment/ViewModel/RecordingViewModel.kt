package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.MVentPeriode
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravaille
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravailleRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravailleRepositoryImpl
import Z_CodePartageEntreApps.Modules.B_RecordingHandler.IRecordingHandler
import Z_CodePartageEntreApps.Modules.B_RecordingHandler.TimeFormatUtils
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
    val B_ClientInfosProtoJuin3List: List<M2Client> = emptyList(),
    val mainLoadingProgress: Float = 0f,

    val bonAchatList: List<M8BonVent> = emptyList(),

    val activePeriodeVent: MVentPeriode? = MVentPeriode(vid = 7L),
    val isRecording: Boolean = false,
    val displayTime: String = "00:00:00",
    val currentDate: String = "",
    val isAbdelwahabLeGerant: Boolean = true,
    val editingInterval: K_TempTravaille.IntervalesDeTravaille? = null,
    val nombreClientsAvecCible: Int = 0,
    val totalWorkedTime: String = "00:00:00"
)

class RecordingViewModel(
    val getter: RepositorysMainGetter,
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    val groupeRepositorysProtoAvJuin3: GroupeRepositorysProtoAvJuin3,
    val recordingHandler: IRecordingHandler,
    val repository: K_TempTravailleRepository = K_TempTravailleRepositoryImpl()
) : ViewModel() {
    private val repos = groupeRepositorysProtoAvJuin3.repositorys_Model
    val reposBonAchatList =getter.repo8BonVent

    val TAG = "RecordingViewModel"
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val bProto_ClientsDataBase = uiState.value.B_ClientInfosProtoJuin3List

    private val _isAbdelwahabLeGerant = MutableStateFlow(true)
    val isAbdelwahabLeGerant: StateFlow<Boolean> = _isAbdelwahabLeGerant.asStateFlow()

    val dateList get() = repository.modelDatas
    val isRecording = recordingHandler.isRecording
    val displayTime = recordingHandler.displayTime

    private val _currentDate = MutableStateFlow(TimeFormatUtils.getCurrentDate())
    private val _editingInterval = MutableStateFlow<K_TempTravaille.IntervalesDeTravaille?>(null)
    val editingInterval = _editingInterval.asStateFlow()

    init {
        viewModelScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    _uiState.value = _uiState.value.copy(
                        B_ClientInfosProtoJuin3List = model.b_ClientInfosProtoJuin3Repository?.modelListFlow
                            ?: emptyList(),
                        mainLoadingProgress = model.progress
                    )
                }
            }
        }

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

    private fun log(list: List<M8BonVent>) {
        val map = list.map { bon ->
            val clientAcheteurID = bon.parent_M2Client_OldLongID
            val cli = bProto_ClientsDataBase.find { it.id == clientAcheteurID }
            bon.vid to cli?.nom
        }
        Log.d(TAG, "$map")
    }

    suspend fun suitUiBonAchet(): Unit {
        snapshotFlow { uiState.value.bonAchatList }.collect { list ->
            val uiState = uiState.value
            log(list)
            if (list.any {
                  //  it.parent_M14VentPeriod_Old_LongID == uiState.activePeriodeVent?.vid &&
                            it.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                })
                Log.i(TAG, "LencePrint")

            //  recordingHandler.toggleRecording()
        }
    }

    private suspend fun collectBonAchatRepoModel() {
        Log.i(TAG, "collectBonAchatRepoModel")

        snapshotFlow { reposBonAchatList.datasValue.toList() }.collect { list ->
            log(list)

            updateUiState {
                it.copy(bonAchatList = list)
            }
        }
    }


    private suspend fun collectActiveVendeurId() {
        snapshotFlow { getter.repo9AppCompt.currentAppCompt }.collect { currentAppCompt ->
            val activePeriodeVent = get_PeriodVentActive()
            updateUiState { currentState ->
                currentState.copy(activePeriodeVent = activePeriodeVent)
            }
        }
    }

    fun get_PeriodVentActive(
    ): MVentPeriode? {
        val repositorysModel1 = groupeRepositorysProtoAvJuin3.repositorys_Model

        val ceComptVendeurInsertBonsAchatAuPeriodID_ComptPeriodActive =
            getter.repo9AppCompt.currentAppCompt?.ceComptVendeurInsertBonsAchatAuPeriodID

        return repositorysModel1.repositoryMVentPeriode.modelDatasSnapList.find {
            it.vid == ceComptVendeurInsertBonsAchatAuPeriodID_ComptPeriodActive
        }
    }


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
        recordId: String,
        startTime: String? = null,
        endTime: String? = null,
        typeTemp: K_TempTravaille.IntervalesDeTravaille.TypeTemp = K_TempTravaille.IntervalesDeTravaille.TypeTemp.ENTRE_PAR_MAIN
    ) {
        dateList.find { it.vid == recordId }?.let { record ->
            record.intervalesDeTravaille.find { it.enCoureDEnregestrement }?.let { interval ->
                repository.updateExistingInterval(
                    record.vid, interval.vid, startTime, endTime, typeTemp
                )
                recordingHandler.updateTotalWorkedTime()
            } ?: run {
                if (startTime != null && endTime != null) {
                    val timeId = TimeFormatUtils.getCurrentTime().replace(":", "_")
                    repository.addNewInterval(record.vid, timeId, startTime)
                    repository.updateExistingInterval(
                        record.vid, timeId, endTime = endTime, typeTemp = typeTemp
                    )
                    recordingHandler.updateTotalWorkedTime()
                }
            }
        }
    }

    fun updateElapsedTime() = recordingHandler.updateElapsedTime()
    fun getTodayRecord(): K_TempTravaille? =
        dateList.find { it.infosDeBase.dateInString == _currentDate.value }

    fun resetSessionTimer() = recordingHandler.resetSessionTimer()
    fun onLifecycleResume() = recordingHandler.onLifecycleResume(getTodayRecord())
    fun onRecordingStopped() = recordingHandler.onRecordingStopped(getTodayRecord())
}
