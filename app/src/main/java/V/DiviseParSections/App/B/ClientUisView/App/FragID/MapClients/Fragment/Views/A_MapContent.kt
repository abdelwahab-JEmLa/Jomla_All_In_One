package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Options.MapSecteursPolygenHandelButtons
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Test.insert2SecteurEtPolygon
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.View.addToMapOsmdroid
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.View.getNoSqlDisplayer
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.handleActiveTransaction
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.handleFilterMarkersClick
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Init.getMapUpdateTriggers
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.updateMapMarkers
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions.cleanupMapResources
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions.initializeMapPosition
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Ui.MarkerEditModeOverlay
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Ui.handleMarkerPositionUpdate
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.MarkerStatusDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options.A_GlobalOptionsControlsFloatingActionButtons_FragId1
import Z_CodePartageEntreApps.Modules.PanelsGroupeButtonHandler
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun MapContent(
    viewModel: ViewModel_MapClients_App2FragID1,
    clientEnCourDeVent: Long,
    onUpdateLongAppSetting: () -> Unit,
    onClear: () -> Unit,
    mapReloadTrigger: Int = 0,
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val currentZoom by remember { mutableDoubleStateOf(18.2) }
    val mapView = remember { MapView(context) }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    var showMarkerDialog by remember { mutableStateOf(false) }
    val showMarkerDetails by remember { mutableStateOf(true) }
    var currentFilterMode by remember {
        mutableStateOf(
            if (viewModel.groupeRepositorysProtoAvJuin3.repositorys_Model.activeIdDeA5Vendeur == 1L) {
                ViewModel_MapClients_App2FragID1.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR
            } else {
                ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAll
            }
        )
    }

    var editingMarkerId by remember { mutableLongStateOf(0L) }
    var showEditMarkerMode by remember { mutableStateOf(false) }
    val activeTransactionId =
        viewModel.groupeRepositorysProtoAvJuin3.repositorys_Model.activeVId_C3_BonAchate_Repository.collectAsState().value
    val clientDataBaseSnapList = viewModel.bProto_ClientsDataBase


    // Collect the current mapReloadTrigger from viewModel for sectors updates
    val sectorMapReloadTrigger = viewModel.mapReloadTigger

    // Handle active transaction by showing the relevant marker
    LaunchedEffect(viewModel.groupeRepositorysProtoAvJuin3.repositorys_Model.activeVId_C3_BonAchate_Repository.collectAsState().value) {
        handleActiveTransaction(activeTransactionId, viewModel, mapView) { marker ->
            selectedMarker = marker
            showMarkerDialog = true
        }
    }

    // Effect to update sectors on map - now responds to viewModel.mapReloadTigger changes
    LaunchedEffect(mapView, sectorMapReloadTrigger) {
        // Execute all database operations on Dispatchers.IO
        withContext(Dispatchers.IO) {
            // Get sector and polygon DAOs
            val polygonDao = viewModel.appDatabase.polygonGeoLimiteDaoDao()

            // Check if there are existing sectors
            if (uiState.e1SecteurDeClientsList.isEmpty()) {
                // If no sectors exist, create two with their polygons
                insert2SecteurEtPolygon(
                    viewModel,
                    polygonDao = polygonDao
                )
            }

            // Get all sectors and polygon points
            val allSecteurs = uiState.e1SecteurDeClientsList
            val allPolygonPoints = polygonDao.getAll()

            // Get structured information about sectors and their polygons

            val secteurPolygonInfoList = getNoSqlDisplayer(
                uiState = uiState,
                viewModel = viewModel,
                polygonDao = polygonDao
            )

            // Back to main thread to update UI
            withContext(Dispatchers.Main) {
                // Clear existing sector polygons first to prevent duplicates
                val sectorsToRemove = mapView.overlays.take(allSecteurs.size)
                mapView.overlays.removeAll(sectorsToRemove)

                // Add sectors to map
                addToMapOsmdroid(mapView, secteurPolygonInfoList, allPolygonPoints, allSecteurs)
            }
        }
    }

    // Initialize map with current location or default position
    LaunchedEffect(Unit) {
        initializeMapPosition(context, mapView, currentZoom)
    }

    // Configure OSMDroid and handle cleanup
    DisposableEffect(context, mapReloadTrigger) {
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        onDispose {
            cleanupMapResources(mapView, viewModel)
        }
    }

    // Main effect for updating markers on the map when data changes
    val updateTriggers = getMapUpdateTriggers(
        viewModel.c3_BonAchate_List,
        viewModel,
        clientDataBaseSnapList.size,
        clientEnCourDeVent,
        currentFilterMode,
        mapReloadTrigger
    )

    LaunchedEffect(updateTriggers) {
        updateMapMarkers(
            mapView,
            viewModel,
            clientDataBaseSnapList,
            currentFilterMode,
            showMarkerDetails
        ) { marker ->
            selectedMarker = marker
            showMarkerDialog = true
        }
    }

    // Main UI layout with map and controls
    Box(modifier = Modifier.fillMaxSize()) {
        // Map view
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView }
        )

        // Center marker indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(ComposeColor.Red, CircleShape)   // Fixed: Using Compose Color
                .align(Alignment.Center)
        )

        val panelsGroupeButtonHandler = koinInject<PanelsGroupeButtonHandler>()

        val isSecteursButtonVisible = panelsGroupeButtonHandler
            ._paneleGroupeButtonList.value
            .find {
                it.key == PanelsGroupeButtonHandler
                    .PanelsGroupeButtonDeClasse.Keys.MapSecteursPolygenHandelButtons
            }
            ?.isVisible ?: false

        if (isSecteursButtonVisible) {
            MapSecteursPolygenHandelButtons(mapView, viewModel)
        }

        A_GlobalOptionsControlsFloatingActionButtons_FragId1(
            viewModel = viewModel,
            mapView = mapView,
            onClear = onClear,
            currentFilterMode = currentFilterMode,
            onFilterMarkers = {
                handleFilterMarkersClick(mapView, currentFilterMode) { newMode ->
                    currentFilterMode = newMode
                }
            },
            onPickFilter = {
                currentFilterMode = it
            }
        )

        // Marker edit mode overlay
        if (showEditMarkerMode) {
            MarkerEditModeOverlay(
                onCancel = {
                    showEditMarkerMode = false
                    editingMarkerId = 0L
                },
                onConfirm = {
                    handleMarkerPositionUpdate(
                        clientDataBaseSnapList,
                        editingMarkerId,
                        mapView,
                        viewModel
                    )
                    showEditMarkerMode = false
                    editingMarkerId = 0L
                }
            )
        }

        // Marker status dialog
        if (showMarkerDialog && selectedMarker != null) {
            MarkerStatusDialog(
                uiState=uiState,
                viewModel = viewModel,
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
