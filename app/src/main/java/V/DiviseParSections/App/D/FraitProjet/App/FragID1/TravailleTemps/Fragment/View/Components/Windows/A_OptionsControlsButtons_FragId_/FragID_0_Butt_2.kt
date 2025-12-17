package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components.PrayerTimesCalculator
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravaille
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.Utilisateur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.time.LocalDate
import java.util.Calendar
import java.util.TimeZone
import kotlin.time.ExperimentalTime

data class Standart_times(
    val its_working_abdelmoumen: Boolean=true,
    val start_abdelmoumen: String = "s",
    val end_abdelmoumen: String = "d",
    val walid_its_working: Boolean=true,
    val start_walid: String = "08:00",
    val end_walid: String = "d"
)

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
    var dateInput by remember { mutableStateOf("") }
    var createdRecordId by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    fun calculatePrayerTimes(date: LocalDate = LocalDate.now()): Pair<String, String> {
        return try {
            val calculator = PrayerTimesCalculator()
            calculator.setCalculationMethod(PrayerTimesCalculator.CalculationMethod.ALGERIA)
            val coordinates = PrayerTimesCalculator.Coordinates(latitude = 36.7167, longitude = 3.1833)
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Africa/Algiers")).apply {
                set(Calendar.YEAR, date.year)
                set(Calendar.MONTH, date.monthValue - 1)
                set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            val prayerTimes = calculator.getPrayerTimes(date = calendar, coordinates = coordinates, timeZoneOffset = 1.0)
            Pair(prayerTimes.fajr, prayerTimes.dhuhr)
        } catch (e: Exception) {
            Pair("05:30", "12:45")
        }
    }

    fun resolveTime(timeString: String, prayerTimes: Pair<String, String>): String {
        return when (timeString.lowercase().trim()) {
            "sobhe", "fajr", "subh", "s" -> prayerTimes.first
            "dohre", "dhuhr", "dhur", "dohr", "d" -> prayerTimes.second
            else -> timeString
        }
    }

    fun formatTimeInput(input: String): String {
        return input.replace(".", ":")
    }

    var startTimeAbdelmoumen by remember { mutableStateOf("") }
    var endTimeAbdelmoumen by remember { mutableStateOf("") }
    var startTimeWalid by remember { mutableStateOf("") }
    var endTimeWalid by remember { mutableStateOf("") }

    var defaultStartAbdelmoumen by remember { mutableStateOf(standardTimes.start_abdelmoumen) }
    var defaultEndAbdelmoumen by remember { mutableStateOf(standardTimes.end_abdelmoumen) }
    var defaultStartWalid by remember { mutableStateOf(standardTimes.start_walid) }
    var defaultEndWalid by remember { mutableStateOf(standardTimes.end_walid) }

    val focusManager = LocalFocusManager.current
    val endAbdelmoumenFocusRequester = remember { FocusRequester() }
    val startWalidFocusRequester = remember { FocusRequester() }
    val endWalidFocusRequester = remember { FocusRequester() }

    LaunchedEffect(showIntervalDialog, selectedDate) {
        if (showIntervalDialog && selectedDate != null) {
            val prayerTimes = calculatePrayerTimes(selectedDate!!)
            defaultStartAbdelmoumen = resolveTime(standardTimes.start_abdelmoumen, prayerTimes)
            defaultEndAbdelmoumen = resolveTime(standardTimes.end_abdelmoumen, prayerTimes)
            defaultStartWalid = resolveTime(standardTimes.start_walid, prayerTimes)
            defaultEndWalid = resolveTime(standardTimes.end_walid, prayerTimes)
        }
    }

