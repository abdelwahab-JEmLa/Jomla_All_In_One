package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.WeekInfo
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravaille
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.Utilisateur
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
import org.koin.compose.koinInject
import java.util.Calendar
import java.util.Locale
@Composable
fun WeekHeader(
    weekInfo: WeekInfo,
    viewModel: RecordingViewModel,
    weekRecords: List<K_TempTravaille>,
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    // Get current user and admin status
    val currentUser by focusedValuesGetter.utilisateurFocused.collectAsState()
    val isAbdelwahabLeGerant by viewModel.isAbdelwahabLeGerant.collectAsState()

    // Calculate total work time PER VENDOR using the passed filtered records
    val (totalMinutesAbdelmoumen, totalMinutesWalid) = calculateTotalWeekWorkTimePerVendor(weekRecords)

    // Calculate total for BOTH vendors
    val totalWeekMinutes = totalMinutesAbdelmoumen + totalMinutesWalid

    // Debug logging
    println("=== WeekHeader Debug ===")
    println("Week ${weekInfo.weekNumber}, ${weekInfo.year}")
    println("Current User: $currentUser, Is Admin: $isAbdelwahabLeGerant")
    println("Abdelmoumen: $totalMinutesAbdelmoumen min")
    println("Walid: $totalMinutesWalid min")
    println("Total: $totalWeekMinutes min")
    println("========================")

    // Calculate earnings per vendor
    val hourlyRate = 1200.0 / 8.0 / 60.0
    val earningsAbdelmoumen = hourlyRate * totalMinutesAbdelmoumen
    val earningsWalid = hourlyRate * totalMinutesWalid
    val totalWeekEarnings = earningsAbdelmoumen + earningsWalid

    // Format time for each vendor
    val hoursAbdelmoumen = totalMinutesAbdelmoumen / 60
    val minutesAbdelmoumen = totalMinutesAbdelmoumen % 60
    val timeAbdelmoumen = "${hoursAbdelmoumen}h ${minutesAbdelmoumen}m"

    val hoursWalid = totalMinutesWalid / 60
    val minutesWalid = totalMinutesWalid % 60
    val timeWalid = "${hoursWalid}h ${minutesWalid}m"

    val daysAbdelmoumen = totalMinutesAbdelmoumen / (8.0 * 60.0)
    val daysWalid = totalMinutesWalid / (8.0 * 60.0)
    val daysAbdelmoumenFormatted = translateWorkDurationToArabic(daysAbdelmoumen, totalMinutesAbdelmoumen)
    val daysWalidFormatted = translateWorkDurationToArabic(daysWalid, totalMinutesWalid)

    // Calculate total days worked
    val daysWorked = totalWeekMinutes / (8.0 * 60.0)
    val daysWorkedFormatted = translateWorkDurationToArabic(daysWorked, totalWeekMinutes)

    // Check if all days are paid
    val areAllDaysPaid = weekRecords.isNotEmpty() && weekRecords.all { it.infosDeBase.paye }
    val allDaysPaid = remember { mutableStateOf(areAllDaysPaid) }

    // Get admin privileges status
    val isAbdelwahabLeGerant by viewModel.isAbdelwahabLeGerant.collectAsState()

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

    val orangeColor = Color(0xFFFF9800)
    val yellowColor = Color(0xFFFFEB3B)
    val greenColor = Color(0xFF4CAF50)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .border(
                width = 4.dp,
                color = if (allDaysPaid.value) greenColor else yellowColor.copy(alpha = borderColorAlpha),
                shape = RoundedCornerShape(12.dp)
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (allDaysPaid.value) greenColor.copy(alpha = 0.7f) else orangeColor,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
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
                        text = "تم",
                        color = Color.White,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "تم",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (isAbdelwahabLeGerant) {
                    IconButton(
                        onClick = {
                            val newPaidState = !allDaysPaid.value
                            allDaysPaid.value = newPaidState
                            markAllDaysAsPaid(weekInfo, viewModel, allDaysPaid)
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (allDaysPaid.value) Icons.Default.Check else Icons.Default.Payment,
                            contentDescription = if (allDaysPaid.value) "تم الدفع" else "في انتظار الدفع",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Text(
                text = "الأسبوع ${weekInfo.weekNumber}, ${weekInfo.year}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            // Total work duration card
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    // Show based on user type
                    when {
                        // Admin sees everything
                        isAbdelwahabLeGerant -> {
                            Text(
                                text = "مدة العمل الاجمالية: $daysWorkedFormatted",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Blue
                            )
                        }
                        // Abdelmoumen sees only his time
                        currentUser == Utilisateur.Abdelmoumen -> {
                            Text(
                                text = "مدة عملك: $daysAbdelmoumenFormatted",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Blue
                            )
                        }
                        // Walid sees only his time
                        currentUser == Utilisateur.Walid -> {
                            Text(
                                text = "مدة عملك: $daysWalidFormatted",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Blue
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(2.dp))

            // Earnings breakdown per vendor
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    // Show based on user type
                    when {
                        // Admin sees both vendors
                        isAbdelwahabLeGerant -> {
                            // Abdelmoumen earnings
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "عبدالمؤمن:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Blue,
                                    fontWeight = FontWeight.Bold
                                )
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = timeAbdelmoumen,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "${String.format("%.2f", earningsAbdelmoumen)} دينار",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Red
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.padding(2.dp))

                            // Walid earnings
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "وليد:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Blue,
                                    fontWeight = FontWeight.Bold
                                )
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = timeWalid,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "${String.format("%.2f", earningsWalid)} دينار",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Red
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.padding(4.dp))

                            // Total earnings
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "المجموع الكلي:",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Blue,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${String.format("%.2f", totalWeekEarnings)} دينار",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Abdelmoumen sees only his earnings
                        currentUser == Utilisateur.Abdelmoumen -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "أرباحك:",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Blue,
                                    fontWeight = FontWeight.Bold
                                )
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = timeAbdelmoumen,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "${String.format("%.2f", earningsAbdelmoumen)} دينار",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Walid sees only his earnings
                        currentUser == Utilisateur.Walid -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "أرباحك:",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Blue,
                                    fontWeight = FontWeight.Bold
                                )
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = timeWalid,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "${String.format("%.2f", earningsWalid)} دينار",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                )
                            }
                        }

                        Text(
                            text = "اليوم/1200 دينار",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }

// Keep all other helper functions unchanged
// (calculateTotalWeekWorkTimePerVendor, markAllDaysAsPaid, etc.)
// Keep all other helper functions unchanged
// (calculateTotalWeekWorkTimePerVendor, markAllDaysAsPaid, etc.)

// NEW FUNCTION: Calculate work time per vendor
fun calculateTotalWeekWorkTimePerVendor(weekRecords: List<K_TempTravaille>): Pair<Int, Int> {
    var totalMinutesAbdelmoumen = 0
    var totalMinutesWalid = 0

    println("=== DEBUG: calculateTotalWeekWorkTimePerVendor ===")
    println("Total records in week: ${weekRecords.size}")

    weekRecords.forEach { record ->
        println("Record date: ${record.infosDeBase.dateInString}, intervals: ${record.intervalesDeTravaille.size}")

        record.intervalesDeTravaille.forEach { interval ->
            println("  Interval: ${interval.tempDepart} -> ${interval.temparrete}, User: ${interval.utilisateur}")

            // Debug the calculation
            val start = interval.tempDepart
            val end = interval.temparrete

            if (start != "HH:mm" && end != "HH:mm") {
                try {
                    val startParts = start.split(":")
                    val endParts = end.split(":")

                    val startMinutes = startParts[0].toInt() * 60 + startParts[1].toInt()
                    val endMinutes = endParts[0].toInt() * 60 + endParts[1].toInt()
                    val duration = endMinutes - startMinutes

                    println("    Start: $start = $startMinutes min, End: $end = $endMinutes min, Duration: $duration min (${duration / 60}h ${duration % 60}m)")

                    if (duration > 0) {
                        when (interval.utilisateur) {
                            Utilisateur.Abdelmoumen -> {
                                totalMinutesAbdelmoumen += duration
                                println("    Added to Abdelmoumen: $duration min")
                            }

                            Utilisateur.Walid -> {
                                totalMinutesWalid += duration
                                println("    Added to Walid: $duration min")
                            }

                            else -> {
                                totalMinutesAbdelmoumen += duration
                                println("    Added to Abdelmoumen (default): $duration min")
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("    ERROR parsing: ${e.message}")
                }
            } else {
                println("    Skipped (invalid time)")
            }
        }
    }

    println("TOTAL Abdelmoumen: $totalMinutesAbdelmoumen min (${totalMinutesAbdelmoumen / 60}h ${totalMinutesAbdelmoumen % 60}m)")
    println("TOTAL Walid: $totalMinutesWalid min (${totalMinutesWalid / 60}h ${totalMinutesWalid % 60}m)")
    println("=== END DEBUG ===")

    return Pair(totalMinutesAbdelmoumen, totalMinutesWalid)
}

// Keep existing helper functions
fun markAllDaysAsPaid(
    weekInfo: WeekInfo,
    viewModel: RecordingViewModel,
    paidStatus: MutableState<Boolean>
) {
    val weekRecords = viewModel.repository.modelDatas.filter { record ->
        val dateString = record.infosDeBase.dateInString
        val parts = dateString.split("/")
        if (parts.size == 3) {
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1
            val day = parts[2].toInt()

            val calendar = Calendar.getInstance().apply {
                firstDayOfWeek = Calendar.SATURDAY
                minimalDaysInFirstWeek = 1
            }
            calendar.set(year, month, day)

            calendar.get(Calendar.YEAR) == weekInfo.year &&
                    calendar.get(Calendar.WEEK_OF_YEAR) == weekInfo.weekNumber
        } else {
            false
        }
    }

    weekRecords.forEach { record ->
        record.infosDeBase.paye = paidStatus.value
        viewModel.repository.updateOnPasseData(record)
    }
}

fun translateWeekTextToArabic(weekInfo: WeekInfo): String {
    return when {
        weekInfo.isCurrentWeek -> "هذا الأسبوع"
        isLastWeek(weekInfo) -> "الأسبوع الماضي"
        else -> {
            val weekDifference = getWeekDifference(weekInfo)
            if (weekDifference > 0) {
                "منذ $weekDifference أسابيع"
            } else {
                "في غضون ${-weekDifference} أسابيع"
            }
        }
    }
}

fun translateWorkDurationToArabic(daysWorked: Double, totalMinutes: Int): String {
    return when {
        totalMinutes == 0 -> "0 يوم"
        daysWorked < 1.0 -> {
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            if (minutes > 0) {
                "$hours ساعات و $minutes دقيقة"
            } else {
                "$hours ساعات"
            }
        }

        daysWorked == 1.0 -> "1 يوم"
        else -> {
            val fullDays = (totalMinutes / (8 * 60))
            val remainingMinutes = totalMinutes % (8 * 60)
            val remainingHours = remainingMinutes / 60
            val remainingMins = remainingMinutes % 60

            when {
                remainingHours > 0 && remainingMins > 0 -> {
                    "$fullDays أيام و $remainingHours ساعات و $remainingMins دقيقة"
                }

                remainingHours > 0 -> {
                    "$fullDays أيام و $remainingHours ساعات"
                }

                remainingMins > 0 -> {
                    "$fullDays أيام و $remainingMins دقيقة"
                }

                else -> {
                    "$fullDays أيام"
                }
            }
        }
    }
}

fun isLastWeek(weekInfo: WeekInfo): Boolean {
    val calendar = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.SATURDAY
        minimalDaysInFirstWeek = 1
    }
    val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
    val currentYear = calendar.get(Calendar.YEAR)

    if (currentWeek == 1 && weekInfo.weekNumber > 50 && weekInfo.year == currentYear - 1) {
        return true
    }

    return weekInfo.year == currentYear && weekInfo.weekNumber == currentWeek - 1
}

fun getWeekDifference(weekInfo: WeekInfo): Int {
    val calendar = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.SATURDAY
        minimalDaysInFirstWeek = 1
    }
    val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
    val currentYear = calendar.get(Calendar.YEAR)

    if (weekInfo.year == currentYear) {
        return currentWeek - weekInfo.weekNumber
    }

    val yearDiff = currentYear - weekInfo.year
    if (yearDiff == 1) {
        val weeksInLastYear = Calendar.getInstance(Locale.getDefault()).apply {
            firstDayOfWeek = Calendar.SATURDAY
            minimalDaysInFirstWeek = 1
            set(Calendar.YEAR, currentYear - 1)
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 31)
        }.get(Calendar.WEEK_OF_YEAR)

        return currentWeek + (weeksInLastYear - weekInfo.weekNumber)
    } else if (yearDiff == -1) {
        val weeksInCurrentYear = Calendar.getInstance(Locale.getDefault()).apply {
            firstDayOfWeek = Calendar.SATURDAY
            minimalDaysInFirstWeek = 1
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 31)
        }.get(Calendar.WEEK_OF_YEAR)

        return -(weekInfo.weekNumber + (weeksInCurrentYear - currentWeek))
    }

    return currentWeek - weekInfo.weekNumber + (yearDiff * 52)
}
