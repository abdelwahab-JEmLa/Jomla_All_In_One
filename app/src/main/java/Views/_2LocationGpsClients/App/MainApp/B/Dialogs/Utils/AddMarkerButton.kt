package com.example.Packages.Views._2LocationGpsClients.App.MainApp.B.Dialogs.Utils

import Views._2LocationGpsClients.App.MainApp.B.Dialogs.ControlButton
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.osmdroid.views.MapView

@Composable
fun AddMarkerButton(
    viewModelInitApp: ViewModelInitApp,
    showLabels: Boolean,
    mapView: MapView,
) {
    ControlButton(
        onClick = {
            viewModelInitApp.mapsHandler
                .onClickAddMarkerButton(
                    mapView
                )
        },
        icon = Icons.Default.Add,
        contentDescription = "Add marker",
        showLabels = showLabels,
        labelText = "Add",
        containerColor = Color(0xFF2196F3)
    )
}
