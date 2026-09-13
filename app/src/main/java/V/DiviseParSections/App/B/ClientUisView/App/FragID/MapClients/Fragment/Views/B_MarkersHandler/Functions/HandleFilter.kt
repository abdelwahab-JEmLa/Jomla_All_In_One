package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions

import EntreApps.Shared.Models.Relative_Vents.Models.Fournisseur_Speciale
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.VisibleClientsNow
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
    currentFilterMode: VisibleClientsNow,
    onFilterChanged: (VisibleClientsNow) -> Unit,
) {
    mapView.overlays.filterIsInstance<Marker>().forEach { it.closeInfoWindow() }

    val newMode = when (currentFilterMode) {
        VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR ->
            VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX

        VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX ->
            VisibleClientsNow.showAll

        VisibleClientsNow.showAll ->
            VisibleClientsNow.showNonAbsentClientsOnly

        VisibleClientsNow.showNonAbsentClientsOnly ->
            VisibleClientsNow.affichePourCollecteurCommendes

        VisibleClientsNow.affichePourCollecteurCommendes ->
            VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2

        VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 ->
            VisibleClientsNow.showAtayClients

        VisibleClientsNow.showAtayClients ->
            VisibleClientsNow.showAlimentionlients

        VisibleClientsNow.showAlimentionlients ->
            VisibleClientsNow.showClientsWithConfirmedProducts

        VisibleClientsNow.showClientsWithConfirmedProducts ->
            VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR

        else -> {
            VisibleClientsNow.showAll
        }
    }

    onFilterChanged(newMode)
}

