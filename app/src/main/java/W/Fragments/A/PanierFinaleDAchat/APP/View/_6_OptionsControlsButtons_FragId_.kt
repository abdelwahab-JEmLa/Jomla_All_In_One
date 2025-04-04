package W.Fragments.A.PanierFinaleDAchat.APP.View

import W.Fragments.A.PanierFinaleDAchat.APP.ViewModel.ViewModelFragment_APP2_FragID_1
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components.LabelsButton
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.Components.MenuButton
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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

const val TAG = "ControlButton"

@Composable
fun A_OptionsControlsButtons_FragId_7(
    viewModel: ViewModelFragment_APP2_FragID_1 = koinViewModel(),
) {
    var showMenu by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(true) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Collect UI state to check if filtering is active
    val uiState by viewModel.uiStateFlow.collectAsState()
    val isFilterActive = uiState.isFilteringActive

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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                // Toggle filter: If active, reset it; if not active, filter by last period
                                if (isFilterActive) {
                                    viewModel.resetFilter()
                                } else {
                                    viewModel.filterByLastPeriode()
                                }
                            },
                            modifier = Modifier.size(40.dp),
                            containerColor = if (isFilterActive) Color(0xFF2196F3) else Color(0xFF4CAF50)
                        ) {
                            Icon(
                                Icons.Filled.FilterList,
                                contentDescription = "Toggle Last Period Filter"
                            )
                        }

                        if (showLabels) {
                            Text(
                                if (isFilterActive) "Clear Filter" else "Last Period",
                                modifier = Modifier
                                    .background(if (isFilterActive) Color(0xFF2196F3) else Color(0xFF4CAF50))
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }

                    // Labels Button
                    LabelsButton(
                        showLabels = showLabels,
                        onShowLabelsChange = { showLabels = it }
                    )
                }

                // Menu Button
                MenuButton(
                    showLabels = showLabels,
                    showMenu = showMenu,
                    onShowMenuChange = { showMenu = it }
                )
            }
        }
    }
}
