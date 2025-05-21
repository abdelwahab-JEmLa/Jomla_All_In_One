package V.DiviseParSections.App.SectionID8.FloatingButtons.App.FragID1.Windows

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.ControlButton
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test.TariffsButtons_TestID2
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun PressistatntMainActivityButtons(
    viewModel: Windows__ViewModel = koinViewModel(),
    onPourFermeWindows: () -> Unit,
) {
    var showLabels by remember { mutableStateOf(true) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val isRecording by viewModel.isRecording.collectAsState()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Set up a timer to upsert_1_3_TransactionCommercial the elapsed time every second when recording
    DisposableEffect(isRecording) {
        var job: Job? = null
        val coroutineScope = CoroutineScope(Dispatchers.Main)

        if (isRecording) {
            job = coroutineScope.launch {
                while (true) {
                    viewModel.updateElapsedTime()
                    delay(1000) // Update every second
                }
            }
        }

        // Monitor lifecycle events to handle pause/resume correctly
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.onLifecycleResume()
                    if (isRecording && job == null) {
                        job = coroutineScope.launch {
                            while (true) {
                                viewModel.updateElapsedTime()
                                delay(1000)
                            }
                        }
                    }
                }

                Lifecycle.Event.ON_PAUSE -> {
                    if (isRecording) {
                        viewModel.onRecordingStopped()
                    }
                    job?.cancel()
                    job = null
                }

                else -> { /* do nothing */
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            job?.cancel()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
                val displayTime by viewModel.displayTime.collectAsState()

                ControlButton(
                    onClick = {
                        viewModel.toggleRecording()
                    },
                    icon = if (isRecording) Icons.Default.PlayArrow else Icons.Default.Stop,
                    contentDescription = if (isRecording) "Stop Recording" else "",
                    showLabels = showLabels,
                    labelText = displayTime,
                    containerColor = if (isRecording) Color(0xFFFF9800) else Color(0xFF8B8781),
                    enabled = false
                )

                TariffsButtons_TestID2(
                    showLabels = showLabels,
                    fermDialoge = onPourFermeWindows,
                )
            }
        }
    }
}

