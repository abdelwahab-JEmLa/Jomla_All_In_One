package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

@Composable
fun FragID_0_Butt_1(
    viewModel: RecordingViewModel,
    showLabels: Boolean,
    label: String,
) {
    val abdelwahabLeGerant by viewModel.isAbdelwahabLeGerant.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val displayTime by viewModel.displayTime.collectAsState()

    ControlButton(
        onClick = {
        },
        icon = if (isRecording) Icons.Default.Stop else Icons.Default.PlayArrow,
        contentDescription = if (isRecording) "Stop Recording" else label,
        showLabels = showLabels,
        labelText = displayTime,
        containerColor = if (isRecording) Color(0xFF2196F3) else Color(0xFFF44336),
        enabled = abdelwahabLeGerant // Add this line to disable the button if not Abdelwahab Le Gérant
    )

}
