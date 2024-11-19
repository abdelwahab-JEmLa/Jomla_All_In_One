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
fun ColorsCards7(
    displayController: ProductDisplayController,
    articlesBasesStatsTable: ArticlesBasesStatsTable,
    modifier: Modifier = Modifier,
    relodeTigger: Int,
    colorsArticlesList: List<ColorsArticlesTabelle>,
) {
    // Track both local and controller-based color selection
    var mainColorId by remember { mutableStateOf(articlesBasesStatsTable.idcolor1) }
    var previousMainColorId by remember { mutableStateOf<Long?>(null) }

    // Update mainColorId when clientWindowsSelectedColorId changes
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

    // Handle auto-scroll for client windows
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

    Column(
        modifier = modifier
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
                // Main color display
                val mainColor = if (displayController.clientWindowsSelectedColorId != 0L) {
                    colors.find { it.idColore == displayController.clientWindowsSelectedColorId }
                } else {
                    colors.find { it.idColore == mainColorId } ?: colors.firstOrNull()
                }

                mainColor?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)  //-->
                            //Hi Claud,what i went from u to do is to
                            //Find All TODOs and Fix Them 

                            //TODO:
                            // fait que si @Composable
                            // fun ImageDisplayer1(
                            //    modifier: Modifier = Modifier,
                            //    article: ArticlesBasesStatsTable,
                            //    viewModel: HeadViewModel,
                            //    indexColor: Int = 0,
                            //    reloadKey: Any = Unit
                            //) {
                            //    val context = LocalContext.current
                            //    val viewModelImagesPath = viewModel.viewModelImagesPath
                            //
                            //    val baseImagePath = remember(viewModelImagesPath, article.idArticle, indexColor) {
                            //        File(viewModelImagesPath, "${article.idArticle}_${if (indexColor == -1) "Unite" else (indexColor + 1)}")
                            //            .absolutePath
                            //    }
                            //
                            //    val imageExist by remember(baseImagePath, reloadKey) {
                            //        mutableStateOf(
                            //            listOf("jpg", "webp").firstNotNullOfOrNull { extension ->
                            //                val file = File("$baseImagePath.$extension")
                            //                if (file.exists() && file.canRead()) {
                            //                    file.absolutePath
                            //                } else null
                            //            }
                            //        )
                            //    }
                            //
                            //    val imageSource = remember(imageExist) {
                            //        imageExist?.let { File(it) } ?: R.drawable.logo
                            //    }
                            //
                            //    val requestKey = remember(article.idArticle, indexColor, reloadKey) {
                            //        "${article.idArticle}_${if (indexColor == -1) "Unite" else indexColor}_$reloadKey"
                            //    }
                            //
                            //    Box(modifier = modifier.fillMaxWidth()) {
                            //        val painter = rememberAsyncImagePainter(
                            //            ImageRequest.Builder(context)
                            //                .data(imageSource)
                            //                .size(350,350)  // Use original size to maintain aspect ratio
                            //                .crossfade(true)
                            //                .setParameter("key", requestKey, memoryCacheKey = requestKey)
                            //                .build()
                            //        )
                            //
                            //        Image(
                            //            painter = painter,
                            //            contentDescription = "Article image ${article.idArticle} color ${indexColor + 1}",
                            //            modifier = Modifier.fillMaxWidth(),
                            //            contentScale = ContentScale.FillWidth
                            //        )
                            //    }
                            //} images avec index1 n exist pas que les lazy row heigh soi 150 et change a lazy column
                            .clip(MaterialTheme.shapes.medium)
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

                // Additional colors display
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

                    LazyRow(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(arrangedColors) { color ->
                            Box(
                                modifier = Modifier
                                    .size(250.dp)
                                    .clip(MaterialTheme.shapes.medium)
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ColorItem7(
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
            ImageDisplayerPC(
                modifier = Modifier.fillMaxSize(),
                article = article,
                indexColor = index,
                reloadKey = relodeTigger
            )

            color?.let { colorData ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = colorData.nameColore,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

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
                                        GlideImage(        //-->
                                            //Hi Claud,what i went from u to do is to
                                            //Find All TODOs and Fix Them 

                                            //TODO:
                                            // change le auneimage blanche san rien
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
