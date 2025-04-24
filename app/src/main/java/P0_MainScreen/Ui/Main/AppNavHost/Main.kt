package P0_MainScreen.Ui.Main.AppNavHost

import P0_MainScreen.Ui.Objects.LoadingOverlay
import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.A_APP1FragID3_MainScreen
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.A_MapClients_A2FragID_1
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.A_MainScreen_APP2_ID_2
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.A_APP3FragID1_MainScreen
import V.DiviseParSections.App.D.FraitProjet.App.FragID2.VentsHistoriquesDisplayer.Fragment.Ui.PeriodeVenteScreen
import Views.FragId3_DialogVendeurAfficheurInfosProduit.A_VendeurAfficheurInfosProduit_FragmentMainId3
import Views.P1._ArticlesStartFacade.FragmentStartupScreen
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Modules.WifiUpdateClientDisplayerStats
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

fun NavGraphBuilder.app2(
    viewModelInitApp: ViewModelInitApp,
    clientEnCourDeVent: Long,
    navController: NavHostController,
    onClear: () -> Unit,
    orderStateManager: OrderStateManager,
    mapReloadTrigger: Int = 0, // Add this parameter
) {
    composable(Screen.A_ClientsLocationGps.route) {
        LaunchedEffect(Unit) {
            if (!orderStateManager.hasCheckedOrderStatus()) {
                delay(300)
                orderStateManager.checkOrderStatus()
            }
        }
        A_MapClients_A2FragID_1(
            viewModelInitApp = viewModelInitApp,
            clientEnCourDeVent = clientEnCourDeVent,
            onUpdateLongAppSetting = {
                allerAuFragment(navController)
            },
            onClear = onClear,
            mapReloadTrigger = mapReloadTrigger
           )
    }
}

