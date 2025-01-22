package com.example.Packages.Views._2LocationGpsClients.App.MainApp

import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import com.example.c_serveur.R
import kotlinx.coroutines.tasks.await
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

class ViewModelExtensionMapsHandler(
    val viewModel: ViewModelInitApp,
    val produitsMainDataBase: MutableList<_ModelAppsFather.ProduitModel>,
    val modelAppsFather: _ModelAppsFather
) {
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
