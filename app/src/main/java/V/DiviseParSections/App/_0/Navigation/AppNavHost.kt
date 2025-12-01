package V.DiviseParSections.App._0.Navigation

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_MapClients_A2FragID_1
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.Z.Main.PanierFinaleDAchatSec1Frag3
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.A_APP3FragID1_MainScreen
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.PresenterElectroBoutiqueAbdelwahab_Sec10Frag1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.MainFastSearchProduitPourVent
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Screen_GrossistAchatSec12FragID1
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.EditeBaseDonneMainScreenIdS9
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import Views.FragId3_DialogVendeurAfficheurInfosProduit.A_VendeurAfficheurInfosProduit_FragmentMainId3
import Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview.A.Main.Main_DataBaseInitFactory_1Produit
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    viewModel: HeadViewModel = koinViewModel(),
    viewModelInitApp: ViewModelInitApp = koinViewModel(),
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    navController: NavHostController,
    onToggleNavBar: () -> Unit,
    isFabVisible: Boolean,
    onClickToDisplayeConexionWifi: () -> Unit,
    onToggleLockHost: () -> Unit,
    targetCategoryId: MutableState<Long?> = mutableStateOf(null),
    lockHost: Boolean,
    onClickImageToShowControles: () -> Unit,
    fragmentNavigationHandler: FragmentNavigationHandler = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val itsDevMode = M18CentralParametresOfAllApps.get_Default().itsDevMode

    val startUpScreen = when {
        !itsDevMode -> Screen.FacadePresentoireProduits
        else -> {
            val devStartUpRoute = M18CentralParametresOfAllApps.get_Default().devStartUpScree
            getScreenFromRoute(devStartUpRoute)
        }
    }

    LaunchedEffect(currentRoute) {
        fragmentNavigationHandler.updateCurrentFragmentByRoute(currentRoute)
    }

    // RepositorysMainSetter startup screen when component initializes
    LaunchedEffect(startUpScreen) {
        if (startUpScreen != null) {
            fragmentNavigationHandler.setStartupScreen(startUpScreen)
        }
    }

    val currentClientId = uiState.appSettingsSaverModel
        .find { it.name == "clientBuyerNowId" }?.valueLong ?: 0
    val currentClient = viewModelInitApp.clientDataBaseSnapList.find { it.id == currentClientId }
    var opnerSaleWindows by rememberSaveable { mutableStateOf(false) }
    var showClientSelection by rememberSaveable { mutableStateOf(false) }
    var showClientSelectionWithoutCondition by rememberSaveable { mutableStateOf(false) }
    var relatedArticleBaseStats by rememberSaveable { mutableStateOf<ArticlesBasesStatsTable?>(null) }
    var pendingIndexColor by rememberSaveable { mutableIntStateOf(0) }
    val reloadTrigger by rememberSaveable { mutableIntStateOf(0) }
    var scrollTiger by rememberSaveable { mutableIntStateOf(0) }
    var lockExpandedPrices by rememberSaveable { mutableStateOf(false) }

    val clientEnCourDeVent by rememberSaveable {
        mutableLongStateOf(
            viewModel._uiState.value
                .appSettingsSaverModel.find { it.name == "clientBuyerNowId" }
                ?.valueLong ?: 0
        )
    }
    val mapReloadTrigger = remember { mutableIntStateOf(0) }
    val bottomNavHeight = 80.dp
    val bottomPadding = 8.dp

    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = bottomNavHeight + bottomPadding)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (startUpScreen != null) {
                NavHost(
                    navController = navController,
                    startDestination = startUpScreen.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(
                        route = Screen.FacadePresentoireProduits.route,
                    ) { backStackEntry ->
                        val screenKey = rememberScreenKey(backStackEntry)
                        Box(modifier = Modifier.fillMaxSize()) {
                            key(screenKey) {
                                PresenterElectroBoutiqueAbdelwahab_Sec10Frag1(
                                    viewModelHeadViewModel = viewModel,
                                    viewModelInitApp = viewModelInitApp,
                                    onToggleNavBar = onToggleNavBar,
                                    reloadTrigger = reloadTrigger,
                                    onClickToOpenWindos = { articleDataBaseOn, indexColor ->
                                        relatedArticleBaseStats = articleDataBaseOn
                                        pendingIndexColor = indexColor

                                        if (currentClientId == 0L) {
                                            showClientSelection = true
                                        } else {
                                            viewModel.openWindowsNewSaleWithUpdateCurrent(
                                                relatedArticleBaseStats!!.id.toLong(),
                                                currentClientId,
                                                pendingIndexColor
                                            )
                                            opnerSaleWindows = true
                                            viewModel.sendOrderToClientDisplayer(
                                                WifiUpdateClientDisplayerStats.ClientWindowsDisplayedProductId.prefix,
                                                relatedArticleBaseStats!!.id.toLong()
                                            )
                                        }
                                    },
                                    onClickToOpenClientsW = {
                                        showClientSelectionWithoutCondition = true
                                    },
                                    isFabVisibleInit = isFabVisible,
                                    onClickToDisplayeConexionWifi = onClickToDisplayeConexionWifi,
                                    onToggleLockHost = onToggleLockHost,
                                    onToggleLockExpandedPricex = {
                                        lockExpandedPrices = !lockExpandedPrices
                                    },
                                    currentClient = currentClient,
                                    targetCategoryId = targetCategoryId,
                                    lockHost = lockHost,
                                    onClickImageToShowControles = onClickImageToShowControles
                                )
                            }

                            if (uiState.isLoading) {
                                LoadingOverlay(
                                    progress = uiState.loadingProgress / 100f,
                                    modifier = Modifier.matchParentSize()
                                )
                            }
                        }
                    }
                    focusedValuesGetter.currentApp_Est_Admin.ifTrue {
                        composable(
                            route = Screen.FragmentProduitFastSearchDialog.route,
                        ) { backStackEntry ->
                            val screenKey = rememberScreenKey(backStackEntry)

                            CleanupEffect {
                            }

                            LaunchedEffect(Unit) {
                                scrollTiger++
                            }

                            Box(modifier = Modifier.fillMaxSize()) {
                                key(screenKey) {
                                    MainFastSearchProduitPourVent()
                                }
                            }
                        }
                    }
                    composable(
                        route = Screen.Screen1PanieVentsFinale.route,
                    ) { backStackEntry ->
                        val screenKey = rememberScreenKey(backStackEntry)

                        CleanupEffect {
                        }

                        LaunchedEffect(Unit) {
                            scrollTiger++
                        }

                        Box(modifier = Modifier.fillMaxSize()) {
                            key(screenKey) {
                                PanierFinaleDAchatSec1Frag3()
                            }
                        }
                    }

                    // Work time recorder screen
                    composable(
                        route = Screen.TravailleTempRecorder.route,
                    ) { backStackEntry ->
                        val screenKey = rememberScreenKey(backStackEntry)

                        CleanupEffect {
                            // Any cleanup needed for this screen
                        }

                        Box(modifier = Modifier.fillMaxSize()) {
                            key(screenKey) {
                                A_APP3FragID1_MainScreen()
                            }
                        }
                    }

                    composable(
                        route = Screen.Achats_Produits_Chez_Grossists.route,
                    ) { backStackEntry ->
                        val screenKey = rememberScreenKey(backStackEntry)

                        CleanupEffect {
                        }

                        Box(modifier = Modifier.fillMaxSize()) {
                            key(screenKey) {
                                Screen_GrossistAchatSec12FragID1()
                            }
                        }
                    }

                    // Product ordering screen
                    composable(
                        route = Screen.EditDatabaseWithCreateNewArticles.route,
                    ) { backStackEntry ->
                        val screenKey = rememberScreenKey(backStackEntry)
                        Box(modifier = Modifier.fillMaxSize()) {
                            key(screenKey) {
                                EditeBaseDonneMainScreenIdS9()
                            }
                        }
                    }

                    composable(
                        route = Screen.Classe_Tahfid_quran.route,
                    ) { backStackEntry ->
                        val screenKey = rememberScreenKey(backStackEntry)
                        Box(modifier = Modifier.fillMaxSize()) {
                            key(screenKey) {
                            }
                        }
                    }

                    // Database Init Factory screen - NEW
                    composable(
                        route = Screen.Main_DataBaseInitFactory_1Produit.route,
                    ) { backStackEntry ->
                        val screenKey = rememberScreenKey(backStackEntry)
                        Box(modifier = Modifier.fillMaxSize()) {
                            key(screenKey) {
                                Main_DataBaseInitFactory_1Produit()
                            }
                        }
                    }

                    // Test fragment screen (empty implementation)
                    composable(
                        route = Screen.NewFragTest.route,
                    ) { backStackEntry ->
                        val screenKey = rememberScreenKey(backStackEntry)
                        Box(modifier = Modifier.fillMaxSize()) {
                            key(screenKey) {
                                //        A_MainScreen_APP2_ID_2PanierFinaleDAchat()
                            }
                        }
                    }

                    app2(
                        viewModelInitApp = viewModelInitApp,
                        clientEnCourDeVent = clientEnCourDeVent,
                        navController = navController,
                        onClear = {
                            viewModel.viewModelScope.launch {
                                viewModel._uiState.update { currentState ->
                                    currentState.copy(soldArticlesModel = emptyList())
                                }

                                // Clear the database in add_New coroutine
                                viewModel.database.soldArticlesModelDao().deleteAll()

                                // Clear Firebase references
                                val database = Firebase.database
                                database.getReference("K_GroupeurBonCommendToSupplierRef").removeValue()
                                database.getReference("O_SoldArticlesTabelle").removeValue()
                            }
                        },
                        mapReloadTrigger = mapReloadTrigger.intValue,
                        fragmentNavigationHandler = fragmentNavigationHandler
                    )
                }
            }

            // Handle client selection navigation logic
            if (showClientSelectionWithoutCondition || (showClientSelection && currentClientId == 0L)) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.A_Clients_LocationGps.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = false // Force recreation of the screen
                    }
                    // Reset dialog states after navigation
                    showClientSelection = false
                    showClientSelectionWithoutCondition = false

                    // Trigger map reload when navigating to client map
                    mapReloadTrigger.intValue++
                }
            }

            if (opnerSaleWindows) {
                A_VendeurAfficheurInfosProduit_FragmentMainId3(
                    uiState = uiState,
                    viewModelHeadViewModel = viewModel,
                    onDismiss = {
                        viewModel.clearCurrentSale()
                        opnerSaleWindows = false
                        viewModel.sendOrderToClientDisplayer(
                            WifiUpdateClientDisplayerStats.DISMISS_PRODUCT_INFO.prefix
                        )
                    },
                    modifier = Modifier.padding(horizontal = 3.dp),
                    lockExpandedPrices = lockExpandedPrices,
                    onToggleLockExpandedPricex = { lockExpandedPrices = !lockExpandedPrices },
                    viewModelInitApp = viewModelInitApp,
                    currentClient = currentClient,
                    clickedCouleurIndex = pendingIndexColor,
                    onFermDialoge = {
                        opnerSaleWindows = false
                    },
                )
            }
        }
    }
}

