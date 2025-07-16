package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
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
    val d_EtateMessageVocaleList: List<M17MessageVocale> = emptyList(),
    val c3_BonAchate: List<M8BonVent> = emptyList(),
    val idActiveAppCompt:Long=0,
    val mainLoadingProgress: Float = 0f
)

class ViewModelMessageur(
    val aCentralFacade : ACentralFacade,
    val getter : RepositorysMainGetter,
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

        val datasG = getter.repo8BonVent.datasValue
        viewModelScope.launch {
            val c3BonAchateList = datasG.toList()

            _uiState.value = _uiState.value.copy(
                c3_BonAchate = c3BonAchateList
            )
        }
    }


    fun addOrUpdateData(data: M17MessageVocale): Unit { masterRepositorys.d_EtateMessageVocaleRepository.addOrUpdateData(data) }
    fun deleteData(data: M17MessageVocale): Unit { masterRepositorys.d_EtateMessageVocaleRepository.deleteData(data) }
}
