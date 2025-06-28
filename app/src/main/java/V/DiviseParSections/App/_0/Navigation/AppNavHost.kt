// Updated AppNavHost.kt - Remove DialogTests composable route
package V.DiviseParSections.App._0.Navigation

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.GrossistAchatSec12FragID1_Main
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_MapClients_A2FragID_1
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Z.Archive.Views.A_MainScreen_APP2_ID_2PanierFinaleDAchat
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.Z.Main.PanierFinaleDAchatSec1Frag3
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.A_APP3FragID1_MainScreen
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.PresenterElectroBoutiqueAbdelwahab_Sec10Frag1
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.EditeBaseDonneMainScreenIdS9
import Views.FragId3_DialogVendeurAfficheurInfosProduit.A_VendeurAfficheurInfosProduit_FragmentMainId3
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
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
    headViewModel: HeadViewModel = koinViewModel(),
    viewModelInitApp: ViewModelInitApp = koinViewModel(),
    navController: NavHostController,
    onToggleNavBar: () -> Unit,
    isFabVisible: Boolean,
    onClickDonne: () -> Unit,
    onClickToDisplayeConexionWifi: () -> Unit,
    onToggleLockHost: () -> Unit,
    targetCategoryId: MutableState<Long?> = mutableStateOf(null),
    lockHost: Boolean,
    onClickImageToShowControles: () -> Unit,
    fragmentNavigationHandler: FragmentNavigationHandler = koinInject(),
) {
    val uiState by headViewModel.uiState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
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
            headViewModel._uiState.value
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
            NavHost(
                navController = navController,
                startDestination = Screen.EditDatabaseWithCreateNewArticles.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(
                    route = Screen.EditDatabaseWithCreateNewArticles.route,
                ) { backStackEntry ->
                    val screenKey = rememberScreenKey(backStackEntry)
                    Box(modifier = Modifier.fillMaxSize()) {
                        key(screenKey) {
                            PresenterElectroBoutiqueAbdelwahab_Sec10Frag1(
                                viewModelHeadViewModel = headViewModel,
                                onToggleNavBar = onToggleNavBar,
                                reloadTrigger = reloadTrigger,
                                onClickToOpenWindos = { articleDataBaseOn, indexColor ->
                                    relatedArticleBaseStats = articleDataBaseOn
                                    pendingIndexColor = indexColor

                                    if (currentClientId == 0L) {
                                        showClientSelection = true
                                    } else {
                                        headViewModel.openWindowsNewSaleWithUpdateCurrent(
                                            relatedArticleBaseStats!!.id.toLong(),
                                            currentClientId,
                                            pendingIndexColor
                                        )
                                        opnerSaleWindows = true
                                        headViewModel.sendOrderToClientDisplayer(
                                            WifiUpdateClientDisplayerStats.ClientWindowsDisplayedProductId.prefix,
                                            relatedArticleBaseStats!!.id.toLong()
                                        )
                                    }
                                },
                                onClickToOpenClientsW = {
                                    showClientSelectionWithoutCondition = true
                                },
                                isFabVisibleInit = isFabVisible,
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
                        }

                        if (uiState.isLoading) {
                            LoadingOverlay(
                                progress = uiState.loadingProgress / 100f,
                                modifier = Modifier.matchParentSize()
                            )
                        }
                    }
                }

                // Cart screen
                composable(
                    route = Screen.SoldCart.route,
                ) { backStackEntry ->
                    val screenKey = rememberScreenKey(backStackEntry)

                    CleanupEffect {
                        // Any cleanup needed for this screen
                    }

                    LaunchedEffect(Unit) {
                        scrollTiger++
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        key(screenKey) {
                            A_MainScreen_APP2_ID_2PanierFinaleDAchat()
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
                    route = Screen.CommandeProduits.route,
                ) { backStackEntry ->
                    val screenKey = rememberScreenKey(backStackEntry)

                    CleanupEffect {
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        key(screenKey) {
                            GrossistAchatSec12FragID1_Main()
                        }
                    }
                }

                // Product ordering screen
                composable(
                    route = Screen.EditeProduitsBaseDonneS9.route,
                ) { backStackEntry ->
                    val screenKey = rememberScreenKey(backStackEntry)
                    Box(modifier = Modifier.fillMaxSize()) {
                        key(screenKey) {
                            EditeBaseDonneMainScreenIdS9()
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
                            PanierFinaleDAchatSec1Frag3()
                        }
                    }
                }

                app2(
                    viewModelInitApp = viewModelInitApp,
                    clientEnCourDeVent = clientEnCourDeVent,
                    navController = navController,
                    onClear = {
                        headViewModel.viewModelScope.launch {
                            headViewModel._uiState.update { currentState ->
                                currentState.copy(soldArticlesModel = emptyList())
                            }

                            // Clear the database in add coroutine
                            headViewModel.database.soldArticlesModelDao().deleteAll()

                            // Clear Firebase references
                            val database = Firebase.database
                            database.getReference("K_GroupeurBonCommendToSupplierRef").removeValue()
                            database.getReference("O_SoldArticlesTabelle").removeValue()
                        }
                    },
                    mapReloadTrigger = mapReloadTrigger.intValue
                )
            }

            // Handle client selection navigation logic
            if (showClientSelectionWithoutCondition || (showClientSelection && currentClientId == 0L)) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.A_ClientsLocationGps.route) {
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

            // Product detail dialog
            if (opnerSaleWindows) {
                A_VendeurAfficheurInfosProduit_FragmentMainId3(
                    uiState = uiState,
                    viewModelHeadViewModel = headViewModel,
                    onDismiss = {
                        headViewModel.clearCurrentSale()
                        opnerSaleWindows = false
                        headViewModel.sendOrderToClientDisplayer(
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

// DialogTestsScreen function REMOVED - now handled as dialog in NavigationBarWithFab

/**
 * Creates client map navigation routes
 */
fun NavGraphBuilder.app2(
    viewModelInitApp: ViewModelInitApp,
    clientEnCourDeVent: Long,
    navController: NavHostController,
    onClear: () -> Unit,
    mapReloadTrigger: Int = 0,
) {
    composable(
        route = Screen.A_ClientsLocationGps.route,
    ) { backStackEntry ->
        // Create add more reliable key that combines time and reload trigger
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
                    navigateToMainScreen(navController)
                },
                onClear = onClear,
                mapReloadTrigger = mapReloadTrigger
            )
        }
    }
}

/**
 * Helper function to create add consistent screen key for proper recomposition
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

/**
 * Navigate to main screen with proper back stack handling
 */
private fun navigateToMainScreen(navController: NavHostController) {
    navController.navigate(Screen.EditDatabaseWithCreateNewArticles.route) {
        // Pop the current fragment off the back stack
        popUpTo(Screen.A_ClientsLocationGps.route) {
            inclusive = true
        }
        launchSingleTop = true
        // Force recreation of destination screen
        restoreState = false
    }
}

/**
 * App2 screen definition
 */
object ScreensApp2 {
    val A_ClientsLocationGps = Screen.A_ClientsLocationGps
}
