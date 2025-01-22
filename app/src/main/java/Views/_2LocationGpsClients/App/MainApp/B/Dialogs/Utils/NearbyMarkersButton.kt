package com.example.Packages.Views._2LocationGpsClients.App.MainApp.B.Dialogs.Utils

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.location.Location
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.B.Dialogs.ControlButton
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
 fun NearbyMarkersButton(
    showLabels: Boolean,
    viewModelInitApp: ViewModelInitApp,
    markers: MutableList<Marker>,
    currentLocation: Location?,
    proximiteMeter: Double,
    mapView: MapView,
) {
    var showNearbyMarkersDialog by remember { mutableStateOf(false) }

    ControlButton(
        onClick = { showNearbyMarkersDialog = true },
        icon = Icons.Default.NearMe,
        contentDescription = "Show nearby markers",
        showLabels = showLabels,
        labelText = "Nearby",
        containerColor = Color(0xFFFF5722)
    )
    if (showNearbyMarkersDialog) {
        NearbyMarkersDialog(
            viewModelInitApp = viewModelInitApp,
            onDismiss = { showNearbyMarkersDialog = false },
            markers = markers,
            currentLocation = currentLocation,
            proxim = proximiteMeter,
            mapView = mapView
        )
    }
}
