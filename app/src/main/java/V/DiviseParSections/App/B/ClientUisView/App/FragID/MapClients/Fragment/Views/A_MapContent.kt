package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views

import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.M2Client
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
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import Z_CodePartageEntreApps.Modules.PanelsGroupeButtonHandler
import android.content.Context
import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.clientjetpack.R
import org.koin.compose.koinInject
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.compose.ui.graphics.Color as ComposeColor

private const val SCROLL_RELOAD_DEBOUNCE_MS = 3_000L
private const val TAG_SCROLL               = "ScrollVelocity"
@Composable
fun MapContent(
    viewModel: MapClientsViewModel,
    fragmentNavigationHandler_NewProto: FragmentNavigationHandler_NewProto,
    focusedValuesGetter: FocusedValuesGetter = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    onUpdateLongAppSetting: () -> Unit,
    onClear: () -> Unit,
) {          //<--
//TODO(1): fait aussi a chaque 2 sec de relode et la proximite c 10km
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val defaultZoom = 18.2
    val currentZoom by remember { mutableDoubleStateOf(defaultZoom) }
    val mapView = remember { MapView(context) }
    val showMarkerDetails by remember { mutableStateOf(true) }

    val currentFilterMode = viewModel.active_Datas.filter_marqueClient_enum_entries
        ?: MapClientsViewModel.VisibleClientsNow.showAll

    val proximityFilterCenter = uiState.proximityFilterCenter

    val locationTracker = remember {
        LocationTracker(
            context = context,
            mapView = mapView,
            radius = 25.0,
            xmlResources = listOf("location_arrow" to R.drawable.ic_location_dot)
        )
    }

    val gpsFollowModeActive = focusedValuesGetter.active_Central_Values.gps_follow_mode_active ?: false
    LaunchedEffect(gpsFollowModeActive) {
        if (gpsFollowModeActive) locationTracker.enableFollowMode() else locationTracker.disableFollowMode()
    }

    // Velocity-based proximity reload:
    //   - track position + timestamp of the previous scroll event
    //   - if instantaneous speed ≥ viewModel.scrollSpeedThresholdMps  →  reload 3-km markers
    //   - debounce: at most 1 reload every SCROLL_RELOAD_DEBOUNCE_MS
    val lastScrollPoint = remember { mutableStateOf<GeoPoint?>(null) }
    val lastScrollMs    = remember { mutableStateOf(0L) }
    val lastReloadMs    = remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        initializeMapPosition(context, mapView, currentZoom, shouldCenterOnLocation = true)
        locationTracker.startTracking()
        ensureLocationOverlayIsAtBottom(mapView)
        val center = mapView.mapCenter as? GeoPoint ?: return@LaunchedEffect
        lastScrollPoint.value = GeoPoint(center.latitude, center.longitude)
        lastScrollMs.value    = System.currentTimeMillis()
        Log.i(TAG_SCROLL, "INIT — rechargement initial au centre (${center.latitude}, ${center.longitude})")
        viewModel.relod_map_marques_du_3km_du_centre_map(center.latitude, center.longitude)
    }

    DisposableEffect(context) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val scrollListener = object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                val center = mapView.mapCenter as? GeoPoint ?: run {
                    Log.w(TAG_SCROLL, "onScroll — mapCenter est null, ignoré")
                    return false
                }
                val nowMs  = System.currentTimeMillis()
                val prevPt = lastScrollPoint.value
                val prevMs = lastScrollMs.value

                // Always update the last-known scroll position
                lastScrollPoint.value = GeoPoint(center.latitude, center.longitude)
                lastScrollMs.value    = nowMs

                if (prevPt == null) {
                    Log.d(TAG_SCROLL, "onScroll — premier événement, ancre initialisée → (${"$"}{center.latitude}, ${"$"}{center.longitude})")
                    return false
                }

                val deltaMs   = (nowMs - prevMs).coerceAtLeast(1L)
                val meters    = haversineMeters(prevPt.latitude, prevPt.longitude, center.latitude, center.longitude)
                val speedMps  = meters / (deltaMs / 1_000.0)
                val speedStr  = "%.1f".format(speedMps)   // extracted to avoid Throwable? mismatch in Log.v

                Log.v(TAG_SCROLL, "onScroll — Δ=${meters.toInt()} m  Δt=${deltaMs} ms  vitesse=$speedStr m/s  (seuil=${viewModel.scrollSpeedThresholdMps} m/s)")

                val debounceOk = (nowMs - lastReloadMs.value) >= SCROLL_RELOAD_DEBOUNCE_MS

                when {
                    speedMps < viewModel.scrollSpeedThresholdMps -> {
                        Log.v(TAG_SCROLL, "  → vitesse insuffisante ($speedStr < ${viewModel.scrollSpeedThresholdMps} m/s), pas de rechargement")
                    }
                    !debounceOk -> {
                        val waitMs = SCROLL_RELOAD_DEBOUNCE_MS - (nowMs - lastReloadMs.value)
                        Log.d(TAG_SCROLL, "  → vitesse OK mais debounce actif, attendre encore ${waitMs} ms")
                    }
                    else -> {
                        Log.i(TAG_SCROLL, "  ✓ RECHARGEMENT DÉCLENCHÉ — vitesse=$speedStr m/s  centre=(${center.latitude}, ${center.longitude})")
                        lastReloadMs.value = nowMs
                        viewModel.relod_map_marques_du_3km_du_centre_map(center.latitude, center.longitude)
                    }
                }
                return false
            }
            override fun onZoom(event: ZoomEvent?): Boolean = false
        }
        mapView.addMapListener(scrollListener)

        onDispose {
            mapView.removeMapListener(scrollListener)
            locationTracker.stopTracking()
            cleanupMapResources(mapView, viewModel)
        }
    }

    LaunchedEffect(
        viewModel.getter.repo9AppCompt.datasValue.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        viewModel.getter.repo8BonVent.datasValue.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        viewModel.getter.repo2Client.datasValue.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        uiState.b_ClientInfosProtoJuin3List.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        focusedValuesGetter.filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        currentFilterMode,
        proximityFilterCenter,
        viewModel.mapReloadTrigger,
    ) {
        addOuUpdateMapMarkers(
            fragmentNavigationHandler_NewProto=fragmentNavigationHandler_NewProto,
            uiState = uiState,
            viewModel = viewModel,
            mapView = mapView,
            currentFilterMode = currentFilterMode,
            showMarkerDetails = showMarkerDetails,
            proximityFilterCenter = proximityFilterCenter,
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
        markerToEdit?.let { handleMarkerPositionUpdate(m2Client = it, uiState = uiState, viewModel = viewModel, mapView = mapView) }
        onEditModeChange(false)
        onMarkerKeyIdChange(null)
        mapView.controller.setZoom(zoomLevel)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val markerStatusDialogActiveM2Client = uiState.markerStatusDialogActiveM2Client

        AndroidView(modifier = Modifier.fillMaxSize(), factory = { mapView })

        Box(modifier = Modifier.size(8.dp).background(ComposeColor.Red, CircleShape).align(Alignment.Center))

        val panelsGroupeButtonHandler = koinInject<PanelsGroupeButtonHandler>()
        panelsGroupeButtonHandler._paneleGroupeButtonList.value
            .find { it.key == PanelsGroupeButtonHandler.PanelsGroupeButtonDeClasse.Keys.MapSecteursPolygenHandelButtons }
            ?.isVisible ?: false

        A_GlobalOptionsControlsFloatingActionButtons_FragId1(
            viewModel = viewModel,
            mapView = mapView,
            onClear = onClear,
            currentFilterMode = currentFilterMode,
            onFilterMarkers = { handleFilterMarkersClick(mapView, currentFilterMode) { viewModel.update_filter_marqueClient(it) } },
            onPickFilter = { viewModel.update_filter_marqueClient(it) }
        )

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
                        onMarkerKeyIdChange = { viewModel.update_uiState_m2Client_In_ShowEditMarkerMode(null) },
                        zoomLevel = defaultZoom
                    )
                },
                onCenterToGPS = {
                    locationTracker.centerMapOnCurrentLocation()
                    mapView.controller.setZoom(19.2)
                }
            )
        }

        val activeOnVentM2ClientInfos = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVentM2ClientInfos
        val shouldShowMarkerDialog = (activeOnVentM2ClientInfos != null || markerStatusDialogActiveM2Client != null) &&
                focusedValuesGetter.active_Central_Values.click_On_Marque != ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients

        if (shouldShowMarkerDialog) {
            MarkerStatusDialog(
                fragmentNavigationHandler_NewProto = fragmentNavigationHandler_NewProto,
                viewModel = viewModel,
                relative_M2Client = activeOnVentM2ClientInfos ?: markerStatusDialogActiveM2Client,
                markerStatusDialogActiveM2Client = markerStatusDialogActiveM2Client,
                onUpdateLongAppSetting = onUpdateLongAppSetting,
                onClickToEditeMarquerPosition = { viewModel.update_uiState_m2Client_In_ShowEditMarkerMode(it) },
                onRemoveMark = { relative_M2Client ->
                    mapView.overlays.filterIsInstance<Marker>()
                        .find { it.id == relative_M2Client?.id.toString() }
                        ?.let { mapView.overlays.remove(it); mapView.invalidate() }
                },
                on_dissmiss_dialog_avec_enleve_focuse_bon = {
                    viewModel.clear_UiState_MarkerStatusDialog_Active_M2Client()
                    viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
                        .desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()
                    focusedValuesGetter.update_activeCentralValues(
                        focusedValuesGetter.active_Central_Values.copy(markerStatusDialogActiveM2Client = null)
                    )
                }
            )
        }

        focusedValuesGetter.active_Central_Values.affiche_Floating_Button_Cible_Client.ifTrue {
            Floating_Separated_FragMap_Button_1()
        }
        focusedValuesGetter.active_Central_Values.affiche_Floating_Button_TogleFilterMarquers.ifTrue {
            Floating_Separated_FragMap_Button_4()
        }
        focusedValuesGetter.active_Central_Values.affiche_Floating_Button_gps_follow_mode_active.ifTrue {
            Floating_Separated_FragMap_Button_2(
                mapView = mapView,
                viewModel = viewModel,
                buttonState = Button_State.get_Default().copy(
                    text_Label = "affiche_Floating_Button_gps_follow_mode_active",
                    icons = Pair(Icons.Default.GpsNotFixed, Icons.Default.GpsFixed),
                    colors = Pair(Color.Red, Color.Green)
                )
            )
        }
    }
}

private fun ensureLocationOverlayIsAtBottom(mapView: MapView) {
    mapView.overlays.find { it.javaClass.simpleName.contains("Location") || it.toString().contains("location", ignoreCase = true) }
        ?.let { mapView.overlays.remove(it); mapView.overlays.add(0, it) }
}

private fun haversineMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val r = 6_371_000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
    return r * 2 * atan2(sqrt(a), sqrt(1 - a))
}
