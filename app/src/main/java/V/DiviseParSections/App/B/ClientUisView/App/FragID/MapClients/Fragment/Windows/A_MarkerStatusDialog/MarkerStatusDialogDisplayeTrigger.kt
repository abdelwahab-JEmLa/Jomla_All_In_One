package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

suspend fun displayLatestTransactions(
    mapView: MapView,
    viewModel: ViewModel_MapClients_App2FragID1,
    onMarkerSelected: (Marker) -> Unit,
) {
    val latestTransactionsMap = viewModel.groupeRepositorysProtoAvJuin3.repositorys_Model
        .c3_BonAchate_Repository.modelDatasSnapList
        .groupBy { it.clientAcheteurID }
        .mapValues { (_, transactions) ->
            transactions.maxByOrNull { it.timestamps }
        }

    latestTransactionsMap.forEach { (clientId, latestTransaction) ->
        if (latestTransaction?.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
       /*     ||
            latestTransaction?.tagCeBonEstOuvertPourComptsIds == true   */
        ) {
            val marker = mapView.overlays.filterIsInstance<Marker>()
                .find { it.id == clientId.toString() }

            marker?.let {
                onMarkerSelected(it)
            }
        }
    }
}

suspend fun displayOpenTransactions(
    mapView: MapView,
    viewModel: ViewModel_MapClients_App2FragID1,
    onMarkerSelected: (Marker) -> Unit,
) {
    viewModel.groupeRepositorysProtoAvJuin3.repositorys_Model
        .c3_BonAchate_Repository.modelDatasSnapList
     //   .filter { it.tagCeBonEstOuvertPourComptsIds }
        .forEach { transaction ->
            val marker = mapView.overlays.filterIsInstance<Marker>()
                .find { it.id == transaction.clientAcheteurID.toString() }

            marker?.let {
                onMarkerSelected(it)
                it.showInfoWindow()
                mapView.controller.animateTo(it.position)
            }
        }
}
