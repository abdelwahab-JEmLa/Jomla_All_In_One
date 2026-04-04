package V.DiviseParSections.App._0.Navigation

import Application4.App.Fragment.ID1.Fragment.A_Compact_Presentoire_App_Produits_App4
import EntreApps.Shared.Models.M01Produit
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_MapClients_A2FragID_1
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.Z.Main.PanierFinaleDAchatSec1Frag3
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.A_APP3FragID1_MainScreen
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.PresenterElectroBoutiqueAbdelwahab_Sec10Frag1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.MainFastSearchProduitPourVent
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Ancien_PresenterApp_FragID5
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Screen_GrossistAchatSec12FragID1
import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.EducationFragment
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.EditeBaseDonneMainScreenIdS9
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.ViewModel.HeadViewModel
import Views.FragId3_DialogVendeurAfficheurInfosProduit.A_VendeurAfficheurInfosProduit_FragmentMainId3
import Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview.A.Main.Main_DataBaseInitFactory_1Produit
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
    isWifiClientConnected_1: Boolean,
) {
    // Initialize UI state and navigation
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if only tahfid fragment should be displayed
    val ne_affiche_que_fragment =
        focusedValuesGetter.currentActive_M9AppCompt?.ne_affiche_que_fragment == "tahfid_classe"

    val isAdmin = focusedValuesGetter.currentActive_M9AppCompt?.its_Admin ?: true

    // Define the startup screen - this is the initial screen shown when the app launches
    // Set to Fragment_Compact_Presentoir_Echantilliants (Product Showcase Fragment)
    val startUpScreen = Screen.Fragment_Compact_Presentoir_Echantilliants

    LaunchedEffect(currentRoute) {
        fragmentNavigationHandler.updateCurrentFragmentByRoute(currentRoute)
    }

    // Set startup screen when component initializes
    LaunchedEffect(startUpScreen) {
        fragmentNavigationHandler.setStartupScreen(startUpScreen)
    }

    val currentClientId = uiState.appSettingsSaverModel
        .find { it.name == "clientBuyerNowId" }?.valueLong ?: 0
    val currentClient = viewModelInitApp.clientDataBaseSnapList.find { it.id == currentClientId }
    var opnerSaleWindows by rememberSaveable { mutableStateOf(false) }
    var showClientSelection by rememberSaveable { mutableStateOf(false) }
    var showClientSelectionWithoutCondition by rememberSaveable { mutableStateOf(false) }
    var relatedArticleBaseStats by rememberSaveable { mutableStateOf<M01Produit?>(null) }
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

    // Adjust bottom padding based on tahfid mode
    val bottomNavHeight = if (ne_affiche_que_fragment) 0.dp else 80.dp
    val bottomPadding = if (ne_affiche_que_fragment) 0.dp else 8.dp

    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = bottomNavHeight + bottomPadding)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = startUpScreen.route,
                modifier = Modifier.fillMaxSize()
            ) {
                // Main presenter screen - accessible to all
                composable(
                    route = Screen.Fragment_Compact_Presentoir_Echantilliants.route,
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

                                    // FIXED: Don't automatically navigate to client map
                                    // Only show dialog/message that client needs to be selected
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
                                    // FIXED: Only this explicit button click should navigate
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
                                onClickImageToShowControles = onClickImageToShowControles,

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

                composable(
                    route = Screen.Compact_Presentoire_App_Produits_FragID5.route,
                ) {
                    Ancien_PresenterApp_FragID5(

                        onClickImageToShowControles = onClickImageToShowControles,
                    )
                }

                composable(Screen.Screen1PanieVentsFinale.route) {
                    PanierFinaleDAchatSec1Frag3(
                    )
                }

                composable(Screen.TravailleTempRecorder.route) {
                    A_APP3FragID1_MainScreen()
                }

                composable(Screen.Achats_Produits_Chez_Grossists.route) {
                    Screen_GrossistAchatSec12FragID1()
                }

                // Admin-only screens
                composable(Screen.EditDatabaseWithCreateNewArticles.route) {
                    EditeBaseDonneMainScreenIdS9()
                }

                composable(Screen.EducationFragment.route) {
                    EducationFragment()
                }

                composable(Screen.FragmentProduitFastSearchDialog.route) {
                    MainFastSearchProduitPourVent()
                }

                composable(Screen.Main_DataBaseInitFactory_1Produit.route) {
                    Main_DataBaseInitFactory_1Produit()
                }

                composable(Screen.Fragment_Compact_Presentoir_Echantilliants.route) {
                    A_Compact_Presentoire_App_Produits_App4()
                }

                // Add client map routes
                app2(
                    viewModelInitApp = viewModelInitApp,
                    clientEnCourDeVent = clientEnCourDeVent,
                    navController = navController,
                    onClear = {
                        viewModel.viewModelScope.launch {
                            viewModel._uiState.update { currentState ->
                                currentState.copy(soldArticlesModel = emptyList())
                            }

                            // Clear the database in coroutine
                            viewModel.database.soldArticlesModelDao().deleteAll()

                            // Clear Firebase references
                            val database = Firebase.database
                            database.getReference("K_GroupeurBonCommendToSupplierRef")
                                .removeValue()
                            database.getReference("O_SoldArticlesTabelle").removeValue()
                        }
                    },
                    mapReloadTrigger = mapReloadTrigger.intValue,
                    fragmentNavigationHandler = fragmentNavigationHandler
                )
            }

            // FIXED: Only navigate when explicitly requested
            if (showClientSelectionWithoutCondition) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.A_Clients_LocationGps.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                    showClientSelectionWithoutCondition = false
                    mapReloadTrigger.intValue++
                }
            }

            // FIXED: Don't navigate automatically when no client selected
            if (showClientSelection && currentClientId == 0L && !showClientSelectionWithoutCondition) {
                LaunchedEffect(Unit) {
                    showClientSelection = false
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

/**
 * Screen shown when user tries to access admin-only content
 */
@Composable
private fun UnauthorizedAccessScreen(
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Access Denied",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "You need admin privileges to access this screen",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onNavigateBack) {
                Text("Go Back")
            }
        }
    }
}

private fun getScreenFromRoute(route: String?): Screen? {
    return when (route) {
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
        val screenKey = remember(backStackEntry, mapReloadTrigger) {
            mutableStateOf("map_${mapReloadTrigger}_${System.currentTimeMillis()}")
        }

        CleanupEffect {
            // Clear any client selection state if necessary
        }

        key(screenKey.value) {
            A_MapClients_A2FragID_1(
                onUpdateLongAppSetting = {
                    // navigateToMainScreen(navController, fragmentNavigationHandler)
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
    navController.navigate(Screen.Fragment_Compact_Presentoir_Echantilliants.route) {
        popUpTo(Screen.A_Clients_LocationGps.route) {
            inclusive = true
        }
        launchSingleTop = true
        restoreState = false
    }
    fragmentNavigationHandler.updateCurrentFragment(Screen.Fragment_Compact_Presentoir_Echantilliants)
}

/**
 * Helper function to create a consistent screen key for proper recomposition
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
