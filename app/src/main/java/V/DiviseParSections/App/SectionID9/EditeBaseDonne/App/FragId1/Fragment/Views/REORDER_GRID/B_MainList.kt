package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.REORDER_GRID

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.C_CategorieProduitInfos
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
    categoriesList: List<C_CategorieProduitInfos>,
    produitList: List<ArticlesBasesStatsTable>,
    modifier: Modifier,
    onCategoriesReordered: (List<C_CategorieProduitInfos>) -> Unit
) {
    var categoriesListLocal by remember(categoriesList) {
        mutableStateOf(categoriesList.sortedBy { it.position })
    }
    var selectedCategories by remember { mutableStateOf(setOf<Long>()) }

    val productsByCategory = remember(produitList) {
        produitList.groupBy { it.idParentCategorie ?: 0L }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8D7EB))
    ) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(categoriesListLocal, key = { it.id }) { category ->
                MainItem(
                    productsByCategory = productsByCategory,
                    category = category,
                    selectedCategories = selectedCategories,
                    categoriesListLocal = categoriesListLocal,
                    onCategoriesReordered = onCategoriesReordered,
                    onSelectionChanged = { newSelection ->
                        selectedCategories = newSelection
                    },
                    onCategoriesUpdated = { newCategories ->
                        categoriesListLocal = newCategories
                    }
                )
            }
        }
    }
}
