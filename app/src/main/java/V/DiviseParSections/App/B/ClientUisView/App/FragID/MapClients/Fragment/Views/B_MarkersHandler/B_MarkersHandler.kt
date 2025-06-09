package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.filterClientsBasedOnMode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.displayLatestTransactions
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.displayOpenTransactions
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.DEFAULT_LATITUDE
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBaseProtoJuin3
import Z_CodePartageEntreApps.Modules.DatesHandler
import Z_MasterOfApps.Resources.XmlsFilesHandler.Companion.xmlResources
import android.content.Context
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

suspend fun updateMapMarkers(
    mapView: MapView,
    viewModel: ViewModel_MapClients_App2FragID1,
    clientDataBaseSnapList: List<B_ClientDataBaseProtoJuin3>,
    currentFilterMode: ViewModel_MapClients_App2FragID1.VisibleClientsNow,
    showMarkerDetails: Boolean,
    onMarkerSelected: (Marker) -> Unit,
) {
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
        onMarkerSelected
    )

    displayOpenTransactions(mapView, viewModel, onMarkerSelected)
    displayLatestTransactions(mapView, viewModel, onMarkerSelected)
}

fun addMarkersForFilteredClients(
    mapView: MapView,
    clientsToShow: List<B_ClientDataBaseProtoJuin3>,
    viewModel: ViewModel_MapClients_App2FragID1,
    showMarkerDetails: Boolean,
    onMarkerSelected: (Marker) -> Unit,
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
                onMarkerSelected
            )
        } catch (e: Exception) {
            // Error handling
        }
    }

    mapView.invalidate()
}

fun createAndAddMarker(
    viewModel: ViewModel_MapClients_App2FragID1,
    mapView: MapView,
    client: B_ClientDataBaseProtoJuin3,
    context: Context,
    showMarkerDetails: Boolean,
    onMarkerSelected: (Marker) -> Unit,
) {

    val marker = Marker(mapView).apply {
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
            configureMarkerInfoWindow(this, mapView, context, viewModel,client)
        } catch (_: Exception) {
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


private fun Marker.title(
    viewModel: ViewModel_MapClients_App2FragID1,
    client: B_ClientDataBaseProtoJuin3,
) {
    title = if (viewModel.afficheLesJoursAuNoms) {
        val dateHandler = DatesHandler()
        val timeStr = viewModel.getLastTransaction(client)?.timestamps?.let { dateHandler.getDateAndTimString(it).time }
        val dayName = dateHandler.getArabicDayNameFromTimestamp(viewModel.getLastTransaction(client)?.timestamps ?: 0)
        val distanceSemain = dateHandler.getAbrgDistanceSemain(viewModel.getLastTransaction(client)?.timestamps)

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
    viewModel: ViewModel_MapClients_App2FragID1,
    client: B_ClientDataBaseProtoJuin3,
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
            ContextCompat.getColor(context, viewModel.getLastTransaction(client)!!.etateActuellementEst.color)
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
