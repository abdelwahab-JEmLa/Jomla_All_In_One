package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions.displayLatestTransactions
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions.displayOpenTransactions
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.DEFAULT_LATITUDE
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.findLastPurchaseDayForClient
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.findLastPurchaseInfoForClient
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.getClientStateInArabic
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_3_TransactionCommercial
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_MasterOfApps.Resources.XmlsFilesHandler.Companion.xmlResources
import android.content.Context
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

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

// Helper functions to support the refactored MapContent
fun handleActiveTransaction(
    activeTransactionId: Long,
    viewModel: ViewModel_MapClients_App2FragID1,
    mapView: MapView,
    onMarkerFound: (Marker) -> Unit,
) {
    if (activeTransactionId != 0L) {
        // Find the transaction to get the client ID
        val activeTransaction = viewModel.repo_0_0_HeadOfRepositorys_Repository.repositorys_Model
            .repository_1_3_TransactionCommercial.modelDatasSnapList
            .find { it.vid == activeTransactionId }

        activeTransaction?.let { transaction ->
            // Find the marker for this client
            val clientMarker = mapView.overlays.filterIsInstance<Marker>()
                .find { it.id == transaction.clientAcheteurID.toString() }

            clientMarker?.let { marker ->
                // Select the marker and show its information
                onMarkerFound(marker)
                marker.showInfoWindow()

                // Animate to the marker position
                mapView.controller.animateTo(marker.position)
            }
        }
    }
}

