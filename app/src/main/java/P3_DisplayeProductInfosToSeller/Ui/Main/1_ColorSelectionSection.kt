package P3_DisplayeProductInfosToSeller.Ui.Main

import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
    // Track the current main color ID
    var mainColorId by remember { mutableStateOf(stats.idcolor1) }

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

                // Function to update the main color
                val updateMainColor: (Long) -> Unit = { newMainColorId ->
                    mainColorId = newMainColorId
                    // Notify the client displayer about the color change
                    viewModel.sendOrderToClientDisplayer(
                        WifiUpdateClientDisplayerStats.ClientWindowsSelectedColorId.prefix,
                        newMainColorId
                    )
                }

                // Display the main color
                val mainColor = colors.find { it.idColore == mainColorId } ?: colors.firstOrNull()
                mainColor?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(MaterialTheme.shapes.medium)
                    ) {
                        ColorItem3(
                            modifier = Modifier.fillMaxSize(),
                            currentSale = currentSale,
                            article = stats,
                            color = it,
                            index = colors.indexOf(it),
                            relodeTigger = reloadTrigger,
                            viewModel = viewModel,
                            height = 150.dp,
                            updateColorToBeMAin = updateMainColor
                        )
                    }
                }

                // Display additional colors
                if (colors.size > 1) {
                    val listState = rememberLazyListState()

                    LaunchedEffect(listState) {
                        snapshotFlow { listState.firstVisibleItemIndex }
                            .distinctUntilChanged()
                            .collect { position ->
                                if (position >= 0 && position < colors.size - 1) {
                                    viewModel.sendOrderToClientDisplayer(
                                        WifiUpdateClientDisplayerStats.WindowsPickerDisplayedQuantity.prefix,
                                        position
                                    )
                                }
                            }
                    }

                    LazyRow(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(colors.filter { it.idColore != mainColorId }) { color ->
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(MaterialTheme.shapes.medium)
                            ) {
                                ColorItem3(
                                    modifier = Modifier.fillMaxSize(),
                                    currentSale = currentSale,
                                    article = stats,
                                    color = color,
                                    index = colors.indexOf(color),
                                    relodeTigger = reloadTrigger,
                                    viewModel = viewModel,
                                    height = 200.dp,
                                    updateColorToBeMAin = updateMainColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
