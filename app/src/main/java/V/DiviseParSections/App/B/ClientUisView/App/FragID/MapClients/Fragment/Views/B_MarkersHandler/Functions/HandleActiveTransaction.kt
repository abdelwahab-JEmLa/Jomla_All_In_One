package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// Helper functions to support the refactored MapContent
fun handleActiveTransaction(
    activeTransactionId: Long,
    viewModel: ViewModel_MapClients_App2FragID1,
    mapView: MapView,
    onMarkerFound: (Marker) -> Unit,
) {
    if (activeTransactionId != 0L) {
        // Find the transaction to get the client ID
        val activeTransaction = viewModel.groupeRepositorysProtoAvJuin3.repositorys_Model
            .c3TransactionCommercialRepository.modelDatasSnapList
            .find { it.vid == activeTransactionId }

        activeTransaction?.let { transaction ->
            // Find the marker for this client
            val clientMarker = mapView.overlays.filterIsInstance<Marker>()
                .find { it.id == transaction.clientAcheteurID.toString() }

            clientMarker?.let { marker ->
                // Select the marker and show its information
                onMarkerFound(marker)
                marker.showInfoWindow()

                // Animate to the marker position
                mapView.controller.animateTo(marker.position)
            }
        }
    }
}
