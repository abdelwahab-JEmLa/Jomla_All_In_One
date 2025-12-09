package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.filterClientsBasedOnMode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Bottons.View.get_Found_Or_Default_M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.get_sum_Bon_Vents
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.DEFAULT_LATITUDE
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import Z_CodePartageEntreApps.Modules.DatesHandler
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_MasterOfApps.Resources.XmlsFilesHandler.Companion.xmlResources
import android.content.Context
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

fun addOuUpdateMapMarkers(
    uiState: UiState,
    viewModel: MapClientsViewModel,
    mapView: MapView,
    currentFilterMode: MapClientsViewModel.VisibleClientsNow,
    showMarkerDetails: Boolean,
) {
    val clientDataBaseSnapList = uiState.b_ClientInfosProtoJuin3List

    val existingMarkers = mapView.overlays.filterIsInstance<Marker>()
    existingMarkers.forEach { it.closeInfoWindow() }

    val markersToRemove = mapView.overlays.filterIsInstance<Marker>()
        .filter { marker -> clientDataBaseSnapList.any { it.id.toString() == marker.id } }
    mapView.overlays.removeAll(markersToRemove)

    val locationOverlay = preserveLocationOverlay(mapView)

    // Filter clientAchteurs based on the current mode
    val clientsToShow =
        filterClientsBasedOnMode(viewModel, currentFilterMode)

    // Add markers for filtered clientAchteurs
    addMarkersForFilteredClients(
        mapView,
        clientsToShow,
        viewModel,
        showMarkerDetails,
    )

    restoreLocationOverlayAtBottom(mapView, locationOverlay)
}

fun addMarkersForFilteredClients(
    mapView: MapView,
    clientsToShow: List<M2Client>,
    viewModel: MapClientsViewModel,
    showMarkerDetails: Boolean,
) {
    val context = mapView.context

    clientsToShow.forEach { client ->
        try {
            createAndAddMarker(
                m2Client = client,
                viewModel,
                mapView = mapView,
                context = context,
                showMarkerDetails = showMarkerDetails,
            )
        } catch (e: Exception) {
        }
    }

    mapView.invalidate()
}

fun createAndAddMarker(
    m2Client: M2Client,
    viewModel: MapClientsViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    mapView: MapView,
    context: Context,
    showMarkerDetails: Boolean,
    fragmentNavigationHandler: FragmentNavigationHandler = aCentralFacade.modulesCentral.fragmentNavigationHandler,
    ) {
    val repo = viewModel.getter.repo2Client

    val marker = Marker(mapView).apply {
        id = m2Client.id.toString()
        position = GeoPoint(
            m2Client.latitude.takeIf { it != 0.0 } ?: DEFAULT_LATITUDE,
            m2Client.longitude
        )

        title(viewModel, m2Client)

        snippet = if (m2Client.cUnClientTemporaire)
            "ClientAchteur temporaire" else "ClientAchteur permanent"
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        try {
            configureMarkerInfoWindow(this, mapView, context, viewModel, m2Client)
        } catch (_: Exception) {
        }

        setOnMarkerClickListener { clickedMarker, _ ->
            val activeCentralValues = focusedValuesGetter.active_Central_Values
            val current_ADD_Au_Ciblage_Clients = activeCentralValues
                .click_On_Marque

            val actuelle_Ciblage_MaxPosition = activeCentralValues
                .actuelle_Ciblage_MaxPosition

            val newPosition = actuelle_Ciblage_MaxPosition + 1


            when (current_ADD_Au_Ciblage_Clients) {
                ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients -> {
                    val found_Or_Default_M8BonVent = get_Found_Or_Default_M8BonVent(
                        aCentralFacade = aCentralFacade,
                        relative_M2Client = m2Client,
                        etateActuellementEst = M8BonVent.EtateActuellementEst.Cible,
                    )

                    aCentralFacade.repositorysMainSetter
                        .addNew_M8BonVent(
                            found_Or_Default_M8BonVent.default_If_No_Found
                                .copy(
                                    position_Don_Lis_Cible_Clients_au_VentPeriod = newPosition
                                )
                        )

                    focusedValuesGetter.update_activeCentralValues(
                        activeCentralValues.copy(
                            actuelle_Ciblage_MaxPosition = newPosition
                        )
                    )

                    true
                }

                ActiveCentralValues.Click_On_Marque.Standart -> {
                    val clickedMarkerM2Client =
                        repo.datasValue.find { it.id.toString() == clickedMarker.id }

                    viewModel.set_M2Client_UiState_In_MarkerStatusDialog(clickedMarkerM2Client)

                    if (showMarkerDetails) clickedMarker.showInfoWindow()
                    true
                }


                ActiveCentralValues.Click_On_Marque.Affiche_OnCommand_VentPeriod_Transaction ->{
                    val datasValue = aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue

                    val onCommandBon_ventPeriod = datasValue.lastOrNull {
                        it.parent_M2Client_KeyID == m2Client.keyID
                                &&
                        it.parent_M14VentPeriod_KeyId == (aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActiveFocuced_M14VentPeriode
                            ?.keyID ?: "")
                                && it.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                    }

                    if (onCommandBon_ventPeriod != null) {
                        aCentralFacade.focusedActiveValuesFacade
                            .focusedValuesSetter
                            .setIN_M9CurrentApp_onVentM8BonVentKey(
                                onCommandBon_ventPeriod
                            )
                        fragmentNavigationHandler.navigateToCartScreen()
                    }

                    true
                }
            }
        }
    }

    mapView.overlays.add(marker)

    if (showMarkerDetails) {
        marker.showInfoWindow()
    }
}

