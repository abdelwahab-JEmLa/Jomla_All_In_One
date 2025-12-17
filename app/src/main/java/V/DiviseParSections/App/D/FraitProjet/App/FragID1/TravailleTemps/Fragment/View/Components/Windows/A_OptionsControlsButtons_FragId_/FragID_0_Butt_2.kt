package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components.PrayerTimesCalculator
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravaille
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.time.LocalDate
import java.util.Calendar
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun FragID_0_Butt_2(
    viewModel: RecordingViewModel,
    showLabels: Boolean,
    labelText: String,
    standardTimes: Standart_times,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
) {
    var showDateDialog by remember { mutableStateOf(false) }
    var showIntervalDialog by remember { mutableStateOf(false) }

    val todayFormatted = remember {
        val today = LocalDate.now()
        String.format("%02d.%02d", today.monthValue, today.dayOfMonth)
    }
    var dateInput by remember { mutableStateOf(todayFormatted) }
    var createdRecordId by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // Calculate prayer times using PrayerTimesCalculator
    fun calculatePrayerTimes(date: LocalDate = LocalDate.now()): Pair<String, String> {
        return try {
            val calculator = PrayerTimesCalculator()
            calculator.setCalculationMethod(PrayerTimesCalculator.CalculationMethod.MWL)

            // Coordinates for Bab Ezzouar, Algiers, DZ
            val coordinates = PrayerTimesCalculator.Coordinates(
                latitude = 36.7167,
                longitude = 3.1833
            )

            // Create Calendar instance for the selected date
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, date.year)
                set(Calendar.MONTH, date.monthValue - 1) // Calendar months are 0-based
                set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
            }

            // Get prayer times with timezone offset for Africa/Algiers (UTC+1)
            val prayerTimes = calculator.getPrayerTimes(
                date = calendar,
                coordinates = coordinates,
                timeZoneOffset = 1.0 // Algeria is UTC+1
            )

            // Return Fajr and Dhuhr times
            Pair(prayerTimes.fajr, prayerTimes.dhuhr)
        } catch (e: Exception) {
            Pair("05:30", "12:45") // Fallback times
        }
    }

    // Convert standard time (could be prayer name or HH:mm) to actual time
    fun resolveTime(timeString: String, prayerTimes: Pair<String, String>): String {
        return when (timeString.lowercase()) {
            "sobhe", "fajr" -> prayerTimes.first
            "dohre", "dhuhr", "dhur" -> prayerTimes.second
            else -> timeString // Assume it's already in HH:mm format
        }
    }

    var startTimeAbdelmoumen by remember { mutableStateOf("") }
    var endTimeAbdelmoumen by remember { mutableStateOf("") }
    var startTimeWalid by remember { mutableStateOf("") }
    var endTimeWalid by remember { mutableStateOf("") }

    // Update times when dialog opens
    LaunchedEffect(showIntervalDialog, selectedDate) {
        if (showIntervalDialog && selectedDate != null) {
            val prayerTimes = calculatePrayerTimes(selectedDate!!)
            startTimeAbdelmoumen = resolveTime(standardTimes.start_abdelmoumen, prayerTimes)
            endTimeAbdelmoumen = resolveTime(standardTimes.end_abdelmoumen, prayerTimes)
            startTimeWalid = resolveTime(standardTimes.start_walid, prayerTimes)
            endTimeWalid = resolveTime(standardTimes.end_walid, prayerTimes)
        }
    }

    // Date Dialog
    if (showDateDialog) {
        AlertDialog(
            onDismissRequest = {
                showDateDialog = false
                dateInput = todayFormatted
            },
            title = { Text("Add New Day") },
            text = {
                OutlinedTextField(
                    value = dateInput,
                    onValueChange = { dateInput = it },
                    label = { Text("Enter date (MM.DD)") },
                    placeholder = { Text("Example: $todayFormatted") },
                    modifier = Modifier.padding(8.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (dateInput.isNotEmpty()) {
                            val currentYear = java.time.Year.now().value
                            val parts = dateInput.split(".")
                            if (parts.size == 2) {
                                val month = parts[0].padStart(2, '0')
                                val day = parts[1].padStart(2, '0')
                                createdRecordId = "${currentYear}_${month}_${day}"
                                selectedDate = LocalDate.of(currentYear, month.toInt(), day.toInt())
                            }
                            showDateDialog = false
                            showIntervalDialog = true
                        }
                    }
                ) {
                    Text("Next")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDateDialog = false
                        dateInput = todayFormatted
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Interval Dialog
    if (showIntervalDialog) {
        AlertDialog(
            onDismissRequest = {
                showIntervalDialog = false
                dateInput = todayFormatted
                createdRecordId = null
                selectedDate = null
            },
            title = { Text("Add Intervals for Both Vendors") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Abdelmoumen",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF2196F3)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startTimeAbdelmoumen,
                            onValueChange = { startTimeAbdelmoumen = it },
                            label = { Text("Start") },
                            placeholder = { Text("08:00") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endTimeAbdelmoumen,
                            onValueChange = { endTimeAbdelmoumen = it },
                            label = { Text("End") },
                            placeholder = { Text("12:45") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider()

                    Text(
                        text = "Walid",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF4CAF50)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startTimeWalid,
                            onValueChange = { startTimeWalid = it },
                            label = { Text("Start") },
                            placeholder = { Text("08:00") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endTimeWalid,
                            onValueChange = { endTimeWalid = it },
                            label = { Text("End") },
                            placeholder = { Text("12:45") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (createdRecordId != null) {
                            val intervalesDeTravalle = mutableListOf<K_TempTravaille.IntervalesDeTravaille>()

                            val abdelmoumenInterval = K_TempTravaille.IntervalesDeTravaille.get_default().apply {
                                vid = "abdelmoumen_interval"
                                vendeur = K_TempTravaille.IntervalesDeTravaille.Vendeur.Abdelmoumen
                                tempDepart = startTimeAbdelmoumen
                                temparrete = endTimeAbdelmoumen
                            }
                            intervalesDeTravalle.add(abdelmoumenInterval)

                            val walidInterval = K_TempTravaille.IntervalesDeTravaille.get_default().apply {
                                vid = "walid_interval"
                                vendeur = K_TempTravaille.IntervalesDeTravaille.Vendeur.Walid
                                tempDepart = startTimeWalid
                                temparrete = endTimeWalid
                            }
                            intervalesDeTravalle.add(walidInterval)

                            val newWorkingDay = K_TempTravaille(vid = createdRecordId!!).apply {
                                this.infosDeBase.dateInString = createdRecordId!!
                                this.intervalesDeTravaille.addAll(intervalesDeTravaille)
                            }

                            viewModel.repository.add_new_Temp(k_TempTravaille = newWorkingDay)

                            showIntervalDialog = false
                            dateInput = todayFormatted
                            createdRecordId = null
                            selectedDate = null
                        }
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showIntervalDialog = false
                        dateInput = todayFormatted
                        createdRecordId = null
                        selectedDate = null
                    }
                ) {
                    Text("Skip")
                }
            }
        )
    }

    ControlButton(
        onClick = { showDateDialog = true },
        icon = Icons.Filled.Add,
        contentDescription = "Add new day",
        showLabels = showLabels,
        labelText = labelText,
        containerColor = Color(0xFF4CAF50)
    )
}
