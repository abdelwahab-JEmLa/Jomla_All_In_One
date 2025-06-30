package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.AGetter
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocale
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.C.Update.addOrUpdateData
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.C.Update.deleteData
import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioRecorderAndPlayHandler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val d_EtateMessageVocaleList: List<D_EtateMessageVocale> = emptyList(),
    val c3_BonAchate: List<GBonVent> = emptyList(),
    val idActiveAppCompt:Long=0,
    val mainLoadingProgress: Float = 0f
)

class ViewModelMessageur(
    val getter : AGetter,
    val masterRepositorys: A_MasterRepositorysGrpProtoJuin3,
    val audioRecorderAndPlayHandler: AudioRecorderAndPlayHandler
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            masterRepositorys.model.collect { masterModel ->
                masterModel?.let { model ->
                    _uiState.value = _uiState.value.copy(
                        d_EtateMessageVocaleList = model.d_EtateMessageVocaleRepository?.modelListFlow
                            ?: emptyList(),
                        mainLoadingProgress = model.progress
                    )
                }
            }
        }

        viewModelScope.launch {
            val c3BonAchateList = masterRepositorys.e_GroupedDataBasesRepositoryProtoAvant3Juin
                .repositorys_Model.c3TransactionCommercialRepository
                .modelDatasSnapList.toList()

            _uiState.value = _uiState.value.copy(
                c3_BonAchate = c3BonAchateList
            )
        }
    }


    fun addOrUpdateData(data: D_EtateMessageVocale): Unit { masterRepositorys.d_EtateMessageVocaleRepository.addOrUpdateData(data) }
    fun deleteData(data: D_EtateMessageVocale): Unit { masterRepositorys.d_EtateMessageVocaleRepository.deleteData(data) }
}
