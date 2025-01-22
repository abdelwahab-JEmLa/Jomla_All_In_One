package Views._2LocationGpsClients.App.MainApp

import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel.BonStatueDeBase.StatueDeCetteVent
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import com.example.clientjetpack.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import java.util.Date

class ViewModelExtensionMapsHandler(
    val viewModel: ViewModelInitApp,
    val produitsMainDataBase: MutableList<_ModelAppsFather.ProduitModel>,
    val modelAppsFather: _ModelAppsFather
) {
    suspend fun handleMarkerClick(
        selectedMarker: Marker?,
        statueVente: StatueDeCetteVent,
        produitsMainDataBase: MutableList<_ModelAppsFather.ProduitModel>
    ) {
        if (selectedMarker == null) {
            Log.d("MarkerHandler", "No marker selected")
            return
        }
        //-->
        //TODO(1): fait au click au infobull de chaneg la couleur de backgond au roge
        // et im

        withContext(Dispatchers.IO) {
            try {
                // Find or create product with ID 0
                val product = produitsMainDataBase.find { it.id == 0L }
                    ?: _ModelAppsFather.ProduitModel(id = 0L).also {
                        produitsMainDataBase.add(it)
                    }

                // Find the bon vent with the selected marker
                val bonVent = product.historiqueBonsVents.find { bonVent ->
                    val marker = bonVent.clientInformations?.gpsLocation?.locationGpsMark
                    marker?.id == selectedMarker.id
                }

                if (bonVent != null) {
                    // Update the status
                    bonVent.bonStatueDeBase = _ModelAppsFather.ProduitModel.ClientBonVentModel.BonStatueDeBase().apply {
                        currentStatue = statueVente
                    }

                    // Update marker appearance based on new status
                    updateMarkerAppearance(selectedMarker, statueVente)

                    // Add timestamp to marker snippet
                    val currentDate = Date()
                    selectedMarker.snippet = """
                        Status: ${statueVente.name}
                        Updated: $currentDate
                    """.trimIndent()

                    // Update marker info window if it's showing
                    if (selectedMarker.isInfoWindowShown) {
                        selectedMarker.showInfoWindow()
                    }

                    // Update Firebase
                    _ModelAppsFather.updateProduit(product, viewModel)

                    Log.d("MarkerHandler", "Successfully updated marker status to ${statueVente.name}")
                } else {
                    Log.w("MarkerHandler", "No matching bon vent found for selected marker")
                }

            } catch (e: Exception) {
                Log.e("MarkerHandler", "Error handling marker click", e)
                throw e
            }
        }
    }

    private fun updateMarkerAppearance(marker: Marker, status: StatueDeCetteVent) {
        val color = when (status) {
            StatueDeCetteVent.CLIENT_ABSENT -> "#FF0000"      // Red
            StatueDeCetteVent.AVEC_MARCHANDISE -> "#00FF00"   // Green
            StatueDeCetteVent.FERME -> "#808080"              // Gray
        }

        // Update marker color
        marker.setTextIcon(status.name)  // Set status text on marker

        // Update the gps location color in the data model
        val gpsLocation = findGpsLocationForMarker(marker)
        gpsLocation?.couleur = color
    }

    private fun findGpsLocationForMarker(marker: Marker): _ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations.GpsLocation? {
        produitsMainDataBase.forEach { product ->
            product.historiqueBonsVents.forEach { bonVent ->
                if (bonVent.clientInformations?.gpsLocation?.locationGpsMark == marker) {
                    return bonVent.clientInformations?.gpsLocation
                }
            }
        }
        return null
    }

    suspend fun clearAllData(mapView: MapView?) {
        try {
            // 1. Clear UI elements first
            mapView?.let { map ->
                map.overlays.clear()
                map.invalidate()
            }

            // 2. Clear local markers and data
            produitsMainDataBase.forEach { produit ->
                produit.bonsVentDeCetteCota.forEach { bonVent ->
                    bonVent.clientInformations?.gpsLocation?.locationGpsMark?.let { marker ->
                        marker.closeInfoWindow()
                        marker.remove(mapView)
                    }
                    bonVent.clientInformations?.gpsLocation?.locationGpsMark = null
                }
            }

            // 3. Remove data from Firebase with correct path structure
            produitsMainDataBase.forEach { produit ->
                produit.historiqueBonsVents.forEachIndexed { index, _ ->
                    try {
                        // Delete all GPS related data for each client
                        val gpsRef = produitsFireBaseRef
                            .child(produit.id.toString())
                            .child("historiqueBonsVents")
                            .child(index.toString())
                            .child("clientInformations")
                            .child("gpsLocation")

                        // Delete specific fields within gpsLocation
                        gpsRef.child("latitude").removeValue().await()
                        gpsRef.child("longitude").removeValue().await()
                        gpsRef.child("title").removeValue().await()
                        gpsRef.child("snippet").removeValue().await()
                        gpsRef.child("couleur").removeValue().await()

                        Log.d(
                            "FirebaseCleanup",
                            "Cleared GPS data for product ${produit.id}, bon vent index $index"
                        )
                    } catch (e: Exception) {
                        Log.e(
                            "FirebaseCleanup",
                            "Failed to clear GPS data for product ${produit.id}, bon vent index $index",
                            e
                        )
                    }
                }
            }

            // Special handling for product with ID 0
            val productZeroRef = produitsFireBaseRef.child("0")
            try {
                productZeroRef
                    .child("historiqueBonsVents")
                    .get()
                    .await()
                    .children
                    .forEach { snapshot ->
                        val clientRef = snapshot.ref
                            .child("clientInformations")
                            .child("gpsLocation")

                        // Delete specific fields within gpsLocation
                        clientRef.child("latitude").removeValue().await()
                        clientRef.child("longitude").removeValue().await()
                        clientRef.child("title").removeValue().await()
                        clientRef.child("snippet").removeValue().await()
                        clientRef.child("couleur").removeValue().await()
                    }

                Log.d("FirebaseCleanup", "Successfully cleared product 0 GPS data")
            } catch (e: Exception) {
                Log.e("FirebaseCleanup", "Failed to clear product 0 GPS data", e)
            }

            Log.d(
                "FirebaseCleanup",
                "Successfully cleared all data from UI, local storage, and Firebase"
            )
        } catch (e: Exception) {
            Log.e("FirebaseCleanup", "Failed to clear data", e)
            throw e
        }
    }
    fun onClickAddMarkerButton(
        mapView: MapView,
        onMarkerSelected: (Marker) -> Unit,
        showMarkerDetails: Boolean,
        markers: MutableList<Marker>
    ) {
        val center = mapView.mapCenter
        val newID = modelAppsFather.clientsDisponible
            .maxOf { it.id } + 1
        val newnom = "Nouveau client #$newID"

        val newClient =
            _ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations(
                id = newID,
                nom = newnom
            ).apply {
                statueDeBase.cUnClientTemporaire = true
                gpsLocation.apply {
                    latitude = center.latitude
                    longitude = center.longitude
                    title = newnom
                    snippet = "Client temporaire"
                    couleur = "#2196F3"

                    locationGpsMark = Marker(mapView).apply {
                        id= newID.toString()
                        position = GeoPoint(latitude, longitude)
                        this.title = title
                        this.snippet = snippet
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        infoWindow = MarkerInfoWindow(R.layout.marker_info_window, mapView)
                        setOnMarkerClickListener { marker, _ ->
                            onMarkerSelected(marker)
                            if (showMarkerDetails) marker.showInfoWindow()
                            true
                        }
                    }
                }
            }

        val newBonVent = _ModelAppsFather.ProduitModel.ClientBonVentModel(
            vid = System.currentTimeMillis(),
            init_clientInformations = newClient
        )

        val product = produitsMainDataBase.find { it.id == 0L }
            ?: _ModelAppsFather.ProduitModel(id = 0L).also {
                produitsMainDataBase.add(it)
            }

        product.historiqueBonsVents.add(newBonVent)

        newClient.gpsLocation.locationGpsMark?.let { marker ->
            markers.add(marker)
            mapView.overlays.add(marker)
            if (showMarkerDetails) marker.showInfoWindow()
        }
        mapView.invalidate()

        _ModelAppsFather.updateProduit(product, viewModel)
    }
}
