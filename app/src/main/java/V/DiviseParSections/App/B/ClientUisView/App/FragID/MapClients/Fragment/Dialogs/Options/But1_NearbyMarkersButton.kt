package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.Options

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.Utils.LocationTracker
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_App2FragID1
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun But1_NearbyMarkersButton(
    showLabels: Boolean,
    viewModelInitApp: ViewModelInitApp,
    markers: MutableList<Marker>,
    locationTracker: LocationTracker,
    proximiteMeter: Double,
    mapView: MapView,
    viewModel: ViewModel_App2FragID1,
) {
    var showNearbyMarkersDialog by remember { mutableStateOf(false) }

    ControlButton(
        onClick = { showNearbyMarkersDialog = true },
        icon = LottieJsonGetterR_Raw_Icons.reacticonanimatedjsonurl,
        contentDescription = "Show nearby markers",
        showLabels = showLabels,
        labelText = "Nearby",
        containerColor = Color(0xFF2196F3)
    )

    if (showNearbyMarkersDialog) {
        But1B_NearbyMarkersDialog(
            viewModelInitApp = viewModelInitApp,
            onDismiss = { showNearbyMarkersDialog = false },
            markers = markers,
            currentLocation = locationTracker.currentLocation,
            proxim = proximiteMeter,
            mapView = mapView
        )
    }
}
