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
    var colorsListToEdite by remember { mutableStateOf(emptyList<ColorsArticlesTabelle>()) }

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
        val initialColorsList = validColors.mapIndexedNotNull { index, colorId ->
            colorsArticlesTabelleModel.find { it.idColore == colorId }?.apply {
                rankingTmpToDisplaye = index + 1
            }
        }

        // Update colorsListToEdite with the initialized list
        colorsListToEdite = initialColorsList
    }

    // Function to update color rankings when main color changes
    fun updateColorRankings(newMainColorId: Long) {
        val oldMainColor = colorsArticlesTabelleModel.find { it.idColore == mainColorId }
        val newMainColor = colorsArticlesTabelleModel.find { it.idColore == newMainColorId }

        if (oldMainColor != null && newMainColor != null) {
            // Create a new mutable list to store updated colors
            val updatedColorsList = colorsListToEdite.toMutableList()
            val oldRank = newMainColor.rankingTmpToDisplaye
            val lastRank = updatedColorsList.size

            // Example of how rankings change when clicking color with rank 3:
            // Before: [1, 2, 3, 4]
            // After:  [3, 2, 4, 1]
            updatedColorsList.forEach { color ->
                when {
                    // The clicked color becomes rank 1
                    color.idColore == newMainColorId -> {
                        color.rankingTmpToDisplaye = 1
                    }
                    // The previous main color goes to last position
                    color.idColore == mainColorId -> {
                        color.rankingTmpToDisplaye = lastRank
                    }
                    // Colors between new main and old main shift as needed
                    color.rankingTmpToDisplaye > oldRank -> {
                        // Colors after clicked color move up one position
                        color.rankingTmpToDisplaye -= 1
                    }
                    // Other colors maintain their position
                    else -> {
                        // No change needed
                    }
                }
            }

            // Sort and update colorsListToEdite
            colorsListToEdite = updatedColorsList.sortedBy { it.rankingTmpToDisplaye }
        } else if (newMainColor != null) {
            // Create a new mutable list for the case of no previous main color
            val updatedColorsList = colorsListToEdite.toMutableList()
            val oldRank = newMainColor.rankingTmpToDisplaye

            updatedColorsList.forEach { color ->
                when {
                    // The clicked color becomes rank 1
                    color.idColore == newMainColorId -> {
                        color.rankingTmpToDisplaye = 1
                    }
                    // Colors after clicked color move up one position
                    color.rankingTmpToDisplaye > oldRank -> {
                        color.rankingTmpToDisplaye -= 1
                    }
                }
            }

            // Sort and update colorsListToEdite
            colorsListToEdite = updatedColorsList.sortedBy { it.rankingTmpToDisplaye }
        }

        // Update mainColorId after rankings are updated
        mainColorId = newMainColorId
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
                // The rest of the UI code uses colorsListToEdite directly
                // since it's now properly maintained...

                // Updated main color handler with ranking update
                val updateMainColor: (Long) -> Unit = { newMainColorId ->
                    updateColorRankings(newMainColorId)
                    viewModel.sendOrderToClientDisplayer(
                        WifiUpdateClientDisplayerStats.ClientWindowsSelectedColorId.prefix,
                        newMainColorId
                    )
                }

                // Display main color and sub-colors as before
                val mainColor = colorsListToEdite.firstOrNull()
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

                if (colorsListToEdite.size > 1) {
                    val listState = rememberLazyListState()

                    LaunchedEffect(listState) {
                        snapshotFlow { listState.firstVisibleItemIndex }
                            .distinctUntilChanged()
                            .collect { position ->
                                if (position >= 0 && position < colorsListToEdite.size - 1) {
                                    viewModel.sendOrderToClientDisplayer(
                                        WifiUpdateClientDisplayerStats.ClientWindowsLazyRowSupColorsScrolle.prefix,
                                        position
                                    )
                                }
                            }
                    }

                    val subColors = colorsListToEdite.drop(1)

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
