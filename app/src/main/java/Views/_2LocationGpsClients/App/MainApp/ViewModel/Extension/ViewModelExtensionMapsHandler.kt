package Views._2LocationGpsClients.App.MainApp.ViewModel.Extension

import Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.Utils.updateAncienClientDataBase
import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import kotlinx.coroutines.CoroutineScope
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class ViewModelExtensionMapsHandler(
    val viewModelScope: CoroutineScope,
    val produitsMainDataBase: MutableList<ProduitModel>,
    val clientsDisponible: List<ClientInformations>,
    val viewModel: ViewModelInitApp,
    val modelAppsFather: _ModelAppsFather
) {
    private fun ClientInformations.updateProduitsClientInfoParThis(
    ) {
        produitsMainDataBase.forEach { produit ->
            produit.historiqueBonsVents.forEach {
                if (it.clientInformations == this) {
                    it.clientInformations = this

                    updateProduit(produit, viewModel)
                }
            }
        }
    }

    fun updateStatueClient(
        selectedMarker: Marker?,
        statueVente: ClientInformations.GpsLocation.DernierEtatAAffiche
    ) {
        clientsDisponible.toMutableList().forEach { client ->
            if (client.gpsLocation.locationGpsMark?.id == selectedMarker?.id) {
                client.gpsLocation.actuelleEtat = statueVente
                client.updateProduitsClientInfoParThis()
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
            ClientInformations(
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

        updateProduit(product, viewModel)
    }
}


