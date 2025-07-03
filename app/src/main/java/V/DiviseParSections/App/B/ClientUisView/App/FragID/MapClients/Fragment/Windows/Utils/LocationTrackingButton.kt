package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options.ControlButton
import V.DiviseParSections.App.Shared.Modules.Helper.M1.LocationTracker.Module.rememberLocationTracker
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import org.osmdroid.views.MapView

@Composable
fun LocationTrackingButton(
    showLabels: Boolean,
    mapView: MapView,
    proximiteMeter: Double,
    xmlResources: List<Pair<String, Int>>?
) {
    var isTracking by remember { mutableStateOf(false) }
    val locationTracker = rememberLocationTracker(mapView, proximiteMeter,xmlResources)

    ControlButton(
        onClick = {
            isTracking = !isTracking
            if (isTracking) {
                locationTracker.startTracking()
            } else {
                locationTracker.stopTracking()
            }
        },
        icon = Icons.Default.LocationOn,
        contentDescription = if (isTracking) "Stop tracking" else "Start tracking",
        showLabels = showLabels,
        labelText = if (isTracking) "Stop tracking" else "Start tracking",
        containerColor = if (isTracking) Color(0xFF4CAF50) else Color(0xFF9C27B0)
    )
}