suspend fun updateMapMarkers(
    mapView: MapView,
    viewModel: ViewModel_MapClients_App2FragID1,
    clientDataBaseSnapList: List<B_ClientDataBase>,
    clientEnCourDeVent: Long,
    currentFilterMode: ViewModel_MapClients_App2FragID1.VisibleClientsNow,
    showMarkerDetails: Boolean,
    onMarkerSelected: (Marker) -> Unit,
) {
    val existingMarkers = mapView.overlays.filterIsInstance<Marker>()
    existingMarkers.forEach { it.closeInfoWindow() }

    val markersToRemove = mapView.overlays.filterIsInstance<Marker>()
        .filter { marker -> clientDataBaseSnapList.any { it.id.toString() == marker.id } }
    mapView.overlays.removeAll(markersToRemove)

    // Filter clients based on the current mode
    val clientsToShow =
        filterClientsBasedOnMode(clientDataBaseSnapList, currentFilterMode, viewModel)

    // Add markers for filtered clients
    addMarkersForFilteredClients(
        mapView,
        clientsToShow,
        viewModel,
        clientEnCourDeVent,
        showMarkerDetails,
        onMarkerSelected
    )

    displayOpenTransactions(mapView, viewModel, onMarkerSelected)
    displayLatestTransactions(mapView, viewModel, onMarkerSelected)
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
                viewModel.repo_0_0_HeadOfRepositorys_Repository.repositorys_Model
                    .repository_1_3_TransactionCommercial.modelDatasSnapList
                    .filter { bonAchat ->
                        bonAchat.etateActuellementEst == _1_3_TransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME
                                || bonAchat.etateActuellementEst == _1_3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
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

suspend fun addMarkersForFilteredClients(
    mapView: MapView,
    clientsToShow: List<B_ClientDataBase>,
    viewModel: ViewModel_MapClients_App2FragID1,
    clientEnCourDeVent: Long,
    showMarkerDetails: Boolean,
    onMarkerSelected: (Marker) -> Unit,
) {
    val context = mapView.context
    val dayFilters = viewModel.filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList
    val shouldApplyDayFilter = dayFilters.isNotEmpty()

    clientsToShow.forEach { client ->
        val lastPurchaseDay = if (shouldApplyDayFilter) {
            findLastPurchaseDayForClient(
                viewModel.repo_01_VentsHistoriquesDataBase.modelDatasSnapList,
                client.id
            )
        } else {
            ""
        }

        val shouldDisplayClient = if (!shouldApplyDayFilter) {
            true
        } else {
            lastPurchaseDay.isNotEmpty() && dayFilters.contains(lastPurchaseDay)
        }

        if (shouldDisplayClient) {
            try {
                createAndAddMarker(
                    mapView,
                    client,
                    clientEnCourDeVent,
                    viewModel,
                    context,
                    showMarkerDetails,
                    onMarkerSelected
                )
            } catch (e: Exception) {
            }
        }
    }

    mapView.invalidate()
}

fun createAndAddMarker(
    mapView: MapView,
    client: B_ClientDataBase,
    clientEnCourDeVent: Long,
    viewModel: ViewModel_MapClients_App2FragID1,
    context: Context,
    showMarkerDetails: Boolean,
    onMarkerSelected: (Marker) -> Unit,
) {
    val actuelleEtat =
        if (client.id == clientEnCourDeVent)
            B_ClientDataBase.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT
        else client.actuelleEtat

    val marker = Marker(mapView).apply {
        id = client.id.toString()
        position = GeoPoint(
            client.latitude.takeIf { it != 0.0 } ?: DEFAULT_LATITUDE,
            client.longitude
        )

        title = if (viewModel.afficheLesJoursAuNoms) {
            val lastPurchaseInfo = findLastPurchaseInfoForClient(
                viewModel.repo_01_VentsHistoriquesDataBase.modelDatasSnapList,
                client.id
            )

            if (lastPurchaseInfo.dayName.isNotEmpty()) {
                val clientStateArabic = getClientStateInArabic(
                    client.id,
                    viewModel.repo_01_VentsHistoriquesDataBase.modelDatasSnapList
                )
                "${lastPurchaseInfo.dayName} (${lastPurchaseInfo.timeStr})" +
                        "\n$clientStateArabic" +
                        "\n${client.nom}"
            } else {
                client.nom
            }
        } else {
            client.nom
        }

        snippet = if (client.cUnClientTemporaire)
            "Client temporaire" else "Client permanent"
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        try {
            configureMarkerInfoWindow(this, mapView, context, actuelleEtat)
        } catch (e: Exception) {
        }

        setOnMarkerClickListener { clickedMarker, _ ->
            onMarkerSelected(clickedMarker)
            if (showMarkerDetails) clickedMarker.showInfoWindow()
            true
        }
    }

    mapView.overlays.add(marker)

    if (showMarkerDetails) {
        marker.showInfoWindow()
    }
}

fun configureMarkerInfoWindow(
    marker: Marker,
    mapView: MapView,
    context: Context,
    actuelleEtat: B_ClientDataBase.DernierEtatAAffiche,
) {
    val markerInfoWindowLayout = xmlResources
        .find { it.first == "marker_info_window" }?.second

    if (markerInfoWindowLayout == null) {
        throw IllegalStateException("marker_info_window layout not found")
    }

    marker.infoWindow = MarkerInfoWindow(markerInfoWindowLayout, mapView)

    val containerResourceId = xmlResources
        .find { it.first == "info_window_container" }?.second

    if (containerResourceId == null) {
        throw IllegalStateException("info_window_container ID not found")
    }

    val container = marker.infoWindow.view.findViewById<LinearLayout>(containerResourceId)
    container?.let {
        val backgroundColor = actuelleEtat.let { statue ->
            ContextCompat.getColor(context, statue.color)
        }

        it.setBackgroundColor(backgroundColor)

        val titleTextViewId = xmlResources
            .find { it.first == "title" }?.second
        titleTextViewId?.let { titleId ->
            val titleTextView =
                marker.infoWindow.view.findViewById<android.widget.TextView>(titleId)
            titleTextView?.gravity = android.view.Gravity.CENTER
        }
    }
}

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

fun handleMarkerPositionUpdate(
    clientDataBaseSnapList: List<B_ClientDataBase>,
    editingMarkerId: Long,
    mapView: MapView,
    viewModel: ViewModel_MapClients_App2FragID1,
) {
    val clientToUpdate = clientDataBaseSnapList.find {
        it.id == editingMarkerId
    }

    clientToUpdate?.let { client ->
        val centerPoint = mapView.mapCenter
        val updatedClient = B_ClientDataBase().apply {
            id = client.id
            nom = client.nom
            numTelephone = client.numTelephone
            couleur = client.couleur
            bonDuClientsSu = client.bonDuClientsSu
            currentCreditBalance = client.currentCreditBalance
            positionDonClientsList = client.positionDonClientsList
            cUnClientTemporaire = client.cUnClientTemporaire
            auFilterFAB = client.auFilterFAB
            typeDeSonMagasine = client.typeDeSonMagasine
            clientTypeMode = client.clientTypeMode

            latitude = centerPoint.latitude
            longitude = centerPoint.longitude
            title = client.title
            snippet = client.snippet
            actuelleEtat = client.actuelleEtat
        }

        viewModel.updateData(updatedClient)
    }
}
