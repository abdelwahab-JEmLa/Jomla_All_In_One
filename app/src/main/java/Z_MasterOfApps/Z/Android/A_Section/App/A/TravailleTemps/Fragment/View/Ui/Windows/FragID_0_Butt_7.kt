package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.Ui.Windows

import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

@Composable
fun FragID_0_Butt_7(
    viewModel: Windows__ViewModel,
    showLabels: Boolean,
) {
    // Collect recording state
    val isRecording by viewModel.isRecording.collectAsState()
    val displayTime by viewModel.displayTime.collectAsState()

    ControlButton(
        onClick = {
            viewModel.toggleRecording()
        },
        icon = if (isRecording) Icons.Default.Stop else Icons.Default.PlayArrow,
        contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
        showLabels = showLabels,
        labelText = displayTime,
        containerColor = if (isRecording) Color(0xFFE53935) else Color(0xFF2196F3)
    )
}
