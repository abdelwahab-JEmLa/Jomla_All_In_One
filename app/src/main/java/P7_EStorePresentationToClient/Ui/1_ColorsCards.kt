package P7_EStorePresentationToClient.Ui

import P7_EStorePresentationToClient.Modules.ImageDisplayerPC
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ColorsArticlesTabelle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.clientjetpack.Models.ProductDisplayController
import com.example.clientjetpack.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ColorsCards(
    displayController: ProductDisplayController,
    articlesBasesStatsTable: ArticlesBasesStatsTable,
    modifier: Modifier = Modifier,
    relodeTigger: Int,
    colorsArticlesList: List<ColorsArticlesTabelle>,
) {
    // Track the previous main color ID
    var previousMainColorId by remember { mutableStateOf<Long?>(null) }

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

    // Update previous main color when main color changes
    LaunchedEffect(displayController.clientWindowsSelectedColorId) {
        if (displayController.clientWindowsSelectedColorId != 0L &&
            displayController.clientWindowsSelectedColorId != previousMainColorId) {
            previousMainColorId = displayController.clientWindowsSelectedColorId
        }
    }

    LaunchedEffect(displayController.clientWindowsLazyRowSupColorsScroll) {
        if (!displayController.isHostPhone && colors.size > 1) {
            try {
                scope.launch {
                    val targetIndex = displayController.clientWindowsLazyRowSupColorsScroll.coerceIn(
                        0,
                        (colors.size - 2).coerceAtLeast(0)
                    )
                    listState.animateScrollToItem(
                        index = targetIndex,
                        scrollOffset = 0
                    )
                    delay(300)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Main color display
            val mainColor = if (displayController.clientWindowsSelectedColorId != 0L) {
                colors.find { it.idColore == displayController.clientWindowsSelectedColorId }
            } else {
                colors.firstOrNull()
            }

            when (colors.size) {
                2 -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(0.6f)
                                .height(360.dp)
                        ) {
                            ColorItem(
                                modifier = Modifier.fillMaxSize(),
                                article = articlesBasesStatsTable,
                                color = mainColor,
                                index = colors.indexOf(mainColor),
                                relodeTigger = relodeTigger,
                            )
                        }

                        val secondaryColor = colors.find { it != mainColor }
                        Box(
                            modifier = Modifier
                                .weight(0.4f)
                                .height(360.dp)
                        ) {
                            ColorItem(
                                modifier = Modifier.fillMaxSize(),
                                article = articlesBasesStatsTable,
                                color = secondaryColor,
                                index = colors.indexOf(secondaryColor),
                                relodeTigger = relodeTigger,
                            )
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                    ) {
                        ColorItem(
                            modifier = Modifier.fillMaxSize(),
                            article = articlesBasesStatsTable,
                            color = mainColor,
                            index = colors.indexOf(mainColor),
                            relodeTigger = relodeTigger,
                        )
                    }

                    if (colors.size > 1) {
                        // Organiser les couleurs comme dans ColorSelectionSection
                        val subColors = colors.filter { it != mainColor }
                        val arrangedColors = if (previousMainColorId != null) {
                            // Trouver l'ancienne couleur principale et la mettre à la fin
                            val oldMainColor = subColors.find { it.idColore == previousMainColorId }
                            if (oldMainColor != null) {
                                val otherColors = subColors.filter { it != oldMainColor }
                                otherColors + oldMainColor
                            } else {
                                subColors
                            }
                        } else {
                            subColors
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
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    ColorItem(
                                        modifier = Modifier.fillMaxSize(),
                                        article = articlesBasesStatsTable,
                                        color = color,
                                        index = colors.indexOf(color),
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
}
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ColorItem(
    modifier: Modifier,
    article: ArticlesBasesStatsTable,
    color: ColorsArticlesTabelle?,
    index: Int,
    relodeTigger: Int,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image
            ImageDisplayerPC(
                modifier = Modifier.fillMaxSize(),
                article = article,
                indexColor = index,
                reloadKey = relodeTigger
            )

            // Color info overlay at bottom
            color?.let { colorData ->
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))
                            )
                        ),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Color name
                        Text(
                            text = colorData.nameColore,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        // Color icon
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    colorData.iconColore in listOf("©", "💯", "") -> {
                                        GlideImage(
                                            model = R.drawable.logo,
                                            contentDescription = "Logo",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = colorData.iconColore,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
