package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.WeekInfo
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravaille
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.Utilisateur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    val currentUser = focusedValuesGetter.active_Central_Values.active_filter_du_utilisateur
    val isAbdelwahabLeGerant by viewModel.isAbdelwahabLeGerant.collectAsState(initial = false)

    val (totalMinutesAbdelmoumen, totalMinutesWalid) = calculateTotalWeekWorkTimePerVendor(weekRecords)
    val totalWeekMinutes = totalMinutesAbdelmoumen + totalMinutesWalid

    val hourlyRate = 1200.0 / 8.0 / 60.0
    val earningsAbdelmoumen = hourlyRate * totalMinutesAbdelmoumen
    val earningsWalid = hourlyRate * totalMinutesWalid
    val totalWeekEarnings = earningsAbdelmoumen + earningsWalid

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

    val daysWorked = totalWeekMinutes / (8.0 * 60.0)
    val daysWorkedFormatted = translateWorkDurationToArabic(daysWorked, totalWeekMinutes)

    val areAllDaysPaid = weekRecords.isNotEmpty() && weekRecords.all { it.infosDeBase.paye }
    val allDaysPaid = remember { mutableStateOf(areAllDaysPaid) }

    // Calculate week sales data
    val weekSalesData = calculateWeekSalesData(weekInfo, focusedValuesGetter)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
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
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )

                Spacer(modifier = Modifier.weight(1f))

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
                            tint = if (allDaysPaid.value) Color(0xFF4CAF50) else Color(0xFFFF9800),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Text(
                text = "الأسبوع ${weekInfo.weekNumber}, ${weekInfo.year}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            // Total work duration card
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    when {
                        isAbdelwahabLeGerant -> {
                            Text(
                                text = "مدة العمل الاجمالية: $daysWorkedFormatted",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Blue
                            )
                        }
                        currentUser == Utilisateur.Abdelmoumen -> {
                            Text(
                                text = "مدة عملك: $daysAbdelmoumenFormatted",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Blue
                            )
                        }
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
                    when {
                        isAbdelwahabLeGerant -> {
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

                            Text(
                                text = "اليوم/1200 دينار",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

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

                            Text(
                                text = "اليوم/1200 دينار",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

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

            // Sales & Financial Summary (Admin only)
            if (isAbdelwahabLeGerant) {
                Spacer(modifier = Modifier.padding(4.dp))

                // Financial Summary Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Sales Card
                    ElevatedCard(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color(0xFFE8F5E9)
                        ),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "💰 المبيعات",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.padding(4.dp))

                            Text(
                                text = "${String.format("%.0f", weekSalesData.totalSales)} دج",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )

                            Spacer(modifier = Modifier.padding(2.dp))

                            Text(
                                text = "نقدي: ${String.format("%.0f", weekSalesData.totalCashSales)} دج",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF388E3C)
                            )
                            Text(
                                text = "آجل: ${String.format("%.0f", weekSalesData.totalCreditSales)} دج",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF388E3C)
                            )

                            if (weekSalesData.totalSales > 0) {
                                Spacer(modifier = Modifier.padding(2.dp))
                                Text(
                                    text = "ربح ${String.format("%.1f", weekSalesData.profitPercentage)}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF66BB6A)
                                )
                            }
                        }
                    }

                    // Saved Balance Card
                    ElevatedCard(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color(0xFFE3F2FD)
                        ),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "💵 الأرباح",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF1565C0),
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.padding(4.dp))

                            Text(
                                text = "${String.format("%.0f", weekSalesData.totalSavedBalance)} دج",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1)
                            )

                            Spacer(modifier = Modifier.padding(2.dp))

                            Text(
                                text = "المحفوظة",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(2.dp))

                // Expenses and Net Position Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Expenses Card
                    ElevatedCard(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color(0xFFFFF3E0)
                        ),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "🚗 المصاريف",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFE65100),
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.padding(4.dp))

                            Text(
                                text = "${String.format("%.0f", weekSalesData.totalExpenses)} دج",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBF360C)
                            )

                            Spacer(modifier = Modifier.padding(2.dp))

                            Text(
                                text = "سوق و سيارة",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFE65100)
                            )
                        }
                    }

                    // Net Position Card
                    ElevatedCard(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color(0xFFF3E5F5)
                        ),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "📊 الصافي",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF6A1B9A),
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.padding(4.dp))

                            val netPosition = weekSalesData.totalSavedBalance -
                                    weekSalesData.totalExpenses -
                                    totalWeekEarnings

                            Text(
                                text = "${String.format("%.0f", netPosition)} دج",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (netPosition >= 0) Color(0xFF388E3C) else Color(0xFFD32F2F)
                            )

                            Spacer(modifier = Modifier.padding(2.dp))

                            Text(
                                text = "للمشروع",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF7B1FA2)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class WeekSalesData(
    val totalCashSales: Double,
    val totalCreditSales: Double,
    val totalSales: Double,
    val totalSavedBalance: Double,
    val totalExpenses: Double,
    val profitPercentage: Double
)

