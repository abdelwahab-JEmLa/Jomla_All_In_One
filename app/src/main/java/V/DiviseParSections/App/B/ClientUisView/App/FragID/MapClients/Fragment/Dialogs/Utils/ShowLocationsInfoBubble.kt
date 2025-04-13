package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.Utils

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.A.ControlButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ShowLocationsInfoBubble(
    showLabels: Boolean,
    showMarkerDetails: Boolean,
    onShowMarkerDetailsChange: (Boolean) -> Unit
) {
    ControlButton(
        onClick = { onShowMarkerDetailsChange(!showMarkerDetails) },
        icon = Icons.Default.Info,
        contentDescription = "Details",
        showLabels = showLabels,
        labelText = if (showMarkerDetails) "Hide details" else "Show details",
        containerColor = Color(0xFF009688)
    )
}
