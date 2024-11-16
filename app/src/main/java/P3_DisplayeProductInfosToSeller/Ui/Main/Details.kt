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

    val currentClientId = uiState.appSettingsSaverModel
        .find { it.name == "clientBuyerNowId" }?.valueLong ?: 0

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
                DetailHeader(isExpanded)

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
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
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
                "س.الأعلى",
                "س.أساس",
                "س.السابق",
                "التفاصيل"
            ),
            isHeader = true
        )

        // Price rows with data
        listOf(
            RowData("ب.الحزمة", article.monPrixVent, latestHistoryPrice, allTimeMaxPrice),
            RowData("ب.الوحدة", article.monPrixVent / article.nmbrUnite, latestHistoryPrice / article.nmbrUnite, allTimeMaxPrice / article.nmbrUnite),
            RowData("ر.العميل", clientSoldPackage - article.monPrixVent, clientSoldPackage - latestHistoryPrice, clientSoldPackage - allTimeMaxPrice),
            RowData("ر.خ.الحزمة", article.monPrixVent - article.monPrixAchat, latestHistoryPrice - article.monPrixAchat, allTimeMaxPrice - article.monPrixAchat),
            RowData("س.ب.عم", clientSoldPackage, clientSoldPackage, clientSoldPackage)
        ).forEach { rowData ->
            PriceGridRow(
                items = listOf(
                    rowData.label,
                    "%.2f ".format(rowData.baseValue),
                    "%.2f ".format(rowData.previousValue),
                    "%.2f ".format(rowData.maxValue)
                )
            )
        }
    }
}

private data class RowData(
    val label: String,
    val baseValue: Double,
    val previousValue: Double,
    val maxValue: Double
)

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
                isFirstColumn = index == 0,
                isHighlighted = index == 2,
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
    isFirstColumn: Boolean,
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
                isFirstColumn -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                .padding(vertical = 4.dp, horizontal = 4.dp),
        ) {
            Text(
                text = text,
                style = when {
                    isHeader -> MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    isHighlighted -> MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error)
                    else -> MaterialTheme.typography.bodyMedium
                },
                textAlign = if (textAlignment == Alignment.Start) TextAlign.Start else TextAlign.End,
                modifier = Modifier
            )
        }
    }
}
