package Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModel_DataBaseInitFactory_1Produit(
    val aCentralFacade: ACentralFacade,
) : ViewModel() {
    data class UiState(
        val value: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Use viewModelScope for long-running operations
    fun updateCarton() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                update_Carton_Internal(aCentralFacade)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private suspend fun update_Carton_Internal(aCentralFacade: ACentralFacade) {
        try {
            val oldDatas = OldDataBase_M1.get_old_Datas()
            val updatedProducts = mutableListOf<ArticlesBasesStatsTable>()

            oldDatas.forEach { old ->
                val m1Produit_IN_New =
                    aCentralFacade.repositorysMainGetter.repoM1ProduitInfos.datasValue
                        .find { it.id == old.id }

                if (m1Produit_IN_New != null) {
                    val updatedProduct = m1Produit_IN_New.copy(
                        quantite_Boit_Par_Carton = old.nmbrCaron
                    )
                    updatedProducts.add(updatedProduct)
                }
            }

            if (updatedProducts.isNotEmpty()) {
                batchFireBaseUpdateArticlesBasesStatsTable(updatedProducts)
                Log.d("update_Carton", "Successfully updated ${updatedProducts.size} products")
            } else {
                Log.d("update_Carton", "No products to update")
            }
        } catch (e: Exception) {
            Log.e("update_Carton", "Error updating carton data: ${e.message}", e)
            throw e
        }
    }
}
