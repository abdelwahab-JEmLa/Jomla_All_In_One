package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import P0_MainScreen.Main.Main.Settings.Windows.WorkCompletionAlertDialog
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.A_MessageurMainScreen
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.TariffsButtonsSec7ID2
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App._0.Navigation.Screen
import Views.Common.Components.ToastData
import Views.Common.Components.ToastType
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
import Z_CodePartageEntreApps.Proto.Par.Type.Models.D_TarificationInfos
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
    onPourFermeWindows: (D_TarificationInfos) -> Unit = {},
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
                    && viewModel.central.focusedVarsHandlerFacade.getter.focusedM1ProduitInfosAuPrixDifineur != null)
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
                    .getSemanticsTag("cLenceDepuitFragmentsSepecialicteDeVents",cLenceDepuitFragmentsSepecialicteDeVents)
                ,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                if (showButtons) {
                    if (!itsFragmentProduitFastSearchDialog) {
                        B1CataloguesAffiche(
                            appComptComposeRepositoryProtoJuin17 = appComptComposeRepositoryProtoJuin17,
                            showLabels = showLabels,
                        ) {
                            showCatalogueDialog = true
                        }

                        ID2MesasgerieTelegramme(showMessageurDialog, showLabels)

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
                        onPourFermeWindows(D_TarificationInfos())
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
