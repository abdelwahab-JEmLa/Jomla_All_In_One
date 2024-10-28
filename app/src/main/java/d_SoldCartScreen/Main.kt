package d_SoldCartScreen
import a_RoomDB.ArticlesBasesStatsTabelle
import a_RoomDB.ClientsModel
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import b_StartupAppDisplayerOfNewArticles.ImageDisplayer
import b_StartupAppDisplayerOfNewArticles.StartUpNewArticlesViewModels

@Composable
fun SoldCartScreen(
    viewModel: StartUpNewArticlesViewModels,
    onNavigateToArticle: (Long) -> Unit,
    onConfirmOrder: () -> Unit,

    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val totalPrice = uiState.soldArticlesModel.sumOf { soldArticle ->
        soldArticle?.let { article ->
            uiState.articlesBasesStatTabelles
                .find { it.idArticle.toLong() == article.idArticle }
                ?.monPrixVent?.times(
                    article.color1SoldQuantity + article.color2SoldQuantity +
                            article.color3SoldQuantity + article.color4SoldQuantity
                ) ?: 0.0
        } ?: 0.0
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(uiState.soldArticlesModel.filterNotNull()) { soldArticle ->
                val baseArticle = uiState.articlesBasesStatTabelles
                    .find { it.idArticle.toLong() == soldArticle.idArticle }

                SoldArticleCard(
                    soldArticle = soldArticle,
                    colors = uiState.colorsArticlesTabelleModel,
                    baseArticle = baseArticle,
                    onDelete = { viewModel.deleteSoldArticle(soldArticle.vid) },
                    viewModel=viewModel
                )
            }
        }

        OrderSummaryCard(
            currentClient = uiState.clientsModel.find {
                it.idClientsSu == uiState.soldArticlesModel.firstOrNull()?.clientSoldToItId
            },
            itemCount = uiState.soldArticlesModel.sumOf { sale ->
                sale?.run {
                    color1SoldQuantity + color2SoldQuantity +
                            color3SoldQuantity + color4SoldQuantity
                } ?: 0
            },
            totalPrice = totalPrice,
            onConfirmOrder = onConfirmOrder
        )
    }
}

@Composable
private fun SoldArticleCard(
    soldArticle: SoldArticlesTabelle,
    colors: List<ColorsArticlesTabelle>,
    baseArticle: ArticlesBasesStatsTabelle?,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StartUpNewArticlesViewModels
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image display component
            baseArticle?.let { article ->
                ImageDisplayer(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.small),
                    article = article,
                    viewModel = viewModel,
                    reloadKey = Unit,
                    onClickToOpenWindos = { _, _ -> }
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                ArticleHeader(
                    name = soldArticle.nameArticle,
                    onDelete = onDelete
                )

                Spacer(modifier = Modifier.height(8.dp))

                ColorQuantityList(
                    soldArticle = soldArticle,
                    colors = colors
                )

                Spacer(modifier = Modifier.height(8.dp))

                ArticleTotal(
                    quantity = soldArticle.run {
                        color1SoldQuantity + color2SoldQuantity +
                                color3SoldQuantity + color4SoldQuantity
                    },
                    price = baseArticle?.monPrixVent
                )
            }
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
private fun ArticleHeader(
    name: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Button(onClick = onDelete) {
            Text("Remove")
        }
    }
}

@Composable
private fun ColorQuantityList(
    soldArticle: SoldArticlesTabelle,
    colors: List<ColorsArticlesTabelle>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Only show colors with quantities > 0
        if (soldArticle.color1SoldQuantity > 0) {
            ColorQuantityItem(
                colorName = colors.find { it.idColore == soldArticle.color1IdPicked }?.nameColore ?: "Unknown",
                quantity = soldArticle.color1SoldQuantity
            )
        }
        if (soldArticle.color2SoldQuantity > 0) {
            ColorQuantityItem(
                colorName = colors.find { it.idColore == soldArticle.color2IdPicked }?.nameColore ?: "Unknown",
                quantity = soldArticle.color2SoldQuantity
            )
        }
        if (soldArticle.color3SoldQuantity > 0) {
            ColorQuantityItem(
                colorName = colors.find { it.idColore == soldArticle.color3IdPicked }?.nameColore ?: "Unknown",
                quantity = soldArticle.color3SoldQuantity
            )
        }
        if (soldArticle.color4SoldQuantity > 0) {
            ColorQuantityItem(
                colorName = colors.find { it.idColore == soldArticle.color4IdPicked }?.nameColore ?: "Unknown",
                quantity = soldArticle.color4SoldQuantity
            )
        }
    }
}

@Composable
private fun ColorQuantityItem(
    colorName: String,
    quantity: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = colorName,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "x$quantity",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ArticleTotal(
    quantity: Int,
    price: Double?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Total Quantity: $quantity",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Price: $${String.format("%.2f", (price ?: 0.0) * quantity)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