private fun allerAuFragment(navController: NavHostController) {
    navController.navigate(Screen.EditDatabaseWithCreateNewArticles.route) {
        // Pop the current fragment off the back stack
        popUpTo(Screen.A_ClientsLocationGps.route) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

object ScreensApp2 {
    val A_ClientsLocationGps = Screen.A_ClientsLocationGps
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    onToggleNavBar: () -> Unit,
    modifier: Modifier = Modifier,
    isFabVisible: Boolean,
    onClickDonne: () -> Unit,
    onClickToDisplayeConexionWifi: () -> Unit,
    onToggleLockHost: () -> Unit,
    viewModelInitApp: ViewModelInitApp,
    headViewModel: HeadViewModel,
    targetCategoryId: MutableState<Long?> = mutableStateOf(null),
    lockHost: Boolean,
    onClickImageToShowControles: () -> Unit
) {
    val repo_01_VentsHistoriquesDataBase_Repository = viewModelInitApp
        .repo_01_VentsHistoriquesDataBase_Repository

    val uiState by headViewModel.uiState.collectAsState()
    // Get current client from settings
    val currentClientId = uiState.appSettingsSaverModel
        .find { it.name == "clientBuyerNowId" }?.valueLong ?: 0
    val currentClient = viewModelInitApp.clientDataBaseSnapList.find { it.id == currentClientId }

    // Repository model for accessing data structures
    val repo00HeadofrepositorysRepository = viewModelInitApp.repo_0_0_HeadOfRepositorys_Repository
    val repositorysModel = repo00HeadofrepositorysRepository.repositorys_Model

    // Existing state management
    var opnerSaleWindows by rememberSaveable { mutableStateOf(false) }
    var showClientSelection by rememberSaveable { mutableStateOf(false) }
    var showClientSelectionWithoutCondition by rememberSaveable { mutableStateOf(false) }
    var relatedArticleBaseStats by rememberSaveable { mutableStateOf<ArticlesBasesStatsTable?>(null) }
    var pendingIndexColor by rememberSaveable { mutableIntStateOf(0) }
    val reloadTrigger by rememberSaveable { mutableIntStateOf(0) }
    var scrollTiger by rememberSaveable { mutableIntStateOf(0) }
    var lockExpandedPrices by rememberSaveable { mutableStateOf(false) }
    var showOrderCompletionDialog by rememberSaveable { mutableStateOf(false) }

    val clientEnCourDeVent by rememberSaveable {
        mutableLongStateOf(
            headViewModel._uiState.value
                .appSettingsSaverModel.find { it.name == "clientBuyerNowId" }
                ?.valueLong ?: 0)
    }

    // Inside AppNavHost
    val mapReloadTrigger = remember { mutableIntStateOf(0) }

    val orderStateManager = remember {
        OrderStateManager(
            repo_01_VentsHistoriquesDataBase_Repository = repo_01_VentsHistoriquesDataBase_Repository,
            repositorysModel = repositorysModel,
            currentClientId = {
                headViewModel._uiState.value.appSettingsSaverModel
                    .find { it.name == "clientBuyerNowId" }?.valueLong ?: 0
            },
            onShowDialog = { showOrderCompletionDialog = true },
            onReloadMap = { mapReloadTrigger.intValue++ } // This will trigger a recomposition
        )
    }

    // Track current route to handle route changes
    var currentRoute by remember { mutableStateOf("") }
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            val previousRoute = currentRoute
            currentRoute = backStackEntry.destination.route ?: ""

            // Reset order status check when navigating away from A_ClientsLocationGps
            if (previousRoute == Screen.A_ClientsLocationGps.route &&
                currentRoute != Screen.A_ClientsLocationGps.route) {
                orderStateManager.resetOrderStatusCheck()
            }
        }
    }

    // Calculate the bottom padding to ensure content doesn't get hidden by the navigation bar
    val bottomNavHeight = 80.dp
    val bottomPadding = 8.dp // Additional padding for the navigation bar

    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = bottomNavHeight + bottomPadding) // Add padding to prevent content from being hidden
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Screen.EditDatabaseWithCreateNewArticles.route,
                modifier = Modifier.fillMaxSize()
            ) {
                app2(
                    viewModelInitApp = viewModelInitApp,
                    clientEnCourDeVent = clientEnCourDeVent,
                    navController = navController,
                    onClear = {
                        headViewModel.viewModelScope.launch {
                            headViewModel._uiState.update { currentState ->
                                currentState.copy(soldArticlesModel = emptyList())
                            }

                            // Clear the database in a coroutine
                            headViewModel.database.soldArticlesModelDao().deleteAll()

                            // Clear Firebase references
                            val database = Firebase.database
                            database.getReference("K_GroupeurBonCommendToSupplierRef").removeValue()
                            database.getReference("O_SoldArticlesTabelle").removeValue()
                        }
                    },
                    orderStateManager = orderStateManager,
                    mapReloadTrigger = mapReloadTrigger.intValue // Pass the trigger value
                )

                composable(Screen.EditDatabaseWithCreateNewArticles.route) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        FragmentStartupScreen(
                            viewModel = headViewModel,
                            onToggleNavBar = onToggleNavBar,
                            reloadTrigger = reloadTrigger,
                            onClickToOpenWindos = { articleDataBaseOn, indexColor ->
                                relatedArticleBaseStats = articleDataBaseOn
                                pendingIndexColor = indexColor

                                if (currentClientId == 0L) {
                                    showClientSelection = true
                                } else {
                                    headViewModel.openWindowsNewSaleWithUpdateCurrent(
                                        relatedArticleBaseStats!!.idArticle.toLong(),
                                        currentClientId,
                                        pendingIndexColor
                                    )
                                    opnerSaleWindows = true
                                    headViewModel.sendOrderToClientDisplayer(
                                        WifiUpdateClientDisplayerStats.ClientWindowsDisplayedProductId.prefix,
                                        relatedArticleBaseStats!!.idArticle.toLong()
                                    )
                                }
                            },
                            onClickToOpenClientsW = {
                                showClientSelectionWithoutCondition = true
                            },
                            isFabVisible = isFabVisible,
                            onClickDonne = onClickDonne,
                            onClickToDisplayeConexionWifi = onClickToDisplayeConexionWifi,
                            scrollTiger = scrollTiger,
                            onToggleLockHost = onToggleLockHost,
                            onToggleLockExpandedPricex = {
                                lockExpandedPrices = !lockExpandedPrices
                            },
                            currentClient = currentClient,
                            viewModelInitApp = viewModelInitApp,
                            targetCategoryId = targetCategoryId,
                            lockHost = lockHost,
                            onClickImageToShowControles = onClickImageToShowControles
                        )

                        if (uiState.isLoading) {
                            LoadingOverlay(
                                progress = uiState.loadingProgress / 100f,
                                modifier = Modifier.matchParentSize()
                            )
                        }
                    }
                }

                composable(Screen.SoldCart.route) {
                    LaunchedEffect(Unit) {
                        scrollTiger++
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        A_MainScreen_APP2_ID_2(
                            onConfirmOrder = {
                                // Update state to ACHAT_TERMINE when order is confirmed
                                if (currentClientId > 0) {
                                    updateAcheteurToAchatTermine(
                                        viewModelInitApp,
                                        currentClientId,
                                        repositorysModel,
                                    )
                                }
                                headViewModel.updateLongAppSetting("clientBuyerNowId", 0)
                            }
                        )
                    }
                }

                composable(Screen.TravailleTempRecorder.route) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        A_APP3FragID1_MainScreen()
                    }
                }

                composable(Screen.CommandeProduits.route) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        A_APP1FragID3_MainScreen()
                    }
                }

                composable(Screen.NewFragTest.route) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        PeriodeVenteScreen()
                    }
                }
            }


            if (showOrderCompletionDialog) {
                OrderCompletionDialog(
                    clientId = currentClientId,
                    clientName = currentClient?.nom ?: "Unknown",
                    onDismiss = {
                        showOrderCompletionDialog = false
                    },
                    onStateChange = { selectedState ->
                        updateAcheteurToAchatTermine(
                            viewModelInitApp = viewModelInitApp,
                            clientId = currentClientId,
                            repositorysModel = repositorysModel,
                            newState = selectedState
                        )
                        // Trigger map reload
                        orderStateManager.onReloadMap()
                    }
                )
            }

            if (showClientSelectionWithoutCondition || (showClientSelection && currentClientId == 0L)) {
                // Navigate to client map selection screen when no client is selected
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.A_ClientsLocationGps.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    // Reset dialog states after navigation
                    showClientSelection = false
                    showClientSelectionWithoutCondition = false
                }
            }

            if (opnerSaleWindows) {
                A_VendeurAfficheurInfosProduit_FragmentMainId3(
                    modifier = Modifier.padding(horizontal = 3.dp),
                    uiState = uiState,
                    viewModel = headViewModel,
                    onDismiss = {
                        headViewModel.clearCurrentSale()
                        opnerSaleWindows = false
                        headViewModel.sendOrderToClientDisplayer(
                            WifiUpdateClientDisplayerStats.DISMISS_PRODUCT_INFO.prefix
                        )
                    },
                    reloadTrigger = reloadTrigger,
                    lockExpandedPrices = lockExpandedPrices,
                    onToggleLockExpandedPricex = { lockExpandedPrices = !lockExpandedPrices },
                    viewModelInitApp = viewModelInitApp,
                    currentClient = currentClient,
                    clickedCouleurIndex = pendingIndexColor,
                )
            }
        }
    }
}

@Composable
fun MainApp(
    headViewModel: HeadViewModel,
    viewModelInitApp: ViewModelInitApp
) {
    val navController = rememberNavController()
    val isFabVisible = remember { mutableStateOf(true) }
    val lockHost = remember { mutableStateOf(false) }

    AppNavHost(
        navController = navController,
        onToggleNavBar = { /* Implementation for toggling navbar */ },
        isFabVisible = isFabVisible.value,
        onClickDonne = { /* Implementation for done click */ },
        onClickToDisplayeConexionWifi = { /* Implementation for WiFi connection display */ },
        onToggleLockHost = { lockHost.value = !lockHost.value },
        viewModelInitApp = viewModelInitApp,
        headViewModel = headViewModel,
        lockHost = lockHost.value,
        onClickImageToShowControles = { /* Implementation for showing controls */ }
    )
}
