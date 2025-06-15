package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CataloguesCaegorie
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.startupeDatas
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
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
    onCategoriesEdite: ((List<CategoriesTabelle>) -> Unit)? = null,
    selectedProducts: Set<ArticlesBasesStatsTable> = emptySet(),
    onProductSelectionToggle: (ArticlesBasesStatsTable) -> Unit = {},
    showBulkMoveDialog: Boolean = false,
    onShowBulkMoveDialog: (Boolean) -> Unit = {}
) {                 //<--
//TODO(1): fait que les produits san categorie  soit au top 
    val categoriesCompoRepository = viewModel.categoriesCompoRepository
    val categoriesListLocal = categoriesCompoRepository.datasValue

    // Group products by category (same as before)
    val categoryMap = remember(categoriesListLocal) {
        categoriesListLocal.associateBy { it.id }
    }
    val groupedProducts = remember(produitList, categoriesListLocal) {
        produitList.groupBy { it.idParentCategorie ?: 0L }
            .toList().sortedBy { (id, _) ->
                if (id == 0L) Int.MIN_VALUE else categoryMap[id]?.position ?: Int.MIN_VALUE
            }.toMap()
    }

    // NEW: Group categories by catalogue (like in REORDER_GRID)
    val catalogues = remember { startupeDatas() }

    val categoriesByCatalogue = remember(
        categoriesListLocal,
        catalogues
    ) {
        val grouped = mutableMapOf<CataloguesCaegorie, List<CategoriesTabelle>>()

        // Group categories by their catalogue parent
        catalogues.forEach { catalogue ->
            val categoriesInCatalogue = categoriesListLocal.filter {
                it.catalogueParentId == catalogue.id
            }
            if (categoriesInCatalogue.isNotEmpty()) {
                grouped[catalogue] = categoriesInCatalogue.sortedBy { it.position }
            }
        }

        // Handle categories without a valid catalogue (orphaned categories)
        val orphanedCategories = categoriesListLocal.filter {
            it.catalogueParentId == 0L || !catalogues.any { c -> c.id == it.catalogueParentId }
        }
        if (orphanedCategories.isNotEmpty()) {
            grouped[CataloguesCaegorie(0, "Autres", 0)] = orphanedCategories.sortedBy { it.position }
        }

        grouped
    }

    val availableCategories = remember(produitList) {
        produitList.mapNotNull { it.idParentCategorie }.distinct().sorted()
    }

    Box(
        modifier = modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // NEW: Iterate through catalogues first, then categories within each catalogue
            categoriesByCatalogue.forEach { (catalogue, categoriesInCatalogue) ->

                // Add catalogue header
                item(key = "catalogue_header_${catalogue.id}") {
                    CatalogueHeader(catalogue = catalogue)
                }

                // For each category in this catalogue, show its products
                categoriesInCatalogue.forEach { category ->
                    val productsInCategory = groupedProducts[category.id] ?: emptyList()

                    if (productsInCategory.isNotEmpty()) {
                        categorieSection(
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
                categoryId != 0L && !categoriesByCatalogue.values.flatten().any { it.id == categoryId }
            }

            if (uncategorizedProducts.isNotEmpty()) {
                item(key = "uncategorized_header") {
                    CatalogueHeader(catalogue = CataloguesCaegorie(0, "Produits non classés", 0))
                }

                categorieSection(
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
