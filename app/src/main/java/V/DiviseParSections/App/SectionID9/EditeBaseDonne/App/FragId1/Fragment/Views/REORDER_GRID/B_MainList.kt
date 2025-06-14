package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.REORDER_GRID

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.CategoriesTabelle
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.Shared.Module.Catalogue.CatalogHeaderCard
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.Shared.Module.Catalogue.CataloguesCaegorie
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.Shared.Module.Catalogue.startupeDatas
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun MainList(
    categoriesList: List<CategoriesTabelle>,
    produitList: List<ArticlesBasesStatsTable>,
    modifier: Modifier,
    onCategoriesReordered: (List<CategoriesTabelle>) -> Unit
) {
    var categoriesListLocal by remember(categoriesList) {
        mutableStateOf(categoriesList.sortedBy { it.position })
    }
    var selectedCategories by remember { mutableStateOf(setOf<Long>()) }

    val productsByCategory = remember(produitList) {
        produitList.groupBy { it.idParentCategorie ?: 0L }
    }

    val catalogues = remember { startupeDatas() }

    val categoriesByCatalogue = remember(categoriesListLocal, catalogues) {
        val grouped = mutableMapOf<CataloguesCaegorie, List<CategoriesTabelle>>()
        catalogues.forEach { cat ->
            categoriesListLocal.filter { it.catalogueParentId == cat.id }.let { if (it.isNotEmpty()) grouped[cat] = it }
        }
        categoriesListLocal.filter { it.catalogueParentId == 0L || !catalogues.any { c -> c.id == it.catalogueParentId } }
            .let { if (it.isNotEmpty()) grouped[CataloguesCaegorie(0, "Autres", 0)] = it }
        grouped
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
                        selectedCategories = selectedCategories,
                        categoriesListLocal = categoriesListLocal,
                        onCategoriesReordered = onCategoriesReordered,
                        onSelectionChanged = { selectedCategories = it },
                        onCategoriesUpdated = { categoriesListLocal = it }
                    )
                }
            }
        }
    }
}
