package Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur

import Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.B_MainItem.B_CouleurAfficheur
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.HeadViewModel

@Composable
fun A_MainListFragId3(
    viewModel: HeadViewModel,
    currentSale: SoldArticlesTabelle,
    stats: ArticlesBasesStatsTable,
    colorsArticlesTabelleModel: List<ColorsArticlesTabelle>,
    reloadTrigger: Int,
    viewModelInitApp: ViewModelInitApp,
    currentClient: B_ClientsDataBase?,
    colorsArticlesTabelleModele: List<ColorsArticlesTabelle>,
    parentCompose_1_2_ProduitAcheteOperationVid: Long,
) {
    var colorsListToDisplay by remember { mutableStateOf(emptyList<ColorsArticlesTabelle>()) }

    // Use LazyListState instead of LazyGridState to avoid layout issues
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        colorsListToDisplay = listOf(stats.idcolor1, stats.idcolor2, stats.idcolor3, stats.idcolor4)
            .filter { it != 0L }
            .mapNotNull { colorId -> colorsArticlesTabelleModel.find { it.idColore == colorId } }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                // Back to using LazyColumn with fixed heights to avoid infinite height constraints
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(colorsListToDisplay) { index, color ->
                        // Calculate if the item is visible in the current viewport
                        val isItemVisible = remember {
                            derivedStateOf {
                                val visibleItems = listState.layoutInfo.visibleItemsInfo
                                visibleItems.any { it.index == index }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .clip(MaterialTheme.shapes.medium)
                        ) {
                            B_CouleurAfficheur(
                                modifier = Modifier,
                                currentSale = currentSale,
                                article = stats,
                                color = color,
                                index = index,
                                reloadTrigger = reloadTrigger,
                                viewModel = viewModel,
                                height = 350.dp,
                                updateColorToBeMain = { colorId ->
                                    // Implementation for color selection
                                    viewModel.updateColorSelection(colorId, 1)
                                },
                                viewModelInitApp = viewModelInitApp,
                                currentClient = currentClient,
                                colorsArticlesTabelleModele = colorsArticlesTabelleModele,
                                parentCompose_1_2_ProduitAcheteOperationVid = parentCompose_1_2_ProduitAcheteOperationVid,
                                isVisibleInList = isItemVisible.value
                            )
                        }
                    }
                }
            }
        }
    }
}
