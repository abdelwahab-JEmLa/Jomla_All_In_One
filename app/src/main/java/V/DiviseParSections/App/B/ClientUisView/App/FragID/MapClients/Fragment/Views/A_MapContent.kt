package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.But1_OnClickMode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.But2_GPSFollowMode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.Button_State
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.ClientFilterMode_Button_4
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.handleFilterMarkersClick
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
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.clientjetpack.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.ui.graphics.Color as ComposeColor

private const val SCROLL_RELOAD_DEBOUNCE_MS = 3_000L

@Composable
fun MapContent(
    viewModel: MapClientsViewModel,
    fragmentNavigationHandler_NewProto: FragmentNavigationHandler_NewProto,
    focusedValuesGetter: FocusedValuesGetter = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    onUpdateLongAppSetting: () -> Unit,
    onClear: () -> Unit,
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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
        viewModel.relod_map_marques_du_3km_du_centre_map(center.latitude, center.longitude)
    }

    DisposableEffect(context) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val scrollListener = object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                val center = mapView.mapCenter as? GeoPoint ?: return false
                val nowMs  = System.currentTimeMillis()
                val prevPt = lastScrollPoint.value
                val prevMs = lastScrollMs.value

                lastScrollPoint.value = GeoPoint(center.latitude, center.longitude)
                lastScrollMs.value    = nowMs

                if (prevPt == null) return false

                val deltaMs   = (nowMs - prevMs).coerceAtLeast(1L)
                val meters    = haversineMeters(prevPt.latitude, prevPt.longitude, center.latitude, center.longitude)
                val speedMps  = meters / (deltaMs / 1_000.0)

                val debounceOk = (nowMs - lastReloadMs.value) >= SCROLL_RELOAD_DEBOUNCE_MS

                when {
                    speedMps < viewModel.scrollSpeedThresholdMps -> Unit
                    !debounceOk -> Unit
                    else -> {
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
    val uiState_viewModelNewProtoPatterns by viewModelNewProtoPatterns.uiState.collectAsState()

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
            fragmentNavigationHandler_NewProto = fragmentNavigationHandler_NewProto,
            uiState = uiState,
            viewModel = viewModel,
            mapView = mapView,
            currentFilterMode = currentFilterMode,
            showMarkerDetails = showMarkerDetails,
            proximityFilterCenter = proximityFilterCenter,
            proximityFilterRadiusMeters = viewModel.proximite_de_vision_meter.toDouble(),
            list_M13TarificationInfos = uiState_viewModelNewProtoPatterns.list_M13TarificationInfos
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

        // Phone-entry dialog: shown when Cree_et_envoi_whatsapp_pdf is tapped for a client
        // whose phone number is missing. After the user enters a number the phone is saved
        // and the PDF + WhatsApp send is triggered automatically.
        uiState.pendingWhatsAppSend?.let { pendingClient ->
            WhatsAppPhoneEntryDialog(
                clientName = pendingClient.nom,
                onDismiss = { viewModel.set_pendingWhatsAppSend(null) },
                onPhoneConfirmed = { enteredPhone ->
                    viewModel.set_pendingWhatsAppSend(null)
                    // Persist the entered phone number so it is available next time
                    viewModel.updateData(pendingClient.copy(numTelephone = enteredPhone))
                    // Generate PDF then open WhatsApp Business
                    scope.launch {
                        delay(300) // Let focused-values settle after updateData
                        val aCentralFacade = viewModel.aCentralFacade
                        val fg = focusedValuesGetter
                        P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll
                            .Windows.But_4_FloatingSearchFAB.Buttons.OnVentBon_LocalPdf.View
                            .initiateBackgroundPdfCreation_NewP(
                                context = context,
                                aCentralFacade = aCentralFacade,
                                focusedValuesGetter = fg,
                                list_M13TarificationInfos = uiState_viewModelNewProtoPatterns.list_M13TarificationInfos,
                                onPdfSaved = { savedPath ->
                                    val pdfFile = java.io.File(savedPath)
                                    var cleaned = enteredPhone.replace(Regex("[^0-9]"), "")
                                    if (!cleaned.startsWith("213")) {
                                        if (cleaned.startsWith("0")) cleaned = cleaned.drop(1)
                                        cleaned = "213$cleaned"
                                    }
                                    try {
                                        val pdfUri = androidx.core.content.FileProvider.getUriForFile(
                                            context, "${context.packageName}.fileprovider", pdfFile
                                        )
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "application/pdf"
                                            setPackage("com.whatsapp.w4b")
                                            putExtra(Intent.EXTRA_STREAM, pdfUri)
                                            putExtra(Intent.EXTRA_TEXT, "Voici votre bon de commande")
                                            putExtra("jid", "$cleaned@s.whatsapp.net")
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Erreur WhatsApp: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                    }
                }
            )
        }

        focusedValuesGetter.active_Central_Values.affiche_Floating_Button_Cible_Client.ifTrue {
            But1_OnClickMode(
                viewModel = viewModel
            )
        }
        focusedValuesGetter.active_Central_Values.affiche_Floating_Button_TogleFilterMarquers.ifTrue {
            ClientFilterMode_Button_4()
        }
        focusedValuesGetter.active_Central_Values.affiche_Floating_Button_gps_follow_mode_active.ifTrue {
            But2_GPSFollowMode(
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

/**
 * Dialog that prompts the user to enter a phone number before the WhatsApp PDF send.
 * Mirrors the PhoneEntryDialog in Button_Click_Send_Stored_Bon_Par_whatsappBuisness.
 */
@Composable
private fun WhatsAppPhoneEntryDialog(
    clientName: String,
    onDismiss: () -> Unit,
    onPhoneConfirmed: (String) -> Unit,
) {
    var phoneNumber by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }

    Dialog(onDismissRequest = onDismiss) {
        androidx.compose.material3.Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Numéro de $clientName",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        showError = false
                    },
                    label = { Text("Numéro de téléphone") },
                    placeholder = { Text("0XXXXXXXXX") },
                    isError = showError,
                    supportingText = if (showError) {
                        { Text("Numéro invalide", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    leadingIcon = {
                        androidx.compose.material3.Icon(Icons.Default.Phone, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val cleaned = phoneNumber.trim()
                            if (cleaned.isNotEmpty()) onPhoneConfirmed(cleaned) else showError = true
                        }
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Annuler") }
                    Button(
                        onClick = {
                            val cleaned = phoneNumber.trim()
                            if (cleaned.isNotEmpty()) onPhoneConfirmed(cleaned) else showError = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Text("Envoyer", color = Color.White)
                    }
                }
            }
        }
    }
}

private fun ensureLocationOverlayIsAtBottom(mapView: MapView) {
    mapView.overlays.find { it.javaClass.simpleName.contains("Location") || it.toString().contains("location", ignoreCase = true) }
        ?.let { mapView.overlays.remove(it); mapView.overlays.add(0, it) }
}


