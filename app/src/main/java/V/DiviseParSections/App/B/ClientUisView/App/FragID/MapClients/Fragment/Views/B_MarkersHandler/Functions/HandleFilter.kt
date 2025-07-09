package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

fun handleFilterMarkersClick(
    mapView: MapView,
    currentFilterMode: MapClientsViewModel.VisibleClientsNow,
    onFilterChanged: (MapClientsViewModel.VisibleClientsNow) -> Unit,
) {
    mapView.overlays.filterIsInstance<Marker>().forEach { it.closeInfoWindow() }

    val newMode = when (currentFilterMode) {
        MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR ->
            MapClientsViewModel.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX

        MapClientsViewModel.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX ->
            MapClientsViewModel.VisibleClientsNow.showAll

        MapClientsViewModel.VisibleClientsNow.showAll ->
            MapClientsViewModel.VisibleClientsNow.showNonAbsentClientsOnly

        MapClientsViewModel.VisibleClientsNow.showNonAbsentClientsOnly ->
            MapClientsViewModel.VisibleClientsNow.affichePourCollecteurCommendes

        MapClientsViewModel.VisibleClientsNow.affichePourCollecteurCommendes ->
            MapClientsViewModel.VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2

        MapClientsViewModel.VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 ->
            MapClientsViewModel.VisibleClientsNow.showAtayClients

        MapClientsViewModel.VisibleClientsNow.showAtayClients ->
            MapClientsViewModel.VisibleClientsNow.showAlimentionlients

        MapClientsViewModel.VisibleClientsNow.showAlimentionlients ->
            MapClientsViewModel.VisibleClientsNow.showClientsWithConfirmedProducts

        MapClientsViewModel.VisibleClientsNow.showClientsWithConfirmedProducts ->
            MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR
    }

    onFilterChanged(newMode)
}

fun filterClientsBasedOnMode(
    clientDataBaseSnapList: List<HClientInfos>,
    currentFilterMode: MapClientsViewModel.VisibleClientsNow,
    viewModel: MapClientsViewModel,
): List<HClientInfos> {
    return when (currentFilterMode) {
        MapClientsViewModel.VisibleClientsNow.showNonAbsentClientsOnly -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat != HClientInfos.DernierEtatAAffiche.ACHETEUR_NON_DISPO
            }
        }

        MapClientsViewModel.VisibleClientsNow.affichePourCollecteurCommendes -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat == HClientInfos.DernierEtatAAffiche.Cible
                        || it.actuelleEtat == HClientInfos.DernierEtatAAffiche.CIBLE_PRIORITE_2
                        || it.actuelleEtat == HClientInfos.DernierEtatAAffiche.VENDU_A_LUI
                        || it.actuelleEtat == HClientInfos.DernierEtatAAffiche.FERME
                        || it.actuelleEtat == HClientInfos.DernierEtatAAffiche.A_EVITE
                        || it.actuelleEtat == HClientInfos.DernierEtatAAffiche.AVEC_MARCHANDISE
                        || it.actuelleEtat == HClientInfos.DernierEtatAAffiche.ACHETEUR_NON_DISPO
            }
        }

        MapClientsViewModel.VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat == HClientInfos.DernierEtatAAffiche.CIBLE_POUR_2
            }
        }

        MapClientsViewModel.VisibleClientsNow.showAtayClients -> {
            clientDataBaseSnapList.filter {
                it.typeDeSonMagasine == HClientInfos.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
            }
        }

        MapClientsViewModel.VisibleClientsNow.showAlimentionlients -> {
            clientDataBaseSnapList.filter {
                it.typeDeSonMagasine == HClientInfos.TypeDeSonMagasine.AlIMENTATION_GENERALE
            }
        }

        MapClientsViewModel.VisibleClientsNow.showAll -> {
            clientDataBaseSnapList
        }

        MapClientsViewModel.VisibleClientsNow.showClientsWithConfirmedProducts -> {
            val datas = viewModel.getter.repo8BonVent.datasValue
            val clientsWithConfirmedProducts =
                datas
                    .filter { bonAchat ->
                        bonAchat.etateActuellementEst == M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
                                || bonAchat.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                    }
                    .map { bonAchat -> bonAchat.parent_M2Client_OldLongID }
                    .distinct()

            clientDataBaseSnapList.filter { client ->
                clientsWithConfirmedProducts.contains(client.id)
            }
        }

        MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR -> {
            clientDataBaseSnapList.filter {
                viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.Cible
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT

            }
        }

        MapClientsViewModel.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX -> {
            clientDataBaseSnapList.filter {
                viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.Cible
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.A_EVITE
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.AVEC_MARCHANDISE
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.ACHETEUR_NON_DISPO
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.FERME
            }
        }

        else -> {
            clientDataBaseSnapList
        }
    }
}
