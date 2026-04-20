package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Test

import EntreApps.Shared.Modules.Loading_Datas.Init.A_MasterRepositorysGrpProtoJuin3
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val b_ClientInfosProtoJuin3List: List<M2Client> = emptyList(),
    val mainLoadingProgress: Float = 0f,
)

class B_ClientInfosProtoJuin3PreviewViewModel(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    _uiState.value = _uiState.value.copy(
                        b_ClientInfosProtoJuin3List = model.b_ClientInfosProtoJuin3Repository?.modelListFlow ?: emptyList(),
                        // FIXED: use lowercase 'b' to match the property name in MasterRepositorysModel
                        mainLoadingProgress = model.progress
                    )
                }
            }
        }
    }
}
