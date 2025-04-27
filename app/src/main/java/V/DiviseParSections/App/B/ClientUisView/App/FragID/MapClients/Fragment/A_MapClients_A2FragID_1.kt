package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.MarkerStatusDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options.A_GlobalOptionsControlsFloatingActionButtons_FragId1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.DEFAULT_LATITUDE
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.DEFAULT_LONGITUDE
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.getCurrentLocation
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_3_TransactionCommercial
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_CodePartageEntreApps.Windows.B.Windows.Options.A_OptionsControlsButtons_Main
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

/**
 * Main composable function for the Map Clients view
 */
@Composable
fun A_MapClients_A2FragID_1(
    modifier: Modifier = Modifier,
    viewModel: ViewModel_MapClients_App2FragID1 = koinViewModel(),
    viewModelInitApp: ViewModelInitApp = viewModel(),
    clientEnCourDeVent: Long = 0,
    onUpdateLongAppSetting: () -> Unit = {},
    onClear: () -> Unit = {},
    mapReloadTrigger: Int = 0,
) {
    val progress by viewModel.mainRepositery.progressRepo.collectAsState()

    // Clean up resources when fragment is disposed
    DisposableEffect(Unit) {
        onDispose {
            // Clean up any resources when navigating away
            viewModel.cleanupResources()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (progress < 1.0f) {
            LoadingProgressOverlay(progress = progress)
        } else {
            MapContent(
                viewModel = viewModel,
                viewModelInitApp = viewModelInitApp,
                clientEnCourDeVent = clientEnCourDeVent,
                onUpdateLongAppSetting = onUpdateLongAppSetting,
                onClear = onClear,
                mapReloadTrigger = mapReloadTrigger
            )
        }
    }
}

/**
 * Main content of the map with all client markers
 */
@Composable
private fun MapContent(
    viewModel: ViewModel_MapClients_App2FragID1,
    viewModelInitApp: ViewModelInitApp,
    clientEnCourDeVent: Long,
    onUpdateLongAppSetting: () -> Unit,
    onClear: () -> Unit,
    mapReloadTrigger: Int = 0
) {
    val context = LocalContext.current
    val currentZoom by remember { mutableDoubleStateOf(18.2) }
    val mapView = remember { MapView(context) }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    var showMarkerDialog by remember { mutableStateOf(false) }
    val showMarkerDetails by remember { mutableStateOf(true) }
    var currentFilterMode by remember { mutableStateOf<ViewModel_MapClients_App2FragID1.VisibleClientsNow>(
        ViewModel_MapClients_App2FragID1.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX) }

    var editingMarkerId by remember { mutableLongStateOf(0L) }
    var showEditMarkerMode by remember { mutableStateOf(false) }
    val activeTransactionId = viewModel.repo_0_0_HeadOfRepositorys_Repository.repositorys_Model.activeVId_1_3_TransactionCommercial.collectAsState().value

    // Handle active transaction by showing the relevant marker
    LaunchedEffect(viewModel.repo_0_0_HeadOfRepositorys_Repository.repositorys_Model.activeVId_1_3_TransactionCommercial.collectAsState().value) {

        if (activeTransactionId != 0L) {
            // Find the transaction to get the client ID
            val activeTransaction = viewModel.repo_0_0_HeadOfRepositorys_Repository.repositorys_Model
                .repository_1_3_TransactionCommercial.modelDatasSnapList
                .find { it.vid == activeTransactionId }

            activeTransaction?.let { transaction ->
                // Find the marker for this client
                val clientMarker = mapView.overlays.filterIsInstance<Marker>()
                    .find { it.id == transaction.clientAcheteurID.toString() }

                clientMarker?.let { marker ->
                    // Select the marker and show its information
                    selectedMarker = marker
                    showMarkerDialog = true
                    marker.showInfoWindow()

                    // Animate to the marker position
                    mapView.controller.animateTo(marker.position)
                }
            }
        }
    }

    // Initialize map with current location or default position
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

            withContext(Dispatchers.Main) {
                controller.animateTo(GeoPoint(initialPosition.latitude, initialPosition.longitude))
            }
        }
    }

    // Configure OSMDroid and handle cleanup
    DisposableEffect(context, mapReloadTrigger) {
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        onDispose {
            // Properly clean up OSMDroid resources
            mapView.onDetach()
            mapView.overlays.clear()
            // Cancel any ongoing operations
            viewModel.cancelActiveOperations()
        }
    }

    val clientDataBaseSnapList = viewModel.bProto_ClientsDataBase

    // Main effect for updating markers on the map when data changes
    LaunchedEffect(
        clientDataBaseSnapList.size,
        clientEnCourDeVent,
        currentFilterMode,
        viewModel.mapReloadTigger,
        viewModel.filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList.size,
        mapReloadTrigger
    ) {
        val existingMarkers = mapView.overlays.filterIsInstance<Marker>()
        existingMarkers.forEach { it.closeInfoWindow() }

        val markersToRemove = mapView.overlays.filterIsInstance<Marker>()
            .filter { marker -> clientDataBaseSnapList.any { it.id.toString() == marker.id } }
        mapView.overlays.removeAll(markersToRemove)

        // Filter clients based on the current mode
        val clientsToShow = when (currentFilterMode) {
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showNonAbsentClientsOnly -> {
                clientDataBaseSnapList.filter {
                    it.actuelleEtat != B_ClientDataBase.DernierEtatAAffiche.ACHETEUR_NON_DISPO
                }
            }
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.affichePourCollecteurCommendes -> {
                clientDataBaseSnapList.filter {
                    it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.Cible
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.CIBLE_PRIORITE_2
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.VENDU_A_LUI
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.FERME
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.A_EVITE
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.AVEC_MARCHANDISE
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.ACHETEUR_NON_DISPO
                }
            }
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 -> {
                clientDataBaseSnapList.filter {
                    it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.CIBLE_POUR_2
                }
            }
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAtayClients -> {
                clientDataBaseSnapList.filter {
                    it.typeDeSonMagasine == B_ClientDataBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
                }
            }
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAlimentionlients -> {
                clientDataBaseSnapList.filter {
                    it.typeDeSonMagasine == B_ClientDataBase.TypeDeSonMagasine.AlIMENTATION_GENERALE
                }
            }
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAll -> {
                clientDataBaseSnapList
            }
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showClientsWithConfirmedProducts -> {
                val clientsWithConfirmedProducts = viewModel.repo_0_0_HeadOfRepositorys_Repository.repositorys_Model
                    .repository_1_3_TransactionCommercial.modelDatasSnapList
                    .filter { bonAchat ->
                        bonAchat.etateActuellementEst == _1_3_TransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME
                                || bonAchat.etateActuellementEst == _1_3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                    }
                    .map { bonAchat -> bonAchat.clientAcheteurID }
                    .distinct()

                clientDataBaseSnapList.filter { client ->
                    clientsWithConfirmedProducts.contains(client.id)
                }
            }
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR -> {
                clientDataBaseSnapList.filter {
                    it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.NON_DEFINI
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.Cible
                }
            }
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX -> {
                clientDataBaseSnapList.filter {
                    it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.NON_DEFINI
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.Cible
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.VENDU_A_LUI
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.FERME
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.A_EVITE
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.AVEC_MARCHANDISE
                            || it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.ACHETEUR_NON_DISPO
                }
            }
            else -> {
                clientDataBaseSnapList
            }
        }

        // Add markers for filtered clients
        clientsToShow.forEach { client ->
            val dayFilters = viewModel.filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList
            val shouldApplyDayFilter = dayFilters.isNotEmpty()

            val lastPurchaseDay = if (shouldApplyDayFilter) {
                findLastPurchaseDayForClient(
                    viewModel.repo_01_VentsHistoriquesDataBase.modelDatasSnapList,
                    client.id
                )
            } else {
                ""
            }

            val shouldDisplayClient = if (!shouldApplyDayFilter) {
                true
            } else {
                lastPurchaseDay.isNotEmpty() && dayFilters.contains(lastPurchaseDay)
            }

            if (shouldDisplayClient) {
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

                        title = if (viewModel.afficheLesJoursAuNoms) {
                            val lastPurchaseInfo = findLastPurchaseInfoForClient(
                                viewModel.repo_01_VentsHistoriquesDataBase.modelDatasSnapList,
                                client.id
                            )

                            if (lastPurchaseInfo.dayName.isNotEmpty()) {
                                val clientStateArabic = getClientStateInArabic(
                                    client.id,
                                    viewModel.repo_01_VentsHistoriquesDataBase.modelDatasSnapList
                                )
                                "${lastPurchaseInfo.dayName} (${lastPurchaseInfo.timeStr})" +
                                        "\n$clientStateArabic"     +
                                        "\n${client.nom}"
                            } else {
                                client.nom
                            }
                        } else {
                            client.nom
                        }

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
                                val backgroundColor = actuelleEtat.let { statue ->
                                    ContextCompat.getColor(context, statue.color)
                                }

                                it.setBackgroundColor(backgroundColor)

                                val titleTextViewId = xmlResources
                                    .find { it.first == "title" }?.second
                                titleTextViewId?.let { titleId ->
                                    val titleTextView = infoWindow.view.findViewById<android.widget.TextView>(titleId)
                                    titleTextView?.gravity = android.view.Gravity.CENTER
                                }
                            }
                        } catch (e: Exception) { }

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
                } catch (e: Exception) { }
            }
        }
        mapView.invalidate()
             //<--
        //TODO(1): cree un autre verification de tout les 
        // Find latest transactions for each transaction du repository_1_3_TransactionCommercial si .ouvert == true 
        //<--
        //TODO(1): ouvre     son client mark 
        val latestTransactionsMap = viewModel.repo_0_0_HeadOfRepositorys_Repository.repositorys_Model
            .repository_1_3_TransactionCommercial.modelDatasSnapList
            .groupBy { it.clientAcheteurID }
            .mapValues { (_, transactions) ->
                transactions.maxByOrNull { it.timestamps }
            }

        latestTransactionsMap.forEach { (clientId, latestTransaction) ->
            if (latestTransaction?.etateActuellementEst == _1_3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT ||
                latestTransaction?.ouvert == true) {
                val marker = mapView.overlays.filterIsInstance<Marker>()
                    .find { it.id == clientId.toString() }

                marker?.let {
                    selectedMarker = it
                    showMarkerDialog = true
                }
            }
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
                .background(Color.Red, CircleShape)
                .align(Alignment.Center)
        )

        // Main controls
        A_OptionsControlsButtons_Main()

        // Floating action buttons for map controls
        if (viewModelInitApp._paramatersAppsViewModelModel.fabsVisibility) {
            A_GlobalOptionsControlsFloatingActionButtons_FragId1(
                viewModel = viewModel,
                mapView = mapView,
                viewModelInitApp = viewModelInitApp,
                onClear = onClear,
                currentFilterMode = currentFilterMode,
                onFilterMarkers = {
                    mapView.overlays.filterIsInstance<Marker>().forEach { it.closeInfoWindow() }

                    val previousMode = currentFilterMode
                    val newMode = when (currentFilterMode) {
                        ViewModel_MapClients_App2FragID1.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR ->
                            ViewModel_MapClients_App2FragID1.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX
                        ViewModel_MapClients_App2FragID1.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX ->
                            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAll
                        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAll ->
                            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showNonAbsentClientsOnly
                        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showNonAbsentClientsOnly ->
                            ViewModel_MapClients_App2FragID1.VisibleClientsNow.affichePourCollecteurCommendes
                        ViewModel_MapClients_App2FragID1.VisibleClientsNow.affichePourCollecteurCommendes ->
                            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2
                        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 ->
                            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAtayClients
                        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAtayClients ->
                            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAlimentionlients
                        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAlimentionlients ->
                            ViewModel_MapClients_App2FragID1.VisibleClientsNow.showClientsWithConfirmedProducts
                        ViewModel_MapClients_App2FragID1.VisibleClientsNow.showClientsWithConfirmedProducts ->
                            ViewModel_MapClients_App2FragID1.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR
                    }
                    currentFilterMode = newMode
                },
                onPickFilter = {
                    currentFilterMode = it
                }
            )
        }

        // Marker edit mode overlay
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

        // Marker status dialog
        if (showMarkerDialog && selectedMarker != null ) {
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

/**
 * Loading progress overlay
 */
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

/**
 * Helper data class for map positioning
 */
private data class MapPosition(
    val latitude: Double,
    val longitude: Double,
    val isInitialized: Boolean
)