fun filterClientsBasedOnMode(
    viewModel: MapClientsViewModel,
    currentFilterMode: VisibleClientsNow,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
): List<M2Client> {
    val clientDataBaseSnapList = repositorysMainGetter.repo2Client.datasValue

    // FIXED: Get always-visible clients based on active UI states
    val alwaysVisibleClients = getAlwaysVisibleClients(viewModel, focusedValuesGetter)

    // Apply the normal filter logic
    val filteredClients = when (currentFilterMode) {
        VisibleClientsNow.showNonAbsentClientsOnly -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat != M2Client.DernierEtatAAffiche.ACHETEUR_NON_DISPO
            }
        }

        VisibleClientsNow.affichePourCollecteurCommendes -> {
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

        VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 -> {
            clientDataBaseSnapList.filter {
                it.actuelleEtat == M2Client.DernierEtatAAffiche.CIBLE_POUR_2
            }
        }

        VisibleClientsNow.showAtayClients -> {
            clientDataBaseSnapList.filter {
                it.typeDeSonMagasine == M2Client.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
            }
        }

        VisibleClientsNow.showAlimentionlients -> {
            clientDataBaseSnapList.filter {
                it.typeDeSonMagasine == M2Client.TypeDeSonMagasine.AlIMENTATION_GENERALE
            }
        }

        VisibleClientsNow.showAll -> {
            clientDataBaseSnapList
        }

        VisibleClientsNow.showClientsWithConfirmedProducts -> {
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

        VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR -> {
            clientDataBaseSnapList.filter {
                viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.Cible
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.Rapport_Entre_On_Etate_De_Bloquage
                        || viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.Bloque_Probleme
            }
        }

        VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX -> {
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

        VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter -> {
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

        VisibleClientsNow.Filter_Leur_Last_TRX_Est_A_COMMANDE_CONFIRME -> {
            clientDataBaseSnapList.filter {
                viewModel.getLastTransaction(it)?.etateActuellementEst == M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
            }
        }

        // Les 4 filtres crédit (client/fournisseur x court/long terme).
        // "Court terme" / "long terme" est le flag ces_credits_son_a_long_term
        // sur M2Client (pas une notion d'ancienneté calculée) — voir M2Client.kt.
        // On réutilise M2Client.calculateCreditsMap / calculateIgnoredCreditsMap
        // (mêmes fonctions que celles qui calculent les totaux affichés dans
        // But1_Floating_ClientsListDialog) pour que la liste de clients montrée
        // ici corresponde exactement aux totaux affichés là-bas.
        VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit -> {
            val keyIdsAvecCredit = M2Client.calculateCreditsMap(
                clients = clientDataBaseSnapList,
                bons = viewModel.getter.repo8BonVent.datasValue,
                forFournisseurs = false,
            ).keys
            clientDataBaseSnapList.filter { it.keyID in keyIdsAvecCredit }
        }

        VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term -> {
            val keyIdsAvecCredit = M2Client.calculateIgnoredCreditsMap(
                clients = clientDataBaseSnapList,
                bons = viewModel.getter.repo8BonVent.datasValue,
                forFournisseurs = false,
            ).keys
            clientDataBaseSnapList.filter { it.keyID in keyIdsAvecCredit }
        }

        VisibleClientsNow.Filter_Fournisseurs_Short_Term_Credit -> {
            val keyIdsAvecCredit = M2Client.calculateCreditsMap(
                clients = clientDataBaseSnapList,
                bons = viewModel.getter.repo8BonVent.datasValue,
                forFournisseurs = true,
            ).keys
            clientDataBaseSnapList.filter { it.keyID in keyIdsAvecCredit }
        }

        VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit -> {
            val keyIdsAvecCredit = M2Client.calculateIgnoredCreditsMap(
                clients = clientDataBaseSnapList,
                bons = viewModel.getter.repo8BonVent.datasValue,
                forFournisseurs = true,
            ).keys
            clientDataBaseSnapList.filter { it.keyID in keyIdsAvecCredit }
        }

        // Clients de Jamale avec un crédit en cours : on recoupe le flag
        // its_Client_De_Jamale avec l'union des 4 map crédit (client/fournisseur
        // x court/long terme), pour couvrir un client de Jamale peu importe
        // son statut fournisseur ou son ancienneté de crédit.
        VisibleClientsNow.Filter_Clients_De_Jamale_Avec_Credit -> {
            val bons = viewModel.getter.repo8BonVent.datasValue
            val keyIdsAvecCredit = M2Client.calculateCreditsMap(
                clients = clientDataBaseSnapList,
                bons = bons,
                forFournisseurs = false,
            ).keys + M2Client.calculateIgnoredCreditsMap(
                clients = clientDataBaseSnapList,
                bons = bons,
                forFournisseurs = false,
            ).keys + M2Client.calculateCreditsMap(
                clients = clientDataBaseSnapList,
                bons = bons,
                forFournisseurs = true,
            ).keys + M2Client.calculateIgnoredCreditsMap(
                clients = clientDataBaseSnapList,
                bons = bons,
                forFournisseurs = true,
            ).keys

            clientDataBaseSnapList.filter {
                it.its_Client_De_Jamale && it.keyID in keyIdsAvecCredit
            }
        }
        else -> {
            clientDataBaseSnapList
        }
    }

    // Les filtres crédit (client/fournisseur x court/long terme, + Jamale avec
    // crédit) doivent montrer TOUS les clients concernés, y compris ceux situés
    // à moins de 100m d'Ami Jamel. La règle d'exclusion de proximité ne
    // s'applique donc que quand un filtre non-crédit est actif.
    val isCreditFilterMode = currentFilterMode in listOf(
        VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit,
        VisibleClientsNow.Filter_Leur_Last_TRX_Est_Credit_Long_Term,
        VisibleClientsNow.Filter_Fournisseurs_Short_Term_Credit,
        VisibleClientsNow.Filter_Fournisseurs_Long_Term_Credit,
        VisibleClientsNow.Filter_Clients_De_Jamale_Avec_Credit,
    )

    val finalClientsList = (filteredClients + alwaysVisibleClients)
        .distinctBy { it.id }
        .filter { isCreditFilterMode || !it.isWithin100mOfAmiJamel() }

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
