package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.CategoriesTabelle
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun LazyListScope.categorieSection(
    keyPrefix: String, // FIXED: Added keyPrefix parameter to ensure unique keys
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
        // FIXED: Use keyPrefix to ensure unique keys across different sections
        item(key = "${keyPrefix}_header_$id") {
            E_StickyHeader(
                viewModel = viewModel,
                groupedProducts = groupedProducts,
                categoryId = id,
                category = categoryMap[id],
            )
        }

        // FIXED: Use keyPrefix to ensure unique keys across different sections
        item(key = "${keyPrefix}_products_$id") {
            LazyRow(contentPadding = PaddingValues(12.dp, 8.dp)) {
                items(products, key = { "${keyPrefix}_product_${it.id}" }) { produit -> // FIXED: Unique product keys
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
