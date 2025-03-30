package P0_MainScreen.Ui.Main.AppNavHost

import P0_MainScreen.Ui.Objects.LoadingOverlay
import P5_DialogeClientsEditer.ClientSelectionDialog
import Views.FragId3_DialogVendeurAfficheurInfosProduit.A_VendeurAfficheurInfosProduit_FragmentMainId3
import Views.P1._ArticlesStartFacade.FragmentStartupScreen
import Views.Package_4.SoldCartScreen.SoldCartScreen
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.A_id1_ClientsLocationGps
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Kotlin._WorkingON.WO_.WifiUpdateClientDisplayerStats
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.View.MainScreen_Windows
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ArticlesBasesStatsTable
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
        A_id1_ClientsLocationGps(
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
    targetCategoryId: MutableState<Long?> = mutableStateOf(null), lockHost: Boolean
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
                startDestination = Screen.A_ClientsLocationGps.route,
                modifier = Modifier.fillMaxSize()
            ) {

                app2(
                    viewModelInitApp, clientEnCourDeVent,
                    navController,
                    {
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
                            scrollTiger = scrollTiger, onToggleLockHost = onToggleLockHost,
                            onToggleLockExpandedPricex = {
                                lockExpandedPrices = !lockExpandedPrices
                            },
                            currentClient = currentClient, viewModelInitApp = viewModelInitApp,
                            targetCategoryId=targetCategoryId, lockHost = lockHost
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
                        SoldCartScreen(
                            viewModel = headViewModel,
                            clientBuyerNow = currentClient,
                            uiState = uiState,
                            onConfirmOrder = {
                                headViewModel
                                    .updateLongAppSetting("clientBuyerNowId", 0)
                            }, viewModelInitApp = viewModelInitApp
                        )
                    }
                }

                composable(Screen.TravailleTempRecorder.route) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        MainScreen_Windows(fabsVisibility = viewModelInitApp._paramatersAppsViewModelModel.fabsVisibility)
                    }
                }
            }

            // Overlay dialogs and windows
            if (showClientSelectionWithoutCondition || (showClientSelection && currentClientId == 0L)) {
                ClientSelectionDialog(
                    soldArticle = uiState.soldArticlesModel,
                    viewModel = headViewModel,
                    clients = viewModelInitApp.clientDataBaseSnapList,
                    onClientSelected = { AppSetting ->
                        headViewModel.updateLongAppSetting(
                            "clientBuyerNowId",
                            AppSetting.id
                        )
                        if (!showClientSelectionWithoutCondition) {
                            headViewModel.openWindowsNewSaleWithUpdateCurrent(
                                relatedArticleBaseStats!!.idArticle.toLong(),
                                AppSetting.id,
                                pendingIndexColor
                            )
                            opnerSaleWindows = true
                        }
                        showClientSelection = false
                        showClientSelectionWithoutCondition = false
                    },
                    onDismiss = {
                        showClientSelection = false
                        showClientSelectionWithoutCondition = false

                    }
                )
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
                            WifiUpdateClientDisplayerStats.DISMISS_PRODUCT_INFO.prefix    //-->
                        )
                    },
                    reloadTrigger = reloadTrigger, lockExpandedPrices = lockExpandedPrices,
                    onToggleLockExpandedPricex = { lockExpandedPrices = !lockExpandedPrices },
                    viewModelInitApp = viewModelInitApp,
                    currentClient = currentClient,
                )
            }
        }
    }
}
