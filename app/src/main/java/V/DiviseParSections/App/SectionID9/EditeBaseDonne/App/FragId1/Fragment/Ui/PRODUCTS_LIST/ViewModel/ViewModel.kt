package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class Sec9FragId1ViewId2ViewModel: ViewModel() {
    data class UiState(var showDetailsExpanded: Boolean = true)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun update_showDetailsExpanded() { _uiState.value = _uiState.value.copy(showDetailsExpanded = !uiState.value.showDetailsExpanded,) }
}
