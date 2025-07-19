package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Views

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun QuickInfoSection(
    modifier: Modifier,
    produit: ArticlesBasesStatsTable,
    updateProduct: (ArticlesBasesStatsTable) -> Unit,
    shouldHideQuickInfoCards: Boolean,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // All items in add_New single row with proper spacing
            val timeDifference = getTimeDifferenceInArabic(produit.prixAchatDernierTimeTempUpdate)
            val benefice = produit.prixVent - produit.prixAchat

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Bénéfice (only show if not zero) - Hide when filter is active
                if (benefice != 0.0 && !shouldHideQuickInfoCards) {
                    QuickInfoCard(
                        title = "Bénéfice",
                        value = "$benefice DA",
                        icon = if (benefice > 0) "📈" else "📉",
                        color = if (benefice > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Prix de Vente - Hide when filter is active
                if (!shouldHideQuickInfoCards) {
                    QuickInfoCard(
                        title = "Prix Vente",
                        value = "${produit.prixVent} DA",
                        icon = "💰",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (!shouldHideQuickInfoCards) {
                    // Combined Visibility and Last Update Card - Always show this one
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = (if (produit.cachePrixVent) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary).copy(
                            alpha = 0.1f
                        ),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {
                            updateProduct(produit.copy(cachePrixVent = !produit.cachePrixVent))
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = if (produit.cachePrixVent) "🔒" else "👁️",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "آخر تحديث: $timeDifference",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Start,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickInfoCard(
    modifier: Modifier,
    title: String,
    value: String,
    icon: String,
    color: Color,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.width(100.dp),
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
