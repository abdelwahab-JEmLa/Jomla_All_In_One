package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.filterClientsBasedOnMode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Bottons.View.get_Found_Or_Default_M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.DEFAULT_LATITUDE
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import Z_CodePartageEntreApps.Modules.DatesHandler
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
        filterClientsBasedOnMode(clientDataBaseSnapList, currentFilterMode, viewModel)

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
    val latestTransaction = viewModel.getLastTransaction(m2Client)
    val position = latestTransaction?.position_Don_Lis_Cible_Clients_au_VentPeriod ?: 0
    val positionPrefix = if (position != 0) "[$position] " else ""

    title =
        if (viewModel.afficheLesJoursAuNoms && position == 0) { // Only show date/time if no position
            val dateHandler = DatesHandler()
            val timeStr = latestTransaction?.creationTimestamps?.let {
                dateHandler.getDateAndTimString(it).time
            }
            val dayName = dateHandler.getArabicDayNameFromTimestamp(
                latestTransaction?.creationTimestamps ?: 0
            )
            val distanceSemain =
                dateHandler.getAbrgDistanceSemain(latestTransaction?.creationTimestamps)

            if (latestTransaction != null) {
                "$distanceSemain.$dayName (${timeStr})" +
                        "\n${latestTransaction.etateActuellementEst.nomArabe}" +
                        "\n${m2Client.nom}"
            } else {
                m2Client.nom
            }
        } else {
            // Show position or just client name
            if (position != 0 && latestTransaction != null) {
                "$positionPrefix${latestTransaction.etateActuellementEst.nomArabe}" +
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
