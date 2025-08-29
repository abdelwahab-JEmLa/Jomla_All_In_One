package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Sec9FragId1ViewId2ViewModel(
    val aCentralFacade: ACentralFacade,
) : ViewModel() {
    data class UiState(
        var showDetailsExpandedPourTout: Boolean = true,
        var selectedTypeChoisi: M13TarificationInfos.TypeChoisi = M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService,
        var hexIdDesProduitsOuLeurDetailsEstFerme: Set<String> = emptySet(),

        var its_Mode_Regle_Prixs: Boolean = false,
        var mode_Edites: Mode_Edites = Mode_Edites.its_Mode_Regle_Prixs,
    ) {
        enum class Mode_Edites {
            its_Mode_Regle_Prixs, standart
        }
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // FIXED: Complete the toggle_selectedTypeChoisi method
    fun toggle_selectedTypeChoisi() {
        val currentType = _uiState.value.selectedTypeChoisi
        val newType = when (currentType) {
            M13TarificationInfos.TypeChoisi.Prix_Detaille -> M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService
            M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService -> M13TarificationInfos.TypeChoisi.Prix_Detaille
            else -> M13TarificationInfos.TypeChoisi.Prix_Detaille
        }

        _uiState.value = _uiState.value.copy(
            selectedTypeChoisi = newType
        )
    }

    // ADDED: Method to update selectedTypeChoisi directly (used in composable)
    fun updateSelectedTypeChoisi(newType: M13TarificationInfos.TypeChoisi) {
        _uiState.value = _uiState.value.copy(
            selectedTypeChoisi = newType
        )
    }

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
