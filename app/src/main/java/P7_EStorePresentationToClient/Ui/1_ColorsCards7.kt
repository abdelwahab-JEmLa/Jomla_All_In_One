package P7_EStorePresentationToClient.Ui

import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
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

    // Add a tag for logs
    val TAG = "ColorsCards7Debug"

    // Log initial values
    LaunchedEffect(Unit) {
        Log.d(TAG, "Initial values:")
        Log.d(TAG, "Colors list size: ${colorsArticlesList.size}")
        Log.d(TAG, "Articles stats table: ${articlesBasesStatsTable}")
        Log.d(TAG, "Is host phone: ${displayController.isHostPhone}")
    }

    // Update colors list based on newArregmentColorsJsonStruct
    LaunchedEffect(displayController.newArregmentColorsJsonStruct) {
        Log.d(TAG, "LaunchedEffect triggered with new arrangement JSON")
        Log.d(TAG, "JSON content: ${displayController.newArregmentColorsJsonStruct}")

        colorsListToDisplaye = try {
            val arrangement = displayController.getColorArrangement()
            Log.d(TAG, "Parsed arrangement size: ${arrangement.size}")
            Log.d(TAG, "Arrangement content: $arrangement")

            if (arrangement.isEmpty()) {
                Log.d(TAG, "Empty arrangement, using default list")
                getDefaultColorsList(articlesBasesStatsTable, colorsArticlesList)
            } else {
                arrangement.mapNotNull { arrangedColor ->
                    Log.d(TAG, "Looking for color ID: ${arrangedColor.idColore}")
                    colorsArticlesList.find { it.idColore == arrangedColor.idColore }?.also { foundColor ->
                        Log.d(TAG, "Found color: $foundColor")
                    } ?: run {
                        Log.w(TAG, "Color not found for ID: ${arrangedColor.idColore}")
                        null
                    }
                }.takeIf { it.isNotEmpty() } ?: getDefaultColorsList(articlesBasesStatsTable, colorsArticlesList)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in color arrangement", e)
            getDefaultColorsList(articlesBasesStatsTable, colorsArticlesList)
        }

        Log.d(TAG, "Final list size: ${colorsListToDisplaye.size}")
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Handle scroll position updates
    LaunchedEffect(displayController.clientWindowsLazyRowSupColorsScroll) {
        if (!displayController.isHostPhone && colorsListToDisplaye.size > 1) {
            scope.launch {
                try {
                    val targetIndex = displayController.clientWindowsLazyRowSupColorsScroll.coerceIn(
                        0,
                        (colorsListToDisplaye.size - 2).coerceAtLeast(0)
                    )
                    Log.d(TAG, "Animating scroll to index: $targetIndex")
                    listState.animateScrollToItem(index = targetIndex)
                    delay(300)
                    Log.d(TAG, "Scroll animation completed")
                } catch (e: Exception) {
                    Log.e(TAG, "Error during scroll animation", e)
                    e.printStackTrace()
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
                                )
                            }
                        } ?: Log.w(TAG, "No main color available")
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
    val TAG = "ColorsCards7Debug"
    Log.d(TAG, "Getting default colors list")
    Log.d(TAG, "Default color IDs: [" +
            "color1: ${articlesBasesStatsTable.idcolor1}, " +
            "color2: ${articlesBasesStatsTable.idcolor2}, " +
            "color3: ${articlesBasesStatsTable.idcolor3}, " +
            "color4: ${articlesBasesStatsTable.idcolor4}]")

    return listOf(
        articlesBasesStatsTable.idcolor1,
        articlesBasesStatsTable.idcolor2,
        articlesBasesStatsTable.idcolor3,
        articlesBasesStatsTable.idcolor4
    ).mapNotNull { colorId ->
        if (colorId != 0L) {
            colorsArticlesList.find { it.idColore == colorId }?.also { foundColor ->
                Log.d(TAG, "Found color for ID $colorId: ${foundColor.idColore}")
            } ?: run {
                Log.w(TAG, "No matching color found for ID: $colorId")
                null
            }
        } else {
            Log.d(TAG, "Skipping color ID 0")
            null
        }
    }.also { resultList ->
        Log.d(TAG, "Default colors list created with ${resultList.size} colors")
    }
}
