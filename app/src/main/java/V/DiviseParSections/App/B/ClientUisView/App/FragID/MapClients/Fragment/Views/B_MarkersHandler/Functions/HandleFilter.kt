package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Test.C3_BonAchate
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
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
    clientDataBaseSnapList: List<B_ClientDataBase>,
    currentFilterMode: ViewModel_MapClients_App2FragID1.VisibleClientsNow,
    viewModel: ViewModel_MapClients_App2FragID1,
): List<B_ClientDataBase> {
    return when (currentFilterMode) {
        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showNonAbsentClientsOnly -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat != B_ClientDataBase.DernierEtatAAffiche.ACHETEUR_NON_DISPO
            }
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.affichePourCollecteurCommendes -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.Cible
                        || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.CIBLE_PRIORITE_2
                        || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.VENDU_A_LUI
                        || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.FERME
                        || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.A_EVITE
                        || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.AVEC_MARCHANDISE
                        || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.ACHETEUR_NON_DISPO
            }
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.CIBLE_POUR_2
            }
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAtayClients -> {
            clientDataBaseSnapList.filter {
                it.typeDeSonMagasine == B_ClientDataBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
            }
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAlimentionlients -> {
            clientDataBaseSnapList.filter {
                it.typeDeSonMagasine == B_ClientDataBase.TypeDeSonMagasine.AlIMENTATION_GENERALE
            }
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAll -> {
            clientDataBaseSnapList
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showClientsWithConfirmedProducts -> {
            val clientsWithConfirmedProducts =
                viewModel.repo_0_0_HeadSQLRepositorys.repositorys_Model
                    .c3_BonAchate_Repository.modelDatasSnapList
                    .filter { bonAchat ->
                        bonAchat.etateActuellementEst == C3_BonAchate.EtateActuellementEst.A_COMMANDE_CONFIRME
                                || bonAchat.etateActuellementEst == C3_BonAchate.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                    }
                    .map { bonAchat -> bonAchat.clientAcheteurID }
                    .distinct()

            clientDataBaseSnapList.filter { client ->
                clientsWithConfirmedProducts.contains(client.id)
            }
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.NON_DEFINI
                        || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.Cible
            }
        }

        ViewModel_MapClients_App2FragID1.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.NON_DEFINI
                        || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.Cible
                        || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.VENDU_A_LUI
                        || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.FERME
                        || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.A_EVITE
                        || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.AVEC_MARCHANDISE
                        || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.ACHETEUR_NON_DISPO
            }
        }

        else -> {
            clientDataBaseSnapList
        }
    }
}
