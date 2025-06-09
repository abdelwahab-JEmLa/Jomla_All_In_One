package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Init

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import androidx.compose.runtime.snapshots.SnapshotStateList

fun getMapUpdateTriggers(
    c3_BonAchateList: SnapshotStateList<C3_TransactionCommercial>,
    viewModel: ViewModel_MapClients_App2FragID1,
    clientDataBaseSize: Int,
    clientEnCourDeVent: Long,
    currentFilterMode: ViewModel_MapClients_App2FragID1.VisibleClientsNow,
    mapReloadTrigger: Int,
): List<Any> {
    return listOf(
        c3_BonAchateList.toList(),

        c3_BonAchateList.map { it.etateActuellementEst },
        c3_BonAchateList.map { it.timestamps },
        c3_BonAchateList.map { it.clientAcheteurID },
        c3_BonAchateList.map { it.vocaleKeyID },

        // Existing triggers
        clientDataBaseSize,
        clientEnCourDeVent,
        currentFilterMode,
        viewModel.mapReloadTigger,
        viewModel.filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList.size,
        mapReloadTrigger,

        // Additional trigger for active transaction changes
        viewModel.groupeRepositorysProtoAvJuin3.repositorys_Model.activeVId_C3_BonAchate_Repository.value
    )
}
