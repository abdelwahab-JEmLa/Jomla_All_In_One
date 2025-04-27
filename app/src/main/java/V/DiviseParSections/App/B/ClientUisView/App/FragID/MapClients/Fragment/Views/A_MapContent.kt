package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.FabButtons
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.addSectorsToMap
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.getPolygenDeChaqueSecteur
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.insert2SecteurEtPolygon
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.handleActiveTransaction
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.handleFilterMarkersClick
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Init.getMapUpdateTriggers
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.updateMapMarkers
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions.cleanupMapResources
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions.configureOSMDroid
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions.initializeMapPosition
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Ui.MarkerEditModeOverlay
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Ui.handleMarkerPositionUpdate
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.MarkerStatusDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options.A_GlobalOptionsControlsFloatingActionButtons_FragId1
import Z_CodePartageEntreApps.Windows.B.Windows.Options.A_OptionsControlsButtons_Main
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
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
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.ui.graphics.Color as ComposeColor


@Composable
fun MapContent(
    viewModel: ViewModel_MapClients_App2FragID1,
    viewModelInitApp: ViewModelInitApp,
    clientEnCourDeVent: Long,
    onUpdateLongAppSetting: () -> Unit,
    onClear: () -> Unit,
    mapReloadTrigger: Int = 0,
) {
    val context = LocalContext.current
    val currentZoom by remember { mutableDoubleStateOf(18.2) }
    val mapView = remember { MapView(context) }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    var showMarkerDialog by remember { mutableStateOf(false) }
    val showMarkerDetails by remember { mutableStateOf(true) }
    var currentFilterMode by remember {
        mutableStateOf<ViewModel_MapClients_App2FragID1.VisibleClientsNow>(
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX
        )
    }

    var editingMarkerId by remember { mutableLongStateOf(0L) }
    var showEditMarkerMode by remember { mutableStateOf(false) }
    val activeTransactionId =
        viewModel.repo_0_0_HeadSQLRepositorys.repositorys_Model.activeVId_1_3_TransactionCommercial.collectAsState().value
    val clientDataBaseSnapList = viewModel.bProto_ClientsDataBase

    // Handle active transaction by showing the relevant marker
    LaunchedEffect(viewModel.repo_0_0_HeadSQLRepositorys.repositorys_Model.activeVId_1_3_TransactionCommercial.collectAsState().value) {
        handleActiveTransaction(activeTransactionId, viewModel, mapView) { marker ->
            selectedMarker = marker
            showMarkerDialog = true
        }
    }


    LaunchedEffect(mapView) {       //<--
    //TODO(1): pk le neveau secteur ne s affiche pas 
        // Exécuter toutes les opérations de base de données sur Dispatchers.IO
        withContext(Dispatchers.IO) {
            // Récupérer les secteurs et leurs polygones
            val secteurDao = viewModel.appDatabase.secteurDeClientsDao()
            val polygonDao = viewModel.appDatabase.polygonGeoLimiteDaoDao()

            // Vérifier s'il y a des secteurs existants
            if (secteurDao.getCount() == 0) {
                // Si aucun secteur n'existe, en créer deux avec leurs polygones
                insert2SecteurEtPolygon(secteurDao, polygonDao)
            }

            // Récupérer tous les secteurs et points de polygone
            val allSecteurs = secteurDao.getAll()
            val allPolygonPoints = polygonDao.getAll()

            // Récupérer les informations structurées sur les secteurs et leurs polygones
            val secteurPolygonInfoList = getPolygenDeChaqueSecteur(secteurDao, polygonDao)

            // Revenir sur le thread principal pour mettre à jour l'interface utilisateur
            withContext(Dispatchers.Main) {
                // Ajouter les secteurs à la carte
                addSectorsToMap(mapView, secteurPolygonInfoList, allPolygonPoints, allSecteurs)
            }
        }
    }

    // Initialize map with current location or default position
    LaunchedEffect(Unit) {
        initializeMapPosition(context, mapView, currentZoom)
    }

    // Configure OSMDroid and handle cleanup
    DisposableEffect(context, mapReloadTrigger) {
        configureOSMDroid(context, mapView)

        onDispose {
            cleanupMapResources(mapView, viewModel)
        }
    }

    // Main effect for updating markers on the map when data changes
    val updateTriggers = getMapUpdateTriggers(
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
            clientEnCourDeVent,
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

        // Main controls
        A_OptionsControlsButtons_Main()
        FabButtons(viewModel)

        // Floating action buttons for map controls
        if (viewModelInitApp._paramatersAppsViewModelModel.fabsVisibility) {
            A_GlobalOptionsControlsFloatingActionButtons_FragId1(
                viewModel = viewModel,
                mapView = mapView,
                viewModelInitApp = viewModelInitApp,
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
        }

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
