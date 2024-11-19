package P3_DisplayeProductInfosToSeller.Ui.Main

import P3_DisplayeProductInfosToSeller.Ui.Main.ColorItem3.ColorItem3
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.example.clientjetpack.ViewModel.WifiUpdateClientDisplayerStats
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ColorSelectionSection(
    currentSale: SoldArticlesTabelle,
    stats: ArticlesBasesStatsTable,
    colorsArticlesTabelleModel: List<ColorsArticlesTabelle>,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
) {
    var mainColorId by remember { mutableStateOf(stats.idcolor1) }
    var previousMainColorId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val colors = listOf(
                    stats.idcolor1,
                    stats.idcolor2,
                    stats.idcolor3,
                    stats.idcolor4
                ).mapNotNull { colorId ->
                    if (colorId != 0L) {
                        colorsArticlesTabelleModel.find { it.idColore == colorId }
                    } else null
                }

                // Fonction mise à jour qui garde trace de l'ancienne couleur principale
                val updateMainColor: (Long) -> Unit = { newMainColorId ->
                    previousMainColorId = mainColorId
                    mainColorId = newMainColorId
                    viewModel.sendOrderToClientDisplayer(
                        WifiUpdateClientDisplayerStats.ClientWindowsSelectedColorId.prefix,
                        newMainColorId
                    )
                }

                // Affichage de la couleur principale
                val mainColor = colors.find { it.idColore == mainColorId } ?: colors.firstOrNull()
                mainColor?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(MaterialTheme.shapes.medium)
                    ) {
                        ColorItem3(
                            modifier = Modifier,
                            currentSale = currentSale,
                            article = stats,
                            color = it,
                            index = 0,
                            reloadTrigger = reloadTrigger,
                            viewModel = viewModel,
                            height = 200.dp,
                            updateColorToBeMain = updateMainColor
                        )
                    }
                }

                // Affichage des couleurs supplémentaires
                if (colors.size > 1) {
                    val listState = rememberLazyListState()

                    LaunchedEffect(listState) {
                        snapshotFlow { listState.firstVisibleItemIndex }
                            .distinctUntilChanged()
                            .collect { position ->
                                if (position >= 0 && position < colors.size - 1) {
                                    viewModel.sendOrderToClientDisplayer(
                                        WifiUpdateClientDisplayerStats.ClientWindowsLazyRowSupColorsScrolle.prefix,
                                        position
                                    )
                                }
                            }
                    }

                    // Réorganiser les couleurs avec l'ancienne couleur principale à la fin
                    val arrangedColors = colors
                        .filter { it.idColore != mainColorId }
                        .sortedBy { color ->
                            when (color.idColore) {
                                previousMainColorId -> 1  // Met l'ancienne couleur principale à la fin
                                else -> 0  // Garde l'ordre original pour les autres
                            }
                        }

                    LazyRow(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(arrangedColors) { color ->
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(MaterialTheme.shapes.medium)
                            ) {
                                ColorItem3(
                                    modifier = Modifier,
                                    currentSale = currentSale,
                                    article = stats,
                                    color = color,
                                    index = arrangedColors.indexOf(color) + 1,
                                    reloadTrigger = reloadTrigger,
                                    viewModel = viewModel,
                                    height = 200.dp,
                                    updateColorToBeMain =  updateMainColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
