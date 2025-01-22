package Views._2LocationGpsClients.App.MainApp

import Views._2LocationGpsClients.App.MainApp.B.Dialogs.MarkerStatusDialog
import Z_MasterOfApps.Kotlin.Model.Extension.clientsDisponible
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.content.Context
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
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.B.Dialogs.MapControls
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.Utils.DEFAULT_LATITUDE
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.Utils.DEFAULT_LONGITUDE
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.Utils.getCurrentLocation
import com.example.clientjetpack.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

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

    // Fonction d'extension pour créer un marqueur personnalisé
    fun createCustomMarkerDrawable(context: Context, color: Int): android.graphics.drawable.Drawable {
        // Créer un LayerDrawable pour combiner le cercle et l'icône
        val layers = arrayOf(
            // Cercle noir en arrière-plan
            android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(android.graphics.Color.WHITE)
                setSize(40, 40) // Taille du cercle en pixels
            },
            // Icône du marqueur
            ContextCompat.getDrawable(context, R.drawable.ic_location_on)?.mutate()?.apply {
                setBounds(8, 8, 32, 32) // Position et taille de l'icône à l'intérieur du cercle
            }
        )

        return android.graphics.drawable.LayerDrawable(layers).apply {
            setLayerInset(0, 0, 0, 0, 0) // Pas d'inset pour le cercle
            setLayerInset(1, 4, 4, 4, 4) // Insets pour centrer l'icône
        }
    }

    LaunchedEffect(viewModel._modelAppsFather.clientsDisponible) {
        markers.clear()
        mapView.overlays.clear()

        viewModel._modelAppsFather.clientsDisponible.forEach { client ->
            client.gpsLocation.locationGpsMark?.let { existingMarker ->
                // Trouver le bon vent associé pour obtenir le statut
                val bonVent = viewModel.mapsHandler.findBonVentForMarker(existingMarker)

                // Obtenir la couleur à partir du statut
                val markerColor = bonVent?.bonStatueDeBase?.currentStatue?.let { statue ->
                    val colorInt = ContextCompat.getColor(context, statue.color)
                    String.format("#%06X", (colorInt and 0xFFFFFF))
                } ?: client.gpsLocation.couleur // Utiliser la couleur du client par défaut

                // Mettre à jour l'apparence du marqueur
                val color = Color(android.graphics.Color.parseColor(markerColor)).toArgb()
                existingMarker.icon = createCustomMarkerDrawable(context, color)

                // Mettre à jour la couleur dans les données du client
                client.gpsLocation.couleur = markerColor

                existingMarker.position = GeoPoint(
                    client.gpsLocation.latitude,
                    client.gpsLocation.longitude
                )
                existingMarker.id = client.id.toString()
                existingMarker.title = client.nom
                existingMarker.snippet = if (client.statueDeBase.cUnClientTemporaire)
                    "Client temporaire" else "Client permanent"

                markers.add(existingMarker)
                mapView.overlays.add(existingMarker)
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