fun calculateWeekSalesData(
    weekInfo: WeekInfo,
    focusedValuesGetter: FocusedValuesGetter
): WeekSalesData {
    val repo14 = focusedValuesGetter.repo14VentPeriode

    if (repo14 == null) {
        return WeekSalesData(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    }

    val weekPeriods = repo14.datasValue.filter { period ->
        val calendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.SATURDAY
            minimalDaysInFirstWeek = 1
            timeInMillis = period.creationTimestamp
        }

        calendar.get(Calendar.YEAR) == weekInfo.year &&
                calendar.get(Calendar.WEEK_OF_YEAR) == weekInfo.weekNumber
    }

    var totalCash = 0.0
    var totalCredit = 0.0
    var totalSavedBalance = 0.0
    var totalExpenses = 0.0

    weekPeriods.forEach { period ->
        totalCash += period.cash_Vents_Totale
        totalCredit += period.credit_Vents_Totale
        totalSavedBalance += period.saved_balance
        totalExpenses += period.pre_fraits_voiture_essance_marche_et_paprasse
    }

    val totalSales = totalCash + totalCredit
    val profitPercentage = if (totalSales > 0) {
        (totalSavedBalance / totalSales) * 100
    } else {
        0.0
    }

    return WeekSalesData(
        totalCashSales = totalCash,
        totalCreditSales = totalCredit,
        totalSales = totalSales,
        totalSavedBalance = totalSavedBalance,
        totalExpenses = totalExpenses,
        profitPercentage = profitPercentage
    )
}

fun calculateTotalWeekWorkTimePerVendor(weekRecords: List<K_TempTravaille>): Pair<Int, Int> {
    var totalMinutesAbdelmoumen = 0
    var totalMinutesWalid = 0

    weekRecords.forEach { record ->
        record.intervalesDeTravaille.forEach { interval ->
            val start = interval.tempDepart
            val end = interval.temparrete

            if (start != "HH:mm" && end != "HH:mm") {
                try {
                    val startParts = start.split(":")
                    val endParts = end.split(":")

                    val startMinutes = startParts[0].toInt() * 60 + startParts[1].toInt()
                    val endMinutes = endParts[0].toInt() * 60 + endParts[1].toInt()
                    val duration = endMinutes - startMinutes

                    if (duration > 0) {
                        when (interval.utilisateur) {
                            Utilisateur.Abdelmoumen -> totalMinutesAbdelmoumen += duration
                            Utilisateur.Walid -> totalMinutesWalid += duration
                            else -> totalMinutesAbdelmoumen += duration
                        }
                    }
                } catch (e: Exception) {
                    // Skip invalid entries
                }
            }
        }
    }

    return Pair(totalMinutesAbdelmoumen, totalMinutesWalid)
}

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
