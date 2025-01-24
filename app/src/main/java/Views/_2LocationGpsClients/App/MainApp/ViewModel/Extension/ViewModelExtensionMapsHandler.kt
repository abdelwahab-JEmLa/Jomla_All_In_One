package Views._2LocationGpsClients.App.MainApp.ViewModel.Extension

import Z_MasterOfApps.Kotlin.Model.ClientsDataBase
import Z_MasterOfApps.Kotlin.Model.ClientsDataBase.Companion.updateClientsDataBase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

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
        Log.d("MapDebug", "Map Center - Lat: ${center.latitude}, Lon: ${center.longitude}")
        require(center.latitude != 0.0) { "Invalid latitude value" }

        val newID = if (clientDataBaseSnapList.isEmpty()) {
            1L // Start with 1 if the list is empty
        } else {
            clientDataBaseSnapList.maxOf { it.id } + 1
        }
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
                }
            }

        viewModel._modelAppsFather.clientDataBaseSnapList.add(newClient)

        ClientsDataBase.refClientsDataBase
            .child(newClient.id.toString())
            .setValue(newClient)
    }
    fun updateStatueClient(
        selectedMarker: Marker?,
        statueVente: ClientsDataBase.GpsLocation.DernierEtatAAffiche
    ) {
        clientDataBaseSnapList.toMutableList().forEach { client ->
            if (client.id == selectedMarker?.id?.toLong()) {
                client.gpsLocation.actuelleEtat = statueVente
                updateClientsDataBase(client,viewModel)
            }
        }
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
        */




}


