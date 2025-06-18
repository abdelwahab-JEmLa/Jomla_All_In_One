package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
@Composable
fun QuickInfoSection(
    produit: ArticlesBasesStatsTable,
    updateProduct: (ArticlesBasesStatsTable) -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Prix de Vente
                QuickInfoCard(
                    title = "Prix Vente",
                    value = "${produit.prixVent} DA",
                    icon = "💰",
                    color = MaterialTheme.colorScheme.primary
                )

                // Bénéfice
                val benefice = produit.prixVent - produit.prixAchat
                if (benefice != 0.0) {
                    QuickInfoCard(
                        title = "Bénéfice",
                        value = "$benefice DA",
                        icon = if (benefice > 0) "📈" else "📉",
                        color = if (benefice > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }

            // Time since last price update - Always show section
            Spacer(modifier = Modifier.height(12.dp))

            val timeDifference = getTimeDifferenceInArabic(produit.prixAchatDernierTimeTempUpdate)

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    // Toggle cachePrixVent on click
                    updateProduct(produit.copy(cachePrixVent = !produit.cachePrixVent))
                }
            ) {
                Text(
                    text = "آخر تحديث للسعر: $timeDifference",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun QuickInfoCard(
    title: String,
    value: String,
    icon: String,
    color: androidx.compose.ui.graphics.Color
) {
    Surface(
        modifier = Modifier.width(100.dp),
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
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
/**
 * Calculates time difference from timestamp and returns formatted Arabic string
 * Example: "قبل أسبوع و 3 أيام" (a week and 3 days ago)
 * Returns "غير محدد" if timestamp is null, 0, or undefined
 */
fun getTimeDifferenceInArabic(timestamp: Long): String {
    // Handle null or undefined timestamp (0 or negative values)
    if (timestamp <= 0) {
        return "غير محدد" // Undefined
    }

    val currentTime = System.currentTimeMillis()
    val diffInMillis = currentTime - timestamp

    if (diffInMillis < 0) return "الآن" // Now (if timestamp is in future)

    val diffInSeconds = diffInMillis / 1000
    val diffInMinutes = diffInSeconds / 60
    val diffInHours = diffInMinutes / 60
    val diffInDays = diffInHours / 24
    val diffInWeeks = diffInDays / 7
    val diffInMonths = diffInDays / 30
    val diffInYears = diffInDays / 365

    return when {
        diffInDays < 7 -> {
            val days = diffInDays.toInt()
            when {
                days == 1 -> "قبل يوم"
                days == 2 -> "قبل يومين"
                days <= 10 -> "قبل $days أيام"
                else -> "قبل $days يوم"
            }
        }
        diffInWeeks < 4 -> {
            val weeks = diffInWeeks.toInt()
            val remainingDays = (diffInDays % 7).toInt()

            val weeksText = when {
                weeks == 1 -> "أسبوع"
                weeks == 2 -> "أسبوعين"
                weeks <= 10 -> "$weeks أسابيع"
                else -> "$weeks أسبوع"
            }

            val daysText = when {
                remainingDays == 0 -> ""
                remainingDays == 1 -> " و يوم"
                remainingDays == 2 -> " و يومين"
                remainingDays <= 10 -> " و $remainingDays أيام"
                else -> " و $remainingDays يوم"
            }

            "قبل $weeksText$daysText"
        }
        diffInMonths < 12 -> {
            val months = diffInMonths.toInt()
            val remainingDays = (diffInDays % 30).toInt()

            val monthsText = when {
                months == 1 -> "شهر"
                months == 2 -> "شهرين"
                months <= 10 -> "$months أشهر"
                else -> "$months شهر"
            }

            val daysText = when {
                remainingDays == 0 -> ""
                remainingDays == 1 -> " و يوم"
                remainingDays == 2 -> " و يومين"
                remainingDays <= 10 -> " و $remainingDays أيام"
                else -> " و $remainingDays يوم"
            }

            "قبل $monthsText$daysText"
        }
        else -> {
            val years = diffInYears.toInt()
            val remainingMonths = ((diffInDays % 365) / 30).toInt()

            val yearsText = when {
                years == 1 -> "سنة"
                years == 2 -> "سنتين"
                years <= 10 -> "$years سنوات"
                else -> "$years سنة"
            }

            val monthsText = when {
                remainingMonths == 0 -> ""
                remainingMonths == 1 -> " و شهر"
                remainingMonths == 2 -> " و شهرين"
                remainingMonths <= 10 -> " و $remainingMonths أشهر"
                else -> " و $remainingMonths شهر"
            }

            "قبل $yearsText$monthsText"
        }
    }
}
