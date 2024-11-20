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
import androidx.compose.foundation.layout.width
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

    // Initialize rankings on first composition
    LaunchedEffect(Unit) {
        // Get all valid colors
        val validColors = listOf(
            stats.idcolor1,
            stats.idcolor2,
            stats.idcolor3,
            stats.idcolor4
        ).filter { it != 0L }

        // Initialize rankings for all colors if not already set
        validColors.forEachIndexed { index, colorId ->
            colorsArticlesTabelleModel.find { it.idColore == colorId }?.let { color ->
                if (color.rankingTmpToDisplaye == 0) {
                    color.rankingTmpToDisplaye = index + 1
                }
            }
        }
    }

    // Function to update color rankings when main color changes
    fun updateColorRankings(newMainColorId: Long) {
        val oldMainColor = colorsArticlesTabelleModel.find { it.idColore == mainColorId }
        val newMainColor = colorsArticlesTabelleModel.find { it.idColore == newMainColorId }

        if (oldMainColor != null && newMainColor != null) {
            val newRank = newMainColor.rankingTmpToDisplaye

            // Update rankings for all affected colors
            colorsArticlesTabelleModel.forEach { color ->
                when {
                    color.idColore == newMainColorId -> {
                        color.rankingTmpToDisplaye = 1
                    }
                    color.idColore == mainColorId -> {
                        color.rankingTmpToDisplaye = colorsArticlesTabelleModel.size
                    }
                    color.rankingTmpToDisplaye in (1..newRank) -> {
                        color.rankingTmpToDisplaye++
                    }
                }
            }
        } else if (newMainColor != null) {
            // If there was no previous main color, just set the new one as main
            newMainColor.rankingTmpToDisplaye = 1

            // Shift other colors up
            colorsArticlesTabelleModel
                .filter { it.idColore != newMainColorId }
                .forEach { color ->
                    if (color.rankingTmpToDisplaye < newMainColor.rankingTmpToDisplaye) {
                        color.rankingTmpToDisplaye++
                    }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
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
                // Get all valid colors and sort them by temporary display order
                val allColors = listOf(
                    stats.idcolor1,
                    stats.idcolor2,
                    stats.idcolor3,
                    stats.idcolor4
                ).mapNotNull { colorId ->
                    if (colorId != 0L) {
                        colorsArticlesTabelleModel.find { it.idColore == colorId }
                    } else null
                }.sortedBy { it.rankingTmpToDisplaye }

                // Updated main color handler with ranking update
                val updateMainColor: (Long) -> Unit = { newMainColorId ->
                    previousMainColorId = mainColorId
                    mainColorId = newMainColorId
                    updateColorRankings(newMainColorId)
                    viewModel.sendOrderToClientDisplayer(
                        WifiUpdateClientDisplayerStats.ClientWindowsSelectedColorId.prefix,
                        newMainColorId
                    )
                }

                // Rest of the component remains the same...
                // Display main color and sub-colors as before
                val mainColor = allColors.firstOrNull()
                mainColor?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(270.dp)
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
                            height = 240.dp,
                            updateColorToBeMain = updateMainColor
                        )
                    }
                }

                if (allColors.size > 1) {
                    val listState = rememberLazyListState()

                    LaunchedEffect(listState) {
                        snapshotFlow { listState.firstVisibleItemIndex }
                            .distinctUntilChanged()
                            .collect { position ->
                                if (position >= 0 && position < allColors.size - 1) {
                                    viewModel.sendOrderToClientDisplayer(
                                        WifiUpdateClientDisplayerStats.ClientWindowsLazyRowSupColorsScrolle.prefix,
                                        position
                                    )
                                }
                            }
                    }

                    val subColors = allColors.drop(1)

                    LazyRow(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(
                            items = subColors,
                            key = { color -> color.idColore }
                        ) { color ->
                            Box(
                                modifier = Modifier
                                    .height(60.dp)
                                    .width(140.dp)
                                    .clip(MaterialTheme.shapes.medium)
                            ) {
                                ColorItem3(
                                    modifier = Modifier,
                                    currentSale = currentSale,
                                    article = stats,
                                    color = color,
                                    index = color.rankingTmpToDisplaye - 1,
                                    reloadTrigger = reloadTrigger,
                                    viewModel = viewModel,
                                    height = 60.dp,
                                    updateColorToBeMain = updateMainColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
