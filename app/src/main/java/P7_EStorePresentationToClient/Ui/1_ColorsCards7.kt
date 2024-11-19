package P7_EStorePresentationToClient.Ui

import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.Models.ProductDisplayController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ColorsCards7(
    displayController: ProductDisplayController,
    articlesBasesStatsTable: ArticlesBasesStatsTable,
    modifier: Modifier = Modifier,
    relodeTigger: Int,
    colorsArticlesList: List<ColorsArticlesTabelle>,
) {
    var mainColorId by remember { mutableStateOf(articlesBasesStatsTable.idcolor1) }
    var previousMainColorId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(displayController.clientWindowsSelectedColorId) {
        if (displayController.clientWindowsSelectedColorId != 0L) {
            previousMainColorId = mainColorId
            mainColorId = displayController.clientWindowsSelectedColorId
        }
    }

    val colors = listOf(
        articlesBasesStatsTable.idcolor1,
        articlesBasesStatsTable.idcolor2,
        articlesBasesStatsTable.idcolor3,
        articlesBasesStatsTable.idcolor4
    ).mapNotNull { colorId ->
        if (colorId != 0L) {
            colorsArticlesList.find { it.idColore == colorId }
        } else null
    }

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(displayController.clientWindowsLazyRowSupColorsScroll) {
        if (!displayController.isHostPhone && colors.size > 1) {
            scope.launch {
                try {
                    val targetIndex = displayController.clientWindowsLazyRowSupColorsScroll.coerceIn(
                        0,
                        (colors.size - 2).coerceAtLeast(0)
                    )
                    listState.animateScrollToItem(
                        index = targetIndex,
                        scrollOffset = 0
                    )
                    delay(300)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),  // Supprimé le padding horizontal et vertical
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(), // Supprimé le padding
                    verticalArrangement = Arrangement.spacedBy(4.dp), // Réduit l'espacement entre les éléments
                    contentPadding = PaddingValues(0.dp) // Supprimé le padding vertical
                ) {
                    // Main color item
                    item {
                        val mainColor = if (displayController.clientWindowsSelectedColorId != 0L) {
                            colors.find { it.idColore == displayController.clientWindowsSelectedColorId }
                        } else {
                            colors.find { it.idColore == mainColorId } ?: colors.firstOrNull()
                        }

                        mainColor?.let {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(360.dp)
                            ) {
                                ColorItem7(
                                    modifier = Modifier.fillMaxSize(),
                                    article = articlesBasesStatsTable,
                                    color = it,
                                    index = 0,
                                    relodeTigger = relodeTigger,
                                )
                            }
                        }
                    }

                    // Additional colors
                    if (colors.size > 1) {
                        val currentMainColorId = if (displayController.clientWindowsSelectedColorId != 0L) {
                            displayController.clientWindowsSelectedColorId
                        } else {
                            mainColorId
                        }

                        val arrangedColors = colors
                            .filter { it.idColore != currentMainColorId }
                            .sortedBy { color ->
                                when (color.idColore) {
                                    previousMainColorId -> 1
                                    else -> 0
                                }
                            }

                        items(arrangedColors) { color ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                            ) {
                                ColorItem7(
                                    modifier = Modifier.fillMaxSize(),
                                    article = articlesBasesStatsTable,
                                    color = color,
                                    index = arrangedColors.indexOf(color) + 1,
                                    relodeTigger = relodeTigger,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
