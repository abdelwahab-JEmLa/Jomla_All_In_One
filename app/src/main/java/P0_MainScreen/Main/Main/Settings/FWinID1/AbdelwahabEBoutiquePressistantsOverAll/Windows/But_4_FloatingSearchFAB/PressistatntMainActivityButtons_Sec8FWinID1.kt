package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option.Z.ClientSearchItem.View.ID4ClientSearchButton
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.B1CataloguesAffiche
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.BlinkingWarningCard
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.Enhanced_Affiche_MotivationAu_Vendeur_De_Plus_De_Benifices.Enhanced_Affiche_MotivationAu_Vendeur_De_Plus_De_Benifices
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.FloatingBonVentToggleFAB
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.FloatingImageDisplay
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.FloatingSecureClickToggleFAB
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.FloatingUpdateAllChecksFAB
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.Button_ID2_Menagerie_Telegram
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.CatalogueSelectionDialog
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.ID3RecordingButton
import P0_MainScreen.Main.Main.Settings.Windows.WorkCompletionAlertDialog
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.A_MessageurTelegram_MainScreen
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.TariffsButtonsSec7ID2
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastData
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastType
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifFalse
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App._0.Navigation.Screen
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
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
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@Composable
fun PressistatntMainActivityButtons_Sec8FWinID1(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    focusedVarsHandlerFacade: FocusedActiveValuesFacade = aCentralFacade.focusedActiveValuesFacade,
    its_Affiche_InfoProduit_Dialog: Boolean = false,
    viewModel: ViewModelPresistantButtonsSec8FWinID1 = koinViewModel(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    viewModelHeadViewModel: HeadViewModel = koinViewModel(),
    recordingViewModel: RecordingViewModel = koinViewModel(),
    onClickAnulationButton: () -> Unit = {},
    onPourFermeWindows: (M13TarificationInfos) -> Unit = {},
) {
    var searchTextForFastPanier by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val appComptComposeRepositoryProtoJuin17 = viewModel.appComptComposeRepositoryProtoJuin17
    val showButtons by remember { mutableStateOf(true) }
    val showLabels by remember { mutableStateOf(true) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var showCatalogueDialog by remember { mutableStateOf(false) }

    var offsetX by remember {
        mutableFloatStateOf(focusedValuesGetter.active_Central_Values.startIntOffset_PresistantFABs.x.toFloat())
    }
    var offsetY by remember {
        mutableFloatStateOf(focusedValuesGetter.active_Central_Values.startIntOffset_PresistantFABs.y.toFloat())
    }

    val isRecording by recordingViewModel.isRecording.collectAsState()
    val displayTime by recordingViewModel.displayTime.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val remainingClients = recordingViewModel.getter.nombreClientsOuLeurDernierEtateCible
    val currentAppCompt = appComptComposeRepositoryProtoJuin17.currentAppCompt
    val isDataLoading = currentAppCompt == null
    val currentSelectedCatalogueId =
        currentAppCompt?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId ?: ""
    var currentToast by remember { mutableStateOf<ToastData?>(null) }

    val fragmentNavigationHandler = viewModel.aCentralFacade.modulesCentral.fragmentNavigationHandler
    val activeFragment by fragmentNavigationHandler.currentFragment.collectAsState()
    val itsFragmentProduitFastSearchDialog = activeFragment == Screen.FragmentProduitFastSearchDialog

    val currentM9AppCompt = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActive_M9AppCompt
    val travailleChezGrossisst3Ali = currentM9AppCompt?.travailleChezGrossisst3Ali

    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val floatingImage = activeCentralValues.image_Flotant

    val shouldShowFloatingSearcher = activeCentralValues.affiche_Dialog_Fast_Affiche_Panie &&
            itsFragmentProduitFastSearchDialog



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
        modifier = Modifier.semantics(mergeDescendants = true) {
            set(value = "fds", key = SemanticsPropertyKey("dsq"))
        },
        viewModel = viewModel,
        showDialog = showAlertDialog,
        onDismiss = { showAlertDialog = false },
        onConfirm = {
            recordingViewModel.recordingHandler.stopRecording()
        },
        nombreClientAvecCibleCommeLastBonAchat = remainingClients
    )

    val m17Message_avec_BonVen = focusedValuesGetter.active_Central_Values.active_OpnerDialog_M17MessageVocale

    if (m17Message_avec_BonVen != null) {
        A_MessageurTelegram_MainScreen(
            onDismiss = {
                focusedValuesGetter.update_activeCentralValues(
                    focusedValuesGetter.active_Central_Values.copy(
                        active_OpnerDialog_M17MessageVocale = null
                    )
                )
            }
        )
    }

    CatalogueSelectionDialog(
        showDialog = showCatalogueDialog,
        currentSelectedCatalogueId = currentSelectedCatalogueId,
        onDismiss = { showCatalogueDialog = false },
        onCatalogueSelected = { catalogueId ->
            val currentBonVent = focusedValuesGetter.activeOnVent_M8BonVent

            currentAppCompt?.let { appCompt ->
                if (currentBonVent != null) {
                    val updatedBonVent = when (catalogueId) {
                        "t1" -> currentBonVent.copy(
                            pourcentage_AffichageDuCatalogue_Conficerie = 100.0,
                        )
                        "t2" -> currentBonVent.copy(
                            pourcentage_AffichageDuCatalogue_Cosmitiques = 100.0,
                        )
                        "t3" -> currentBonVent.copy(
                            pourcentage_AffichageDuCatalogue_tebnage = 100.0
                        )
                        else -> currentBonVent
                    }
                    repositorysMainSetter.repo8BonVent.upsert(updatedBonVent)
                }

                val currentActiveCentralValues = focusedValuesGetter.active_Central_Values
                val updatedActiveCentralValues = when (catalogueId) {
                    "t1" -> currentActiveCentralValues.copy(
                        pourcentage_AffichageDuCatalogue_Conficerie = 100.0,
                    )
                    "t2" -> currentActiveCentralValues.copy(
                        pourcentage_AffichageDuCatalogue_Cosmitiques = 100.0,
                    )
                    "t3" -> currentActiveCentralValues.copy(
                        pourcentage_AffichageDuCatalogue_tebnage = 100.0,
                    )
                    else -> currentActiveCentralValues.copy()
                }

                focusedValuesGetter.update_activeCentralValues(updatedActiveCentralValues)

                val updatedAppCompt = appCompt.copy(
                    presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId = catalogueId
                )
                appComptComposeRepositoryProtoJuin17.upsert(updatedAppCompt)

                viewModelHeadViewModel.sendOrderToClientDisplayer(
                    WifiUpdateClientDisplayerStats.FilterProduitsParCatalogueBsonID_ET_Autres_Types.prefix,
                    catalogueId
                )
            }
        }
    )

    val activeDialogSearchM1Produit = focusedValuesGetter.currentActive_M9AppCompt?.activeDialogSearchM1Produit

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (floatingImage != null && floatingImage.exists()) {
            FloatingImageDisplay(
                imageFile = floatingImage,
                onDismiss = {
                    focusedValuesGetter.update_activeCentralValues(
                        activeCentralValues.copy(image_Flotant = null)
                    )
                }
            )
        }

        val cLenceDepuitFragmentsSepecialicteDeVents =
            (its_Affiche_InfoProduit_Dialog
                    || itsFragmentProduitFastSearchDialog
                    && viewModel.aCentralFacade
                .focusedActiveValuesFacade.focusedValuesGetter.focused_M1ProduitInfos_Pour_PrixDifineur != null)

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
                        nomVal = "cLenceDepuitFragmentsSepecialicteDeVents",
                        data = cLenceDepuitFragmentsSepecialicteDeVents
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showButtons) {
                    focusedValuesGetter.currentActive_M9AppCompt?.text_Message_Warning?.let { warningMessage ->
                        if (warningMessage.isNotBlank()) {
                            BlinkingWarningCard(warningMessage)
                        }
                    }

                    if (focusedValuesGetter.activeOnVent_M8BonVent == null || focusedValuesGetter.currentApp_Est_Admin) {
                        ID3RecordingButton(
                            viewModel,
                            isRecording,
                            showLabels,
                            displayTime
                        ) {
                            showAlertDialog = true
                        }
                    } else {
                        if (travailleChezGrossisst3Ali == false && !cLenceDepuitFragmentsSepecialicteDeVents) {
                            Enhanced_Affiche_MotivationAu_Vendeur_De_Plus_De_Benifices(
                                aCentralFacade = aCentralFacade,
                                focusedValuesGetter = focusedValuesGetter
                            )
                        }
                    }

                    (activeDialogSearchM1Produit == false && !itsFragmentProduitFastSearchDialog).ifTrue {
                        B1CataloguesAffiche(
                            appComptComposeRepositoryProtoJuin17 = appComptComposeRepositoryProtoJuin17,
                            showLabels = showLabels,
                        ) {
                            showCatalogueDialog = true
                        }
                    }

                    if (!itsFragmentProduitFastSearchDialog) {
                        Button_ID2_Menagerie_Telegram(
                            showLabels = showLabels,
                        )
                    }

                    (focusedValuesGetter.currentApp_Est_Admin).ifTrue {
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
                }

                itsFragmentProduitFastSearchDialog.ifTrue {

                    FloatingBonVentToggleFAB(
                        showLabels = showLabels
                    )

                    if (shouldShowFloatingSearcher) {
                        FloatingSecureClickToggleFAB (
                            showLabels = showLabels
                        )

                        But_4_FloatingSearchFAB(
                            searchText = searchTextForFastPanier,
                            onSearchTextChange = { newText ->
                                searchTextForFastPanier = newText
                                focusedValuesGetter.update_activeCentralValues(
                                    activeCentralValues.copy(
                                        outlined_filter_searcher_floating_abouve_all = newText
                                    )
                                )
                            },
                            showLabels = showLabels
                        )

                        FloatingFilterToggleFAB(
                            activeFilters = activeCentralValues.activeFilters,
                            onToggleFilter = {
                                val currentFilters = activeCentralValues.activeFilters
                                val newFilters = when {
                                    currentFilters.contains(ActiveCentralValues.ActiveFilter.premier_Check_Donne) -> {
                                        (currentFilters - ActiveCentralValues.ActiveFilter.premier_Check_Donne) +
                                                ActiveCentralValues.ActiveFilter.non_premier_Check_Donne
                                    }
                                    currentFilters.contains(ActiveCentralValues.ActiveFilter.non_premier_Check_Donne) -> {
                                        currentFilters - ActiveCentralValues.ActiveFilter.non_premier_Check_Donne
                                    }
                                    else -> {
                                        currentFilters + ActiveCentralValues.ActiveFilter.premier_Check_Donne
                                    }
                                }

                                focusedValuesGetter.update_activeCentralValues(
                                    activeCentralValues.copy(activeFilters = newFilters)
                                )
                            },
                            showLabels = showLabels
                        )

                        FloatingUpdateAllChecksFAB (
                            showLabels = showLabels
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            modifier = Modifier
                                .getSemanticsTag(focusedValuesGetter.currentActive_M9AppCompt, "")
                                .size(40.dp),
                            onClick = {
                                val currentState =
                                    focusedValuesGetter.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie
                                viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.update_activeCentralValues(
                                    focusedValuesGetter.active_Central_Values.copy(
                                        affiche_Dialog_Fast_Affiche_Panie = !currentState
                                    )
                                )
                                focusedVarsHandlerFacade.focusedValuesSetter.clear_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID()
                            },
                            containerColor = if (focusedValuesGetter.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Paid,
                                contentDescription = if (focusedValuesGetter.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie) {
                                    "Fermer Dialog Fast Affiche Panier"
                                } else {
                                    "Ouvrir Dialog Fast Affiche Panier"
                                },
                                tint = Color.White
                            )
                        }

                        if (showLabels) {
                            Text(
                                text = if (focusedValuesGetter.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie) {
                                    "Fermer Panier"
                                } else {
                                    "Ouvrir Panier"
                                },
                                modifier = Modifier
                                    .background(
                                        if (focusedValuesGetter.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie) {
                                            MaterialTheme.colorScheme.secondary
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        }
                                    )
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }
                }

                (activeDialogSearchM1Produit == true || itsFragmentProduitFastSearchDialog).ifFalse {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            modifier = Modifier
                                .getSemanticsTag(focusedValuesGetter.currentActive_M9AppCompt, "")
                                .size(40.dp),
                            onClick = {
                                viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
                                    .active_CurrentApp_dialogAboveAll_OutlinedSearchListProduits(true)
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Rechercher Produit",
                                tint = Color.White
                            )
                        }

                        if (showLabels) {
                            Text(
                                text = "Rechercher Produit",
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }
                }

                TariffsButtonsSec7ID2(
                    showLabels = showLabels,
                    fermeDialog = {
                        onPourFermeWindows(M13TarificationInfos())
                        val message = "ترافي"
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
                    its_ProduitVentsInfosDialog = cLenceDepuitFragmentsSepecialicteDeVents
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
fun FloatingFilterToggleFAB(
    activeFilters: Set<ActiveCentralValues.ActiveFilter>,
    onToggleFilter: () -> Unit,
    showLabels: Boolean,
    modifier: Modifier = Modifier
) {
    val currentFilterState = when {
        activeFilters.contains(ActiveCentralValues.ActiveFilter.premier_Check_Donne) ->
            FilterState.PREMIER_CHECK
        activeFilters.contains(ActiveCentralValues.ActiveFilter.non_premier_Check_Donne) ->
            FilterState.NON_PREMIER_CHECK
        else -> FilterState.AUCUN
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        FloatingActionButton(
            modifier = Modifier.size(40.dp),
            onClick = onToggleFilter,
            containerColor = when (currentFilterState) {
                FilterState.PREMIER_CHECK -> Color(0xFF2196F3)
                FilterState.NON_PREMIER_CHECK -> Color(0xFF9C27B0)
                FilterState.AUCUN -> MaterialTheme.colorScheme.surfaceVariant
            },
        ) {
            Icon(
                imageVector = when (currentFilterState) {
                    FilterState.PREMIER_CHECK -> Icons.Default.Check
                    FilterState.NON_PREMIER_CHECK -> Icons.Default.Close
                    FilterState.AUCUN -> Icons.Default.FilterList
                },
                contentDescription = "Toggle Filter",
                tint = when (currentFilterState) {
                    FilterState.PREMIER_CHECK, FilterState.NON_PREMIER_CHECK -> Color.White
                    FilterState.AUCUN -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        if (showLabels) {
            Text(
                text = when (currentFilterState) {
                    FilterState.PREMIER_CHECK -> "Premier Check Donné"
                    FilterState.NON_PREMIER_CHECK -> "Non Premier Check Donné"
                    FilterState.AUCUN -> "Aucun Filtre"
                },
                modifier = Modifier
                    .background(
                        when (currentFilterState) {
                            FilterState.PREMIER_CHECK -> Color(0xFF2196F3)
                            FilterState.NON_PREMIER_CHECK -> Color(0xFF9C27B0)
                            FilterState.AUCUN -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = when (currentFilterState) {
                    FilterState.PREMIER_CHECK, FilterState.NON_PREMIER_CHECK -> Color.White
                    FilterState.AUCUN -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private enum class FilterState {
    AUCUN,
    PREMIER_CHECK,
    NON_PREMIER_CHECK
}
