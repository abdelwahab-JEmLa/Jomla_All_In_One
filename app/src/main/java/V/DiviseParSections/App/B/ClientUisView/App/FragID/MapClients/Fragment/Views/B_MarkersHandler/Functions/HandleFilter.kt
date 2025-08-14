package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.find_its_Confirmation_de_Transaction
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
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

        else -> {
            MapClientsViewModel.VisibleClientsNow.showAll
        }
    }

    onFilterChanged(newMode)
}

fun filterClientsBasedOnMode(
    viewModel: MapClientsViewModel,
    clientDataBaseSnapList: List<M2Client>,
    currentFilterMode: MapClientsViewModel.VisibleClientsNow,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
): List<M2Client> {
    return when (currentFilterMode) {
        MapClientsViewModel.VisibleClientsNow.showNonAbsentClientsOnly -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat != M2Client.DernierEtatAAffiche.ACHETEUR_NON_DISPO
            }
        }

        MapClientsViewModel.VisibleClientsNow.affichePourCollecteurCommendes -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat == M2Client.DernierEtatAAffiche.Cible
                        || it.actuelleEtat == M2Client.DernierEtatAAffiche.CIBLE_PRIORITE_2
                        || it.actuelleEtat == M2Client.DernierEtatAAffiche.VENDU_A_LUI
                        || it.actuelleEtat == M2Client.DernierEtatAAffiche.FERME
                        || it.actuelleEtat == M2Client.DernierEtatAAffiche.A_EVITE
                        || it.actuelleEtat == M2Client.DernierEtatAAffiche.AVEC_MARCHANDISE
                        || it.actuelleEtat == M2Client.DernierEtatAAffiche.ACHETEUR_NON_DISPO
            }
        }

        MapClientsViewModel.VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat == M2Client.DernierEtatAAffiche.CIBLE_POUR_2
            }
        }

        MapClientsViewModel.VisibleClientsNow.showAtayClients -> {
            clientDataBaseSnapList.filter {
                it.typeDeSonMagasine == M2Client.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
            }
        }

        MapClientsViewModel.VisibleClientsNow.showAlimentionlients -> {
            clientDataBaseSnapList.filter {
                it.typeDeSonMagasine == M2Client.TypeDeSonMagasine.AlIMENTATION_GENERALE
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
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.Rapport_Entre_On_Etate_De_Bloquage
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.Bloque_Probleme

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
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.Rapport_Entre_On_Etate_De_Bloquage
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.Bloque_Probleme
            }
        }

        MapClientsViewModel.VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter -> {
            val keyID_currentActiveFocuced_M14VentPeriode =
                focusedValuesGetter.currentActiveFocuced_M14VentPeriode.keyID
            clientDataBaseSnapList.filter {
                val lastTransaction = viewModel.getLastTransaction(it)
                (
                        (lastTransaction?.etateActuellementEst == M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI
                                || lastTransaction?.etateActuellementEst == M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit
                                )
                                && (find_its_Confirmation_de_Transaction(
                            aCentralFacade.repositorysMainGetter,
                            lastTransaction
                        )
                            ?.parent_M14VentPeriod_KeyId ?: "")
                                == keyID_currentActiveFocuced_M14VentPeriode
                        )

            }
        }

        MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME -> {
            clientDataBaseSnapList.filter {
                viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
            }
        }

        else -> {
            clientDataBaseSnapList
        }
    }
}
