package P3_DisplayeProductInfosToSeller.Ui.Main

import a_RoomDB.ArticlesBasesStatsTable
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.Models.PriceRecord
import com.example.clientjetpack.Models.UiState
import com.example.clientjetpack.ViewModel.HeadViewModel

@SuppressLint("DefaultLocale")
@Composable
fun ColumnScope.Details(
    isDetailsVisible: Boolean,
    article: ArticlesBasesStatsTable,
    uiState: UiState,
    viewModel: HeadViewModel
) {
    var isExpanded by remember { mutableStateOf(true) }

    // Get current client ID from settings
    val currentClientId = uiState.appSettingsSaverModel
        .find { it.name == "clientBuyerNowId" }?.valueLong ?: 0

    // Get price history data
    val allTimeMaxPrice = viewModel.getMaxPrice(article.idArticle)
    val priceHistory = viewModel.getHistoryProductForClient(article.idArticle, currentClientId)

    AnimatedVisibility(
        visible = isDetailsVisible,
        enter = fadeIn() + expandVertically(),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Header
                DetailHeader(isExpanded)

                // Animated content
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = slideInVertically() + expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        // Price table
                        PriceDetailsTable(
                            article = article,
                            allTimeMaxPrice = allTimeMaxPrice,
                            priceHistory = priceHistory
                        )

                    }
                }
            }
        }
    }
}

@Composable
private fun DetailHeader(isExpanded: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "معلومات الأسعار والأرباح",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) "طي" else "توسيع"
        )
    }
}

@Composable
private fun PriceDetailsTable(
    article: ArticlesBasesStatsTable,
    allTimeMaxPrice: Double,
    priceHistory: List<PriceRecord>
) {
    val clientSoldPackage = article.clienPrixVentUnite * article.nmbrUnite
    val latestHistoryPrice = priceHistory.lastOrNull()?.price ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Headers row
        PriceGridRow(
            items = listOf(
                "التفاصيل",
                "السعر الأساسي",
                "السعر السابق",
                "أعلى سعر"
            ),
            isHeader = true
        )

        // Values rows
        // Row 1: Wholesale price
        PriceGridRow(
            items = listOf(
                "ب.الحزمة",
                "%.2f دج".format(article.monPrixVent),
                "%.2f دج".format(latestHistoryPrice),
                "%.2f دج".format(allTimeMaxPrice)
            )
        )

        // Row 2: Unit price
        PriceGridRow(
            items = listOf(
                "ب.الوحدة",
                "%.2f دج".format(article.monPrixVent / article.nmbrUnite),
                "%.2f دج".format(latestHistoryPrice / article.nmbrUnite),
                "%.2f دج".format(allTimeMaxPrice / article.nmbrUnite)
            )
        )

        // Row 3: Client profit
        PriceGridRow(
            items = listOf(
                "ر.العميل",
                "%.2f دج".format(clientSoldPackage - article.monPrixVent),
                "%.2f دج".format(clientSoldPackage - latestHistoryPrice),
                "%.2f دج".format(clientSoldPackage - allTimeMaxPrice)
            )
        )

        // Row 4: Package profit
        PriceGridRow(
            items = listOf(
                "ر.الحزمة",
                "%.2f دج".format(article.monPrixVent - article.monPrixAchat),
                "%.2f دج".format(latestHistoryPrice - article.monPrixAchat),
                "%.2f دج".format(allTimeMaxPrice - article.monPrixAchat)
            )
        )

        // Row 5: Client package price
        PriceGridRow(
            items = listOf(
                "سعر.ب.العميل",
                "%.2f دج".format(clientSoldPackage),
                "%.2f دج".format(clientSoldPackage),
                "%.2f دج".format(clientSoldPackage)
            )
        )
    }
}

@Composable
private fun PriceGridRow(
    items: List<String>,
    isHeader: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEachIndexed { index, text ->
            PriceCard(
                text = text,
                isHeader = isHeader,
                isHighlighted = index == 2,  // Previous price column
                modifier = Modifier.weight(1f),
                textAlignment = if (index == 0) Alignment.Start else Alignment.End
            )
        }
    }
}

@Composable
private fun PriceCard(
    text: String,
    isHeader: Boolean,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier,
    textAlignment: Alignment.Horizontal = Alignment.End
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = when {
                isHeader -> MaterialTheme.colorScheme.surfaceVariant
                isHighlighted -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isHeader) 4.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = if (isHeader) 12.dp else 8.dp,
                    horizontal = 8.dp
                ),
        ) {
            Text(
                text = text,
                style = if (isHeader) {
                    MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    MaterialTheme.typography.bodyMedium
                },
                textAlign = if (textAlignment == Alignment.Start) TextAlign.Start else TextAlign.End
            )
        }
    }
}
