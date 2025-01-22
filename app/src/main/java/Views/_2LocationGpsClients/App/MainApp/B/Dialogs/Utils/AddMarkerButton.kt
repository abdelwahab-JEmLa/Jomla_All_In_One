package com.example.Packages.Views._2LocationGpsClients.App.MainApp.B.Dialogs.Utils

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.B.Dialogs.ControlButton
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun AddMarkerButton(
    markers: MutableList<Marker>,
    viewModelInitApp: ViewModelInitApp,
    showLabels: Boolean,
    mapView: MapView,
    showMarkerDetails: Boolean,
    onMarkerSelected: (Marker) -> Unit
) {
    ControlButton(
        onClick = {
            viewModelInitApp.onClickAddMarkerButton(
                mapView,
                onMarkerSelected,
                showMarkerDetails,
                markers
            )
        },
        icon = Icons.Default.Add,
        contentDescription = "Add marker",
        showLabels = showLabels,
        labelText = "Add",
        containerColor = Color(0xFF2196F3)
    )
}
