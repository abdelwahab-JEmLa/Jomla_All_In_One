package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components.LabelsButton
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components.MenuButton
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

data class Standart_times(
    val start_abdelmoumen: String = "06:00",//"Sobhe",
    val end_abdelmoumen: String = "12:45",
    val start_walid: String = "08:00",
    val end_walid: String = "12:00"    ,//"Dohre"
)

@Composable
fun A_OptionsControlsButtons_FragId_(
    viewModel: RecordingViewModel = koinViewModel(),
) {
    var standart_times by remember { mutableStateOf(Standart_times()) }
    var showStandardTimesDialog by remember { mutableStateOf(false) }

    val isAbdelwahabLeGerant by viewModel.isAbdelwahabLeGerant.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(true) }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Standard Times Dialog
    if (showStandardTimesDialog) {
        StandardTimesDialog(
            currentTimes = standart_times,
            onDismiss = { showStandardTimesDialog = false },
            onConfirm = { newTimes ->
                standart_times = newTimes
                showStandardTimesDialog = false
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showMenu) {

                    if (isAbdelwahabLeGerant) {
                        FragID_0_Butt_2(
                            viewModel = viewModel,
                            showLabels = showLabels,
                            labelText = "Add Day",
                            standardTimes = standart_times
                        )

                        // Button to edit standard times
                        ControlButton(
                            onClick = { showStandardTimesDialog = true },
                            icon = Icons.Filled.Edit,
                            contentDescription = "Edit standard times",
                            showLabels = showLabels,
                            labelText = "Edit Times",
                            containerColor = Color(0xFFFF9800)
                        )

                        FragID_0_Butt_1(viewModel, showLabels, "Start Recording")
                    }

                    FragID_0_Butt_3(viewModel, showLabels, "Mode Admin")

                    LabelsButton(
                        showLabels = showLabels,
                        onShowLabelsChange = { showLabels = it }
                    )
                }

                MenuButton(
                    showLabels = showLabels,
                    showMenu = showMenu,
                    onShowMenuChange = { showMenu = it }
                )
            }
        }
    }
}

@Composable
fun StandardTimesDialog(
    currentTimes: Standart_times,
    onDismiss: () -> Unit,
    onConfirm: (Standart_times) -> Unit
) {
    var startAbdelmoumen by remember { mutableStateOf(currentTimes.start_abdelmoumen) }
    var endAbdelmoumen by remember { mutableStateOf(currentTimes.end_abdelmoumen) }
    var startWalid by remember { mutableStateOf(currentTimes.start_walid) }
    var endWalid by remember { mutableStateOf(currentTimes.end_walid) }

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
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        Standart_times(
                            start_abdelmoumen = startAbdelmoumen,
                            end_abdelmoumen = endAbdelmoumen,
                            start_walid = startWalid,
                            end_walid = endWalid
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
