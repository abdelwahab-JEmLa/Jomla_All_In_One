package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.handleFilterMarkersClick
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.addOuUpdateMapMarkers
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions.cleanupMapResources
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions.initializeMapPosition
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Ui.MarkerEditModeOverlay
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Ui.handleMarkerPositionUpdate
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.MarkerStatusDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options.A_GlobalOptionsControlsFloatingActionButtons_FragId1
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.koin.compose.koinInject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun MapContent(
    viewModel: MapClientsViewModel,
    onUpdateLongAppSetting: () -> Unit,
    onClear: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val currentZoom by remember { mutableDoubleStateOf(18.2) }
    val mapView = remember { MapView(context) }
    val showMarkerDetails by remember { mutableStateOf(true) }
    var currentFilterMode by remember {
        mutableStateOf(
            if ((viewModel.getter.repo9AppCompt.currentAppCompt?.keyID
                    ?: "") == viewModel.getter.parametresAppComptNonSaved.currentActiveFocucedM9AppComptKeyID
            ) {
                MapClientsViewModel.VisibleClientsNow.showAll
            } else {
                MapClientsViewModel.VisibleClientsNow.showAll
            }
        )
    }
    var editingMarkerKeyId by remember { mutableStateOf<M2Client?>(null) }
    var showEditMarkerMode by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        initializeMapPosition(context, mapView, currentZoom)
    }

    DisposableEffect(context) {
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        onDispose {
            cleanupMapResources(mapView, viewModel)
        }
    }

    LaunchedEffect(
        viewModel.getter.repo9AppCompt.datasValue.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        viewModel.getter.repo8BonVent.datasValue.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        viewModel.getter.repo2Client.datasValue.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        uiState.b_ClientInfosProtoJuin3List.map { it.dernierTimeTampsSynchronisationAvecFireBase },
        currentFilterMode,
    ) {
        addOuUpdateMapMarkers(
            viewModel = viewModel,
            uiState = uiState,
            mapView = mapView,
            currentFilterMode = currentFilterMode,
            showMarkerDetails = showMarkerDetails
        )
    }


    Box(modifier = Modifier.fillMaxSize()) {
        val markerStatusDialogActiveM2Client = uiState.markerStatusDialogActiveM2Client

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .getSemanticsTag(
                    nomVal = "markerStatusDialogActiveM2Client",
                    data = markerStatusDialogActiveM2Client
                )
            ,
            factory = { mapView }
        )

        // Center marker indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(ComposeColor.Red, CircleShape)
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
            // MapSecteursPolygenHandelButtons(mapView, viewModel)
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

        if (showEditMarkerMode) {
            MarkerEditModeOverlay(
                onCancel = {
                    showEditMarkerMode = false
                    editingMarkerKeyId = null
                },
                onConfirm = {
                    editingMarkerKeyId?.let {
                        handleMarkerPositionUpdate(
                            m2Client = it,
                            uiState = uiState,
                            viewModel = viewModel,
                            mapView = mapView,
                        )
                    }
                    showEditMarkerMode = false
                    editingMarkerKeyId = null
                }
            )
        }

        val activeOnVentM2ClientInfos = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVentM2ClientInfos

        if (activeOnVentM2ClientInfos != null ||  markerStatusDialogActiveM2Client != null) {
            MarkerStatusDialog(
                viewModel = viewModel,
                relative_M2Client = activeOnVentM2ClientInfos ?: markerStatusDialogActiveM2Client,
                mapView = mapView,
                uiState = uiState,
                onUpdateLongAppSetting = onUpdateLongAppSetting,
                onClickToEditeMarquerPosition = { client ->
                    editingMarkerKeyId = client
                    showEditMarkerMode = true
                },
                onRemoveMark = { marker ->
                    marker?.let {
                        mapView.overlays.remove(it)
                        mapView.invalidate()
                    }
                },
            )
        }
    }
}
