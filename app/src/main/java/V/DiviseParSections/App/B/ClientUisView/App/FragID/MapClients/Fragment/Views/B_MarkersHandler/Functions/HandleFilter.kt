package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

fun handleFilterMarkersClick(
    mapView: MapView,
    currentFilterMode: ViewModel_MapClients_App2FragID1.VisibleClientsNow,
    onFilterChanged: (ViewModel_MapClients_App2FragID1.VisibleClientsNow) -> Unit,
) {
    mapView.overlays.filterIsInstance<Marker>().forEach { it.closeInfoWindow() }

    val newMode = when (currentFilterMode) {
        ViewModel_MapClients_App2FragID1.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR ->
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX ->
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAll

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAll ->
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showNonAbsentClientsOnly

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showNonAbsentClientsOnly ->
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.affichePourCollecteurCommendes

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.affichePourCollecteurCommendes ->
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 ->
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAtayClients

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAtayClients ->
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAlimentionlients

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAlimentionlients ->
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showClientsWithConfirmedProducts

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showClientsWithConfirmedProducts ->
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR
    }

    onFilterChanged(newMode)
}

fun filterClientsBasedOnMode(
    clientDataBaseSnapList: List<B_ClientInfosProtoJuin3>,
    currentFilterMode: ViewModel_MapClients_App2FragID1.VisibleClientsNow,
    viewModel: ViewModel_MapClients_App2FragID1,
): List<B_ClientInfosProtoJuin3> {
    return when (currentFilterMode) {
        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showNonAbsentClientsOnly -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat != B_ClientInfosProtoJuin3.DernierEtatAAffiche.ACHETEUR_NON_DISPO
            }
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.affichePourCollecteurCommendes -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat == B_ClientInfosProtoJuin3.DernierEtatAAffiche.Cible
                        || it.actuelleEtat == B_ClientInfosProtoJuin3.DernierEtatAAffiche.CIBLE_PRIORITE_2
                        || it.actuelleEtat == B_ClientInfosProtoJuin3.DernierEtatAAffiche.VENDU_A_LUI
                        || it.actuelleEtat == B_ClientInfosProtoJuin3.DernierEtatAAffiche.FERME
                        || it.actuelleEtat == B_ClientInfosProtoJuin3.DernierEtatAAffiche.A_EVITE
                        || it.actuelleEtat == B_ClientInfosProtoJuin3.DernierEtatAAffiche.AVEC_MARCHANDISE
                        || it.actuelleEtat == B_ClientInfosProtoJuin3.DernierEtatAAffiche.ACHETEUR_NON_DISPO
            }
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat == B_ClientInfosProtoJuin3.DernierEtatAAffiche.CIBLE_POUR_2
            }
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAtayClients -> {
            clientDataBaseSnapList.filter {
                it.typeDeSonMagasine == B_ClientInfosProtoJuin3.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
            }
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAlimentionlients -> {
            clientDataBaseSnapList.filter {
                it.typeDeSonMagasine == B_ClientInfosProtoJuin3.TypeDeSonMagasine.AlIMENTATION_GENERALE
            }
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAll -> {
            clientDataBaseSnapList
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showClientsWithConfirmedProducts -> {
            val clientsWithConfirmedProducts =
                viewModel.groupeRepositorysProtoAvJuin3.repositorys_Model
                    .c3TransactionCommercialRepository.modelDatasSnapList
                    .filter { bonAchat ->
                        bonAchat.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME
                                || bonAchat.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                    }
                    .map { bonAchat -> bonAchat.clientAcheteurID }
                    .distinct()

            clientDataBaseSnapList.filter { client ->
                clientsWithConfirmedProducts.contains(client.id)
            }
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR -> {
            clientDataBaseSnapList.filter {
                viewModel.getLastTransaction(it)?.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.Cible
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT

            }
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX -> {
            clientDataBaseSnapList.filter {
                viewModel.getLastTransaction(it)?.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.Cible
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.A_EVITE
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.AVEC_MARCHANDISE
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.ACHETEUR_NON_DISPO
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.FERME
            }
        }

        else -> {
            clientDataBaseSnapList
        }
    }
}
