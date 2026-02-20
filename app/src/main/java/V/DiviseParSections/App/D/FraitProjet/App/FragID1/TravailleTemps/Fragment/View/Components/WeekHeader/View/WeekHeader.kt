package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.WeekHeader.View

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.WeekHeader.View.Functions.calculateTotalWeekWorkTimePerVendor
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.WeekHeader.View.Functions.calculateWeekSalesData
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.WeekHeader.View.Functions.markAllDaysAsPaid
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.WeekHeader.View.Functions.translateWeekTextToArabic
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.WeekHeader.View.Functions.translateWorkDurationToArabic
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.WeekInfo
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravaille
import EntreApps.Shared.Models.Components.Utilisateur
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun WeekHeader(
    weekInfo: WeekInfo,
    viewModel: RecordingViewModel,
    weekRecords: List<K_TempTravaille>,
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    val currentUser = focusedValuesGetter.active_Central_Values.active_filter_du_utilisateur
    val isAbdelwahabLeGerant = true

    val (totalMinutesAbdelmoumen, totalMinutesWalid) = calculateTotalWeekWorkTimePerVendor(
        weekRecords
    )
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
    val daysAbdelmoumenFormatted =
        translateWorkDurationToArabic(daysAbdelmoumen, totalMinutesAbdelmoumen)
    val daysWalidFormatted = translateWorkDurationToArabic(daysWalid, totalMinutesWalid)

    val daysWorked = totalWeekMinutes / (8.0 * 60.0)
    val daysWorkedFormatted = translateWorkDurationToArabic(daysWorked, totalWeekMinutes)

    val areAllDaysPaid = weekRecords.isNotEmpty() && weekRecords.all { it.infosDeBase.paye }
    val allDaysPaid = remember { mutableStateOf(areAllDaysPaid) }

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

                // Expenses and Net Position Section - Extracted Component
                Section_ExpensesAndNetPosition(
                    weekSalesData = weekSalesData,
                    totalWeekEarnings = totalWeekEarnings
                )
            }
        }
    }
}

data class WeekSalesData(
    val totalCashSales: Double,
    val totalCreditSales: Double,
    val totalSales: Double,
    val totalSavedBalance: Double,
    val pre_fraits_voiture_essance_marche_et_paprasse: Double,
    val profitPercentage: Double
)
