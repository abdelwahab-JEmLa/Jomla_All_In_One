package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
        _uiState.value = _uiState.value.copy(searchText = newText)
    }

    fun onAddNewProduct() {
        _uiState.value = _uiState.value.copy(showAddDialog = true)
    }

    fun onDismissAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false)
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(searchText = "")
    }
}

@Stable
class MainFilterSorter(
    private val products: List<ArticlesBasesStatsTable>,
    private val categories: List<CategoriesTabelle>
) {

    private val categoryMap = categories.associateBy { it.id }
    private val catalogues = B4CatalogueCategoriesRepository().associateBy { it.id }

    val categoryGroupedSortedProducts: List<ArticlesBasesStatsTable> by derivedStateOf {
        val (regularProducts, orphanProducts) = products.partition { product ->
            val categoryId = product.idParentCategorie ?: 0L
            val category = categoryMap[categoryId]
            val catalogueId = category?.catalogueParentId ?: 4L

            category != null &&
                    catalogueId != 4L &&
                    !category.nom.equals("NONE", ignoreCase = true)
        }

        val sortedRegular = regularProducts.sortedWith(
            compareBy<ArticlesBasesStatsTable> { product ->
                val categoryId = product.idParentCategorie ?: 0L
                val category = categoryMap[categoryId]
                val catalogueId = category?.catalogueParentId ?: 4L
                catalogues[catalogueId]?.position ?: Int.MAX_VALUE
            }.thenBy { product ->
                val categoryId = product.idParentCategorie ?: 0L
                categoryMap[categoryId]?.position ?: Int.MAX_VALUE
            }.thenBy { it.positionDonSonCesFrereCategorieProduits }
                .thenBy { it.nom.lowercase() }
        )

        val sortedOrphan = orphanProducts.sortedWith(
            compareBy<ArticlesBasesStatsTable> { product ->
                val categoryId = product.idParentCategorie ?: 0L
                val category = categoryMap[categoryId]
                category?.nom?.takeIf { !it.equals("NONE", ignoreCase = true) }
                    ?: "ZZZZZ_NO_CATEGORY"
            }.thenBy { it.positionDonSonCesFrereCategorieProduits }
                .thenBy { it.nom.lowercase() }
        )

        sortedRegular + sortedOrphan
    }

    fun filterBySearch(searchText: String): List<ArticlesBasesStatsTable> {
        return if (searchText.isBlank()) {
            categoryGroupedSortedProducts
        } else {
            categoryGroupedSortedProducts.filter { product ->
                product.nom.contains(searchText, ignoreCase = true) ||
                        product.nomMutable.contains(searchText, ignoreCase = true) ||
                        product.nomArab.contains(searchText, ignoreCase = true) ||
                        product.autreNomDarticle?.contains(searchText, ignoreCase = true) == true
            }
        }
    }
}
