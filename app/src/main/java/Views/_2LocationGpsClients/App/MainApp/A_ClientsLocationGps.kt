package Views._2LocationGpsClients.App.MainApp

import Views._2LocationGpsClients.App.MainApp.B.Dialogs.MapControls
import Views._2LocationGpsClients.App.MainApp.B.Dialogs.MarkerStatusDialog
import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.content.Context
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.Utils.DEFAULT_LATITUDE
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.Utils.DEFAULT_LONGITUDE
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.Utils.getCurrentLocation
import com.example.clientjetpack.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

@Composable
fun A_ClientsLocationGps(
    modifier: Modifier = Modifier,
    viewModel: ViewModelInitApp = viewModel(),
) {
    val context = LocalContext.current
    val currentZoom by remember { mutableDoubleStateOf(18.2) }
    val mapView = remember { viewModel.initializeMapView(context) }
    val markers = remember { mutableStateListOf<Marker>() }

    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    var showMarkerDialog by remember { mutableStateOf(false) }
    var showMarkerDetails by remember { mutableStateOf(true) }

    // Initialize map position with current location
    LaunchedEffect(Unit) {
        val location = getCurrentLocation(context)

        // Set initial position
        val initialPosition = if (location != null) {
            MapPosition(
                latitude = location.latitude,
                longitude = location.longitude,
                isInitialized = true
            )
        } else {
            MapPosition(
                latitude = DEFAULT_LATITUDE,
                longitude = DEFAULT_LONGITUDE,
                isInitialized = true
            )
        }

        // Configure map settings
        mapView.apply {
            setMultiTouchControls(true)
            setTileSource(TileSourceFactory.MAPNIK)
            controller.setZoom(currentZoom)
            controller.animateTo(GeoPoint(initialPosition.latitude, initialPosition.longitude))
        }
    }
    // Configuration initiale de la carte
    DisposableEffect(context) {
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        onDispose {
            mapView.overlays.clear()
        }
    }

    // Function to create custom marker drawable
    fun createCustomMarkerDrawable(context: Context, color: Int): android.graphics.drawable.Drawable {
        val layers = arrayOf(
            android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(android.graphics.Color.WHITE)
                setSize(40, 40)
            },
            ContextCompat.getDrawable(context, R.drawable.ic_location_on)?.mutate()?.apply {
                setBounds(8, 8, 32, 32)
            }
        )

        return android.graphics.drawable.LayerDrawable(layers).apply {
            setLayerInset(0, 0, 0, 0, 0)
            setLayerInset(1, 4, 4, 4, 4)
        }
    }

    // In A_ClientsLocationGps.kt
    LaunchedEffect(viewModel._modelAppsFather.clientsDisponible) {
        markers.clear()
        mapView.overlays.clear()

        viewModel._modelAppsFather.clientsDisponible.forEach { client ->
            client.gpsLocation.locationGpsMark?.let { existingMarker ->
                existingMarker.apply {
                    position = GeoPoint(
                        client.gpsLocation.latitude,
                        client.gpsLocation.longitude
                    )
                    id = client.id.toString()
                    title = client.nom
                    snippet = if (client.statueDeBase.cUnClientTemporaire)
                        "Client temporaire" else "Client permanent"

                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    val actuelleEtat = client.gpsLocation.actuelleEtat
                    val markerColor = actuelleEtat.let { statue ->
                        val colorInt = statue?.color?.let { ContextCompat.getColor(context, it) }
                        String.format("#%06X", (colorInt?.and(0xFFFFFF)))
                    }
                    val color = Color(android.graphics.Color.parseColor(markerColor)).toArgb()
                    icon = createCustomMarkerDrawable(context, color)

                    infoWindow = MarkerInfoWindow(R.layout.marker_info_window, mapView)

                    val container = infoWindow.view.findViewById<LinearLayout>(R.id.info_window_container) ?: return@LaunchedEffect
                    val backgroundColor = actuelleEtat.let { statue ->
                        statue?.let { ContextCompat.getColor(context, it.color) }
                    } ?: ContextCompat.getColor(context, android.R.color.white)
                    container.setBackgroundColor(backgroundColor)

                    setOnMarkerClickListener { marker, _ ->
                        selectedMarker = marker
                        showMarkerDialog = true
                        if (showMarkerDetails) marker.showInfoWindow()
                        true
                    }
                }

                markers.add(existingMarker)
                mapView.overlays.add(existingMarker)
            } ?: run {
                // Création d'un nouveau marqueur si aucun n'existe
                Marker(mapView).apply {
                    val actuelleEtat = client.gpsLocation.actuelleEtat

                    position = GeoPoint(
                        client.gpsLocation.latitude,
                        client.gpsLocation.longitude
                    )
                    title = client.nom
                    snippet = if (client.statueDeBase.cUnClientTemporaire)
                        "Client temporaire" else "Client permanent"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    infoWindow = MarkerInfoWindow(R.layout.marker_info_window, mapView)

                    // Même click listener pour les nouveaux marqueurs
                    setOnMarkerClickListener { marker, _ ->
                        selectedMarker = marker
                        showMarkerDialog = true
                        if (showMarkerDetails) marker.showInfoWindow()
                        true
                    }

                    // Appliquer la couleur par défaut pour les nouveaux marqueurs
                    icon = createCustomMarkerDrawable(context,
                        Color(android.graphics.Color.parseColor(client.couleur)).toArgb())

                    val container = infoWindow.view.findViewById<LinearLayout>(R.id.info_window_container) ?: return@LaunchedEffect
                    val backgroundColor = actuelleEtat.let { statue ->
                        statue?.let { ContextCompat.getColor(context, it.color) }
                    } ?: ContextCompat.getColor(context, android.R.color.white)
                    container.setBackgroundColor(backgroundColor)

                    client.gpsLocation.locationGpsMark = this
                    markers.add(this)
                    mapView.overlays.add(this)
                }
            }
        }

        if (showMarkerDetails) {
            markers.forEach { it.showInfoWindow() }
        }

        mapView.invalidate()
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView }
        )

        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Red, CircleShape)
                .align(Alignment.Center)
        )

        if (viewModel._paramatersAppsViewModelModel.fabsVisibility) {
            MapControls(
                viewModelInitApp = viewModel,
                mapView = mapView,
                markers = markers,
                showMarkerDetails = showMarkerDetails,
                onShowMarkerDetailsChange = {
                    showMarkerDetails = it
                    markers.forEach { marker ->
                        if (showMarkerDetails) marker.showInfoWindow()
                        else marker.closeInfoWindow()
                    }
                    mapView.invalidate()
                },
                onMarkerSelected = {
                    selectedMarker = it
                    showMarkerDialog = true
                }
            )
        }
        if (showMarkerDialog && selectedMarker != null) {
            MarkerStatusDialog(
                viewModel = viewModel,
                selectedMarker = selectedMarker,
                onDismiss = { showMarkerDialog = false }
            )
        }
    }
}

private data class MapPosition(
    val latitude: Double,
    val longitude: Double,
    val isInitialized: Boolean
)

