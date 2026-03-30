// RecordingViewModel.kt - FIXED
package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.M2Client
import EntreApps.Shared.Models.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.MVentPeriode
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravaille
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravailleRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravailleRepositoryImpl
import EntreApps.Shared.Models.Utilisateur
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
    val aCentralFacade: ACentralFacade,
    val getter: RepositorysMainGetter,
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    val groupeRepositorysProtoAvJuin3: GroupeRepositorysProtoAvJuin3,
    val recordingHandler: IRecordingHandler,
    val repository: K_TempTravailleRepository = K_TempTravailleRepositoryImpl()
) : ViewModel() {
    private val repos = groupeRepositorysProtoAvJuin3.repositorys_Model
    val reposBonAchatList = getter.repo8BonVent

    val TAG = "RecordingViewModel"
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val bProto_ClientsDataBase = uiState.value.B_ClientInfosProtoJuin3List

    private val _isAbdelwahabLeGerant = MutableStateFlow(true)
    val isAbdelwahabLeGerant: StateFlow<Boolean> = _isAbdelwahabLeGerant.asStateFlow()

    // Get active filter from central facade
    val active_filter_du_vendeur
        get() = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
            .active_Central_Values.active_filter_du_utilisateur

    /**
     * Toggle active centrale vendeur filter
     * Cycles through: Admin (shows all) -> Abdelmoumen -> Walid -> Admin
     */
    fun toggleActiveCentraleVendeur() {
        val currentFilter = active_filter_du_vendeur
        val nextFilter = Utilisateur.toggleFrom(currentFilter)

        // Update the central facade with new filter
        val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
        focusedValuesGetter.update_activeCentralValues(
            focusedValuesGetter.active_Central_Values.copy(
                active_filter_du_utilisateur = nextFilter  // FIXED: Changed from active_filter_du_vendeur
            )
        )

        Log.i(TAG, "Toggled vendeur filter: $currentFilter -> $nextFilter")
    }

    /**
     * Get display name for current active vendeur filter
     */
    fun getActiveVendeurDisplayName(): String {
        return active_filter_du_vendeur?.getDisplayName() ?: "Tous"
    }

    /**
     * Filter dateList by active vendor
     * When Admin is active (or null), shows all intervals
     * When specific user is active, filters to show only their intervals
     */
    val dateList get() = repository.modelDatas.map { tempTravaille ->
        K_TempTravaille(tempTravaille.vid).apply {
            this.infosDeBase = tempTravaille.infosDeBase
            this.intervalesDeTravaille.clear()

            // If Admin or null, show all intervals
            // Otherwise, filter by the active vendor
            this.intervalesDeTravaille.addAll(
                when (active_filter_du_vendeur) {
                    Utilisateur.Admin, null -> tempTravaille.intervalesDeTravaille
                    else -> tempTravaille.intervalesDeTravaille.filter { interval ->
                        interval.utilisateur == active_filter_du_vendeur
                    }
                }
            )
        }
    }

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

    fun updatePareMainForWalid(
        recordId: String,
        startTime: String?,
        endTime: String?,
        typeTemp: K_TempTravaille.IntervalesDeTravaille.TypeTemp
    ) {
        val currentTime = TimeFormatUtils.getCurrentTime()
        val intervalId = "${currentTime.replace(":", "_")}_walid"

        if (startTime != null && endTime != null) {
            val formattedStartTime = startTime.replace(".", ":")
            val formattedEndTime = endTime.replace(".", ":")

            repository.addNewIntervalForWalid(
                recordId = recordId,
                intervalId = intervalId,
                startTime = formattedStartTime
            )

            repository.updateExistingIntervalForWalid(
                recordId = recordId,
                intervalId = intervalId,
                startTime = formattedStartTime,
                endTime = formattedEndTime,
                typeTemp = typeTemp
            )
        } else if (startTime != null) {
            repository.addNewIntervalForWalid(
                recordId = recordId,
                intervalId = intervalId,
                startTime = startTime.replace(".", ":")
            )

            repository.updateExistingIntervalForWalid(
                recordId = recordId,
                intervalId = intervalId,
                startTime = null,
                endTime = null,
                typeTemp = typeTemp
            )
        } else if (endTime != null) {
            val record = repository.modelDatas.find { it.vid == recordId }
            val activeInterval = record?.intervalesDeTravaille?.findLast {
                it.enCoureDEnregestrement &&
                        it.utilisateur == Utilisateur.Walid
            }

            if (activeInterval != null) {
                repository.updateExistingIntervalForWalid(
                    recordId = recordId,
                    intervalId = activeInterval.vid,
                    startTime = null,
                    endTime = endTime.replace(".", ":"),
                    typeTemp = typeTemp
                )
            }
        }
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
                    it.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                })
                Log.i(TAG, "LencePrint")
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

    fun get_PeriodVentActive(): MVentPeriode? {
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