// Replace the showDateDialog AlertDialog with this fixed version:

    if (showDateDialog) {
        val dateFieldFocusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(100)
            dateFieldFocusRequester.requestFocus()
        }

        AlertDialog(
            onDismissRequest = {
                showDateDialog = false
                dateInput = ""  // Changed: Reset to empty instead of todayFormatted
            },
            title = { Text("Add New Day") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dateInput,
                        onValueChange = { dateInput = it },
                        label = { Text("Enter date (MM.DD)") },
                        placeholder = { Text("Example: $todayFormatted") },
                        modifier = Modifier
                            .padding(8.dp)
                            .focusRequester(dateFieldFocusRequester),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )
                    Text(
                        text = "Leave empty to use today's date",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalDateInput = dateInput.ifBlank { todayFormatted }
                        val currentYear = java.time.Year.now().value

                        println("DEBUG: dateInput = '$dateInput'")
                        println("DEBUG: finalDateInput = '$finalDateInput'")

                        val parts = finalDateInput.split(".")
                        println("DEBUG: parts = $parts, size = ${parts.size}")

                        if (parts.size == 2) {
                            try {
                                val monthStr = parts[0].trim()
                                val dayStr = parts[1].trim()

                                println("DEBUG: monthStr = '$monthStr', dayStr = '$dayStr'")

                                val month = monthStr.toInt()
                                val day = dayStr.toInt()

                                println("DEBUG: month = $month, day = $day")

                                val monthPadded = month.toString().padStart(2, '0')
                                val dayPadded = day.toString().padStart(2, '0')

                                createdRecordId = "${currentYear}_${monthPadded}_${dayPadded}"
                                selectedDate = LocalDate.of(currentYear, month, day)

                                println("DEBUG: createdRecordId = $createdRecordId")
                                println("DEBUG: selectedDate = $selectedDate")

                                showDateDialog = false
                                showIntervalDialog = true
                            } catch (e: Exception) {
                                println("DEBUG: Exception = ${e.message}")
                            }
                        } else {
                            println("DEBUG: parts.size != 2")
                        }
                    }
                ) {
                    Text("Next")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDateDialog = false
                    dateInput = ""  // Changed: Reset to empty instead of todayFormatted
                }) {
                    Text("Cancel")
                }
            }
        )
    }

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
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (standardTimes.its_working_abdelmoumen) {
                        Text(text = "Abdelmoumen", style = MaterialTheme.typography.titleMedium, color = Color(0xFF2196F3))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = startTimeAbdelmoumen,
                                onValueChange = { startTimeAbdelmoumen = formatTimeInput(it) },
                                label = { Text("Start: $defaultStartAbdelmoumen") },
                                placeholder = { Text(defaultStartAbdelmoumen) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(onNext = { endAbdelmoumenFocusRequester.requestFocus() }),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = endTimeAbdelmoumen,
                                onValueChange = { endTimeAbdelmoumen = formatTimeInput(it) },
                                label = { Text("End: $defaultEndAbdelmoumen") },
                                placeholder = { Text(defaultEndAbdelmoumen) },
                                modifier = Modifier.weight(1f).focusRequester(endAbdelmoumenFocusRequester),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = if (standardTimes.walid_its_working) ImeAction.Next else ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { if (standardTimes.walid_its_working) startWalidFocusRequester.requestFocus() },
                                    onDone = { if (!standardTimes.walid_its_working) focusManager.clearFocus() }
                                ),
                                singleLine = true
                            )
                        }
                    }

                    if (standardTimes.its_working_abdelmoumen && standardTimes.walid_its_working) {
                        HorizontalDivider()
                    }

                    if (standardTimes.walid_its_working) {
                        Text(text = "Walid", style = MaterialTheme.typography.titleMedium, color = Color(0xFF4CAF50))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = startTimeWalid,
                                onValueChange = { startTimeWalid = formatTimeInput(it) },
                                label = { Text("Start: $defaultStartWalid") },
                                placeholder = { Text(defaultStartWalid) },
                                modifier = Modifier.weight(1f).focusRequester(startWalidFocusRequester),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(onNext = { endWalidFocusRequester.requestFocus() }),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = endTimeWalid,
                                onValueChange = { endTimeWalid = formatTimeInput(it) },
                                label = { Text("End: $defaultEndWalid") },
                                placeholder = { Text(defaultEndWalid) },
                                modifier = Modifier.weight(1f).focusRequester(endWalidFocusRequester),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                singleLine = true
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (createdRecordId != null) {
                            val intervalesDeTravaille = mutableListOf<K_TempTravaille.IntervalesDeTravaille>()

                            if (standardTimes.its_working_abdelmoumen) {
                                val abdelmoumenInterval = K_TempTravaille.IntervalesDeTravaille.get_default().apply {
                                    vid = "abdelmoumen_interval"
                                    utilisateur = Utilisateur.Abdelmoumen
                                    tempDepart = startTimeAbdelmoumen.ifBlank { defaultStartAbdelmoumen }
                                    temparrete = endTimeAbdelmoumen.ifBlank { defaultEndAbdelmoumen }
                                }
                                intervalesDeTravaille.add(abdelmoumenInterval)
                            }

                            if (standardTimes.walid_its_working) {
                                val walidInterval = K_TempTravaille.IntervalesDeTravaille.get_default().apply {
                                    vid = "walid_interval"
                                    utilisateur = Utilisateur.Walid
                                    tempDepart = startTimeWalid.ifBlank { defaultStartWalid }
                                    temparrete = endTimeWalid.ifBlank { defaultEndWalid }
                                }
                                intervalesDeTravaille.add(walidInterval)
                            }

                            val newWorkingDay = K_TempTravaille(vid = createdRecordId!!).apply {
                                this.infosDeBase.dateInString = createdRecordId!!
                                this.intervalesDeTravaille.clear()
                                this.intervalesDeTravaille.addAll(intervalesDeTravaille)
                            }

                            viewModel.repository.add_new_Temp(newWorkingDay)

                            showIntervalDialog = false
                            dateInput = todayFormatted
                            createdRecordId = null
                            selectedDate = null
                            startTimeAbdelmoumen = ""
                            endTimeAbdelmoumen = ""
                            startTimeWalid = ""
                            endTimeWalid = ""
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
                        startTimeAbdelmoumen = ""
                        endTimeAbdelmoumen = ""
                        startTimeWalid = ""
                        endTimeWalid = ""
                    }
                ) {
                    Text("Skip")
                }
            }
        )
    }

    ControlButton(
        onClick = {
            dateInput = ""
            showDateDialog = true
        },
        icon = Icons.Filled.Add,
        contentDescription = "Add new day",
        showLabels = showLabels,
        labelText = labelText,
        containerColor = Color(0xFF4CAF50)
    )
}