private fun getScreenFromRoute(route: String?): Screen? {
    return when (route) {
        Screen.FacadePresentoireProduits.route -> Screen.FacadePresentoireProduits
        Screen.FragmentProduitFastSearchDialog.route -> Screen.FragmentProduitFastSearchDialog
        Screen.Screen1PanieVentsFinale.route -> Screen.Screen1PanieVentsFinale
        Screen.TravailleTempRecorder.route -> Screen.TravailleTempRecorder
        Screen.Achats_Produits_Chez_Grossists.route -> Screen.Achats_Produits_Chez_Grossists
        Screen.EditDatabaseWithCreateNewArticles.route -> Screen.EditDatabaseWithCreateNewArticles
        Screen.Main_DataBaseInitFactory_1Produit.route -> Screen.Main_DataBaseInitFactory_1Produit
        Screen.NewFragTest.route -> Screen.NewFragTest
        Screen.A_Clients_LocationGps.route -> Screen.A_Clients_LocationGps
        Screen.DialogTests.route -> Screen.DialogTests
        Screen.ToggleFab.route -> Screen.ToggleFab
        else -> null
    }
}

/**
 * Creates client map navigation routes
 */
fun NavGraphBuilder.app2(
    viewModelInitApp: ViewModelInitApp,
    clientEnCourDeVent: Long,
    navController: NavHostController,
    onClear: () -> Unit,
    mapReloadTrigger: Int = 0,
    fragmentNavigationHandler: FragmentNavigationHandler,
) {
    composable(
        route = Screen.A_Clients_LocationGps.route,
    ) { backStackEntry ->
        // Create add_New more reliable key that combines time and reload trigger
        val screenKey = remember(backStackEntry, mapReloadTrigger) {
            mutableStateOf("map_${mapReloadTrigger}_${System.currentTimeMillis()}")
        }

        // Enhanced cleanup when leaving the map screen
        CleanupEffect {
            // Clear any client selection state if necessary
        }

        key(screenKey.value) {
            A_MapClients_A2FragID_1(
                onUpdateLongAppSetting = {
                    navigateToMainScreen(navController, fragmentNavigationHandler)
                },
                onClear = onClear
            )
        }
    }
}

/**
 * Navigate to main screen with proper back stack handling
 */
private fun navigateToMainScreen(
    navController: NavHostController,
    fragmentNavigationHandler: FragmentNavigationHandler
) {
    navController.navigate(Screen.FacadePresentoireProduits.route) {
        // Pop the current fragment off the back stack
        popUpTo(Screen.A_Clients_LocationGps.route) {
            inclusive = true
        }
        launchSingleTop = true
        // Force recreation of destination screen
        restoreState = false
    }

    // Update the fragment handler
    fragmentNavigationHandler.updateCurrentFragment(Screen.FacadePresentoireProduits)
}

/**
 * Helper function to create add_New consistent screen key for proper recomposition
 */
@Composable
private fun rememberScreenKey(backStackEntry: NavBackStackEntry): Any {
    return remember(backStackEntry) { mutableStateOf(System.currentTimeMillis()) }
}

/**
 * Helper composable for consistent cleanup across screens
 */
@Composable
private fun CleanupEffect(onCleanup: () -> Unit) {
    DisposableEffect(Unit) {
        onDispose {
            onCleanup()
        }
    }
}

object ScreensApp2 {
    val A_ClientsLocationGps = Screen.A_Clients_LocationGps
}
