package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.Components

import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.WeekInfo
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Locale

@Composable
fun WeekHeader(
    weekInfo: WeekInfo,
    viewModel: Windows__ViewModel
) {
    // Get all records for this specific week and check if all are paid
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

    // Calculate if all days are paid - initialize from actual data
    val areAllDaysPaid = weekRecords.isNotEmpty() && weekRecords.all { it.infosDeBase.paye }
    val allDaysPaid = remember { mutableStateOf(areAllDaysPaid) }

    // Get admin privileges status
    val isAbdelwahabLeGerant by viewModel.isAbdelwahabLeGerant.collectAsState()

    // Calculate total work time for the week
    val totalWeekMinutes = calculateTotalWeekWorkTime(weekInfo, viewModel)
    val totalHours = totalWeekMinutes / 60
    val remainingMinutes = totalWeekMinutes % 60
    val totalWeekTimeFormatted = "${totalHours}h ${remainingMinutes}m"

    // Calculate daily rate based on total week minutes
    val hourlyRate = 1200.0 / 8.0 / 60.0 // 1200 DA per day, 8 hours per day, 60 minutes per hour
    val totalWeekEarnings = hourlyRate * totalWeekMinutes

    // Calculate days worked (1 day = 8 hours)
    val daysWorked = totalWeekMinutes / (8.0 * 60.0)
    val daysWorkedFormatted = translateWorkDurationToArabic(daysWorked, totalWeekMinutes)

    // Animation de clignotement jaune
    val infiniteTransition = rememberInfiniteTransition(label = "card_animation")
    val borderColorAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border_blink_animation"
    )

    val orangeColor = Color(0xFFFF9800) // Orange color
    val yellowColor = Color(0xFFFFEB3B) // Yellow color for blinking border
    val greenColor = Color(0xFF4CAF50) // Green color for paid status

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .border(
                width = 4.dp, // Increased border thickness
                color = if (allDaysPaid.value) greenColor else yellowColor.copy(alpha = borderColorAlpha),
                shape = RoundedCornerShape(12.dp)
            ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (allDaysPaid.value) greenColor.copy(alpha = 0.7f) else orangeColor, // Changed color based on paid status
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Display week relative to current week
            val weekText = translateWeekTextToArabic(weekInfo)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weekText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.weight(1f))
                if (allDaysPaid.value) {
                    Text(
                        text = if (allDaysPaid.value) "تم" else "",
                        color = Color.White,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Icon(
                        imageVector = if (allDaysPaid.value) Icons.Default.Check else Icons.Default.Payment,
                        contentDescription = if (allDaysPaid.value) "تم " else "في انتظار الدفع",  // "Paid" or "Awaiting payment" in Arabic
                        tint = if (allDaysPaid.value) Color.White else Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Add toggle button for marking week as paid (only visible in admin mode)
                if (isAbdelwahabLeGerant) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        IconButton(
                            onClick = {
                                // Toggle the paid state for all days in this week
                                val newPaidState = !allDaysPaid.value
                                allDaysPaid.value = newPaidState
                                markAllDaysAsPaid(weekInfo, viewModel, allDaysPaid)
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (allDaysPaid.value) Icons.Default.Check else Icons.Default.Payment,
                                contentDescription = if (allDaysPaid.value) "تم الدفع" else "في انتظار الدفع",  // "Paid" or "Awaiting payment" in Arabic
                                tint = if (allDaysPaid.value) Color.White else Color.White.copy(
                                    alpha = 0.7f
                                ),
                                modifier = Modifier.size(24.dp)
                            )
                        }


                    }
                }
            }

            Text(
                text = "الأسبوع ${weekInfo.weekNumber}, ${weekInfo.year}",  // "Week" in Arabic
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                Text(
                    text = "مدة العمل الاجمالية: $daysWorkedFormatted",  // "Total work duration" in Arabic
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Blue,
                    modifier = Modifier.padding(4.dp)
                )
            }

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                // Show daily rate and total earnings
                Text(
                    text = "اليوم/1200 دينار == ${
                        String.format(
                            "%.2f",
                            totalWeekEarnings
                        )
                    } دينار",  // "Day/1200 DA" in Arabic
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

// Function to mark all days in the week as paid
fun markAllDaysAsPaid(
    weekInfo: WeekInfo,
    viewModel: Windows__ViewModel,
    paidStatus: MutableState<Boolean>
) {
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

    // Update each record's paid status based on the new state
    weekRecords.forEach { record ->
        record.infosDeBase.paye = paidStatus.value
        // Update the record in the repository
        viewModel.repository.updateOnPasseData(record)
    }
}

// Function to translate week text to Arabic
fun translateWeekTextToArabic(weekInfo: WeekInfo): String {
    return when {
        weekInfo.isCurrentWeek -> "هذا الأسبوع"  // "This week" in Arabic
        isLastWeek(weekInfo) -> "الأسبوع الماضي"  // "Last week" in Arabic
        else -> {
            val weekDifference = getWeekDifference(weekInfo)
            if (weekDifference > 0) {
                "منذ $weekDifference أسابيع"  // "N weeks ago" in Arabic
            } else {
                "في غضون ${-weekDifference} أسابيع"  // "In N weeks" in Arabic
            }
        }
    }
}

// Function to translate work duration to Arabic
fun translateWorkDurationToArabic(daysWorked: Double, totalMinutes: Int): String {
    return when {
        daysWorked == 0.0 -> "0 يوم"  // "0 days" in Arabic
        daysWorked < 1.0 -> {
            val hours = totalMinutes / 60
            "$hours ساعات"  // "N hours" in Arabic
        }

        daysWorked == 1.0 -> "1 يوم"  // "1 day" in Arabic
        daysWorked < 2.0 -> {
            "1 يوم و ${(daysWorked - 1.0) * 8} ساعات"  // "1 day and N hours" in Arabic
        }

        else -> {
            val fullDays = daysWorked.toInt()
            val remainingHours = ((daysWorked - fullDays) * 8).toInt()
            if (remainingHours > 0) {
                "$fullDays أيام و $remainingHours ساعات"  // "N days and M hours" in Arabic
            } else {
                "$fullDays أيام"  // "N days" in Arabic
            }
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
            val intervalMinutes = Z_CodePartageEntreApps.Model.K_TempTravailleRepository.calculateDurationMinutes(
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
