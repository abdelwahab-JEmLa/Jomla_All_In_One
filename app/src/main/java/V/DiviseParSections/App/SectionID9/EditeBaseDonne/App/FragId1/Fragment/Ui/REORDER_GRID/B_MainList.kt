package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.REORDER_GRID

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CatalogHeaderCard
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.CataloguesCaegorie
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun MainList(
    modifier: Modifier,
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    produitList: List<ArticlesBasesStatsTable>,
) {
    val TAG = "List_GroupeAchatProduit"
    val categoriesCompoRepository = viewModel.categoriesCompoRepository
    val currentCategories = categoriesCompoRepository.datasValue

    val productsByCategory = remember(produitList, categoriesCompoRepository.tigerDataRecompose) {
        produitList.groupBy { it.idParentCategorie ?: 0L }
    }

    val catalogues = remember { B4CatalogueCategoriesRepository() }

    val categoriesByCatalogue = remember(
        categoriesCompoRepository.tigerDataRecompose,
        catalogues
    ) {
        groupCategoriesByCatalogue(currentCategories, catalogues)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8D7EB))
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            categoriesByCatalogue.forEach { (catalogue, categories) ->
                val shouldShowCatalogue = if (M18CentralParametresOfAllApps.get_Default().itsDevMode) {
                    catalogue.id != 4L
                } else {
                    true
                }

                if (shouldShowCatalogue) {
                    item(span = { GridItemSpan(4) }) {
                        CatalogHeaderCard(catalogue = catalogue, modifier = Modifier)
                    }

                    items(categories, key = { it.id }) { category ->
                        MainItem(
                            productsByCategory = productsByCategory,
                            relative_category = category,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

/**
 * Groups categories by their parent catalogue.
 * This function handles the logic for organizing categories under their respective catalogues,
 * including proper sorting and handling of orphan categories.
 */
private fun groupCategoriesByCatalogue(
    currentCategories: List<CategoriesTabelle>,
    catalogues: List<CataloguesCaegorie>
): LinkedHashMap<CataloguesCaegorie, List<CategoriesTabelle>> {
    val grouped = linkedMapOf<CataloguesCaegorie, List<CategoriesTabelle>>()

    // Process catalogues in order
    catalogues.forEach { catalogue ->
        val categoriesForCatalogue = currentCategories
            .filter { it.catalogueParentId == catalogue.id }
            .sortedWith(
                compareBy<CategoriesTabelle> { it.positionDouble }
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
                    !catalogues.any { catalogue -> catalogue.id == category.catalogueParentId }
        }
        .sortedWith(
            compareBy<CategoriesTabelle> { it.positionDouble }
                .thenByDescending { it.dernierTimeTampsSynchronisationAvecFireBase }
        )

    if (orphanCategories.isNotEmpty()) {
        val othersCatalogue = CataloguesCaegorie(
            id = 0,
            nom = "Autres",
            premierCategorieId = 0
        )
        grouped[othersCatalogue] = orphanCategories
    }

    return grouped
}
