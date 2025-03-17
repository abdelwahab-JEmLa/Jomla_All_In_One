package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.Ui.Windows

import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

@Composable
fun FragID_0_Butt_1(
    viewModel: Windows__ViewModel,
    showLabels: Boolean,
    label: String,
) {
    val abdelwahabLeGerant by viewModel.isAbdelwahabLeGerant.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val displayTime by viewModel.displayTime.collectAsState()

    ControlButton(
        onClick = {
            viewModel.toggleRecording()
        },
        icon = if (isRecording) Icons.Default.PlayArrow else Icons.Default.PlayArrow,
        contentDescription = if (isRecording) "Stop Recording" else label,
        showLabels = showLabels,
        labelText = displayTime,
        containerColor = if (isRecording) Color(0xFFE53935) else Color(0xFFF44336),
        enabled = abdelwahabLeGerant // Add this line to disable the button if not Abdelwahab Le Gérant
    )

}
