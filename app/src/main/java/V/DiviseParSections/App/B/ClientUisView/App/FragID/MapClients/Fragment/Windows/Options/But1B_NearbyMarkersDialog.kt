package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options

import android.location.Location
import androidx.compose.runtime.Composable
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun But1B_NearbyMarkersDialog(
    onDismiss: () -> Unit,
    markers: List<Marker>,
    currentLocation: Location?,
    proxim: Double,
    mapView: MapView,
) {
    /*
    // RepositorysMainGetter reference location with proper fallback handling
    val referenceLocation = remember(currentLocation, mapView.mapCenter) {
        currentLocation ?: Location("map_center").apply {
            val center = mapView.mapCenter
            latitude = center.latitude
            longitude = center.longitude
        }
    }

    // Filter markers based on distance, with null safety
    val nearbyMarkers = remember(markers, referenceLocation, proxim) {
        markers.filter { marker ->
            try {
                val markerLocation = Location("marker").apply {
                    latitude = marker.position.latitude
                    longitude = marker.position.longitude
                }
                referenceLocation.distanceTo(markerLocation) <= proxim
            } catch (e: Exception) {
                false // Skip markers with invalid positions
            }
        }
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    var editedName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Markers Within $proxim Meters of ${
                    if (currentLocation != null) "Current Location"
                    else "Map Center"
                }"
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(nearbyMarkers) { marker ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = marker.title ?: "Unnamed Location",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Distance: ${
                                        try {
                                            val markerLocation = Location("marker").apply {
                                                latitude = marker.position.latitude
                                                longitude = marker.position.longitude
                                            }
                                            "${
                                                referenceLocation.distanceTo(markerLocation)
                                                    .toInt()
                                            }m"
                                        } catch (e: Exception) {
                                            "Unknown"
                                        }
                                    }",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            IconButton(
                                onClick = {
                                    selectedMarker = marker
                                    editedName = marker.title ?: ""
                                    showEditDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit name")
                            }
                        }
                    }
                }
                if (nearbyMarkers.isEmpty()) {
                    item {
                        Text(
                            "No markers found within $proxim meters of ${
                                if (currentLocation != null) "your location"
                                else "the map center"
                            }"
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )

    if (showEditDialog && selectedMarker != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Name") },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Location Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedMarker?.let { marker ->
                            // Update marker title
                            marker.title = editedName

                            // Find and upsertLenceCommandeRepoGroupedProtoAvantJuin3 the corresponding client in the database
                            val client = viewModelInitApp._modelAppsFather.clientDataBase
                                .find {
                                    it.gpsLocation.latitude == marker.position.latitude
                                            && it.gpsLocation.longitude == marker.position.longitude
                                }

                            client?.let { foundClient ->
                                // Update client name
                                foundClient.nom = editedName

                                val product =
                                    viewModelInitApp.produitsMainDataBase.find { produit ->
                                        produit.bonsVentDeCetteCota.any { bonVent ->
                                            bonVent.clientIdChoisi == foundClient.keyID
                                        }
                                    }

                                product?.let { foundProduct ->
                                    // Update the product in the database
                                    _ModelAppsFather.updateProduit(
                                        foundProduct,
                                        viewModelInitApp
                                    )
                                }
                            }

                            mapView.invalidate()
                        }
                        showEditDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }          */
}
