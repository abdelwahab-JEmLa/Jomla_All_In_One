package Views._2LocationGpsClients.App.MainApp.ViewModel.Extension

import Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.Utils.safeUpdateInfoWindows
import Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.Utils.updateAncienClientDataBase
import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel.BonStatueDeBase.StatueDeCetteVent
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
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
                    ?: ProduitModel(id = 0L).also {
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
                        ClientBonVentModel.BonStatueDeBase().apply {
                            currentStatue = statueVente
                        }

                    safeUpdateInfoWindows(
                        context = selectedMarker.infoWindow.view.context,
                        marker = selectedMarker
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

    fun onClickAddMarkerButton(
        mapView: MapView,
    ) {
        val center = mapView.mapCenter
        val newID = modelAppsFather.clientsDisponible
            .maxOf { it.id } + 1
        val newnom = "Nouveau client #$newID"

        val newClient =
            ClientBonVentModel.ClientInformations(
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

                }
            }

        val newBonVent = ClientBonVentModel(
            vid = System.currentTimeMillis(),
            init_clientInformations = newClient
        )

        val product = produitsMainDataBase.find { it.id == 0L }
            ?: ProduitModel(id = 0L).also {
                produitsMainDataBase.add(it)
            }

        product.historiqueBonsVents.add(newBonVent)

        updateAncienClientDataBase(newClient)

        _ModelAppsFather.updateProduit(product, viewModel)
    }
}


