package P3_DisplayeProductInfosToSeller.Ui.Objects

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale")
@Composable
fun ColumnScope.Details(
    isDetailsVisible: Boolean,
    article: ArticlesBasesStatsTable
) {
    var isExpanded by remember { mutableStateOf(true) }

    // Calculate client profits
    val clientPrixVentUnite = article.clienPrixVentUnite ?: 0.0
    val clientPrixVentGros = clientPrixVentUnite * article.nmbrUnite
    val clientBenefice = clientPrixVentGros - article.monPrixVent
    val clientBeneficeUnite = clientBenefice / article.nmbrUnite

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
                    .padding(4.dp)
            ) {
                // Header section that's always visible
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "معلومات السعر",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = slideInVertically() + expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        // My Wholesale Section
                        PriceSection(
                            title = "سعر البيع الخاص بي",
                            icon = Icons.Default.Store,
                            items = listOf(
                                PriceItem(
                                    label = "السعر بالجملة",
                                    value = "${article.monPrixVent}",
                                    unite = "دج"
                                ),
                                PriceItem(
                                    label = "السعر للوحدة",
                                    value = String.format("%.2f", article.monPrixVent / article.nmbrUnite.toFloat()),
                                    unite = "دج"
                                )
                            )
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )

                        // Purchase Section
                        PriceSection(
                            title = "سعر الشراء",
                            icon = Icons.Default.ShoppingCart,
                            items = listOf(
                                PriceItem(
                                    label = "سعر الشراء للوحدة",
                                    value = String.format("%.2f", article.monPrixAchat / article.nmbrUnite.toFloat()),
                                    unite = "دج"
                                ),
                                PriceItem(
                                    label = "سعر الشراء بالجملة",
                                    value = String.format("%.2f", article.monPrixAchat),
                                    unite = "دج"
                                )
                            )
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )

                        // My Profit Section
                        PriceSection(
                            title = "أرباحي",
                            icon = Icons.Default.TrendingUp,
                            items = listOf(
                                PriceItem(
                                    label = "الربح بالجملة",
                                    value = String.format("%.2f", article.monPrixVent - article.monPrixAchat),
                                    unite = "دج"
                                ),
                                PriceItem(
                                    label = "الربح للوحدة",
                                    value = String.format(
                                        "%.2f",
                                        (article.monPrixVent - article.monPrixAchat) / article.nmbrUnite.toFloat()
                                    ),
                                    unite = "دج"
                                )
                            )
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )

                        // Client Price Section
                        PriceSection(
                            title = "سعر البيع للعميل",
                            icon = Icons.Default.Person,
                            items = listOf(
                                PriceItem(
                                    label = "سعر البيع للوحدة",
                                    value = String.format("%.2f", clientPrixVentUnite),
                                    unite = "دج"
                                ),
                                PriceItem(
                                    label = "سعر البيع بالجملة",
                                    value = String.format("%.2f", clientPrixVentGros),
                                    unite = "دج"
                                )
                            )
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )

                        // Client Profit Section
                        PriceSection(
                            title = "أرباح العميل",
                            icon = Icons.Default.PriceCheck,
                            items = listOf(
                                PriceItem(
                                    label = "ربح العميل بالجملة",
                                    value = String.format("%.2f", clientBenefice),
                                    unite = "دج"
                                ),
                                PriceItem(
                                    label = "ربح العميل للوحدة",
                                    value = String.format("%.2f", clientBeneficeUnite),
                                    unite = "دج"
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}

private data class PriceItem(
    val label: String,
    val value: String,
    val unite: String
)

@Composable
private fun PriceSection(
    title: String,
    icon: ImageVector,
    items: List<PriceItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.unite,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = item.value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