// Fix 2: Title display - don't show date/time when position is displayed
private fun Marker.title(
    viewModel: MapClientsViewModel,

    m2Client: M2Client,
) {
    val relative_M8Transaction = viewModel.getLastTransaction(m2Client)

    val sumBonVents = relative_M8Transaction?.let { get_sum_Bon_Vents(viewModel.getter, it) }

    val position = relative_M8Transaction?.position_Don_Lis_Cible_Clients_au_VentPeriod ?: 0
    val positionPrefix = if (position != 0) "[$position] " else ""

    title =
        if (viewModel.afficheLesJoursAuNoms && position == 0) {
            val dateHandler = DatesHandler()
            val timeStr = relative_M8Transaction?.creationTimestamps?.let {
                dateHandler.getDateAndTimString(it).time
            }
            val dayName = dateHandler.getArabicDayNameFromTimestamp(
                relative_M8Transaction?.creationTimestamps ?: 0
            )
            val distanceSemain =
                dateHandler.getAbrgDistanceSemain(relative_M8Transaction?.creationTimestamps)

            if (relative_M8Transaction != null) {
                val text = " بالتقريب$sumBonVents"
                val texy_Safe = text.takeIf { sumBonVents!! > 0.0 } ?: ""
                val demande_Versemet_si_Type = relative_M8Transaction.demande_Versemet_si_Type
                    .takeIf { relative_M8Transaction.demande_Versemet_si_Type > 0.0 } ?: ""

                "$distanceSemain.$dayName (${timeStr})" +
                        "\n${relative_M8Transaction.etateActuellementEst.nomArabe}" +
                        texy_Safe +
                        demande_Versemet_si_Type +
                        "\n${m2Client.nom}"
            } else {
                m2Client.nom
            }
        } else {
            // Show position or just client name
            if (position != 0 && relative_M8Transaction != null) {
                "$positionPrefix${relative_M8Transaction.etateActuellementEst.nomArabe}" +
                        "\n${m2Client.nom}"
            } else {
                "$positionPrefix${m2Client.nom}"
            }
        }
}

fun configureMarkerInfoWindow(
    marker: Marker,
    mapView: MapView,
    context: Context,
    viewModel: MapClientsViewModel,
    client: M2Client,
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
    container?.let { it ->
        val backgroundColor = viewModel.getLastTransaction(client)?.let {
            ContextCompat.getColor(
                context,
                viewModel.getLastTransaction(client)!!.etateActuellementEst.color
            )
        }

        if (backgroundColor != null) {
            it.setBackgroundColor(backgroundColor)
        }

        val titleTextViewId = xmlResources
            .find { it.first == "title" }?.second
        titleTextViewId?.let { titleId ->
            val titleTextView =
                marker.infoWindow.view.findViewById<android.widget.TextView>(titleId)
            titleTextView?.gravity = android.view.Gravity.CENTER
        }
    }
}

private fun preserveLocationOverlay(mapView: MapView): Any? {
    return mapView.overlays.find { overlay ->
        overlay.javaClass.simpleName.contains("Location") ||
                overlay.toString().contains("location", ignoreCase = true)
    }
}

private fun restoreLocationOverlayAtBottom(mapView: MapView, locationOverlay: Any?) {
    locationOverlay?.let { overlay ->
        mapView.overlays.remove(overlay)
        mapView.overlays.add(0, overlay as Overlay?)
    }
}
