package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.Ui

import Z_CodePartageEntreApps.Model.K_TempTravaille
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.WeekInfo
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Locale

@Composable
fun WeekHeader(
    weekInfo: WeekInfo,
    viewModel: Windows__ViewModel
) {
    // Calculate total work time for the week
    val totalWeekMinutes = calculateTotalWeekWorkTime(weekInfo, viewModel)
    val totalHours = totalWeekMinutes / 60
    val remainingMinutes = totalWeekMinutes % 60
    val totalWeekTimeFormatted = "${totalHours}h ${remainingMinutes}m"

    // Animation de clignotement orange
    val infiniteTransition = rememberInfiniteTransition(label = "card_animation")
    val borderColorAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border_blink_animation"
    )

    val orangeColor = Color(0xFFF44336) // Couleur orange vive

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .border(
                width = 2.dp,
                color = orangeColor.copy(alpha = borderColorAlpha),
                shape = RoundedCornerShape(12.dp)
            ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Display week relative to current week
            val weekText = when {
                weekInfo.isCurrentWeek -> "Cette semaine"
                isLastWeek(weekInfo) -> "Semaine dernière"
                else -> {
                    val weekDifference = getWeekDifference(weekInfo)
                    if (weekDifference > 0) {
                        "Il y a $weekDifference semaines"
                    } else {
                        "Dans ${-weekDifference} semaines"
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weekText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.weight(1f))

                // Display total work time for the week
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = orangeColor.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = "Total: $totalWeekTimeFormatted",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.DarkGray
                    )
                }
            }

            Text(
                text = "Semaine ${weekInfo.weekNumber}, ${weekInfo.year}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Function to calculate total work time for the week
fun calculateTotalWeekWorkTime(weekInfo: WeekInfo, viewModel: Windows__ViewModel): Int {
    var totalMinutes = 0

    // Get all records for the specific week and year
    val weekRecords = viewModel.dateList.filter { record ->
        val dateString = record.infosDeBase.dateInString
        val parts = dateString.split("/")
        if (parts.size == 3) {
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1 // Month is 0-based in Calendar
            val day = parts[2].toInt()

            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)

            // Check if this record falls within the specified week and year
            calendar.get(Calendar.YEAR) == weekInfo.year && calendar.get(Calendar.WEEK_OF_YEAR) == weekInfo.weekNumber
        } else {
            false
        }
    }

    // Calculate total minutes from all intervals in all days of the week
    weekRecords.forEach { record ->
        record.intervalesDeTravaille.forEach { interval ->
            val intervalMinutes = K_TempTravaille.calculateDurationMinutes(
                interval.tempDepart,
                interval.temparrete
            )
            if (intervalMinutes > 0) {
                totalMinutes += intervalMinutes
            }
        }
    }

    return totalMinutes
}

fun isLastWeek(weekInfo: WeekInfo): Boolean {
    val calendar = Calendar.getInstance()
    val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
    val currentYear = calendar.get(Calendar.YEAR)

    if (currentWeek == 1 && weekInfo.weekNumber > 50 && weekInfo.year == currentYear - 1) {
        return true
    }

    return weekInfo.year == currentYear && weekInfo.weekNumber == currentWeek - 1
}

fun getWeekDifference(weekInfo: WeekInfo): Int {
    val calendar = Calendar.getInstance()
    val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
    val currentYear = calendar.get(Calendar.YEAR)

    if (weekInfo.year == currentYear) {
        return currentWeek - weekInfo.weekNumber
    }

    val yearDiff = currentYear - weekInfo.year
    if (yearDiff == 1) {
        val weeksInLastYear = Calendar.getInstance(Locale.getDefault()).apply {
            set(Calendar.YEAR, currentYear - 1)
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 31)
        }.get(Calendar.WEEK_OF_YEAR)

        return currentWeek + (weeksInLastYear - weekInfo.weekNumber)
    } else if (yearDiff == -1) {
        val weeksInCurrentYear = Calendar.getInstance(Locale.getDefault()).apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 31)
        }.get(Calendar.WEEK_OF_YEAR)

        return -(weekInfo.weekNumber + (weeksInCurrentYear - currentWeek))
    }

    return currentWeek - weekInfo.weekNumber + (yearDiff * 52)
}
