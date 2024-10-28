package d_SoldCartScreen
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import b_StartupAppDisplayerOfNewArticles.StartUpNewArticlesViewModels

@Composable
fun SoldCartScreen(
    viewModel: StartUpNewArticlesViewModels,
    onNavigateToArticle: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
        ) {
            items(uiState.soldArticlesModel.filterNotNull()) { soldArticle ->
                SoldArticleCard(
                    soldArticle = soldArticle,
                    colors = uiState.colorsArticlesTabelleModel,
                    onDelete = { viewModel.deleteSoldArticle(soldArticle.vid) },
                    onArticleClick = { onNavigateToArticle(soldArticle.idArticle) }
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                val currentClient = uiState.clientsModel.find {
                    it.idClientsSu == uiState.soldArticlesModel.firstOrNull()?.clientSoldToItId
                }

                Text(
                    text = "Client: ${currentClient?.nomClientsSu ?: "Unknown"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                val total = uiState.soldArticlesModel.sumOf { sale ->
                    sale?.run {
                        color1SoldQuantity + color2SoldQuantity +
                                color3SoldQuantity + color4SoldQuantity
                    } ?: 0
                }

                Text(
                    text = "Total Items: $total",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

// Fixed SoldArticleCard color display
@Composable
private fun ColorQuantityItem(
    colorId: Long,
    quantity: Int,
    colors: List<ColorsArticlesTabelle>
) {
    if (colorId != 0L && quantity > 0) {
        val colorName = colors.find { it.idColore == colorId }?.nameColore ?: "Unknown"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = colorName,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SoldArticleCard(
    soldArticle: SoldArticlesTabelle,
    colors: List<ColorsArticlesTabelle>,
    onDelete: () -> Unit,
    onArticleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onArticleClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = soldArticle.nameArticle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ColorQuantityItem(soldArticle.color1IdPicked, soldArticle.color1SoldQuantity, colors)
            ColorQuantityItem(soldArticle.color2IdPicked, soldArticle.color2SoldQuantity, colors)
            ColorQuantityItem(soldArticle.color3IdPicked, soldArticle.color3SoldQuantity, colors)
            ColorQuantityItem(soldArticle.color4IdPicked, soldArticle.color4SoldQuantity, colors)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total: ${
                    soldArticle.run {
                        color1SoldQuantity + color2SoldQuantity +
                                color3SoldQuantity + color4SoldQuantity
                    }
                }",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
