package Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package

import Z_CodePartageEntreApps.Model.A_Produit.A_Produit
import Z_CodePartageEntreApps.Model.I_CategorieProduits.I_CategorieProduits
import Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.ViewModel.ViewModel_A4FragID1
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun B_MainList_A4FragID_1(
    categories: List<I_CategorieProduits>,
    selectedCategories: List<I_CategorieProduits>,
    movingCategory: I_CategorieProduits?,
    heldCategory: I_CategorieProduits?,
    reorderMode: Boolean,
    onCategoryClick: (I_CategorieProduits) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ViewModel_A4FragID1
) {
    val addNewCategoryItem = remember {
        I_CategorieProduits(
            id = (categories.maxOfOrNull { it.id } ?: 0) + 1,
        ).apply {
            nom = "Add New Category"
            indexDonsParentList = 0
        }
    }

    // Pre-compute product mappings by category ID
    val productsByCategory = remember(viewModel.a_ProduitModelRepository.modelDatas) {
        viewModel.a_ProduitModelRepository.modelDatas.groupBy { it.parentCategoryId }
    }

    LazyColumn(
        contentPadding = PaddingValues(2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            items = listOf(addNewCategoryItem) + categories,
        ) { category ->
            val categoryClickHandler = remember(category) {
                { onCategoryClick(category) }
            }

            CategoriesStikyHeaderF1(
                viewModel=viewModel,
                category = category,
                isSelected = category in selectedCategories,
                isMoving = category == movingCategory,
                isHeld = category == heldCategory,
                isReorderTarget = reorderMode && category !in selectedCategories,
                selectionOrder = selectedCategories.indexOf(category) + 1,
                onClick = categoryClickHandler
            )

            val categoryProducts =
                if (viewModel.filterProduits) {
                    productsByCategory[category.id]
                        ?.filter { it.enumVarNonDispoPourClients !=
                                A_Produit.NON_DISPO_POUR_CLIENTS.TOUT }
                        ?: emptyList()
                } else {
                    productsByCategory[category.id]
                        ?: emptyList()
                }

            // LazyRow of products in this category
            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = categoryProducts,
                ) { index, produit ->
                    C_MainItemF1(
                        viewModel=viewModel,
                        mainItem = produit,
                        position = index + 1,
                        onClickOnMain = {}
                    )
                }
            }
        }
    }
}
