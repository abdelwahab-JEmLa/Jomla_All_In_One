package Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.UI

import Z_CodePartageEntreApps.Model.I_CategorieProduits.I_CategorieProduits
import Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.ViewModel.ViewModel_A4FragID1

fun handleCategoryClick_F1(
    category: I_CategorieProduits,
    filterText: String,
    viewModel: ViewModel_A4FragID1,
    renameOrFusionMode: Boolean,
    multiSelectionMode: Boolean,
    reorderMode: Boolean,
    heldCategory: I_CategorieProduits?,
    selectedCategories: List<I_CategorieProduits>,
    movingCategory: I_CategorieProduits?,
    onHeldCategoryChange: (I_CategorieProduits?) -> Unit,
    onSelectedCategoriesChange: (List<I_CategorieProduits>) -> Unit,
    onRenameOrFusionModeChange: (Boolean) -> Unit,
    onMovingCategoryChange: (I_CategorieProduits?) -> Unit,
    onReorderModeChange: (Boolean) -> Unit,
    onCategorySelected: (I_CategorieProduits) -> Unit,
    onDismiss: () -> Unit
) {
    when {
        category.nom == "Add New Category" -> {
            if (filterText.isNotBlank()) {
                viewModel.addNewCategory(filterText)
            }
        }
        reorderMode -> {
            // Pass the clicked category as the target position for the selected categories
            viewModel.movePlusieurCategories(selectedCategories, category)

            // No need for individual moves anymore since we're handling it in one operation

            // Reset reorder mode
            onReorderModeChange(false)
            onSelectedCategoriesChange(emptyList())
        }
        renameOrFusionMode -> {
            if (heldCategory == null) {
                onHeldCategoryChange(category)
            } else if (heldCategory != category) {
                viewModel.moveArticlesBetweenCategories(
                    fromCategoryId = heldCategory.indexDonsParentList,
                    toCategoryId = category.indexDonsParentList
                )
                onHeldCategoryChange(null)
                onRenameOrFusionModeChange(false)
            }
        }
        multiSelectionMode -> {
            onSelectedCategoriesChange(
                if (category in selectedCategories) {
                    selectedCategories.filterNot { it == category }
                } else {
                    selectedCategories + category
                }
            )
        }
        movingCategory != null -> {
            viewModel.handleCategoryMove(
                holdedIdCate = movingCategory.indexDonsParentList,
                clickedCategoryId = category.indexDonsParentList
            ) {
                onMovingCategoryChange(null)
            }
        }
        else -> {
            onCategorySelected(category)
            onDismiss()
        }
    }
}
