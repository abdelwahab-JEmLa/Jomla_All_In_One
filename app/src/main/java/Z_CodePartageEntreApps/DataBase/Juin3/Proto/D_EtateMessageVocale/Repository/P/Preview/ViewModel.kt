package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.P.Preview

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorys
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val mainList: List<D_EtateMessageVocale> = emptyList(),
    val mainLoadingProgress: Float = 0f
)

class D_EtateMessageVocalePreviewViewModel(
    private val masterRepositorys: A_MasterRepositorys,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            masterRepositorys.model.collect { masterModel ->
                masterModel?.let { model ->
                    _uiState.value = _uiState.value.copy(
                        mainList = model.d_EtateMessageVocaleRepository?.modelListFlow ?: emptyList(),
                        mainLoadingProgress = model.progress
                    )
                }
            }
        }
    }
}
