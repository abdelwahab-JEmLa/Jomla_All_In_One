package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.get_ListM21CataloguesCategorie
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun EditeCategoriesMainList(
    modifier: Modifier = Modifier,
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    produitList: List<M01Produit>,
    onProductCategoryChanged: (M01Produit) -> Unit,
    selectedProducts: Set<M01Produit> = emptySet(),
    onProductSelectionToggle: (M01Produit) -> Unit = {},
    showBulkMoveDialog: Boolean = false,
    onShowBulkMoveDialog: (Boolean) -> Unit = {}
) {
    val categoriesCompoRepository = viewModel.categoriesCompoRepository
    val categoriesListLocal = categoriesCompoRepository.datasValue

    val categoryMap = remember(categoriesListLocal) {
        categoriesListLocal.associateBy { it.id }
    }

    val catalogues = remember {
        get_ListM21CataloguesCategorie().sortedBy { it.position }
    }

    // Use the catalogue-based grouping logic
    val categoriesByCatalogue = remember(categoriesListLocal, catalogues) {
        groupCategoriesByCatalogue(categoriesListLocal, catalogues)
    }

    val groupedProducts = remember(produitList, categoriesListLocal) {
        produitList.groupBy { it.idParentCategorie ?: 0L }
    }

    val availableCategories = remember(produitList) {
        produitList.mapNotNull { it.idParentCategorie }.distinct().sorted()
    }

    val productsWithoutCategory = remember(groupedProducts) {
        groupedProducts[0L] ?: emptyList()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Products without category
            if (productsWithoutCategory.isNotEmpty()) {
                item(key = "no_category_header_top") {
                    CatalogueHeader(
                        catalogue = M21CataloguesCategorie(
                            id = -1,
                            nom = "Produits sans catégorie",
                            premierCategorieId = 0
                        )
                    )
                }

                categorieSection(
                    keyPrefix = "no_category_top",
                    viewModel = viewModel,
                    groupedProducts = mapOf(0L to productsWithoutCategory),
                    availableCategories = availableCategories,
                    onProductCategoryChanged = onProductCategoryChanged,
                    categoryMap = categoryMap,
                    selectedProducts = selectedProducts,
                    onProductSelectionToggle = onProductSelectionToggle,
                    showBulkMoveDialog = showBulkMoveDialog,
                    onShowBulkMoveDialog = onShowBulkMoveDialog,
                )
            }

            // Process catalogues using the grouped structure
            categoriesByCatalogue.forEach { (catalogue, categoriesInCatalogue) ->
                val shouldShowCatalogue = if (M18CentralParametresOfAllApps.get_Default().itsDevMode) {
                    catalogue.id != 4L
                } else {
                    true
                }

                if (shouldShowCatalogue) {
                    item(key = "catalogue_header_${catalogue.id}") {
                        CatalogueHeader(catalogue = catalogue)
                    }

                    categoriesInCatalogue.forEachIndexed { categoryIndex, category ->
                        val productsInCategory = groupedProducts[category.id] ?: emptyList()

                        if (productsInCategory.isNotEmpty()) {
                            categorieSection(
                                keyPrefix = "catalogue_${catalogue.id}_category_${category.id}_${categoryIndex}",
                                viewModel = viewModel,
                                groupedProducts = mapOf(category.id to productsInCategory),
                                availableCategories = availableCategories,
                                onProductCategoryChanged = onProductCategoryChanged,
                                categoryMap = categoryMap,
                                selectedProducts = selectedProducts,
                                onProductSelectionToggle = onProductSelectionToggle,
                                showBulkMoveDialog = showBulkMoveDialog,
                                onShowBulkMoveDialog = onShowBulkMoveDialog,
                            )
                        }
                    }
                }
            }

            // Handle any remaining uncategorized products that don't belong to any catalogue
            val processedCategoryIds = categoriesByCatalogue.values.flatten().map { it.id }.toSet()
            val uncategorizedProducts = groupedProducts.filterKeys { categoryId ->
                categoryId != 0L && !processedCategoryIds.contains(categoryId)
            }

            if (uncategorizedProducts.isNotEmpty()) {
                item(key = "uncategorized_header_bottom") {
                    CatalogueHeader(
                        catalogue = M21CataloguesCategorie(
                            id = 0,
                            nom = "Produits non classés",
                            premierCategorieId = 0
                        )
                    )
                }

                categorieSection(
                    keyPrefix = "uncategorized_bottom",
                    viewModel = viewModel,
                    groupedProducts = uncategorizedProducts,
                    availableCategories = availableCategories,
                    onProductCategoryChanged = onProductCategoryChanged,
                    categoryMap = categoryMap,
                    selectedProducts = selectedProducts,
                    onProductSelectionToggle = onProductSelectionToggle,
                    showBulkMoveDialog = showBulkMoveDialog,
                    onShowBulkMoveDialog = onShowBulkMoveDialog,
                )
            }
        }
    }
}

/**
 * Groups categories by their parent catalogue.
 * This function handles the logic for organizing categories under their respective catalogues,
 * including proper sorting and handling of orphan categories.
 *
 * Extracted from A_Main.kt to maintain consistency across the application.
 */
private fun groupCategoriesByCatalogue(
    currentCategories: List<M16CategorieProduit>,
    catalogues: List<M21CataloguesCategorie>
): LinkedHashMap<M21CataloguesCategorie, List<M16CategorieProduit>> {
    val grouped = linkedMapOf<M21CataloguesCategorie, List<M16CategorieProduit>>()

    // Process catalogues in order
    catalogues.forEach { catalogue ->
        val categoriesForCatalogue = currentCategories
            .filter { it.catalogueParentId == catalogue.id.toLong() }
            .sortedWith(
                compareBy<M16CategorieProduit> { it.position }
                    .thenByDescending { it.dernierTimeTampsSynchronisationAvecFireBase }
            )

        if (categoriesForCatalogue.isNotEmpty()) {
            grouped[catalogue] = categoriesForCatalogue
        }
    }

    // Handle orphan categories (categories without a valid catalogue parent)
    val orphanCategories = currentCategories
        .filter { category ->
            category.catalogueParentId == 0L ||
                    !catalogues.any { catalogue -> catalogue.id.toLong() == category.catalogueParentId }
        }
        .sortedWith(
            compareBy<M16CategorieProduit> { it.position }
                .thenByDescending { it.dernierTimeTampsSynchronisationAvecFireBase }
        )

    if (orphanCategories.isNotEmpty()) {
        val othersCatalogue = M21CataloguesCategorie(
            id = 0,
            nom = "Autres",
            premierCategorieId = 0
        )
        grouped[othersCatalogue] = orphanCategories
    }

    return grouped
}

@Composable
private fun CatalogueHeader(
    catalogue: M21CataloguesCategorie,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        androidx.compose.material3.Text(
            text = catalogue.nom,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
