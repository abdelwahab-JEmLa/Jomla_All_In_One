package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun LazyListScope.categorieSection(
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    groupedProducts: Map<Long, List<ArticlesBasesStatsTable>>,
    availableCategories: List<Long>,
    onProductCategoryChanged: (ArticlesBasesStatsTable) -> Unit,
    categoryMap: Map<Long, CategoriesTabelle> = emptyMap(),
    onAddCategory: ((String) -> Unit)? = null,
    onUpdateCategory: ((Long, String) -> Unit)? = null,
    selectedProducts: Set<ArticlesBasesStatsTable> = emptySet(),
    onProductSelectionToggle: (ArticlesBasesStatsTable) -> Unit = {},
    showBulkMoveDialog: Boolean = false,
    onShowBulkMoveDialog: (Boolean) -> Unit = {},
) {
    groupedProducts.forEach { (id, products) ->
        item(key = "header_$id") {
            E_StickyHeader(
                categoryId = id,
                category = categoryMap[id],
            )
        }
        item(key = "products_$id") {
            LazyRow(contentPadding = PaddingValues(12.dp, 8.dp)) {
                items(products, key = { "product_${it.id}" }) { produit ->
                    MainItemEditeCategories(
                        produit = produit,
                        availableCategories = availableCategories,
                        onCategoryChanged = onProductCategoryChanged,
                        modifier = Modifier.size(120.dp),
                        categoriesMap = categoryMap,
                        onAddCategory = onAddCategory,
                        onUpdateCategory = onUpdateCategory,
                        selectedProducts = selectedProducts,
                        onProductSelectionToggle = onProductSelectionToggle,
                        showBulkMoveDialog = showBulkMoveDialog,
                        onShowBulkMoveDialog = onShowBulkMoveDialog,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
