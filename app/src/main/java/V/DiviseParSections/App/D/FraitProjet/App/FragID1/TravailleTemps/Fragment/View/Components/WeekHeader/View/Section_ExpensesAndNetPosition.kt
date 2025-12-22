package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.WeekHeader.View

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun Section_ExpensesAndNetPosition(
    weekSalesData: WeekSalesData,
    totalWeekEarnings: Double,
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    // Paperwork expenses
    var impots by remember { mutableStateOf(30000.0) }
    var impotsPenalities by remember { mutableStateOf(10000.0) }
    var casnos by remember { mutableStateOf(35000.0) }

    var showExpensesDialog by remember { mutableStateOf(false) }

    // Calculations
    val totalePaprasseFraitParAnne = impots + impotsPenalities + casnos
    val totalePaprasseFraitParSemain = totalePaprasseFraitParAnne / 52.0

    // Vehicle expenses are now calculated from weekSalesData (per period)
    val vehicleExpenses = weekSalesData.pre_fraits_voiture_essance_marche_et_paprasse

    val totalExpensesWithAll = vehicleExpenses + totalePaprasseFraitParSemain

    // Expenses Dialog
    if (showExpensesDialog) {
        AlertDialog(
            onDismissRequest = { showExpensesDialog = false },
            title = {
                Text(
                    text = "تعديل المصاريف",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Paperwork Section
                    Text(
                        text = "📋 مصاريف الأوراق الإدارية",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    OutlinedTextField(
                        value = impots.toString(),
                        onValueChange = { impots = it.toDoubleOrNull() ?: impots },
                        label = { Text("الضرائب السنوية (دج)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.padding(4.dp))

                    OutlinedTextField(
                        value = impotsPenalities.toString(),
                        onValueChange = {
                            impotsPenalities = it.toDoubleOrNull() ?: impotsPenalities
                        },
                        label = { Text("غرامات الضرائب (دج)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.padding(4.dp))

                    OutlinedTextField(
                        value = casnos.toString(),
                        onValueChange = { casnos = it.toDoubleOrNull() ?: casnos },
                        label = { Text("الكاسنوس السنوية (دج)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "المجموع: ${
                            String.format(
                                "%.0f",
                                totalePaprasseFraitParSemain
                            )
                        } دج/أسبوع",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    // Vehicle Section - NOW DISPLAYS CALCULATED VALUES
                    Text(
                        text = "🚗 مصاريف سير الشاحنة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Text(
                        text = "المصاريف المسجلة لهذا الأسبوع:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF5D4037),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Text(
                        text = "${String.format("%.0f", vehicleExpenses)} دج",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                        modifier = Modifier.padding(start = 8.dp)
                    )

                    Text(
                        text = "يتم حسابها من فترات البيع المسجلة",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.padding(12.dp))

                    // Total Summary
                    Text(
                        text = "📊 الملخص الإجمالي",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6A1B9A),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Text(
                        text = "الأوراق الأسبوعية: ${
                            String.format(
                                "%.0f",
                                totalePaprasseFraitParSemain
                            )
                        } دج",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "السيارة هذا الأسبوع: ${String.format("%.0f", vehicleExpenses)} دج",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(
                        text = "المجموع الكلي: ${
                            String.format(
                                "%.0f",
                                totalExpensesWithAll
                            )
                        } دج/أسبوع",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBF360C)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showExpensesDialog = false }) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExpensesDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Expenses and Net Position Row
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Expenses Card
        ElevatedCard(
            modifier = Modifier
                .weight(1f)
                .clickable { showExpensesDialog = true },
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color(0xFFFFF3E0)
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💰 المصاريف",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.Bold
                    )

                    val currentUser = M18CentralParametresOfAllApps.get_utilisateur(
                        focusedValuesGetter.currentActive_M9AppCompt?.keyID ?: ""
                    )
                    val canEdit = M18CentralParametresOfAllApps.canEdit(currentUser)
                    if (canEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "تعديل",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(4.dp))

                Text(
                    text = "${String.format("%.0f", totalExpensesWithAll)} دج",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBF360C)
                )

                Spacer(modifier = Modifier.padding(2.dp))
                Text(
                    text = "أوراق: ${String.format("%.0f", totalePaprasseFraitParSemain)} دج",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFE65100)
                )
                Text(
                    text = "سيارة: ${String.format("%.0f", vehicleExpenses)} دج",
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
                        totalExpensesWithAll -
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
