package P0_MainScreen.Ui.Main.AppNavHost

import P0_MainScreen.Ui.Objects.LoadingOverlay
import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.A_APP1FragID3_MainScreen
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.A_MapClients_A2FragID_1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._013_Acheteurs
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._14A_HistoriuesDeCetteJour
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Repository._01_VentsHistoriquesDataBase_Repository
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog._01_Upsert_013_Acheteurs
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.A_MainScreen_APP2_ID_2
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.A_APP3FragID1_MainScreen
import V.DiviseParSections.App.D.FraitProjet.App.FragID2.VentsHistoriquesDisplayer.Fragment.Ui.PeriodeVenteScreen
import Views.FragId3_DialogVendeurAfficheurInfosProduit.A_VendeurAfficheurInfosProduit_FragmentMainId3
import Views.P1._ArticlesStartFacade.FragmentStartupScreen
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Modules.WifiUpdateClientDisplayerStats
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
private const val TAG_ORDER_STATE = "OrderStateTracker"

// Extension function to check if an acheteur's last state is COMMANDE_LENCE
private fun _013_Acheteurs.isLastStateCommandeLence(): Boolean {
    // Add logging to track function execution
    Log.d(TAG_ORDER_STATE, "Checking last state for client ID: $idClient, name: $nomClient")

    if (child_14A_HistoriquesDeCetteJour.isEmpty()) {
        Log.d(TAG_ORDER_STATE, "No historical entries found for client $idClient")
        return false
    }

    // Log the number of historical entries
    Log.d(TAG_ORDER_STATE, "Found ${child_14A_HistoriquesDeCetteJour.size} historical entries for client $idClient")

    // Sort by date and time to get the most recent entry
    val sortedHistoriques = child_14A_HistoriquesDeCetteJour.sortedWith(
        compareByDescending<_14A_HistoriuesDeCetteJour> { it.dateCreationStr }
            .thenByDescending { it.tempCreationStr }
    )

    // Get the most recent state and log it
    val lastHistorique = sortedHistoriques.firstOrNull()
    val lastState = lastHistorique?.etate

    Log.d(TAG_ORDER_STATE, "Last state for client $idClient is: ${lastState?.name ?: "NULL"}, " +
            "date: ${lastHistorique?.dateCreationStr ?: "N/A"}, " +
            "time: ${lastHistorique?.tempCreationStr ?: "N/A"}")

    // Check if the most recent state is COMMANDE_LENCE
    return lastState == _14A_HistoriuesDeCetteJour.Etate.COMMANDE_LENCE
}

// Update acheteur state to ACHAT_TERMINE
private fun updateAcheteurToAchatTermine(
    viewModelInitApp: ViewModelInitApp,
    clientId: Long,
    repositorysModel: _0_0_HeadOfRepositorys_Model
) {
    val ceComptVendeurInsertBonsAchatAuPeriodID =
        repositorysModel.repository_1_5_Vendeur.modelDatasSnapList
            .find { it.vid == repositorysModel.activeIdDe_1_5_Vendeur }
            ?.ceComptVendeurInsertBonsAchatAuPeriodID

    _01_Upsert_013_Acheteurs(
        ceComptVendeurInsertBonsAchatAuPeriodID = ceComptVendeurInsertBonsAchatAuPeriodID,
        repositorysModel = repositorysModel,
        clientId = clientId,
        historiqueState = _14A_HistoriuesDeCetteJour.Etate.ACHAT_TERMINE,
        nom = viewModelInitApp.clientDataBaseSnapList.find { it.id == clientId }?.nom ?: "",
        repo_01_VentsHistoriquesDataBase = viewModelInitApp.repo_01_VentsHistoriquesDataBase_Repository
    )
}

