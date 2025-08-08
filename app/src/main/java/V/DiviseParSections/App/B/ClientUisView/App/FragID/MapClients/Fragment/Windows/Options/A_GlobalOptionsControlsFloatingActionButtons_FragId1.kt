package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.C.FilterView
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.AddMarkerButton
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.LabelsButton
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.LocationTrackingButton
import V.DiviseParSections.App.Shared.Modules.Helper.M1.LocationTracker.Module.rememberLocationTracker
import Z_CodePartageEntreApps.Modules.PanelsGroupeButtonHandler
import Z_CodePartageEntreApps.Windows.A.B_DataBaseEdite.Windows.DataBaseEditeWindows
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import Z_MasterOfApps.Resources.XmlsFilesHandler.Companion.xmlResources
import Z_MasterOfApps.Z_AppsFather.Kotlin.Partage.Views.AnimatedIconLottieJsonFileFF
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.roundToInt

private class FilterLogger {
    companion object {
        private const val TAG = "FilterChangeLog"
        private val logs = mutableListOf<String>()

        fun logFilterChange(
            previousMode: MapClientsViewModel.VisibleClientsNow,
            newMode: MapClientsViewModel.VisibleClientsNow,
        ) {
            val timestamp =
                java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                    .format(java.util.Date())
            val logMessage = "[$timestamp] Filter changed: $previousMode -> $newMode"
            logs.add(logMessage)
            android.util.Log.d(TAG, logMessage)
        }

        fun getLogs(): List<String> = logs.toList()
    }
}

@Composable
fun A_GlobalOptionsControlsFloatingActionButtons_FragId1(
    viewModel: MapClientsViewModel,
    mapView: MapView,
    onClear: () -> Unit,
    onPickFilter: (MapClientsViewModel.VisibleClientsNow) -> Unit,
    onFilterMarkers: () -> Unit,
    currentFilterMode: MapClientsViewModel.VisibleClientsNow,
    panelsGroupeButtonHandler: PanelsGroupeButtonHandler =
        koinInject<PanelsGroupeButtonHandler>()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatabaseEditDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(false) }
    val proximiteMeter = 50.0
    val context = mapView.context
    val packageName = context.packageName
    var showDayFilterDialog by remember { mutableStateOf(false) }

    // Create LocationTracker
    val locationTracker = rememberLocationTracker(
        mapView = mapView,
        proxim = proximiteMeter,
        xmlResources = xmlResources
    )

    // États pour le drag
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AfficheTemporaireDeCibleEtPasseAEux(
                    showLabels = showLabels,
                    viewModel = viewModel,
                    onFilterChanged = { newMode ->
                        onPickFilter(newMode)
                    },
                )

                if (showMenu) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val couleurButton1 = Color(0xFFF44336)
                        FloatingActionButton(
                            onClick = {
                                showDayFilterDialog = true
                            },
                            modifier = Modifier.size(40.dp),
                            containerColor = couleurButton1
                        ) {
                            Icon(Icons.Filled.SearchOff, "Filter by day")
                        }

                        if (showLabels) {
                            Text(
                                "فلتر: ${viewModel.filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList}",
                                modifier = Modifier
                                    .background(couleurButton1)
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }

                    val coloreButton = Color(0xFFF44336)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                showFilterDialog = true
                            },
                            modifier = Modifier.size(40.dp),
                            containerColor = coloreButton
                        ) {
                            Icon(Icons.Filled.FilterAlt, "Filter clientAchteurs")
                        }

                        if (showLabels) {
                            Text(
                                "Filtrer les clientAchteurs",
                                modifier = Modifier
                                    .background(coloreButton)
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }

                    ControlButton(
                        onClick = {
                            showDatabaseEditDialog = true
                        },
                        icon = Icons.Default.Fireplace,
                        contentDescription = "showDatabaseEditDialog",
                        showLabels = showLabels,
                        labelText = "showDatabaseEditDialog",
                        containerColor = Color(0xFFF44336)
                    )

                    LocationTrackingButton(
                        showLabels = showLabels,
                        mapView = mapView,
                        proximiteMeter = proximiteMeter,
                        xmlResources = xmlResources
                    )


                    AddMarkerButton(
                        viewModel = viewModel,
                        showLabels = showLabels,
                        mapView = mapView,
                    )

                    FragId1But_3(
                        viewModel = viewModel,
                        showLabels = showLabels,
                        contentDescription = "auClickeCaUpdateClientPar",
                    )


                    But1_NearbyMarkersButton(
                        viewModel = viewModel,
                        showLabels = showLabels,
                        markers = mapView.overlays.filterIsInstance<Marker>().toMutableList(),
                        locationTracker = locationTracker,
                        proximiteMeter = proximiteMeter,
                        mapView = mapView
                    )

                    But_2(
                        viewModel = viewModel,
                        textButton = "onFilterMarkers",
                        showLabels = showLabels,
                        onClick = {
                            mapView.overlays.filterIsInstance<Marker>()
                                .forEach { it.closeInfoWindow() }

                            // Log the filter change
                            val previousMode = currentFilterMode
                            val newMode = when (currentFilterMode) {
                                MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR ->
                                    MapClientsViewModel.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX

                                MapClientsViewModel.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX ->
                                    MapClientsViewModel.VisibleClientsNow.showAll

                                MapClientsViewModel.VisibleClientsNow.showAll ->
                                    MapClientsViewModel.VisibleClientsNow.showNonAbsentClientsOnly

                                MapClientsViewModel.VisibleClientsNow.showNonAbsentClientsOnly ->
                                    MapClientsViewModel.VisibleClientsNow.affichePourCollecteurCommendes

                                MapClientsViewModel.VisibleClientsNow.affichePourCollecteurCommendes ->
                                    MapClientsViewModel.VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2

                                MapClientsViewModel.VisibleClientsNow.showClientsOnlyAcEtateCIBLE_POUR_2 ->
                                    MapClientsViewModel.VisibleClientsNow.showAtayClients

                                MapClientsViewModel.VisibleClientsNow.showAtayClients ->
                                    MapClientsViewModel.VisibleClientsNow.showAlimentionlients

                                MapClientsViewModel.VisibleClientsNow.showAlimentionlients ->
                                    MapClientsViewModel.VisibleClientsNow.showClientsWithConfirmedProducts

                                MapClientsViewModel.VisibleClientsNow.showClientsWithConfirmedProducts ->
                                    MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR

                                else -> {
                                    MapClientsViewModel.VisibleClientsNow.showAll
                                }
                            }

                            FilterLogger.logFilterChange(previousMode, newMode)
                            onFilterMarkers()
                        },
                        currentFilterMode = currentFilterMode
                    )
                }
                if (showMenu) {
                    LabelsButton(
                        showLabels = showLabels,
                        onShowLabelsChange = { showLabels = it }
                    )
                }

                MenuButton(
                    showLabels = showLabels,
                    showMenu = showMenu,
                    onShowMenuChange = { showMenu = it }
                )
            }
        }


        // Show DataBaseEditeWindows dialog when showDatabaseEditDialog is true
        if (showDatabaseEditDialog) {
            DataBaseEditeWindows(
                onDissmis = { showDatabaseEditDialog = false }
            )
        }

        // Remplacement de FilterModesDialog par FilterView
        if (showFilterDialog) {
            FilterView(
                currentFilterMode = currentFilterMode,
                onFilterSelect = { selectedMode ->
                    mapView.overlays.filterIsInstance<Marker>().forEach { it.closeInfoWindow() }

                    // Log the filter change when selecting from dialog
                    FilterLogger.logFilterChange(currentFilterMode, selectedMode)

                    onPickFilter(selectedMode)
                },
                onDismiss = { showFilterDialog = false }
            )
        }
        if (showDayFilterDialog) {
            DayFilterDialog(
                viewModel = viewModel,
                onDismiss = { showDayFilterDialog = false }
            )
        }

        panelsGroupeButtonHandler.AfficheDialogesHeadApps()

    }
}

