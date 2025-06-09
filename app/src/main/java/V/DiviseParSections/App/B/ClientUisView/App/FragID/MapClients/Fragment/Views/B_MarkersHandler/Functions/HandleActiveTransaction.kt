package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// Helper functions to support the refactored MapContent
fun handleActiveTransaction(
    uiState: UiState,
    viewModel: ViewModel_MapClients_App2FragID1,
    onMarkerFound: (Marker) -> Unit,
) {
   val clients = uiState.b_ClientInfosProtoJuin3List



            clientMarker?.let { marker ->
                onMarkerFound(marker)
            }
        }
    }
}

