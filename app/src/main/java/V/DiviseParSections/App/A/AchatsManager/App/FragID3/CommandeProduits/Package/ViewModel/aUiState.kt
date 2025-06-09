package V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.ViewModel

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val B_ClientInfosProtoJuin3List: List<B_ClientInfosProtoJuin3> = emptyList(),
    val mainLoadingProgress: Float = 0f
)

class CommandeProduitsViewModel(
    val masterRepositorys: A_MasterRepositorysGrpProtoJuin3,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            masterRepositorys.model.collect { masterModel ->
                masterModel?.let { model ->
                    _uiState.value = _uiState.value.copy(
                        B_ClientInfosProtoJuin3List = model.b_ClientInfosProtoJuin3Repository?.modelListFlow ?: emptyList(),
                        // FIXED: use lowercase 'b' to match the property name in MasterRepositorysModel
                        mainLoadingProgress = model.progress
                    )
                }
            }
        }
    }
}