@Composable
fun MenuButton(
    showLabels: Boolean,
    showMenu: Boolean,
    onShowMenuChange: (Boolean) -> Unit
) {
    ControlButton(
        onClick = { onShowMenuChange(!showMenu) },
        icon = if (showMenu) Icons.Default.ExpandLess else Icons.Default.Warning,
        contentDescription = if (showMenu) "Hide menu" else "Show menu",
        showLabels = showLabels,
        labelText = if (showMenu) "Hide" else "Options",
        containerColor = Color(0xFFE3DEDE)
    )
}

@Composable
fun ControlButton(
    onClick: () -> Unit,
    icon: Any,
    contentDescription: String,
    showLabels: Boolean,
    labelText: String,
    containerColor: Color,
    modifier: Modifier = Modifier,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        when (icon) {
            is ImageVector -> {
                FloatingActionButton(
                    onClick = {
                        onClick()
                    },
                    modifier = modifier.size(40.dp),
                    containerColor = containerColor
                ) {
                    Icon(icon, contentDescription)
                }
            }
            is LottieJsonGetterR_Raw_Icons -> {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            onClick()
                        }
                        .background(
                            color = containerColor,
                            shape = CircleShape
                        )
                        .also {
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedIconLottieJsonFileFF(
                        ressourceXml = icon,
                        onClick = onClick
                    )
                }
            }

            else -> {
                throw IllegalArgumentException("Unsupported icon type")
            }
        }

        if (showLabels) {
            Text(
                labelText,
                modifier = Modifier
                    .background(containerColor)
                    .padding(4.dp),
                color = Color.White
            )
        }
    }
}
