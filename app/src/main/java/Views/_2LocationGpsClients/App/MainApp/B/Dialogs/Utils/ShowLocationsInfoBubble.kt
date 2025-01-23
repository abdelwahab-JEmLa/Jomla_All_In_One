package com.example.Packages.Views._2LocationGpsClients.App.MainApp.B.Dialogs.Utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import Views._2LocationGpsClients.App.MainApp.B.Dialogs.ControlButton

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
