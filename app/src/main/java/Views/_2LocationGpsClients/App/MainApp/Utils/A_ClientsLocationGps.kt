package com.example.Packages.Views._2LocationGpsClients.App.MainApp.Utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.views.overlay.Marker

@Composable
fun NavigationDialog(
    onDismiss: () -> Unit,
    onConfirm: (Marker) -> Unit,
    marker: Marker
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Navigation") },
        text = { Text("Voulez-vous démarrer la navigation vers ce point ?") },
        confirmButton = {
            Button(onClick = { onConfirm(marker); onDismiss() }) {
                Text("Démarrer")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

 const val DEFAULT_LATITUDE = 36.7389350566438
const val DEFAULT_LONGITUDE = 3.1720169070695476

// Update getCurrentLocation function to be suspend
suspend fun getCurrentLocation(context: Context): Location? = withContext(Dispatchers.IO) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return@withContext locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    }
    return@withContext null
}
