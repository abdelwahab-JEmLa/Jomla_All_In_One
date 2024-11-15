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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clientjetpack.Models.UiState

@SuppressLint("DefaultLocale")
@Composable
fun ColumnScope.Details(
    isDetailsVisible: Boolean,
    article: ArticlesBasesStatsTable,
    uiState: UiState,
    viewModel: ViewModel
) {
    var isExpanded by remember { mutableStateOf(true) }

    // Calculate client profits
    val clientPrixVentUnite = article.clienPrixVentUnite ?: 0.0
    val clientPrixVentGros = clientPrixVentUnite * article.nmbrUnite
    val clientBenefice = clientPrixVentGros - article.monPrixVent
    val clientBeneficeUnite = clientBenefice / article.nmbrUnite
    val maxPrice = viewModel.article.getMaxPrice(article.id)

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
                // Header with price info title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "معلومات السعر",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        // Display max sale price if available
                        // TODO : fati que ici de change l affiche a un tablaeu
                        // TODO : de droit a gauche la premier column ce nom
                        // TODO : "Prix Base & ces calcule " il contien au bas
                        //ces calcule normale
                        // TODO : la 2 colone "Max Ancien prix & ces calcule"
                        // TODO : ca contien le max prix de l article et au base cchaque et calcule comme
                        // TODO :                                     PriceItem("س.وحدة", "%.2f".format(article.monPrixVent / article.nmbrUnite), "دج")
                        // TODO : ici max prix / nmbr ute

                        // TODO :et l autre comme  clientBeneficeUnite= clientPrixVentGros -  max prix  

                        //TODO :
                        //  la 3 colone "Ancien prix de Client & ces calcule" 
                        // comme lautre 
                        
                        
                        // TODO : ..ect
                         uiState.maxSalePrice?.let { maxPrice -
                                 // TODO : Unresolved reference: maxSalePrice
                            Text(
                                text = "أكبر سعر:  maxPrice$ دج",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
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
                        val sections = listOf(
                            PriceSection(
                                title = "س.البيع",
                                icon = Icons.Default.Store,
                                items = listOf(
                                    PriceItem("س.جملة", "${article.monPrixVent}", "دج"),
                                    PriceItem("س.وحدة", "%.2f".format(article.monPrixVent / article.nmbrUnite), "دج")
                                )
                            ),
                            PriceSection(
                                title = "س.شراء",
                                icon = Icons.Default.ShoppingCart,
                                items = listOf(
                                    PriceItem("س.وحدة", "%.2f".format(article.monPrixAchat / article.nmbrUnite), "دج"),
                                    PriceItem("س.جملة", "%.2f".format(article.monPrixAchat), "دج")
                                )
                            ),
                            PriceSection(
                                title = "ربحي",
                                icon = Icons.AutoMirrored.Filled.TrendingUp,
                                items = listOf(
                                    PriceItem("ر.جملة", "%.2f".format(article.monPrixVent - article.monPrixAchat), "دج"),
                                    PriceItem("ر.وحدة", "%.2f".format((article.monPrixVent - article.monPrixAchat) / article.nmbrUnite), "دج")
                                )
                            ),
                            PriceSection(
                                title = "س.عميل",
                                icon = Icons.Default.Person,
                                items = listOf(
                                    PriceItem("س.وحدة", "%.2f".format(clientPrixVentUnite), "دج"),
                                    PriceItem("س.جملة", "%.2f".format(clientPrixVentGros), "دج")
                                )
                            ),
                            PriceSection(
                                title = "ر.عميل",
                                icon = Icons.Default.PriceCheck,
                                items = listOf(
                                    PriceItem("ر.جملة", "%.2f".format(clientBenefice), "دج"),
                                    PriceItem("ر.وحدة", "%.2f".format(clientBeneficeUnite), "دج")
                                )
                            )
                        )

                        sections.forEachIndexed { index, section ->
                            if (index > 0) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                )
                            }
                            PriceSectionContent(section)
                        }
                    }
                }
            }
        }
    }
}

private data class PriceSection(
    val title: String,
    val icon: ImageVector,
    val items: List<PriceItem>
)

private data class PriceItem(
    val label: String,
    val value: String,
    val unite: String
)

@Composable
private fun PriceSectionContent(
    section: PriceSection,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = section.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        section.items.forEach { item ->
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
