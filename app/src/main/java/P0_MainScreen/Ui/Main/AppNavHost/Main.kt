package P0_MainScreen.Ui.Main.AppNavHost

import P0_MainScreen.Ui.Objects.LoadingOverlay
import Views.FragId3_DialogVendeurAfficheurInfosProduit.A_VendeurAfficheurInfosProduit_FragmentMainId3
import Views.P1._ArticlesStartFacade.FragmentStartupScreen
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.A_MainScreen_APP2_ID_2
import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.A_MainScreen_APP2_FragID3
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_CodePartageEntreApps.Modules.WifiUpdateClientDisplayerStats
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.A_MapClients_A2FragID_1
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

fun NavGraphBuilder.app2(
    viewModelInitApp: ViewModelInitApp,
    clientEnCourDeVent: Long,
    navController: NavHostController, onClear: () -> Unit,
) {
    composable(Screen.A_ClientsLocationGps.route) {
        A_MapClients_A2FragID_1(
            viewModelInitApp = viewModelInitApp,
            clientEnCourDeVent = clientEnCourDeVent,
            onUpdateLongAppSetting = {
                allerAuFragment(navController)
            },
            onClear = onClear,
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
    onClear: () -> Unit,
    headViewModel: HeadViewModel,
    targetCategoryId: MutableState<Long?> = mutableStateOf(null),
    lockHost: Boolean
) {
    val uiState by headViewModel.uiState.collectAsState()
    // Get current client from settings
    val currentClientId = uiState.appSettingsSaverModel
        .find { it.name == "clientBuyerNowId" }?.valueLong ?: 0
    val currentClient = viewModelInitApp.clientDataBaseSnapList.find { it.id == currentClientId }

    // Existing state management
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
                ?.valueLong ?: 0)
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
                // Changed the startDestination to make FragmentStartupScreen the start fragment
                startDestination = Screen.EditDatabaseWithCreateNewArticles.route,
                modifier = Modifier.fillMaxSize()
            ) {

                app2(
                    viewModelInitApp, clientEnCourDeVent,
                    navController,
                ) {
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
                }

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
                            lockHost = lockHost
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
                    // Increment navigation count when entering SoldCart
                    LaunchedEffect(Unit) {
                        scrollTiger++
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        A_MainScreen_APP2_ID_2(
                            onConfirmOrder = {
                                headViewModel
                                    .updateLongAppSetting("clientBuyerNowId", 0)
                            }
                        )
                    }
                }

                composable(Screen.TravailleTempRecorder.route) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        A_MainScreen_APP2_FragID3()
                    }
                }
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
                    clickedCouleurIndex=pendingIndexColor,
                )
            }
        }
    }
}
