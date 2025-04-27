package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.LabelsButton
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.MenuButton
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
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import kotlin.math.roundToInt

@Composable
fun FabButtons(
    viewModel: ViewModel_MapClients_App2FragID1,
) {
    var showMenu by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(false) }

    // États pour le drag
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Get dialog states from ViewModel
    val showSecteurDialog by viewModel.showSecteurDialog
    val showAddSecteurDialog by viewModel.showAddSecteurDialog

    // Display dialogs if needed
    if (showSecteurDialog) {
        SecteurDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.hideSecteurDialog() }
        )
    }

    if (showAddSecteurDialog) {
        AddSecteurDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.hideAddSecteurDialog() }
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
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showMenu) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val couleurButton1 = Color(0xFFF44336)
                        FloatingActionButton(
                            onClick = {
                                viewModel.showSecteurDialog()
                            },
                            modifier = Modifier.size(40.dp),
                            containerColor = couleurButton1
                        ) {
                            Icon(Icons.Filled.FilterAlt, "Filter by sectors")
                        }

                        if (showLabels) {
                            Text("قطاعات العملاء", // Client Sectors
                                modifier = Modifier
                                    .background(couleurButton1)
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }

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
