package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.Button_State
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.Floating_Separated_FragMap_Button_1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.Floating_Separated_FragMap_Button_2
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.Floating_Separated_FragMap_Button_4
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.handleFilterMarkersClick
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.addOuUpdateMapMarkers
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions.cleanupMapResources
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions.initializeMapPosition
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Ui.MarkerEditModeOverlay
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Ui.handleMarkerPositionUpdate
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.MarkerStatusDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options.A_GlobalOptionsControlsFloatingActionButtons_FragId1
import V.DiviseParSections.App.Shared.Modules.Helper.M1.LocationTracker.Module.LocationTracker
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import Z_CodePartageEntreApps.Modules.PanelsGroupeButtonHandler
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.clientjetpack.R
import org.koin.compose.koinInject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun MapContent(
    viewModel: MapClientsViewModel,
    focusedValuesGetter: FocusedValuesGetter = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    onUpdateLongAppSetting: () -> Unit,
    onClear: () -> Unit,
) {      //<--
//TODO(1): fait que au start de focuse current position pendnt  1 sec
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val defaultZoom = 18.2
    val currentZoom by remember { mutableDoubleStateOf(defaultZoom) }
    val mapView = remember { MapView(context) }
    val showMarkerDetails by remember { mutableStateOf(true) }

    var currentFilterMode by remember {
        mutableStateOf(
            when (focusedValuesGetter.currentApp_Est_Admin) {
                false -> MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR
                true -> MapClientsViewModel.VisibleClientsNow.showAll
            }
        )
    }

    LaunchedEffect(
        focusedValuesGetter.currentApp_Est_Admin,
        focusedValuesGetter.active_Central_Values.visibleClientsNow
    ) {
        currentFilterMode = focusedValuesGetter.active_Central_Values.visibleClientsNow ?: run {
            when (focusedValuesGetter.currentApp_Est_Admin) {
                false -> MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR
                true -> MapClientsViewModel.VisibleClientsNow.showAll
            }
        }
    }

    // Location tracker initialization
    val locationTracker = remember {
        LocationTracker(
            context = context,
            mapView = mapView,
            radius = 25.0,
            xmlResources = listOf("location_arrow" to R.drawable.ic_location_dot)
        )
    }

    // FIXED: Listen for GPS follow mode changes from the FAB dropdown
    val gpsFollowModeActive = focusedValuesGetter.active_Central_Values.gps_follow_mode_active ?: false

    LaunchedEffect(gpsFollowModeActive) {
        if (gpsFollowModeActive) {
            locationTracker.enableFollowMode()
        } else {
            locationTracker.disableFollowMode()
        }
    }

    // Initialize map and start location tracking
    LaunchedEffect(Unit) {
        initializeMapPosition(context, mapView, currentZoom, shouldCenterOnLocation = true)

        locationTracker.startTracking()
        ensureLocationOverlayIsAtBottom(mapView)
    }

    // Map configuration and cleanup
    DisposableEffect(context) {
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        onDispose {
            locationTracker.stopTracking()
            cleanupMapResources(mapView, viewModel)
        }
    }

    // Update markers when data changes
    LaunchedEffect(
        viewModel.getter.repo9AppCompt.datasValue.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        viewModel.getter.repo8BonVent.datasValue.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        viewModel.getter.repo2Client.datasValue.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        uiState.b_ClientInfosProtoJuin3List.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        focusedValuesGetter.filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        currentFilterMode,
    ) {
        addOuUpdateMapMarkers(
            viewModel = viewModel,
            uiState = uiState,
            mapView = mapView,
            currentFilterMode = currentFilterMode,
            showMarkerDetails = showMarkerDetails
        )
        ensureLocationOverlayIsAtBottom(mapView)
    }

    fun handleEditGps(
        markerToEdit: M2Client?,
        uiState: UiState,
        viewModel: MapClientsViewModel,
        mapView: MapView,
        onEditModeChange: (Boolean) -> Unit,
        onMarkerKeyIdChange: (M2Client?) -> Unit,
        zoomLevel: Double
    ) {
        markerToEdit?.let { marker ->
            handleMarkerPositionUpdate(
                m2Client = marker,
                uiState = uiState,
                viewModel = viewModel,
                mapView = mapView,
            )
        }

        onEditModeChange(false)
        onMarkerKeyIdChange(null)
        mapView.controller.setZoom(zoomLevel)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val markerStatusDialogActiveM2Client = uiState.markerStatusDialogActiveM2Client

        // Main map view
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .getSemanticsTag(
                    nomVal = "markerStatusDialogActiveM2Client",
                    data = markerStatusDialogActiveM2Client
                ),
            factory = { mapView }
        )

        // Center crosshair indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(ComposeColor.Red, CircleShape)
                .align(Alignment.Center)
        )

        // Secteurs button handler
        val panelsGroupeButtonHandler = koinInject<PanelsGroupeButtonHandler>()
        val isSecteursButtonVisible = panelsGroupeButtonHandler
            ._paneleGroupeButtonList.value
            .find {
                it.key == PanelsGroupeButtonHandler
                    .PanelsGroupeButtonDeClasse.Keys.MapSecteursPolygenHandelButtons
            }
            ?.isVisible ?: false

        if (isSecteursButtonVisible) {
            // MapSecteursPolygenHandelButtons(mapView, viewModel)
        }

        // Global options and controls
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
        if (uiState.m2Client_In_ShowEditMarkerMode != null) {
            MarkerEditModeOverlay(
                onCancel = {
                    viewModel.update_uiState_m2Client_In_ShowEditMarkerMode(null)
                    mapView.controller.setZoom(defaultZoom)
                },
                onConfirm = {
                    handleEditGps(
                        markerToEdit = uiState.m2Client_In_ShowEditMarkerMode,
                        uiState = uiState,
                        viewModel = viewModel,
                        mapView = mapView,
                        onEditModeChange = { },
                        onMarkerKeyIdChange = {
                            viewModel.update_uiState_m2Client_In_ShowEditMarkerMode(
                                null
                            )
                        },
                        zoomLevel = defaultZoom
                    )
                },
                onCenterToGPS = {
                    locationTracker.centerMapOnCurrentLocation()
                    mapView.controller.setZoom(19.2)
                }
            )
        }

        // Marker status dialog
        val activeOnVentM2ClientInfos =
            viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVentM2ClientInfos

        if (activeOnVentM2ClientInfos != null || markerStatusDialogActiveM2Client != null) {
            MarkerStatusDialog(
                viewModel = viewModel,
                relative_M2Client = activeOnVentM2ClientInfos ?: markerStatusDialogActiveM2Client,
                mapView = mapView,
                uiState = uiState,
                onUpdateLongAppSetting = onUpdateLongAppSetting,
                onClickToEditeMarquerPosition = { client ->
                    viewModel.update_uiState_m2Client_In_ShowEditMarkerMode(client)
                },
                onRemoveMark = { marker ->
                    marker?.let {
                        mapView.overlays.remove(it)
                        mapView.invalidate()
                    }
                },
            )
        }

        // Floating button for client targeting
        val affiche_Floating_Button_Cible_Client =
            focusedValuesGetter.active_Central_Values.affiche_Floating_Button_Cible_Client
        affiche_Floating_Button_Cible_Client.ifTrue {
            Floating_Separated_FragMap_Button_1()
        }
        // Floating button for client targeting
        val affiche_Floating_Button_TogleFilterMarquers =
            focusedValuesGetter.active_Central_Values.affiche_Floating_Button_TogleFilterMarquers
        affiche_Floating_Button_TogleFilterMarquers.ifTrue {
            Floating_Separated_FragMap_Button_4()
        }

        // Floating button for client targeting
        val affiche_Floating_Button_gps_follow_mode_active =
            focusedValuesGetter.active_Central_Values.affiche_Floating_Button_gps_follow_mode_active
        affiche_Floating_Button_gps_follow_mode_active.ifTrue {
            Floating_Separated_FragMap_Button_2(
                buttonState= Button_State.get_Default().copy(
                text_Label = "affiche_Floating_Button_gps_follow_mode_active",
                icons = Pair(Icons.Default.GpsNotFixed, Icons.Default.GpsFixed),
                colors = Pair(Color.Red, Color.Green)
            )
            )
        }
    }
}
private fun ensureLocationOverlayIsAtBottom(mapView: MapView) {
    val locationOverlay = mapView.overlays.find { overlay ->
        overlay.javaClass.simpleName.contains("Location") ||
                overlay.toString().contains("location", ignoreCase = true)
    }

    locationOverlay?.let { overlay ->
        mapView.overlays.remove(overlay)
        mapView.overlays.add(0, overlay) // Add at index 0 to keep it at bottom
    }
}
