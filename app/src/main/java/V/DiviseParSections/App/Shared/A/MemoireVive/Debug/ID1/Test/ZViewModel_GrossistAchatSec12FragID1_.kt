package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewModelMainFastSearchProduitPourVent(
    aCentral: ACentralFacade,
) : ViewModel() {
    val getter = aCentral.getter

    data class UiState(
        val searchText: String = "",
        val isLoading: Boolean = false,
        val showAddDialog: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onSearchTextChange(newText: String) {
        _uiState.value.copy(searchText = newText).also { _uiState.value = it }
    }

    fun onAddNewProduct() {
        _uiState.value = _uiState.value.copy(showAddDialog = true)
    }
}

