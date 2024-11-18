package P0_MainScreen.Main

import P0_MainScreen.Modules.HandleFullscreenMode
import P0_MainScreen.Ui.Main.AppNavHost.AppNavHost
import P0_MainScreen.Ui.Main.AppNavHost.NavigationBarWithFab
import P0_MainScreen.Ui.Main.AppNavHost.NavigationItems
import P0_MainScreen.Ui.Main.AppNavHost.Screen
import P0_MainScreen.Ui.Objects.ConnexionCard
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.clientjetpack.AppViewModels

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(
    appViewModels: AppViewModels,
    modifier: Modifier = Modifier
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

    //  Handle product display navigation
    LaunchedEffect(productDisplayController.clientWindowsDisplayedProductId) {
        if (productDisplayController.clientWindowsDisplayedProductId != null) {
            // Navigate to product display when ID is present
            navController.navigate(Screen.ClientProductDisplay.createRoute(productDisplayController.clientWindowsDisplayedProductId)) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        } else {
            // Navigate back to startup screen when ID becomes null
            navController.navigate(Screen.EditDatabaseWithCreateNewArticles.route) {
                // Pop up to remove all screens from the back stack
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true // Include the start destination in the pop
                }
                // Avoid multiple copies of the same destination
                launchSingleTop = true
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
                    visible = isDisplayedConnexionWifiVisible || !productDisplayController.isConnected
                ) {
                    ConnexionCard(
                        productDisplayController = productDisplayController,
                        appViewModels = appViewModels,
                        onClickToStartAsClient = {
                            isNavBarVisible = false
                            isFabVisible = false
                        }
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
                        }
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

            // Loading Indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { uiState.loadingProgress / 100f },
                        modifier = Modifier.size(48.dp),
                        trackColor = ProgressIndicatorDefaults.circularTrackColor,
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
