package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.filterClientsBasedOnMode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.displayLatestTransactions
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.displayOpenTransactions
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.DEFAULT_LATITUDE
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
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

    // Filter clientAchteurs based on the current mode
    val clientsToShow =
        filterClientsBasedOnMode(clientDataBaseSnapList, currentFilterMode, viewModel)

    // Add markers for filtered clientAchteurs
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

fun addMarkersForFilteredClients(
    mapView: MapView,
    clientsToShow: List<B_ClientDataBase>,
    viewModel: ViewModel_MapClients_App2FragID1,
    clientEnCourDeVent: Long,
    showMarkerDetails: Boolean,
    onMarkerSelected: (Marker) -> Unit,
) {
    val context = mapView.context

    clientsToShow.forEach { client ->
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
            // Error handling
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

        title(viewModel, client)

        snippet = if (client.cUnClientTemporaire)
            "ClientAchteur temporaire" else "ClientAchteur permanent"
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        try {
            configureMarkerInfoWindow(this, mapView, context, actuelleEtat)
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
    client: B_ClientDataBase,
) {
    title = if (viewModel.afficheLesJoursAuNoms) {
        val historicalData = viewModel.repo_0_0_HeadSQLRepositorys
            .repositorys_Model
            .c3_BonAchate_Repository
            .modelDatasSnapList

        val lastTransaction = historicalData
            .filter { it.clientAcheteurID == client.id }
            .maxByOrNull { it.timestamps }

        val dateHandler = DatesHandler()
        val timeStr = lastTransaction?.timestamps?.let { dateHandler.getDateAndTimString(it).time }
        val dayName = dateHandler.getArabicDayNameFromTimestamp(lastTransaction?.timestamps ?: 0)
        val distanceSemain = dateHandler.getAbrgDistanceSemain(lastTransaction?.timestamps)

        if (lastTransaction != null) {
            "$distanceSemain.$dayName (${timeStr})" +
                    "\n${lastTransaction.etateActuellementEst?.nomArabe}" +
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
