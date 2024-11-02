package d_SoldCartScreen

import a_RoomDB.ArticlesBasesStatsTabelle
import a_RoomDB.ClientsModel
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import b_StartupAppDisplayerOfNewArticles.StartUpNewArticlesViewModels
import b_StartupAppDisplayerOfNewArticles.UiState
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.clientjetpack.R
import java.io.File

@Composable
fun SoldCartScreen(
    viewModel: StartUpNewArticlesViewModels,
    onConfirmOrder: () -> Unit,
    modifier: Modifier = Modifier,
    clientBuyerNow: ClientsModel? = null
    , onOpenArticleStats: (ArticlesBasesStatsTabelle, Int) -> Unit, uiState: UiState
) {

    // Filter articles by clientBuyerNow.id
    val filteredSoldArticles = uiState.soldArticlesModel.filterNotNull().filter { soldArticle ->
        if (clientBuyerNow != null) {
            soldArticle.clientSoldToItId == clientBuyerNow.idClientsSu
        } else {
            false
        }
    }

    val totalPrice = filteredSoldArticles.sumOf { soldArticle ->
        uiState.articlesBasesStatTabelles
            .find { it.idArticle.toLong() == soldArticle.idArticle }
            ?.monPrixVent?.times(
                soldArticle.color1SoldQuantity + soldArticle.color2SoldQuantity +
                        soldArticle.color3SoldQuantity + soldArticle.color4SoldQuantity
            ) ?: 0.0
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(filteredSoldArticles) { soldArticle ->
                val baseArticle = uiState.articlesBasesStatTabelles
                    .find { it.idArticle.toLong() == soldArticle.idArticle }

                SoldArticleCard(
                    soldArticle = soldArticle,
                    colors = uiState.colorsArticlesTabelleModel,
                    baseArticle = baseArticle,
                    onDeleteArticle = { viewModel.deleteSoldArticle(soldArticle.vid) },
                    viewModel = viewModel,
                    onOpenArticleStats=onOpenArticleStats,
                    uiState =uiState
                )
            }
        }

        OrderSummaryCard(
            currentClient = clientBuyerNow,
            itemCount = filteredSoldArticles.sumOf { sale ->
                sale.color1SoldQuantity + sale.color2SoldQuantity +
                        sale.color3SoldQuantity + sale.color4SoldQuantity
            },
            totalPrice = totalPrice,
            onConfirmOrder = onConfirmOrder
        )
    }
}

