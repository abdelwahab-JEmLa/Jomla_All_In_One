package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Init

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1

fun getMapUpdateTriggers(
    viewModel: ViewModel_MapClients_App2FragID1,
    clientDataBaseSize: Int,
    clientEnCourDeVent: Long,
    currentFilterMode: ViewModel_MapClients_App2FragID1.VisibleClientsNow,
    mapReloadTrigger: Int,
): List<Any> {
    return listOf(
        clientDataBaseSize,
        clientEnCourDeVent,
        currentFilterMode,
        viewModel.mapReloadTigger,
        viewModel.filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList.size,
        mapReloadTrigger
    )
}
