package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Models.C3_BonAchate
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

suspend fun displayLatestTransactions(
    mapView: MapView,
    viewModel: ViewModel_MapClients_App2FragID1,
    onMarkerSelected: (Marker) -> Unit,
) {
    val latestTransactionsMap = viewModel.repo_0_0_HeadSQLRepositorys.repositorys_Model
        .repository_1_3_TransactionCommercial.modelDatasSnapList
        .groupBy { it.clientAcheteurID }
        .mapValues { (_, transactions) ->
            transactions.maxByOrNull { it.timestamps }
        }

    latestTransactionsMap.forEach { (clientId, latestTransaction) ->
        if (latestTransaction?.etateActuellementEst == C3_BonAchate.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT ||
            latestTransaction?.ouvert == true
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
    viewModel.repo_0_0_HeadSQLRepositorys.repositorys_Model
        .repository_1_3_TransactionCommercial.modelDatasSnapList
        .filter { it.ouvert }
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
