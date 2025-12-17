package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_

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
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun FragID_0_Butt_2(
    viewModel: RecordingViewModel,
    showLabels: Boolean,
    labelText: String,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
) {
    var showDateDialog by remember { mutableStateOf(false) }
    var showIntervalDialog by remember { mutableStateOf(false) }
    var dateInput by remember { mutableStateOf("") }
    var createdRecordId by remember { mutableStateOf<String?>(null) }

    // Interval inputs
    var startTimeAbdelmoumen by remember { mutableStateOf("08:00") }
    var endTimeAbdelmoumen by remember { mutableStateOf("09:30") }
    var startTimeWalid by remember { mutableStateOf("09:30") }
    var endTimeWalid by remember { mutableStateOf("10:30") }

    // Date Dialog
    if (showDateDialog) {
        AlertDialog(
            onDismissRequest = {
                showDateDialog = false
                dateInput = ""
            },
            title = { Text("Add New Day") },
            text = {
                OutlinedTextField(
                    value = dateInput,
                    onValueChange = { dateInput = it },
                    label = { Text("Enter date (MM.DD)") },
                    placeholder = { Text("Example: 03.17") },
                    modifier = Modifier.padding(8.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (dateInput.isNotEmpty()) {
                            // Format the record ID
                            val currentYear = java.time.Year.now().value
                            val parts = dateInput.split(".")
                            if (parts.size == 2) {
                                val month = parts[0].padStart(2, '0')
                                val day = parts[1].padStart(2, '0')
                                createdRecordId = "${currentYear}_${month}_${day}"
                            }

                            // Close date dialog and open interval dialog
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
                        dateInput = ""
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
                dateInput = ""
                startTimeAbdelmoumen = ""
                endTimeAbdelmoumen = ""
                startTimeWalid = ""
                endTimeWalid = ""
                createdRecordId = null
            },
            title = { Text("Add Intervals for Both Vendors") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Abdelmoumen Section
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
                            placeholder = { Text("17:00") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Divider()

                    // Walid Section
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
                            placeholder = { Text("17:00") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (createdRecordId != null) {
                            val intervalesDeTravaille = mutableListOf<K_TempTravaille.IntervalesDeTravaille>()

                            val abdelmoumenInterval = K_TempTravaille.IntervalesDeTravaille.get_default().apply {
                                vid = "abdelmoumen_interval"
                                vendeur = K_TempTravaille.IntervalesDeTravaille.Vendeur.Abdelmoumen
                                tempDepart = startTimeAbdelmoumen
                                temparrete = endTimeAbdelmoumen
                            }
                            intervalesDeTravaille.add(abdelmoumenInterval)

                            val walidInterval = K_TempTravaille.IntervalesDeTravaille.get_default().apply {
                                vid = "walid_interval"
                                vendeur = K_TempTravaille.IntervalesDeTravaille.Vendeur.Walid
                                tempDepart = startTimeWalid
                                temparrete = endTimeWalid
                            }
                            intervalesDeTravaille.add(walidInterval)

                            // Create the K_TempTravaille instance with the intervals
                            val newWorkingDay = K_TempTravaille(vid = createdRecordId!!).apply {
                                this.infosDeBase.dateInString = createdRecordId!!
                                this.intervalesDeTravaille.addAll(intervalesDeTravaille)
                            }

                            viewModel.repository.add_new_Temp(k_TempTravaille = newWorkingDay)

                            showIntervalDialog = false
                            dateInput = ""
                            createdRecordId = null
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
                        dateInput = ""
                        startTimeAbdelmoumen = ""
                        endTimeAbdelmoumen = ""
                        startTimeWalid = ""
                        endTimeWalid = ""
                        createdRecordId = null
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