@Composable
fun SoldArticleCard(
    soldArticle: SoldArticlesTabelle,
    colors: List<ColorsArticlesTabelle>,
    baseArticle: ArticlesBasesStatsTabelle?,
    onDeleteArticle: () -> Unit,
    viewModel: StartUpNewArticlesViewModels,
    onOpenArticleStats: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier
) {
    val totalQuantity = soldArticle.run {
        color1SoldQuantity + color2SoldQuantity +
                color3SoldQuantity + color4SoldQuantity
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = baseArticle?.nomArticleFinale ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
                if (totalQuantity == 0) {
                    IconButton(
                        onClick = onDeleteArticle,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.error,
                                shape = CircleShape
                            )
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete article",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Row {
                Box(
                    modifier = Modifier.weight(0.7f)
                ) {
                    LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        // Color 1
                        if (soldArticle.color1SoldQuantity > 0) {
                            item {
                                ColorItemWithQuantity(
                                    article = baseArticle!!,
                                    colorIndex = 0,
                                    quantity = soldArticle.color1SoldQuantity,
                                    onDelete = { viewModel.resetColorSelection(0) },
                                    viewModel = viewModel,
                                    onOpenArticleStats = onOpenArticleStats,
                                    uiState = uiState,
                                    colors = colors
                                )
                            }
                        }
                        // Color 2
                        if (soldArticle.color2SoldQuantity > 0) {
                            item {
                                ColorItemWithQuantity(
                                    article = baseArticle!!,
                                    colorIndex = 1,
                                    quantity = soldArticle.color2SoldQuantity,
                                    onDelete = { viewModel.resetColorSelection(1) },
                                    viewModel = viewModel,
                                    onOpenArticleStats = onOpenArticleStats,
                                    uiState = uiState,
                                    colors = colors
                                )
                            }
                        }
                        // Color 3
                        if (soldArticle.color3SoldQuantity > 0) {
                            item {
                                ColorItemWithQuantity(
                                    article = baseArticle!!,
                                    colorIndex = 2,
                                    quantity = soldArticle.color3SoldQuantity,
                                    onDelete = { viewModel.resetColorSelection(2) },
                                    viewModel = viewModel,
                                    onOpenArticleStats = onOpenArticleStats,
                                    uiState = uiState,
                                    colors = colors
                                )
                            }
                        }
                        // Color 4
                        if (soldArticle.color4SoldQuantity > 0) {
                            item {
                                ColorItemWithQuantity(
                                    article = baseArticle!!,
                                    colorIndex = 3,
                                    quantity = soldArticle.color4SoldQuantity,
                                    onDelete = { viewModel.resetColorSelection(3) },
                                    viewModel = viewModel,
                                    onOpenArticleStats = onOpenArticleStats,
                                    uiState = uiState,
                                    colors = colors
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Quantity: $totalQuantity × ${baseArticle?.monPrixVent ?: 0.0}Da",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Subtotal: ${(baseArticle?.monPrixVent ?: 0.0) * totalQuantity} Da",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorItemWithQuantity(
    article: ArticlesBasesStatsTabelle,
    colorIndex: Int,
    quantity: Int,
    onDelete: () -> Unit,
    viewModel: StartUpNewArticlesViewModels,
    onOpenArticleStats: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState,
    colors: List<ColorsArticlesTabelle>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(150.dp)
            .height(200.dp)
    ) {
        // Delete Button
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .zIndex(1f)
                .background(
                    color = MaterialTheme.colorScheme.error,
                    shape = CircleShape
                )
                .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete color",
                tint = Color.White
            )
        }

        // Image with Quantity Overlay
        ImageDisplayer(
            modifier = Modifier.fillMaxSize(),
            article = article,
            viewModel = viewModel,
            indexColor = colorIndex,
            onClickToOpenWindos = onOpenArticleStats,
            uiState = uiState
        )

        // Color info overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                )
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Get color name
            val colorId = when (colorIndex) {
                0 -> article.idcolor1
                1 -> article.idcolor2
                2 -> article.idcolor3
                3 -> article.idcolor4
                else -> 0L
            }
            val colorName = colors.find { it.idColore == colorId }?.nameColore ?: ""

            Text(
                text = colorName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "×$quantity",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
@Composable
private fun OrderSummaryCard(
    currentClient: ClientsModel?,
    itemCount: Int,
    totalPrice: Double,
    onConfirmOrder: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Client: ${currentClient?.nomClientsSu ?: "Unknown"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Total Items: $itemCount",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Total Price: $${String.format("%.2f", totalPrice)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onConfirmOrder,
                modifier = Modifier.fillMaxWidth(),
                enabled = currentClient != null && itemCount > 0
            ) {
                Text("Confirm Order")
            }
        }
    }
}

@Composable
fun ImageDisplayer(
    modifier: Modifier = Modifier,
    article: ArticlesBasesStatsTabelle,
    viewModel: StartUpNewArticlesViewModels,
    indexColor: Int = 0,
    reloadKey: Any = Unit,
    onClickToOpenWindos: (ArticlesBasesStatsTabelle, Int) -> Unit,
    uiState: UiState
) {
    val context = LocalContext.current
    val viewModelImagesPath = viewModel.viewModelImagesPath

    val baseImagePath = remember(viewModelImagesPath, article.idArticle, indexColor) {
        File(viewModelImagesPath, "${article.idArticle}_${if (indexColor == -1) "Unite" else (indexColor + 1)}")
            .absolutePath
    }

    val imageExist by remember(baseImagePath, reloadKey) {
        mutableStateOf(
            listOf("jpg", "webp").firstNotNullOfOrNull { extension ->
                val file = File("$baseImagePath.$extension")
                if (file.exists() && file.canRead()) {
                    file.absolutePath
                } else null
            }
        )
    }

    val imageSource = remember(imageExist) {
        imageExist?.let { File(it) } ?: R.drawable.baked_goods_1
    }

    val requestKey = remember(article.idArticle, indexColor, reloadKey) {
        "${article.idArticle}_${if (indexColor == -1) "Unite" else indexColor}_$reloadKey"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClickToOpenWindos(article, indexColor) }
    ) {
        // Background image with reduced opacity when no actual image exists
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context)
                .data(imageSource)
                .size(Size.ORIGINAL)
                .crossfade(true)
                .setParameter("key", requestKey, memoryCacheKey = requestKey)
                .build()
        )

        Image(
            painter = painter,
            contentDescription = "Article image ${article.idArticle} color ${indexColor + 1}",
            modifier = Modifier
                .fillMaxWidth()
                .then(if (imageExist == null) Modifier.alpha(0.7f) else Modifier),
            contentScale = ContentScale.FillWidth
        )
    }
}
