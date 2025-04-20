package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows
         /*
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00._01_VentsNoSQl
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._01._01_VentsHistoriquesDataBase_Repository
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Data class for representing UI state
data class PeriodesUiState(
    val a01PeriodesVent: SnapshotStateList<_01_VentsNoSQl> = mutableStateListOf()
)

// ViewModel for handling commands/orders
open class PeriodesViewModel(
    val _01_VentsHistoriquesDataBase_Repository: _01_VentsHistoriquesDataBase_Repository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PeriodesUiState())
    open val uiState: StateFlow<PeriodesUiState> = _uiState.asStateFlow()

    init {
        loadApreProgressEnd()
    }

    private fun loadApreProgressEnd() {
        viewModelScope.launch {
            _01_VentsHistoriquesDataBase_Repository.progressRepo.collectLatest { progress ->
                if (progress >= 1.0f) {
                    val updatedUiState = PeriodesUiState(
                        a01PeriodesVent = _01_VentsHistoriquesDataBase_Repository.modelDatasSnapList
                    )
                    _uiState.value = updatedUiState
                }
            }
        }
    }
}
                                                   */
