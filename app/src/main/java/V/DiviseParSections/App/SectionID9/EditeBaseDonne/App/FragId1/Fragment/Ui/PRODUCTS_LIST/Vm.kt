package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UiState(
    var showDetailsExpanded: Boolean = true
)

class PRODUCTS_LISTViewModel(
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun update(): Unit {
        _uiState.value = _uiState.value.copy(
            showDetailsExpanded = !uiState.value.showDetailsExpanded,
        )
    }
}
