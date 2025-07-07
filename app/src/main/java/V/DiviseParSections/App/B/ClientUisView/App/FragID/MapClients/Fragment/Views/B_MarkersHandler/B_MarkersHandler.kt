package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.filterClientsBasedOnMode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.DEFAULT_LATITUDE
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import Z_CodePartageEntreApps.Modules.DatesHandler
import Z_MasterOfApps.Resources.XmlsFilesHandler.Companion.xmlResources
import android.content.Context
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
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
}

fun addMarkersForFilteredClients(
    mapView: MapView,
    clientsToShow: List<HClientInfos>,
    viewModel: MapClientsViewModel,
    showMarkerDetails: Boolean,
) {
    val context = mapView.context

    clientsToShow.forEach { client ->
        try {
            createAndAddMarker(
                viewModel,
                mapView,
                client,
                context,
                showMarkerDetails,
            )
        } catch (e: Exception) {
            // Error handling
        }
    }

    mapView.invalidate()
}

fun createAndAddMarker(
    viewModel: MapClientsViewModel,
    mapView: MapView,
    client: HClientInfos,
    context: Context,
    showMarkerDetails: Boolean,
) {
   val repo= viewModel.getter.repo2Client

    val marker = Marker(mapView).apply {
        val m2Client= repo.datasValue.find { it.id.toString()==id }
        id = client.id.toString()
        position = GeoPoint(
            client.latitude.takeIf { it != 0.0 } ?: DEFAULT_LATITUDE,
            client.longitude
        )

        title(viewModel, client)

        snippet = if (client.cUnClientTemporaire)
            "ClientAchteur temporaire" else "ClientAchteur permanent"
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        try {
            configureMarkerInfoWindow(this, mapView, context, viewModel, client)
        } catch (_: Exception) {
        }

        setOnMarkerClickListener { clickedMarker, _ ->
            viewModel.set_M2Client_UiState_In_MarkerStatusDialog(m2Client)

            if (showMarkerDetails) clickedMarker.showInfoWindow()
            true
        }
    }

    mapView.overlays.add(marker)

    if (showMarkerDetails) {
        marker.showInfoWindow()
    }
}

private fun Marker.title(
    viewModel: MapClientsViewModel,
    client: HClientInfos,
) {
    title = if (viewModel.afficheLesJoursAuNoms) {
        val dateHandler = DatesHandler()
        val timeStr = viewModel.getLastTransaction(client)?.creationTimestamps?.let {
            dateHandler.getDateAndTimString(it).time
        }
        val dayName = dateHandler.getArabicDayNameFromTimestamp(
            viewModel.getLastTransaction(client)?.creationTimestamps ?: 0
        )
        val distanceSemain =
            dateHandler.getAbrgDistanceSemain(viewModel.getLastTransaction(client)?.creationTimestamps)

        if (viewModel.getLastTransaction(client) != null) {
            "$distanceSemain.$dayName (${timeStr})" +
                    "\n${viewModel.getLastTransaction(client)!!.etateActuellementEst.nomArabe}" +
                    "\n${client.nom}"
        } else {
            client.nom
        }
    } else {
        client.nom
    }
}

fun configureMarkerInfoWindow(
    marker: Marker,
    mapView: MapView,
    context: Context,
    viewModel: MapClientsViewModel,
    client: HClientInfos,
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
