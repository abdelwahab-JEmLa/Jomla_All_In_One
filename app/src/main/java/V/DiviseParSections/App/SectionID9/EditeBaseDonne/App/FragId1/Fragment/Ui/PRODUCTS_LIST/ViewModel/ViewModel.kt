package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Sec9FragId1ViewId2ViewModel(
    a_CentralCompoRepositoryProtoJuin9: RepositorysMainGetter,
) : ViewModel() {
    data class UiState(
        var showDetailsExpandedPourTout: Boolean = true,
        var hexIdDesProduitsOuLeurDetailsEstFerme: Set<String> = emptySet()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun toggleProductDetailsVisibility(productHexId: String) {
        val currentSet = _uiState.value.hexIdDesProduitsOuLeurDetailsEstFerme
        _uiState.value = _uiState.value.copy(
            hexIdDesProduitsOuLeurDetailsEstFerme = if (currentSet.contains(productHexId)) {
                currentSet - productHexId
            } else {
                currentSet + productHexId
            }
        )
    }

    fun isProductDetailsExpanded(productHexId: String): Boolean {
        return !_uiState.value.hexIdDesProduitsOuLeurDetailsEstFerme.contains(productHexId)
    }

    fun update_showDetailsExpanded() {
        _uiState.value =
            _uiState.value.copy(showDetailsExpandedPourTout = !uiState.value.showDetailsExpandedPourTout)
    }
}
