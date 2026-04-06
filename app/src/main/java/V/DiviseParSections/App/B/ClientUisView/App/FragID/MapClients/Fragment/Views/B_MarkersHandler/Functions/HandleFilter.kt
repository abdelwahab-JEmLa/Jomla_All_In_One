package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions

import EntreApps.Shared.Models.M2Client
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Models.Relative_Vents.Models.Fournisseur_Speciale
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.find_its_Confirmation_de_Transaction
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

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
    currentFilterMode: MapClientsViewModel.VisibleClientsNow,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
): List<M2Client> {
    val clientDataBaseSnapList = repositorysMainGetter.repo2Client.datasValue

    // FIXED: Get always-visible clients based on active UI states
    val alwaysVisibleClients = getAlwaysVisibleClients(viewModel, focusedValuesGetter)

    // Apply the normal filter logic
    val filteredClients = when (currentFilterMode) {
        MapClientsViewModel.VisibleClientsNow.showNonAbsentClientsOnly -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat != M2Client.DernierEtatAAffiche.ACHETEUR_NON_DISPO
            }
        }

        MapClientsViewModel.VisibleClientsNow.affichePourCollecteurCommendes -> {
            clientDataBaseSnapList.filter {
                viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT ||
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
                focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID
            clientDataBaseSnapList.filter {
                val lastTransaction = viewModel.getLastTransaction(it)
                (
                        (lastTransaction?.etateActuellementEst == M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI
                                || lastTransaction?.etateActuellementEst == M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit
                                || lastTransaction?.etateActuellementEst == M8BonVent.EtateActuellementEst.Passed_Sans_Livre
                                || lastTransaction?.etateActuellementEst == M8BonVent.EtateActuellementEst.Demande_Versemet
                                )
                                && (find_its_Confirmation_de_Transaction(aCentralFacade.repositorysMainGetter, lastTransaction)
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

        MapClientsViewModel.VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit -> {
            clientDataBaseSnapList.filter {
                viewModel.getLastTransaction(it)?.etateActuellementEst ==
                        M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit
            }
        }

        else -> {
            clientDataBaseSnapList
        }
    }

    val finalClientsList = (filteredClients + alwaysVisibleClients)
        .distinctBy { it.id }
        .filter { !it.isWithin100mOfAmiJamel() }

    return finalClientsList
}

// ─── Ami_Jamel proximity exclusion ───────────────────────────────────────────

private const val AMI_JAMEL_EXCLUSION_RADIUS_METERS = 100.0

private fun M2Client.isWithin100mOfAmiJamel(): Boolean {
    val jamel = Fournisseur_Speciale.Ami_Jamel
    return haversineMeters(latitude, longitude, jamel.latitude, jamel.longitude) <= AMI_JAMEL_EXCLUSION_RADIUS_METERS
}

private fun haversineMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val r = 6_371_000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
    return r * 2 * atan2(sqrt(a), sqrt(1 - a))
}

private fun getAlwaysVisibleClients(
    viewModel: MapClientsViewModel,
    focusedValuesGetter: FocusedValuesGetter
): List<M2Client> {
    val alwaysVisibleClients = mutableListOf<M2Client>()

    // Add client from activeOnVentM2ClientInfos if it exists
    focusedValuesGetter.activeOnVentM2ClientInfos?.let { activeClient ->
        alwaysVisibleClients.add(activeClient)
    }

    // Add client from markerStatusDialogActiveM2Client if it exists
    viewModel.uiState.value.markerStatusDialogActiveM2Client?.let { dialogClient ->
        alwaysVisibleClients.add(dialogClient)
    }

    return alwaysVisibleClients.distinctBy { it.id }
}
