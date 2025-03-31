package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.B.Dialogs.A.A_GlobalOptionsControlsFloatingActionButtons_FragId1
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.B.Dialogs.MarkerStatusDialog
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.B.Dialogs.Utils.DEFAULT_LATITUDE
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.B.Dialogs.Utils.DEFAULT_LONGITUDE
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.B.Dialogs.Utils.getCurrentLocation
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.ViewModel_App2FragID1
import Z_MasterOfApps.Resources.XmlsFilesHandler.Companion.xmlResources
import android.content.Context
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

@Composable
fun A_id1_ClientsLocationGps(
    modifier: Modifier = Modifier,
    viewModel: ViewModel_App2FragID1 = koinViewModel(),
    viewModelInitApp: ViewModelInitApp = viewModel(),
    clientEnCourDeVent: Long = 0,
    onUpdateLongAppSetting: () -> Unit = {},
    onClear: () -> Unit = {},
) {
    val progress by viewModel.mainRepositery.progressRepo.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        if (progress < 1.0f) {
            LoadingProgressOverlay(progress = progress)
        } else {
            MapContent(
                viewModel = viewModel,
                viewModelInitApp = viewModelInitApp,
                clientEnCourDeVent = clientEnCourDeVent,
                onUpdateLongAppSetting = onUpdateLongAppSetting,
                onClear = onClear
            )
        }
    }
}

