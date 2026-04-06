package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Z.Preview

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.UiState
import EntreApps.Shared.Modules.Loading_Datas.Init.A_MasterRepositorysGrpProtoJuin3
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val d_EtateMessageVocaleList: List<M17MessageVocale> = emptyList(),
    val mainLoadingProgress: Float = 0f
)

class D_EtateMessageVocalePreviewViewModel(
    val masterRepositorys: A_MasterRepositorysGrpProtoJuin3,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            masterRepositorys.model.collect { masterModel ->
                masterModel?.let { model ->
                    _uiState.value = _uiState.value.copy(
                        d_EtateMessageVocaleList = model.d_EtateMessageVocaleRepository?.modelListFlow ?: emptyList(),
                        mainLoadingProgress = model.progress
                    )
                }
            }
        }
    }
}
