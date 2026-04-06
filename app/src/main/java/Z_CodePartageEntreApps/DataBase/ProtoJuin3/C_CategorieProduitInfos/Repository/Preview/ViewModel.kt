package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.Preview

import EntreApps.Shared.Modules.Loading_Datas.Init.A_MasterRepositorysGrpProtoJuin3
import EntreApps.Shared.Models.M16CategorieProduit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val c_CategorieProduitInfosList: List<M16CategorieProduit> = emptyList(),
    val mainLoadingProgress: Float = 0f
)

class CategoriePrevViewModel(
    private val masterRepositorys: A_MasterRepositorysGrpProtoJuin3,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            masterRepositorys.model.collect { masterModel ->
                masterModel?.let { model ->
                    _uiState.value = _uiState.value.copy(
                        c_CategorieProduitInfosList = model.repoStateC_CategorieProduitInfos?.modelListFlow ?: emptyList(),
                        mainLoadingProgress = model.progress
                    )
                }
            }
        }
    }
}
