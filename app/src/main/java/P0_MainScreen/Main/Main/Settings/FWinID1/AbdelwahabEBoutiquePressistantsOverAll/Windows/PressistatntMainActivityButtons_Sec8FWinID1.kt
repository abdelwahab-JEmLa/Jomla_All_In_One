package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import P0_MainScreen.Main.Main.Settings.Windows.WorkCompletionAlertDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandlerP2
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.A_MessageurMainScreen
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.TariffsButtonsSec7ID2
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App._0.Navigation.Screen
import Views.Common.Components.ToastData
import Views.Common.Components.ToastType
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun PressistatntMainActivityButtons_Sec8FWinID1(
    viewModel: ViewModelPresistantButtonsSec8FWinID1 = koinViewModel(),
    recordingViewModel: RecordingViewModel = koinViewModel(),
    cLenceDepuitFragmentsSepecialisteDeVents: Boolean = false,
    onPourFermeWindows: (M13TarificationInfos) -> Unit = {},
    idProduitActuelle: Long = 0,
    onClickAnulationButton: () -> Unit = {},
    viewModelHeadViewModel: HeadViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val appComptComposeRepositoryProtoJuin17 = viewModel.appComptComposeRepositoryProtoJuin17
    val showButtons by remember { mutableStateOf(true) }
    val showLabels by remember { mutableStateOf(true) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var showMessageurDialog by remember { mutableStateOf(false) }
    var showCatalogueDialog by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val isRecording by recordingViewModel.isRecording.collectAsState()
    val displayTime by recordingViewModel.displayTime.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val remainingClients =
        recordingViewModel.getter.nombreClientsOuLeurDernierEtateCible
    val currentAppCompt = appComptComposeRepositoryProtoJuin17.currentAppCompt
    val isDataLoading = currentAppCompt == null
    val currentSelectedCatalogueId =
        currentAppCompt?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId ?: ""
    var currentToast by remember { mutableStateOf<ToastData?>(null) }

    val fragmentNavigationHandler = viewModel.central.modulesCentral.fragmentNavigationHandler
    val activeFragment by fragmentNavigationHandler.currentFragment.collectAsState()

    // Check if current fragment is FragmentProduitFastSearchDialog
    val itsFragmentProduitFastSearchDialog =
        activeFragment == Screen.FragmentProduitFastSearchDialog

    val currentM9AppCompt =
        viewModel.central.focusedVarsHandlerFacade.get.currentM9AppCompt
    val travailleChezGrossisst3Ali = currentM9AppCompt?.travailleChezGrossisst3Ali

    DisposableEffect(isRecording) {
        var job: Job? = null
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        if (isRecording) {
            job = coroutineScope.launch {
                while (true) {
                    recordingViewModel.updateElapsedTime()
                    delay(1000)
                }
            }
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    recordingViewModel.onLifecycleResume()
                    if (isRecording && job == null) {
                        job = coroutineScope.launch {
                            while (true) {
                                recordingViewModel.updateElapsedTime()
                                delay(1000)
                            }
                        }
                    }
                }

                Lifecycle.Event.ON_PAUSE -> {
                    if (isRecording) {
                        recordingViewModel.onRecordingStopped()
                    }
                    job?.cancel()
                    job = null
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            job?.cancel()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (isDataLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Chargement des données...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        return
    }

    WorkCompletionAlertDialog(
        showDialog = showAlertDialog,
        onDismiss = { showAlertDialog = false },
        onConfirm = {
            recordingViewModel.recordingHandler.stopRecording()
        },
        nombreClientAvecCibleCommeLastBonAchat = remainingClients
    )

    if (showMessageurDialog) {
        A_MessageurMainScreen(
            onDismiss = { showMessageurDialog = false }
        )
    }

    CatalogueSelectionDialog(
        showDialog = showCatalogueDialog,
        currentSelectedCatalogueId = currentSelectedCatalogueId,
        onDismiss = { showCatalogueDialog = false },
        onCatalogueSelected = { catalogueId ->
            currentAppCompt?.let { appCompt ->
                val updatedAppCompt = appCompt.copy(
                    presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId = catalogueId
                )
                appComptComposeRepositoryProtoJuin17.upsert(updatedAppCompt)

                viewModel.sendOrderAuPresentoireDevice(catalogueId)

                viewModelHeadViewModel.sendOrderToClientDisplayer(
                    WifiUpdateClientDisplayerStats.FilterProduitsParCatalogueBsonID.prefix,
                    catalogueId
                )
            }
        }
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val cLenceDepuitFragmentsSepecialicteDeVents =
            (cLenceDepuitFragmentsSepecialisteDeVents
                    || itsFragmentProduitFastSearchDialog
                    && viewModel.central.focusedVarsHandlerFacade.get.focused_M1ProduitInfos_Pour_PrixDifineur != null)
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
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .getSemanticsTag(
                        cLenceDepuitFragmentsSepecialicteDeVents,
                        "cLenceDepuitFragmentsSepecialicteDeVents"
                    )
                ,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                if (showButtons) {
                    if (!itsFragmentProduitFastSearchDialog && travailleChezGrossisst3Ali==false) {
                        B1CataloguesAffiche(
                            appComptComposeRepositoryProtoJuin17 = appComptComposeRepositoryProtoJuin17,
                            showLabels = showLabels,
                        ) {
                            showCatalogueDialog = true
                        }

                        ID2MesasgerieTelegramme(
                            showMessageurDialog = showMessageurDialog,
                            showLabels = showLabels,
                            onTelegramClick = { showMessageurDialog = true }
                        )

                        if (!cLenceDepuitFragmentsSepecialisteDeVents) {
                            ID3RecordingButton(
                                isRecording,
                                showLabels,
                                displayTime,
                                remainingClients
                            ) {
                                showAlertDialog = true
                            }
                        }

                    }


                    if(activeFragment == Screen.Screen1PanieVentsFinale) {
                        GroupePanierButtons()
                    }

                    ID4ClientSearchButton(
                        uiState = uiState,
                        hClientRepository = uiState.hClientRepository,
                        showLabels = showLabels,
                        onClientSelectedToToast = { selectedClient ->
                            currentToast = ToastData(
                                message = "Client sélectionné: ${selectedClient.nom}",
                                type = ToastType.SUCCESS,
                                duration = 2000L
                            )
                        },
                        viewModel = viewModel
                    )

                }

                TariffsButtonsSec7ID2(
                    showLabels = showLabels,
                    fermeDialog = {
                        onPourFermeWindows(M13TarificationInfos())
                        val message = "تراضي"

                        currentToast = ToastData(
                            message = message,
                            type = ToastType.SUCCESS,
                            duration = 1500L
                        )
                    },
                    onFermDialogeAvecAnllation = {
                        onClickAnulationButton()
                        currentToast = ToastData(
                            message = "تم الإلغاء",
                            type = ToastType.INFO,
                            duration = 1500L
                        )
                    },
                    cLenceDepuitFragmentsSepecialicteDeVents = cLenceDepuitFragmentsSepecialicteDeVents
                )
            }
        }

        currentToast?.let { toast ->
            LaunchedEffect(toast) {
                delay(toast.duration)
                currentToast = null
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .background(
                        color = when (toast.type) {
                            ToastType.SUCCESS -> Color.Green
                            ToastType.INFO -> Color.Blue
                            else -> Color.Gray
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = toast.message,
                    color = Color.White
                )
            }
        }
    }
}
@Composable
fun GroupePanierButtons(
    viewModel: ZViewModel_Sec1Frag3 = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isMinimized = uiState.isMinimized
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val printHandler = remember { PrintReceiptHandlerP2() }
    val currentBonVent = viewModel.aCentral.focusedVarsHandlerFacade.get.onVentM8BonVent
    val fVentCouleurOperationRepository = viewModel.uiStateCentralRepositorys.repo10OperationVentCouleur

    fun updateBonVent(data: M8BonVent, newEtate: M8BonVent.EtateActuellementEst) =
        viewModel.aCentral.mainRepositorysSetterFacade.updateM8BonVent(
            data.copy(etateActuellementEst = newEtate)
        )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mode Toggle Button (Delivery/Vent)
        FloatingActionButton(
            onClick = {
                viewModel.togglePanieMode()
            },
            containerColor = when (uiState.panieMode) {
                ZViewModel_Sec1Frag3.PanieMode.Delivery -> Color(0xFF4CAF50) // Green for Delivery
                ZViewModel_Sec1Frag3.PanieMode.Vent -> Color(0xFF2196F3) // Blue for Vent
            },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = when (uiState.panieMode) {
                    ZViewModel_Sec1Frag3.PanieMode.Delivery -> Icons.Default.LocalShipping
                    ZViewModel_Sec1Frag3.PanieMode.Vent -> Icons.Default.Storefront
                },
                contentDescription = "Mode: ${uiState.panieMode.name}",
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
        }

        // Filter Button
        FloatingActionButton(
            onClick = {
                viewModel.toggelePanierFilterNonTrouve()
            },
            containerColor = if (uiState.filterNonTrouve) {
                Color(0xFFFF5722) // Orange when filter is active
            } else {
                MaterialTheme.colorScheme.tertiary
            },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = if (uiState.filterNonTrouve)
                    "Désactiver filtre"
                else
                    "Activer filtre",
                modifier = Modifier.size(18.dp),
                tint = if (uiState.filterNonTrouve) Color.White else MaterialTheme.colorScheme.onTertiary
            )
        }

        // Print Button
        FloatingActionButton(
            onClick = {
                val fClientRepository = viewModel.uiStateCentralRepositorys.iD2ClientRepository
                printHandler.printVentReceipt(
                    context = context,
                    fVentCouleurOperationRepository = fVentCouleurOperationRepository,
                    bProduitInfosRepository = viewModel.uiStateCentralRepositorys.repoM1ProduitInfos,
                    b1CouleurOuGoutProduitDataBaseRepository = viewModel.uiStateCentralRepositorys.repo3CouleurProduitInfos,
                    client = fClientRepository.onVentId2ClientInfos,
                    scope = scope
                )
            },
            containerColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Print,
                contentDescription = "Imprimer",
                modifier = Modifier.size(18.dp)
            )
        }

        // Order Status Button
        FloatingActionButton(
            onClick = {
                currentBonVent?.let { bonVent ->
                    when (bonVent.etateActuellementEst) {
                        M8BonVent.EtateActuellementEst.CreeMaisNonDefinie -> {
                            updateBonVent(
                                bonVent,
                                M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
                            )
                        }
                        M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME -> {
                            updateBonVent(
                                bonVent,
                                M8BonVent.EtateActuellementEst.CreeMaisNonDefinie
                            )
                            viewModel.aCentral.focusedVarsHandlerFacade.set.desactive_currentApp_M8BonVent()
                        }
                        else -> {
                            updateBonVent(
                                bonVent,
                                M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
                            )
                        }
                    }
                }
            },
            containerColor = when (currentBonVent?.etateActuellementEst) {
                M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME -> Color(0xFF4CAF50) // Green when confirmed
                M8BonVent.EtateActuellementEst.CreeMaisNonDefinie -> Color(0xFF9E9E9E) // Gray when not defined
                else -> Color(0xFFFF9800) // Orange for unknown/other states
            },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = when (currentBonVent?.etateActuellementEst) {
                    M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME -> "Annuler"
                    M8BonVent.EtateActuellementEst.CreeMaisNonDefinie -> "Confirmer"
                    else -> "Gérer"
                },
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
        }

        // Minimize/Maximize Button
        FloatingActionButton(
            onClick = {
                viewModel.toggleMinimizedState()
            },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (isMinimized) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = if (isMinimized) "Afficher" else "Masquer",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun B1CataloguesAffiche(
    appComptComposeRepositoryProtoJuin17: Repo9AppCompt,
    showLabels: Boolean,
    onClickPourAfficheDialog: () -> Unit = {}
) {
    val catalogues = B4CatalogueCategoriesRepository()
    val catalogueId =
        appComptComposeRepositoryProtoJuin17.currentAppCompt?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId
    val buttonAFficheAuCata = catalogues.find { it.key == catalogueId }

    // Get the catalogue name and color, with fallbacks
    val catalogueName = buttonAFficheAuCata?.nom ?: "Catalogues"
    val buttonBackgroundColor = buttonAFficheAuCata?.couleur ?: Color(0xFF9C27B0)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            onClick = {
                onClickPourAfficheDialog()
            },
            modifier = Modifier.size(40.dp),
            containerColor = buttonBackgroundColor,
        ) {
            val iconColor = Color.Black

            Icon(
                imageVector = Icons.Default.Category,
                contentDescription = "Sélectionner Catalogue",
                tint = iconColor
            )
        }

        if (showLabels) {
            Text(
                text = catalogueName, // Now displays the actual catalogue name
                modifier = Modifier
                    .background(buttonBackgroundColor)
                    .padding(4.dp),
                color = Color.White
            )
        }
    }
}
