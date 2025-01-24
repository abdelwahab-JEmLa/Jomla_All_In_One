package Views._2LocationGpsClients.App.MainApp.ViewModel.Extension

import Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.Utils.updateAncienClientDataBase
import Z_MasterOfApps.Kotlin.Model.ClientsDataBase
import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class ViewModelExtensionMapsHandler(
    val viewModelScope: CoroutineScope,
    val produitsMainDataBase: MutableList<ProduitModel>,
    var clientDataBaseSnapList: SnapshotStateList<ClientsDataBase>,
    val viewModel: ViewModelInitApp,
    val modelAppsFather: _ModelAppsFather,
) {
    /*
    fun alimentclientDBDepuitCalcule (): Unit {
        viewModel.clientsDisponible.forEach {
            viewModel._modelAppsFather.clientDataBaseSnapList.add(
                ClientsDataBase(
                    id = it.id ,
                    nom= it.nom
                ) .apply {
                    statueDeBase=it.statueDeBase
                }
            )
        }


    }      */

    private fun ClientInformations.updateProduitsClientInfoParThis(
    ) {
        produitsMainDataBase.find { it.id==0L }?.apply {
            historiqueBonsVents.forEach {
                if (it.clientInformations == this) {
                    it.clientInformations = this

                    updateProduit(this, viewModel)
                }
            }


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
                    geoPoint= center as GeoPoint?
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


