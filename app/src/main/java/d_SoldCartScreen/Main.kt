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

// Create SoldCartScreen.kt
@Composable
fun SoldCartScreen(
    viewModel: StartUpNewArticlesViewModels,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(uiState.soldArticlesModel.filterNotNull()) { soldArticle ->
            SoldArticleCard(
                soldArticle = soldArticle,
                colors = uiState.colorsArticlesTabelleModel,
                onDelete = { viewModel.deleteSoldArticle(soldArticle.vid) }
            )
        }
    }
}

@Composable
private fun SoldArticleCard(
    soldArticle: SoldArticlesTabelle,
    colors: List<ColorsArticlesTabelle>,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = soldArticle.nameArticle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Display color quantities
            listOf(
                Triple(soldArticle.color1IdPicked, soldArticle.color1SoldQuantity, "Color 1"),
                Triple(soldArticle.color2IdPicked, soldArticle.color2SoldQuantity, "Color 2"),
                Triple(soldArticle.color3IdPicked, soldArticle.color3SoldQuantity, "Color 3"),
                Triple(soldArticle.color4IdPicked, soldArticle.color4SoldQuantity, "Color 4")
            ).forEach { (colorId, quantity, label) ->
                if (colorId != 0L && quantity > 0) {
                    val colorName = colors.find { it.idColore == colorId }?.nameColore ?: "Unknown"
                    Text(
                        text = "$label: $colorName - Quantity: $quantity",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total Quantity: ${
                    soldArticle.run {
                        color1SoldQuantity + color2SoldQuantity + color3SoldQuantity + color4SoldQuantity
                    }
                }",
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }
    }
}

// Updated SoldCartScreen to include navigation callback
@Composable
fun SoldCartScreen(
    viewModel: StartUpNewArticlesViewModels,
    onNavigateToArticle: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
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

            // Color quantities section
            listOf(
                Triple(soldArticle.color1IdPicked, soldArticle.color1SoldQuantity, "Color 1"),
                Triple(soldArticle.color2IdPicked, soldArticle.color2SoldQuantity, "Color 2"),
                Triple(soldArticle.color3IdPicked, soldArticle.color3SoldQuantity, "Color 3"),
                Triple(soldArticle.color4IdPicked, soldArticle.color4SoldQuantity, "Color 4")
            ).forEach { (colorId, quantity, label) ->
                if (colorId != 0L && quantity > 0) {
                    val colorName = colors.find { it.idColore == colorId }?.nameColore ?: "Unknown"
                    Text(
                        text = "$label: $colorName - Quantity: $quantity",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total Quantity: ${
                    soldArticle.run {
                        color1SoldQuantity + color2SoldQuantity + color3SoldQuantity + color4SoldQuantity
                    }
                }",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
