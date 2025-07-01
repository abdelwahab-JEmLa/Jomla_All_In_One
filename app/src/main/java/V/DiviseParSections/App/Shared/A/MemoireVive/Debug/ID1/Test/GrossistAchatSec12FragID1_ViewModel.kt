package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.CategoriesTabelle
import androidx.compose.runtime.Stable
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
class MainFilterSorter(    //->
//TODO(FIXME):Fix erreur Class "MainFilterSorter" is never used  pk ce n ai pas utilise 
    private val products: List<ArticlesBasesStatsTable>,
    private val categories: List<CategoriesTabelle>
) {
    private val categoryMap = categories.associateBy { it.id }
    private val catalogues = B4CatalogueCategoriesRepository().associateBy { it.id }

    val categoryGroupedSortedProducts: List<ArticlesBasesStatsTable> by lazy {
        val (regular, orphan) = products.partition { product ->
            val category = categoryMap[product.idParentCategorie ?: 0L]
            val catalogueId = category?.catalogueParentId ?: 4L
            category != null && catalogueId != 4L && !category.nom.equals("NONE", true)
        }

        val sortedRegular = regular.sortedWith(
            compareBy<ArticlesBasesStatsTable> {
                val category = categoryMap[it.idParentCategorie ?: 0L]
                catalogues[category?.catalogueParentId ?: 4L]?.position ?: Int.MAX_VALUE
            }.thenBy { categoryMap[it.idParentCategorie ?: 0L]?.position ?: Int.MAX_VALUE }
                .thenBy { it.positionDonSonCesFrereCategorieProduits }
                .thenBy { it.nom.lowercase() }
        )

        val sortedOrphan = orphan.sortedWith(
            compareBy<ArticlesBasesStatsTable> {
                val category = categoryMap[it.idParentCategorie ?: 0L]
                category?.nom?.takeIf { !it.equals("NONE", true) } ?: "ZZZZZ_NO_CATEGORY"
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
                product.nom.contains(searchText, true) ||
                        product.nomMutable.contains(searchText, true) ||
                        product.nomArab.contains(searchText, true) ||
                        product.autreNomDarticle?.contains(searchText, true) == true
            }
        }
    }
}
