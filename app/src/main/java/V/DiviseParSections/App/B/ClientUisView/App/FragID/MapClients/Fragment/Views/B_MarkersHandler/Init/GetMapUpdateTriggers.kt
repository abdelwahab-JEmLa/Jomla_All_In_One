package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Init

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import androidx.compose.runtime.snapshots.SnapshotStateList

fun getMapUpdateTriggers(
    c3_BonAchateList: SnapshotStateList<C3_TransactionCommercial>,
    currentFilterMode: ViewModel_MapClients_App2FragID1.VisibleClientsNow,
    mapReloadTrigger: Int,
    uiState: UiState,
): List<Any> {
    return listOf(
        c3_BonAchateList.map { it.dernierFireBaseUpdateTimestamps },
        uiState.b_ClientInfosProtoJuin3List.map { it.dernierFireBaseUpdateTimestamps },

        currentFilterMode,
        mapReloadTrigger,
    )
}
