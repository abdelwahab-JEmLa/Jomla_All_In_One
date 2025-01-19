package P7_EStorePresentationToClient.Ui

import Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Models.ArticlesBasesStatsTable
import Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Models.ColorsArticlesTabelle
import android.util.Log
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
    var colorsListToDisplaye by remember { mutableStateOf(emptyList<ColorsArticlesTabelle>()) }
    val colorArrangements = remember(displayController.newArregmentColorsJsonStruct) {
        displayController.getColorArrangement()
    }
    // Add a tag for logs
    val TAG = "ColorsCards7Debug"


    // Update colors list based on newArregmentColorsJsonStruct
    LaunchedEffect(displayController.newArregmentColorsJsonStruct) {
        colorsListToDisplaye = try {
            val arrangement = displayController.getColorArrangement()
            if (arrangement.isEmpty()) {
                getDefaultColorsList(articlesBasesStatsTable, colorsArticlesList)
            } else {
                arrangement.mapNotNull { arrangedColor ->
                    colorsArticlesList.find { it.idColore == arrangedColor.idColore }
                }.takeIf { it.isNotEmpty() } ?: getDefaultColorsList(articlesBasesStatsTable, colorsArticlesList)
            }
        } catch (e: Exception) {
            getDefaultColorsList(articlesBasesStatsTable, colorsArticlesList)
        }
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(displayController.clientWindowsLazyRowSupColorsScroll) {
        if (!displayController.isHostPhone && colorsListToDisplaye.size > 1) {
            scope.launch {
                try {
                    val targetIndex = displayController.clientWindowsLazyRowSupColorsScroll.coerceIn(
                        0,
                        (colorsListToDisplaye.size - 2).coerceAtLeast(0)
                    )
                    listState.animateScrollToItem(index = targetIndex)
                    delay(300)
                } catch (e: Exception) {
                    // Silent catch - no logging needed
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    // Main color (first in list)
                    item {
                        colorsListToDisplaye.firstOrNull()?.let { mainColor ->
                            Log.d(TAG, "Rendering main color: ${mainColor.idColore}")
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(360.dp)
                            ) {
                                ColorItem7(
                                    modifier = Modifier.fillMaxSize(),
                                    article = articlesBasesStatsTable,
                                    color = mainColor,
                                    index = 0,
                                    relodeTigger = relodeTigger,
                                    colorArrangement = colorArrangements.find { it.idColore == mainColor.idColore }
                                )
                            }
                        }
                    }

                    // Secondary colors
                    items(colorsListToDisplaye.drop(1)) { color ->
                        Log.d(TAG, "Rendering secondary color: ${color.idColore}")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                        ) {
                            ColorItem7(
                                modifier = Modifier.fillMaxSize(),
                                article = articlesBasesStatsTable,
                                color = color,
                                index = colorsListToDisplaye.indexOf(color),
                                relodeTigger = relodeTigger,
                                colorArrangement = colorArrangements.find { it.idColore == color.idColore }
                            )
                        }
                    }
                }
            }
        }
    }
}
private fun getDefaultColorsList(
    articlesBasesStatsTable: ArticlesBasesStatsTable,
    colorsArticlesList: List<ColorsArticlesTabelle>
): List<ColorsArticlesTabelle> {
    return listOf(
        articlesBasesStatsTable.idcolor1,
        articlesBasesStatsTable.idcolor2,
        articlesBasesStatsTable.idcolor3,
        articlesBasesStatsTable.idcolor4
    ).mapNotNull { colorId ->
        if (colorId != 0L) {
            colorsArticlesList.find { it.idColore == colorId }
        } else {
            null
        }
    }
}
