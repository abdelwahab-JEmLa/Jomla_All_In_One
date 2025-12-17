package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Standart_times
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StandardTimesDialog(
    currentTimes: Standart_times,
    onDismiss: () -> Unit,
    onConfirm: (Standart_times) -> Unit
) {
    var itsWorkingAbdelmoumen by remember { mutableStateOf(currentTimes.its_working_abdelmoumen) }
    var startAbdelmoumen by remember { mutableStateOf(currentTimes.start_abdelmoumen) }
    var endAbdelmoumen by remember { mutableStateOf(currentTimes.end_abdelmoumen) }

    var walidItsWorking by remember { mutableStateOf(currentTimes.walid_its_working) }
    var startWalid by remember { mutableStateOf(currentTimes.start_walid) }
    var endWalid by remember { mutableStateOf(currentTimes.end_walid) }

    var selectedType by remember { mutableStateOf(currentTimes.au_click_start_par) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Standard Times") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Default Date Behavior Section
                Text(
                    text = "Default Date Behavior",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Column(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == Standart_times.Type.TodayeDate,
                            onClick = { selectedType = Standart_times.Type.TodayeDate }
                        )
                        Text("Start with Today's Date")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == Standart_times.Type.NextLastDay,
                            onClick = { selectedType = Standart_times.Type.NextLastDay }
                        )
                        Text("Start with Next Day (Last Date + 1)")
                    }
                }

                HorizontalDivider()

                // Abdelmoumen Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Abdelmoumen",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = itsWorkingAbdelmoumen,
                            onCheckedChange = { itsWorkingAbdelmoumen = it }
                        )
                        Text("Working")
                    }
                }

                if (itsWorkingAbdelmoumen) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startAbdelmoumen,
                            onValueChange = { startAbdelmoumen = it },
                            label = { Text("Start") },
                            placeholder = { Text("08:00 or Sobhe") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endAbdelmoumen,
                            onValueChange = { endAbdelmoumen = it },
                            label = { Text("End") },
                            placeholder = { Text("Dohre or 12:45") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalDivider()

                // Walid Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Walid",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = walidItsWorking,
                            onCheckedChange = { walidItsWorking = it }
                        )
                        Text("Working")
                    }
                }

                if (walidItsWorking) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startWalid,
                            onValueChange = { startWalid = it },
                            label = { Text("Start") },
                            placeholder = { Text("08:00 or Sobhe") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endWalid,
                            onValueChange = { endWalid = it },
                            label = { Text("End") },
                            placeholder = { Text("Dohre or 12:45") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        Standart_times(
                            its_working_abdelmoumen = itsWorkingAbdelmoumen,
                            start_abdelmoumen = startAbdelmoumen,
                            end_abdelmoumen = endAbdelmoumen,
                            walid_its_working = walidItsWorking,
                            start_walid = startWalid,
                            end_walid = endWalid,
                            au_click_start_par = selectedType
                        )
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
