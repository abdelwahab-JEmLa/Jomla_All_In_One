package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.REORDER_GRID

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CatalogHeaderCard
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CataloguesCaegorie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.CategoriesTabelle
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
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
    val TAG ="MainList"
    val categoriesCompoRepository = viewModel.categoriesCompoRepository
    val currentCategories = categoriesCompoRepository.datasValue

    val productsByCategory = remember(produitList, categoriesCompoRepository.tigerDataRecompose ) {
        produitList.groupBy { it.idParentCategorie ?: 0L }
    }

    val catalogues = remember { B4CatalogueCategoriesRepository() }

    val categoriesByCatalogue = remember(
        categoriesCompoRepository.tigerDataRecompose,
        catalogues
    ) {
        val grouped = mutableMapOf<CataloguesCaegorie, List<CategoriesTabelle>>()
        catalogues.forEach { cat ->
            currentCategories.filter { it.catalogueParentId == cat.id }
                .let { if (it.isNotEmpty()) grouped[cat] = it }
        }
        currentCategories.filter { it.catalogueParentId == 0L || !catalogues.any { c -> c.id == it.catalogueParentId } }
            .let { if (it.isNotEmpty()) grouped[CataloguesCaegorie(
                id = 0,
                nom = "Autres",
                premierCategorieId = 0
            )] = it }
        grouped
    }.also {
        val cate=  currentCategories.find { it.nom.contains("huil") }
        if (cate != null) {
            CategoriesTabelle.logCategory(cate, TAG)
        }
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
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(4) }) {
                    CatalogHeaderCard(catalogue = catalogue, modifier = Modifier)
                }
                items(categories, key = { it.id }) { category ->
                    MainItem(
                        productsByCategory = productsByCategory,
                        category = category,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