@Composable
private fun MapContent(
    viewModel: ViewModel_App2FragID1,
    viewModelInitApp: ViewModelInitApp,
    clientEnCourDeVent: Long,
    onUpdateLongAppSetting: () -> Unit,
    onClear: () -> Unit
) {

    val context = LocalContext.current
    val currentZoom by remember { mutableDoubleStateOf(18.2) }
    val mapView = remember { MapView(context) }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    var showMarkerDialog by remember { mutableStateOf(false) }
    val showMarkerDetails by remember { mutableStateOf(true) }
    var currentFilterMode by remember { mutableStateOf<ViewModel_App2FragID1.VisbleClientsNow>(ViewModel_App2FragID1.VisbleClientsNow.affichePourCollecteurCommendes) }

    var editingMarkerId by remember { mutableLongStateOf(0L) }
    var showEditMarkerMode by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val location = getCurrentLocation(context)

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

        mapView.apply {
            setMultiTouchControls(true)
            setTileSource(TileSourceFactory.MAPNIK)
            controller.setZoom(currentZoom)
            controller.animateTo(GeoPoint(initialPosition.latitude, initialPosition.longitude))
        }
    }

    DisposableEffect(context) {
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        onDispose {
            mapView.overlays.clear()
        }
    }

    val clientDataBaseSnapList = viewModel.bProto_ClientsDataBase

    LaunchedEffect(clientDataBaseSnapList.toList(), clientEnCourDeVent, currentFilterMode,
            viewModel.mapReloadTigger
    ) {
        val existingMarkers = mapView.overlays.filterIsInstance<Marker>()
        existingMarkers.forEach { it.closeInfoWindow() }

        val markersToRemove = mapView.overlays.filterIsInstance<Marker>()
            .filter { marker -> clientDataBaseSnapList.any { it.id.toString() == marker.id } }
        mapView.overlays.removeAll(markersToRemove)

        val clientsToShow = when (currentFilterMode) {
            ViewModel_App2FragID1.VisbleClientsNow.showNonAbsentClientsOnly -> {
                clientDataBaseSnapList.filter {
                    it.actuelleEtat != B_ClientDataBase.DernierEtatAAffiche.CLIENT_ABSENT
                }
            }
            ViewModel_App2FragID1.VisbleClientsNow.affichePourCollecteurCommendes -> {
                clientDataBaseSnapList.filter {
                    it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.Cible
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.CIBLE_PRIORITE_2
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.VENDU_A_LUI
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.FERME
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.A_EVITE
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.AVEC_MARCHANDISE
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.CLIENT_ABSENT
                }
            }
            ViewModel_App2FragID1.VisbleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 -> {
                clientDataBaseSnapList.filter {
                    it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.CIBLE_POUR_2
                }
            }
            ViewModel_App2FragID1.VisbleClientsNow.showAtayClients -> {
                clientDataBaseSnapList.filter {
                    it.typeDeSonMagasine == B_ClientDataBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
                }
            }
            ViewModel_App2FragID1.VisbleClientsNow.showAlimentionlients -> {
                clientDataBaseSnapList.filter {
                    it.typeDeSonMagasine == B_ClientDataBase.TypeDeSonMagasine.AlIMENTATION_GENERALE
                }
            }
            ViewModel_App2FragID1.VisbleClientsNow.showAll -> {
                clientDataBaseSnapList
            }
        }

        clientsToShow.forEach { client ->
            try {
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
                    title = client.nom
                    snippet = if (client.cUnClientTemporaire)
                        "Client temporaire" else "Client permanent"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                    try {
                        val markerInfoWindowLayout = xmlResources
                            .find { it.first == "marker_info_window" }?.second

                        if (markerInfoWindowLayout == null) {
                            throw IllegalStateException("marker_info_window layout not found")
                        }

                        infoWindow = MarkerInfoWindow(markerInfoWindowLayout, mapView)

                        val containerResourceId = xmlResources
                            .find { it.first == "info_window_container" }?.second

                        if (containerResourceId == null) {
                            throw IllegalStateException("info_window_container ID not found")
                        }

                        val container = infoWindow.view.findViewById<LinearLayout>(containerResourceId)
                        container?.let {
                            val backgroundColor = actuelleEtat?.let { statue ->
                                ContextCompat.getColor(context, statue.color)
                            } ?: ContextCompat.getColor(context, android.R.color.white)
                            it.setBackgroundColor(backgroundColor)
                        }
                    } catch (e: Exception) {
                    }

                    setOnMarkerClickListener { clickedMarker, _ ->
                        selectedMarker = clickedMarker
                        showMarkerDialog = true
                        if (showMarkerDetails) clickedMarker.showInfoWindow()
                        true
                    }
                }

                mapView.overlays.add(marker)

                if (showMarkerDetails) {
                    marker.showInfoWindow()
                }
            } catch (_: Exception) {
            }
        }

        mapView.invalidate()
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

        if (viewModelInitApp._paramatersAppsViewModelModel.fabsVisibility) {
            A_GlobalOptionsControlsFloatingActionButtons_FragId1(
                viewModel = viewModel,
                mapView = mapView,
                viewModelInitApp = viewModelInitApp,
                onClear = onClear,
                currentFilterMode = currentFilterMode,
                onFilterMarkers = {
                    mapView.overlays.filterIsInstance<Marker>().forEach { it.closeInfoWindow() }

                    currentFilterMode = when (currentFilterMode) {
                        ViewModel_App2FragID1.VisbleClientsNow.showAll -> ViewModel_App2FragID1.VisbleClientsNow.showNonAbsentClientsOnly
                        ViewModel_App2FragID1.VisbleClientsNow.showNonAbsentClientsOnly -> ViewModel_App2FragID1.VisbleClientsNow.affichePourCollecteurCommendes
                        ViewModel_App2FragID1.VisbleClientsNow.affichePourCollecteurCommendes -> ViewModel_App2FragID1.VisbleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2
                        ViewModel_App2FragID1.VisbleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 -> ViewModel_App2FragID1.VisbleClientsNow.showAtayClients
                        ViewModel_App2FragID1.VisbleClientsNow.showAtayClients -> ViewModel_App2FragID1.VisbleClientsNow.showAlimentionlients
                        ViewModel_App2FragID1.VisbleClientsNow.showAlimentionlients -> ViewModel_App2FragID1.VisbleClientsNow.showAll
                    }
                }
            )
        }

        if (showEditMarkerMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "Mode Édition de Marqueur",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FloatingActionButton(
                        onClick = {
                            showEditMarkerMode = false
                            editingMarkerId = 0L
                        },
                        containerColor = MaterialTheme.colorScheme.error
                    ) {
                        Icon(Icons.Default.Close, "Cancel")
                    }

                    FloatingActionButton(
                        onClick = {
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

                            showEditMarkerMode = false
                            editingMarkerId = 0L
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Check, "Confirm")
                    }
                }
            }
        }

        if (showMarkerDialog && selectedMarker != null) {
            MarkerStatusDialog(
                viewModel = viewModel,
                viewModelInitApp = viewModelInitApp,
                selectedMarker = selectedMarker,
                onDismiss = { showMarkerDialog = false },
                onUpdateLongAppSetting = onUpdateLongAppSetting,
                onClickToEditeMarquerPosition = { clientId ->
                    showMarkerDialog = false
                    editingMarkerId = clientId
                    showEditMarkerMode = true
                },
                onRemoveMark = { marker ->
                    marker?.let {
                        mapView.overlays.remove(it)
                        mapView.invalidate()
                    }
                }
            )
        }
    }
}

@Composable
private fun LoadingProgressOverlay(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = "Chargement des données...",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

private data class MapPosition(
    val latitude: Double,
    val longitude: Double,
    val isInitialized: Boolean
)
