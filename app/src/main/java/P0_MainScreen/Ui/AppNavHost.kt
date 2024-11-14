package P0_MainScreen.Ui
import P0_MainScreen.Ui.Objects.LoadingOverlay
import P0_MainScreen.Ui.Objects.Screen
import P1_StartupScreen.FragmentStartupScreen
import P3_DisplayeProductInfosToSeller.Main.P3DisplayeProductInfosToSeller
import P4_SoldCartScreen.SoldCartScreen
import P5_DialogeClientsEditer.ClientSelectionDialog
import P6_AiGroupeForSupplier.GenerativeAiScreen
import a_RoomDB.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.clientjetpack.AppViewModels

@Composable
fun AppNavHost(
    appViewModels: AppViewModels,
    navController: NavHostController,
    onToggleNavBar: () -> Unit,
    modifier: Modifier = Modifier,
    isFabVisible: Boolean, onClickDonne: () -> Unit, onClickToDisplayeConexionWifi: () -> Unit
) {
    val uiState by appViewModels.headViewModel.uiState.collectAsState()

    // Get current client from settings
    val currentClientId = uiState.appSettingsSaverModel
        .find { it.name == "clientBuyerNowId" }?.valueLong ?: 0
    val currentClient = uiState.clientsModel.find { it.idClientsSu == currentClientId }

    // Existing state management
    var opnerSaleWindows by rememberSaveable { mutableStateOf(false) }
    var showClientSelection by rememberSaveable { mutableStateOf(false) }
    var showClientSelectionWithoutCondition by rememberSaveable { mutableStateOf(false) }
    var relatedArticleBaseStats by rememberSaveable { mutableStateOf<ArticlesBasesStatsTable?>(null) }
    var pendingIndexColor by rememberSaveable { mutableIntStateOf(0) }
    val reloadTrigger by rememberSaveable { mutableIntStateOf(0) }

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.EditDatabaseWithCreateNewArticles.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.EditDatabaseWithCreateNewArticles.route) {
                Box(modifier = Modifier.fillMaxSize()) {
                    FragmentStartupScreen(
                        viewModel = appViewModels.headViewModel,
                        onToggleNavBar = onToggleNavBar,
                        reloadTrigger = reloadTrigger,
                        onClickToOpenWindos = { articleDataBase, indexColor ->
                            relatedArticleBaseStats = articleDataBase
                            pendingIndexColor = indexColor

                            if (currentClientId == 0L) {
                                showClientSelection = true
                            } else {
                                appViewModels.headViewModel.openWindowsNewSaleWithUpdateCurrent(
                                    relatedArticleBaseStats!!.idArticle.toLong(),
                                    currentClientId,
                                    pendingIndexColor)
                                opnerSaleWindows=true
                                appViewModels.headViewModel.sendOrderToClient("idProdect", relatedArticleBaseStats!!.idArticle.toLong())                            }
                        },
                        onClickToOpenClientsW = {
                            showClientSelectionWithoutCondition=true
                        },
                        isFabVisible=isFabVisible, onClickDonne = onClickDonne,
                        onClickToDisplayeConexionWifi = onClickToDisplayeConexionWifi,

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
                Box(modifier = Modifier.fillMaxSize()) {
                    SoldCartScreen(
                        viewModel = appViewModels.headViewModel,
                        clientBuyerNow = currentClient,
                        uiState = uiState,
                        onConfirmOrder = {
                            appViewModels.headViewModel.updateLongAppSetting("clientBuyerNowId",0)
                        }
                    )
                }
            }
            composable(Screen.BakingScreen.route) {
                Box(modifier = Modifier.fillMaxSize()) {
                    GenerativeAiScreen(
                        generativeAiViewModel = appViewModels.generativeAiViewModel,
                    )
                }
            }
        }

        // Overlay dialogs and windows
        if (showClientSelectionWithoutCondition ||(showClientSelection && currentClientId == 0L)) {
            ClientSelectionDialog(
                soldArticle = uiState.soldArticlesModel,
                viewModel = appViewModels.headViewModel,
                clients = uiState.clientsModel,
                onClientSelected = { client ->
                    appViewModels.headViewModel.updateLongAppSetting("clientBuyerNowId",client.idClientsSu)
                    if (!showClientSelectionWithoutCondition) {
                        appViewModels.headViewModel.openWindowsNewSaleWithUpdateCurrent(
                            relatedArticleBaseStats!!.idArticle.toLong(),
                            client.idClientsSu,
                            pendingIndexColor
                        )
                        opnerSaleWindows = true
                    }
                    showClientSelection = false
                    showClientSelectionWithoutCondition= false
                },
                onDismiss = {
                    showClientSelection = false
                    showClientSelectionWithoutCondition= false

                }
            )
        }

        if (opnerSaleWindows) {
            P3DisplayeProductInfosToSeller(
                modifier = Modifier.padding(horizontal = 3.dp),
                uiState = uiState,
                viewModel = appViewModels.headViewModel,
                onDismiss = {
                    appViewModels.headViewModel.clearCurrentSale()
                    opnerSaleWindows=false
                },
                reloadTrigger = reloadTrigger,
            )
        }

    }
}
