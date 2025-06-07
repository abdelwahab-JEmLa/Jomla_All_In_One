package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.LabelsButton
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.MenuButton
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.ButtonMessageVocale
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun FabButtonsMessageurMainScreen(
    viewModel: ViewModelMessageur,
) {
    var showMenu by remember { mutableStateOf(true) }
    var showLabels by remember { mutableStateOf(true) }

    // Get screen configuration to position at the right edge
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

// Initialize offset to start at the right edge of the screen
    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 180f)) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value + 100f) }

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
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showMenu) {
                    ButtonMessageVocale(
                        viewModel = viewModel
                    )
                }

                LabelsButton(
                    showLabels = showLabels,
                    onShowLabelsChange = { showLabels = it }
                )

                MenuButton(
                    showLabels = showLabels,
                    showMenu = showMenu,
                    onShowMenuChange = { showMenu = it }
                )
            }
        }
    }
}