// Order completion dialog component
@Composable
private fun OrderCompletionDialog(
    clientId: Long,
    clientName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Complete Order") },
        text = {
            Text("Mark the order for client $clientName (ID: $clientId) as completed?")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Complete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun NavGraphBuilder.app2(
    viewModelInitApp: ViewModelInitApp,
    clientEnCourDeVent: Long,
    navController: NavHostController,
    onClear: () -> Unit,
    orderStateManager: OrderStateManager, // Added parameter for tracking order state
) {
    composable(Screen.A_ClientsLocationGps.route) {
        // Check for active orders only when first entering this screen
        LaunchedEffect(Unit) {
            // Only check for active orders if we haven't already dismissed the dialog
            if (!orderStateManager.hasCheckedOrderStatus()) {
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

// Class to manage order state checks and prevent redundant checks
class OrderStateManager(
    private val repo_01_VentsHistoriquesDataBase_Repository: _01_VentsHistoriquesDataBase_Repository, // Update with actual type
    private val repositorysModel: _0_0_HeadOfRepositorys_Model,
    private val currentClientId: () -> Long,
    private val onShowDialog: () -> Unit
) {
    private var checkedOrderStatus = false

    fun hasCheckedOrderStatus(): Boolean = checkedOrderStatus

    fun resetOrderStatusCheck() {
        checkedOrderStatus = false
    }

    fun checkOrderStatus() {
        val clientId = currentClientId()
        if (clientId <= 0) {
            Log.d(TAG_ORDER_STATE, "No current client selected (ID is 0)")
            checkedOrderStatus = true
            return
        }

        Log.d(TAG_ORDER_STATE, "Checking for active order for client ID: $clientId")

        val period = repo_01_VentsHistoriquesDataBase_Repository
            .modelDatasSnapList
            .firstOrNull()

        if (period == null) {
            Log.d(TAG_ORDER_STATE, "No period found in database")
            checkedOrderStatus = true
            return
        }

        Log.d(TAG_ORDER_STATE, "Found period with ID: ${period.idPeriodDonAncienDataBase}")

        val vendeur = period.child_012_Compts_Vendeurs?.firstOrNull {
            it.idCompt == repositorysModel.activeIdDe_1_5_Vendeur
        }

        if (vendeur == null) {
            Log.d(TAG_ORDER_STATE, "No vendeur found with ID: ${repositorysModel.activeIdDe_1_5_Vendeur}")
            checkedOrderStatus = true
            return
        }

        Log.d(TAG_ORDER_STATE, "Found vendeur with ID: ${vendeur.idCompt}")

        val acheteur = vendeur.child_013_Acheteurs?.firstOrNull {
            it.idClient == clientId
        }

        if (acheteur == null) {
            Log.d(TAG_ORDER_STATE, "No acheteur found with ID: $clientId")
            checkedOrderStatus = true
            return
        }

        Log.d(TAG_ORDER_STATE, "Checking last state for acheteur: ${acheteur.nomClient} (ID: ${acheteur.idClient})")

        if (acheteur.isLastStateCommandeLence()) {
            Log.d(TAG_ORDER_STATE, "Acheteur ${acheteur.nomClient} has COMMANDE_LENCE state, showing dialog")
            onShowDialog()
        } else {
            Log.d(TAG_ORDER_STATE, "Acheteur ${acheteur.nomClient} does not have COMMANDE_LENCE state")
        }

        // Mark as checked so we don't recheck
        checkedOrderStatus = true
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

    // Create order state manager to handle order status checks
    val orderStateManager = remember {
        OrderStateManager(
            repo_01_VentsHistoriquesDataBase_Repository = repo_01_VentsHistoriquesDataBase_Repository,
            repositorysModel = repositorysModel,
            currentClientId = { currentClientId },
            onShowDialog = { showOrderCompletionDialog = true }
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
                    orderStateManager = orderStateManager, // Pass order state manager
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
                                        repositorysModel
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

            // Show order completion dialog if needed
            if (showOrderCompletionDialog) {
                OrderCompletionDialog(
                    clientId = currentClientId,
                    clientName = currentClient?.nom ?: "Unknown",
                    onConfirm = {
                        updateAcheteurToAchatTermine(
                            viewModelInitApp,
                            currentClientId,
                            repositorysModel
                        )
                        showOrderCompletionDialog = false
                    },
                    onDismiss = {
                        showOrderCompletionDialog = false
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
        onClear = { /* Implementation for clear operation */ },
        headViewModel = headViewModel,
        lockHost = lockHost.value,
        onClickImageToShowControles = { /* Implementation for showing controls */ }
    )
}
