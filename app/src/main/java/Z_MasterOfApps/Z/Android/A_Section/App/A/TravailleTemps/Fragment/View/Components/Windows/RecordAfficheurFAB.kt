package Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.Components.Windows

import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.ControlButton
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
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

@Composable
fun RecordAfficheurFAB(
    viewModel: Windows__ViewModel = koinViewModel(),
) {

    var showLabels by remember { mutableStateOf(true) }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

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
                val abdelwahabLeGerant by viewModel.isAbdelwahabLeGerant.collectAsState()
                val isRecording by viewModel.isRecording.collectAsState()
                val displayTime by viewModel.displayTime.collectAsState()

                ControlButton(
                    onClick = {
                        viewModel.toggleRecording()
                    },
                    icon = if (isRecording) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isRecording) "Stop Recording" else "",
                    showLabels = showLabels,
                    labelText = displayTime,
                    containerColor = if (isRecording) Color(0xFFE53935) else Color(0xFFF44336),
                    enabled = abdelwahabLeGerant // Add this line to disable the button if not Abdelwahab Le Gérant
                )
            }
        }
    }
}
