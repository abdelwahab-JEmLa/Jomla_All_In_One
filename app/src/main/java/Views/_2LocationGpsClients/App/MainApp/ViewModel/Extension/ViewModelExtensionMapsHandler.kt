package Views._2LocationGpsClients.App.MainApp.ViewModel.Extension

import Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.Utils.findBonVentForMarker
import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel.BonStatueDeBase.StatueDeCetteVent
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Models.ClientsModel
import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.clientjetpack.R
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import java.util.Date

class ViewModelExtensionMapsHandler(
    val viewModelScope: CoroutineScope,
    val produitsMainDataBase: MutableList<ProduitModel>,
    val clientsDisponible: List<ClientBonVentModel.ClientInformations>,
    val viewModel: ViewModelInitApp,
    val modelAppsFather: _ModelAppsFather
) {

    // Extension function to update marker info window style
    suspend fun handleDialialogeClientMarkClick(
        selectedMarker: Marker?,
        statueVente: StatueDeCetteVent,
    ) {
        if (selectedMarker == null) {
            Log.d("MarkerHandler", "No marker selected")
            return
        }

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
                    bonVent.bonStatueDeBase =
                        _ModelAppsFather.ProduitModel.ClientBonVentModel.BonStatueDeBase().apply {
                            currentStatue = statueVente
                        }

                    selectedMarker.safeUpdateInfoWindow(
                        context = selectedMarker.infoWindow.view.context
                    )
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

                    Log.d(
                        "MarkerHandler",
                        "Successfully updated marker status to ${statueVente.name}"
                    )
                } else {
                    Log.w("MarkerHandler", "No matching bon vent found for selected marker")
                }

            } catch (e: Exception) {
                Log.e("MarkerHandler", "Error handling marker click", e)
                throw e
            }
        }
    }


    // Extension function to safely update marker info window
    fun Marker.safeUpdateInfoWindow(context: Context) {
        try {
            val infoWindow = this.infoWindow as? MarkerInfoWindow ?: return
            val container = infoWindow.view.findViewById<LinearLayout>(R.id.info_window_container) ?: return

            val bonVent = findBonVentForMarker(this)
            val backgroundColor = bonVent?.bonStatueDeBase?.currentStatue?.let { statue ->
                ContextCompat.getColor(context, statue.color)
            } ?: ContextCompat.getColor(context, android.R.color.white)

            container.setBackgroundColor(backgroundColor)

            if (isInfoWindowShown) {
                closeInfoWindow()
                showInfoWindow()
            }
        } catch (e: Exception) {
            Log.e("Marker", "Error updating info window", e)
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
                        id = newID.toString()
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

        updateAncienClientDataBase(newClient)

        _ModelAppsFather.updateProduit(product, viewModel)
    }

}

fun updateAncienClientDataBase(newClient: _ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations) {
    Firebase.database
        .getReference(newClient.statueDeBase.caRefDonAncienDataBase)
        .child(newClient.id.toString()).setValue(
            ClientsModel(
                idClientsSu = newClient.id,
                nomClientsSu = newClient.nom
            )
        )
}
