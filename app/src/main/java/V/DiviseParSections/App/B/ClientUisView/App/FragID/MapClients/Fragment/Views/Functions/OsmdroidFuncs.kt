package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.DEFAULT_LATITUDE
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.DEFAULT_LONGITUDE
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.getCurrentLocation
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

fun cleanupMapResources(mapView: MapView, viewModel: MapClientsViewModel) {
    // Properly clean up OSMDroid resources
    mapView.onDetach()
    mapView.overlays.clear()
    // Cancel any ongoing operations
    viewModel.cancelActiveOperations()
}

suspend fun initializeMapPosition(
    context: Context,
    mapView: MapView,
    currentZoom: Double,
    shouldCenterOnLocation: Boolean = true // Only center on first load
) {
    val location = getCurrentLocation(context)

    // Create GeoPoint directly instead of using MapPosition
    val geoPoint = if (location != null) {
        GeoPoint(location.latitude, location.longitude)
    } else {
        GeoPoint(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    }

    mapView.apply {
        setMultiTouchControls(true)
        setTileSource(TileSourceFactory.MAPNIK)
        controller.setZoom(currentZoom)

        // Only center the map on initial load, not on subsequent updates
        if (shouldCenterOnLocation) {
            withContext(Dispatchers.Main) {
                controller.animateTo(geoPoint)
            }
        }
    }
}
