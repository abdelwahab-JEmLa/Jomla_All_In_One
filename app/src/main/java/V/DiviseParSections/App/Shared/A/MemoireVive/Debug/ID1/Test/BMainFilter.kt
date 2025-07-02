package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.A.ViewModel.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.CategoriesTabelle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey

@Composable
fun MainFilterT1(
    viewModel: ViewModelMainFastSearchProduitPourVent,
    products: List<ArticlesBasesStatsTable>,
    categories: List<CategoriesTabelle>,
    searchFilter: String,
    modifier: Modifier = Modifier,
    semanticsInfo: Pair<SemanticsPropertyKey<String>, String>
) {
    val categoryMap = remember(categories) { categories.associateBy { it.id } }
    val catalogues = remember { B4CatalogueCategoriesRepository().associateBy { it.id } }

    val filteredProducts = remember(products, searchFilter) {
        if (searchFilter.isBlank()) products
        else products.filter {
            it.nom.contains(searchFilter, true) ||
                    it.nomMutable.contains(searchFilter, true) ||
                    it.nomArab.contains(searchFilter, true)
        }
    }

    val sortedProducts = remember(filteredProducts, categories) {
        val (regular, orphan) = filteredProducts.partition { product ->
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

    MainListT1(viewModel,modifier, searchFilter, sortedProducts, categoryMap,semanticsInfo)
}

