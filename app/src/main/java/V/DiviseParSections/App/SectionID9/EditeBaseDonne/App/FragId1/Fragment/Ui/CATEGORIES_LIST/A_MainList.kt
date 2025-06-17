package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CataloguesCaegorie
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
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
    produitList: List<ArticlesBasesStatsTable>,
    onProductCategoryChanged: (ArticlesBasesStatsTable) -> Unit,
    selectedProducts: Set<ArticlesBasesStatsTable> = emptySet(),
    onProductSelectionToggle: (ArticlesBasesStatsTable) -> Unit = {},
    showBulkMoveDialog: Boolean = false,
    onShowBulkMoveDialog: (Boolean) -> Unit = {}
) {
    val categoriesCompoRepository = viewModel.categoriesCompoRepository
    val categoriesListLocal = categoriesCompoRepository.datasValue

    // Group products by category
    val categoryMap = remember(categoriesListLocal) {
        categoriesListLocal.associateBy { it.id }
    }

    val groupedProducts = remember(produitList, categoriesListLocal) {
        produitList.groupBy { it.idParentCategorie ?: 0L }
            .toList().sortedBy { (id, _) ->
                if (id == 0L) Int.MIN_VALUE else categoryMap[id]?.position ?: Int.MIN_VALUE
            }.toMap()
    }

    // Get catalogues and sort them by position
    val catalogues = remember {
        B4CatalogueCategoriesRepository().sortedBy { it.position }
    }

    val categoriesByCatalogue = remember(
        categoriesListLocal,
        catalogues
    ) {
        // Use LinkedHashMap to preserve order
        val grouped = linkedMapOf<CataloguesCaegorie, List<CategoriesTabelle>>()

        // Group categories by their catalogue parent - iterate through sorted catalogues
        catalogues.forEach { catalogue ->
            val categoriesInCatalogue = categoriesListLocal.filter {
                it.catalogueParentId == catalogue.id.toLong()
            }

            grouped[catalogue] = categoriesInCatalogue.sortedBy { it.position }
        }

        grouped
    }

    val availableCategories = remember(produitList) {
        produitList.mapNotNull { it.idParentCategorie }.distinct().sorted()
    }

    // Separate products without categories (idParentCategorie == null or 0)
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
            // Show products without categories at the top first (before catalogues)
            if (productsWithoutCategory.isNotEmpty()) {
                item(key = "no_category_header_top") { // FIXED: Unique key
                    CatalogueHeader(
                        catalogue = CataloguesCaegorie(
                            id = -1,
                            nom = "Produits sans catégorie",
                            premierCategorieId = 0
                        )
                    )
                }

                categorieSection(
                    keyPrefix = "no_category_top", // FIXED: Add unique prefix
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

            // Then iterate through catalogues in sorted order (by position)
            catalogues.forEachIndexed { catalogueIndex, catalogue -> // FIXED: Use forEachIndexed for unique keys
                val categoriesInCatalogue = categoriesByCatalogue[catalogue] ?: emptyList()

                item(key = "catalogue_header_${catalogue.id}_${catalogueIndex}") { // FIXED: More unique key
                    CatalogueHeader(catalogue = catalogue)
                }

                categoriesInCatalogue.forEachIndexed { categoryIndex, category -> // FIXED: Use forEachIndexed
                    val productsInCategory = groupedProducts[category.id] ?: emptyList()

                    if (productsInCategory.isNotEmpty()) {
                        categorieSection(
                            keyPrefix = "catalogue_${catalogue.id}_category_${category.id}_${categoryIndex}", // FIXED: Unique prefix
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

            // Handle categories that have products but aren't in any catalogue
            val uncategorizedProducts = groupedProducts.filterKeys { categoryId ->
                categoryId != 0L && !categoriesByCatalogue.values.flatten()
                    .any { it.id == categoryId }
            }

            if (uncategorizedProducts.isNotEmpty()) {
                item(key = "uncategorized_header_bottom") { // FIXED: Unique key
                    CatalogueHeader(
                        catalogue = CataloguesCaegorie(
                            id = 0,
                            nom = "Produits non classés",
                            premierCategorieId = 0
                        )
                    )
                }

                categorieSection(
                    keyPrefix = "uncategorized_bottom", // FIXED: Unique prefix
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

@Composable
private fun CatalogueHeader(
    catalogue: CataloguesCaegorie,
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
