package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components.LabelsButton
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components.MenuButton
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components.StandardTimesDialog
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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

