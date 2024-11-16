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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
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
    val clientSoldPackage =article.clienPrixVentUnite * article.nmbrUnite
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            //TODO :
            // fait que entre chaque une un divider

            TableHeader("التفاصيل")
            TableHeader("السعر الأساسي")

            TableHeader("السعر السابق")
            //TODO :
            // fait que les element de du ca soi on rouge
            TableHeader("أعلى سعر")
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        // Price rows
        val latestHistoryPrice = priceHistory.lastOrNull()?.price ?: 0.0

        // Wholesale price row
        PriceRow(
            label = "ب.الحزمة",
            basePrice = article.monPrixVent,
            maxPrice = allTimeMaxPrice,
            historyPrice = latestHistoryPrice
        )


        // Unit price row
        PriceRow(
            label = "ب.الوحدة",
            basePrice = article.monPrixVent / article.nmbrUnite,
            maxPrice = allTimeMaxPrice / article.nmbrUnite,
            historyPrice = latestHistoryPrice / article.nmbrUnite
        )
        // Client package profit row
        PriceRow(
            label = "ر.العميل",
            basePrice = clientSoldPackage - article.monPrixVent,
            maxPrice = clientSoldPackage -allTimeMaxPrice ,
            historyPrice = clientSoldPackage-latestHistoryPrice
        )
        // Regular profit row
        PriceRow(
            label = "ر.الحزمة",
            basePrice = article.monPrixVent - article.monPrixAchat,
            maxPrice = allTimeMaxPrice - article.monPrixAchat,
            historyPrice = latestHistoryPrice - article.monPrixAchat
        )
        // Client package price row
        PriceRow(
            label = "سعر.ب.العميل",
            basePrice = clientSoldPackage,
            maxPrice = allTimeMaxPrice,
            historyPrice = latestHistoryPrice
        )
    }
}

@Composable
private fun TableHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(4.dp)
    )
}

@Composable
private fun PriceRow(
    label: String,
    basePrice: Double,
    maxPrice: Double,
    historyPrice: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = "%.2f دج".format(basePrice))
        Text(text = "%.2f دج".format(maxPrice))
        Text(text = "%.2f دج".format(historyPrice))

    }
}








