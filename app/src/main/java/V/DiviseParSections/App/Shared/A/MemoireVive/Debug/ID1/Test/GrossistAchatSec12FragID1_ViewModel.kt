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
        val v: String=""
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
}
    /*
@Stable
class MainFilterSorter{

    val categoryGroupedSortedProducts: List<ArticlesBasesStatsTable> by derivedStateOf {      //<--
        //TODO(1): passe a aluit ce qui il faut
        val categoryMap = b3CategoriesCompoRepository.datasValue.associateBy { it.id }
        val catalogues = B4CatalogueCategoriesRepository().associateBy { it.id }

        val (regularProducts, orphanProducts) = a_ProduitDataBaseComposeRepositoryPJ17.datasValue.partition { product ->
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
}
                   */
