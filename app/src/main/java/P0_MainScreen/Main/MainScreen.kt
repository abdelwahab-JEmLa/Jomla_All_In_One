package P0_MainScreen.Main

import P0_MainScreen.Modules.HandleFullscreenMode
import P0_MainScreen.Ui.Main.AppNavHost.AppNavHost
import P0_MainScreen.Ui.Main.AppNavHost.NavigationBarWithFab
import P0_MainScreen.Ui.Main.AppNavHost.NavigationItems
import P0_MainScreen.Ui.Main.AppNavHost.Screen
import P0_MainScreen.Ui.Objects.ConnexionCard
import P7_EStorePresentationToClient.Main.FragmentDisplayeInfoProductToClient7
import P7_EStorePresentationToClient.Main.SearchArticle
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.clientjetpack.AppViewModels

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(
    appViewModels: AppViewModels,
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp = viewModel(),
    xmlResources: List<Pair<String, Int>>?=null,
) {
    val headViewModel = appViewModels.headViewModel
    val uiState by headViewModel.uiState.collectAsState()
    val productDisplayController = uiState.productDisplayController


    // Handle fullscreen mode
    HandleFullscreenMode(productDisplayController)

    // Navigation setup
    val navController = rememberNavController()
    val items = NavigationItems.getItems()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // State management
    var isNavBarVisible by remember { mutableStateOf(true) }
    var isFabVisible by remember { mutableStateOf(false) }
    var isDisplayedConnexionWifiVisible by remember { mutableStateOf(false) }
    var showProductDisplay by remember { mutableStateOf(false) }
    var lockHost by remember { mutableStateOf(false) }

    LaunchedEffect(productDisplayController.clientWindowsDisplayedProductId) {
        showProductDisplay = productDisplayController.clientWindowsDisplayedProductId != null
        if (productDisplayController.clientWindowsDisplayedProductId == null) {
            // Only navigate back if we're not already on the startup screen
            if (currentRoute != Screen.A_ClientsLocationGps.route) {
                navController.navigate(Screen.A_ClientsLocationGps.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // WiFi Connection Card
                AnimatedVisibility(
                    visible = isDisplayedConnexionWifiVisible
                            ||
                            !productDisplayController.isConnected
                            && !lockHost
                ) {
                    ConnexionCard(
                        productDisplayController = productDisplayController,
                        appViewModels = appViewModels,
                        onClickToStartAsClient = {
                            isNavBarVisible = false
                            isFabVisible = false
                        } ,
                        lockHost
                    )
                }

                // Main Content Area
                Box(modifier = Modifier.weight(1f)) {
                    AppNavHost(
                        appViewModels = appViewModels,
                        navController = navController,
                        onToggleNavBar = { isNavBarVisible = !isNavBarVisible },
                        modifier = Modifier.fillMaxSize(),
                        isFabVisible = isFabVisible,
                        onClickDonne = { isFabVisible = false },
                        onClickToDisplayeConexionWifi = {
                            isDisplayedConnexionWifiVisible = !isDisplayedConnexionWifiVisible
                        },
                        onToggleLockHost = {lockHost=!lockHost}, viewModelInitApp = viewModelInitApp,
                        onClear = {},
                        headViewModel = headViewModel,
                    )

                    // Disable interactions when not host phone
                    if (!productDisplayController.isHostPhone && productDisplayController.isConnected) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(enabled = false) { }
                        )
                    }
                }
            }

            // Navigation Bar with FAB
            AnimatedVisibility(
                visible = productDisplayController.isHostPhone || !productDisplayController.isConnected,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                NavigationBarWithFab(
                    items = items.filter { it != Screen.ToggleFab },
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    isFabVisible = isFabVisible,
                    onToggleFabVisibility = {
                        isFabVisible = !isFabVisible
                        isDisplayedConnexionWifiVisible = false
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            // Product Display Dialog
            if (showProductDisplay) {
                val productId = productDisplayController.clientWindowsDisplayedProductId
                val displayProductDataBase = productId?.let { id ->
                    uiState.articlesBasesStatTables.find { it.idArticle.toLong() == id }
                }

                if (displayProductDataBase != null) {
                    FragmentDisplayeInfoProductToClient7(
                        displayController = productDisplayController,
                        articleStatsDataBase = displayProductDataBase,
                        colorsArticlesList = uiState.colorsArticlesTabelleModel,
                        reloadTrigger = 0, // Use state if needed
                        modifier = Modifier.fillMaxSize()
                    )
                }

            }
            // Search Display Dialog
            if (productDisplayController.searchWindowsDisplaye.isNotEmpty()) {
                SearchArticle(
                    dsipayeText = productDisplayController.searchWindowsDisplaye
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { uiState.loadingProgress / 100f },
                        modifier = Modifier.size(48.dp),
                        trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
    // Initialize connection using LaunchedEffect
    LaunchedEffect(Unit) {
        headViewModel.initializeConnection()
    }
}
