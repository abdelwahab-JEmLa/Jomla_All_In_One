package Views._2LocationGpsClients.App.MainApp.ViewModel.Extension

import Z_MasterOfApps.Kotlin.Model.ClientsDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class ViewModelExtensionMapsHandler(
    val viewModelScope: CoroutineScope,
    val produitsMainDataBase: MutableList<ProduitModel>,
    val clientDataBaseSnapList: SnapshotStateList<ClientsDataBase>,
    val viewModel: ViewModelInitApp,
) {
    fun onClickAddMarkerButton(
        mapView: MapView,
    ) {
        val center = mapView.mapCenter
        val newID = clientDataBaseSnapList
            .maxOf { it.id } + 1
        val newnom = "Nouveau client #$newID"

        val newClient =
            ClientsDataBase(
                id = newID,
                nom = newnom
            ).apply {
                statueDeBase.cUnClientTemporaire = true
                gpsLocation.apply {
                    latitude = center.latitude
                    longitude = center.longitude
                    title = newnom
                    snippet = "Client temporaire"
                    geoPoint= center as GeoPoint?
                }
            }

        viewModel._modelAppsFather.clientDataBaseSnapList.add(newClient)

        ClientsDataBase.refClientsDataBase
            .child(newClient.id.toString())
            .setValue(newClient)

    }
      /*
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
       */

}


