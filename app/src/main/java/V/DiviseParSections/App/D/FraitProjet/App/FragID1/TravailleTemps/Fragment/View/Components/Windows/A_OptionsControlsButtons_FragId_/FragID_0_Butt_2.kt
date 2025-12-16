package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
    focusedValuesGetter: FocusedValuesGetter= koinInject (),
    viewModel: RecordingViewModel,
    showLabels: Boolean,
    labelText: String,
) {
    var showDialog by remember { mutableStateOf(false) }
    var dateInput by remember { mutableStateOf("") }

    // Display the dialog when showDialog is true
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false

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
                            viewModel.ajoutJour(dateInput)
                            dateInput = ""
                            focusedValuesGetter.update_activeCentralValues(
                                focusedValuesGetter.active_Central_Values.copy(
                                    affiche_dialoge_add_temp_travaille = true
                                )
                            )
                            showDialog = false
                        }
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    ControlButton(
        onClick = { showDialog = true },
        icon = Icons.Filled.Add,
        contentDescription = "Add new day",
        showLabels = showLabels,
        labelText = labelText,
        containerColor = Color(0xFF4CAF50)  // Green color for upsert button
    )
}
