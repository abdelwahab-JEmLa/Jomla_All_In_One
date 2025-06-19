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
            // All items in a single row with proper spacing
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

    // Get calendar dates to compare actual days, not just 24-hour periods
    val currentCalendar = java.util.Calendar.getInstance()
    val timestampCalendar = java.util.Calendar.getInstance().apply {
        timeInMillis = timestamp
    }

    val currentYear = currentCalendar.get(java.util.Calendar.YEAR)
    val currentDayOfYear = currentCalendar.get(java.util.Calendar.DAY_OF_YEAR)
    val timestampYear = timestampCalendar.get(java.util.Calendar.YEAR)
    val timestampDayOfYear = timestampCalendar.get(java.util.Calendar.DAY_OF_YEAR)

    val dayDifference = if (currentYear == timestampYear) {
        currentDayOfYear - timestampDayOfYear
    } else {
        // For different years, calculate the actual day difference
        val diffInDays = (diffInMillis / (24 * 60 * 60 * 1000)).toInt()
        diffInDays
    }

    return when {
        // Same day
        dayDifference == 0 -> "اليوم" // Today

        // Yesterday
        dayDifference == 1 -> "أمس" // Yesterday

        // More than 1 day ago - use the original calculation logic
        dayDifference >= 2 -> {
            val diffInSeconds = diffInMillis / 1000
            val diffInMinutes = diffInSeconds / 60
            val diffInHours = diffInMinutes / 60
            val diffInDays = diffInHours / 24
            val diffInWeeks = diffInDays / 7
            val diffInMonths = diffInDays / 30
            val diffInYears = diffInDays / 365

            when {
                diffInDays < 7 -> {
                    val days = diffInDays.toInt()
                    when {
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

        else -> "غير محدد"
    }
}
