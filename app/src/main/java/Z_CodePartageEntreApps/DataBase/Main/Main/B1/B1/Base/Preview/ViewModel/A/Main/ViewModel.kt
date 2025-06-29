package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.A.Main

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class B1CouleurOuGoutProduitDataBaseTestDatasViewModel(
    val a_CentralDatasHandlerProtoJuin9: ACentralCompoRepositoryProtoJuin9,
) : ViewModel() {
    val b1CouleurOuGoutProduitDataBaseRepository =
        a_CentralDatasHandlerProtoJuin9.b1CouleurOuGoutProduitDataBaseRepository

    val mainRepo = a_CentralDatasHandlerProtoJuin9
        .b1CouleurOuGoutProduitDataBaseRepository


    data class UiState(
        val progressCount: Int = 0,
        val isGeneratingData: Boolean = false,
        val totalItems: Int = 0
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun genereDatasDepuitParent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingData = true, progressCount = 0)
            try {
                val totalGenerated = genereDatasDepuitParentWithCount { count ->
                    // Update progress count as items are processed
                    _uiState.value = _uiState.value.copy(progressCount = count)
                }
                _uiState.value = _uiState.value.copy(
                    progressCount = totalGenerated,
                    totalItems = totalGenerated
                )
            } finally {
                _uiState.value = _uiState.value.copy(isGeneratingData = false)
            }
        }
    }


    private fun genereDatasDepuitParentWithCount(onProgress: (Int) -> Unit): Int {
        val products = a_CentralDatasHandlerProtoJuin9.bProduitInfosRepository.datasValue
        var totalColorVariants = 0

        // Clear existing data first
        mainRepo.mainInitDataBase.deleteAll()

        // Process each product
        products.forEach { produit ->
            val colorVariants = mainRepo.mainInitDataBase.initCreationDepuitOld(produit)
            colorVariants.forEach { colorVariant ->
                mainRepo.addOrUpdateData(colorVariant)
                totalColorVariants++
                onProgress(totalColorVariants)
            }
        }

        return totalColorVariants
    }
}
