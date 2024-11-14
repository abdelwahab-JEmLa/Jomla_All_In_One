package P0_MainScreen

import P0_MainScreen.Modules.HandleFullscreenMode
import P0_MainScreen.Ui.AppNavHost
import P0_MainScreen.Ui.Objects.ConnexionCard
import P0_MainScreen.Ui.Objects.NavigationBarWithFab
import P0_MainScreen.Ui.Objects.NavigationItems
import P0_MainScreen.Ui.Objects.Screen
import P2_EStorePresentationToClient.Main.WindowsPresentationInfoProduct
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
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

@Composable
fun MainScreen(
    appViewModels: AppViewModels
) {
    val headViewModel = appViewModels.headViewModel
    val uiState by headViewModel.uiState.collectAsState()
    val productDisplayController =   uiState.productDisplayController

    HandleFullscreenMode(productDisplayController)


    val displayProductDataBase = productDisplayController.windowsProductIdWhoInfoDisplayed?.let { id ->
        uiState.articlesBasesStatTables.find { it.idArticle.toLong() == id }
    }

    val navController = rememberNavController()
    val items = NavigationItems.getItems()
    var isNavBarVisible by remember { mutableStateOf(true) }
    var isFabVisible by remember { mutableStateOf(false) }
    var isDisplayedConnexionWifiVisible by remember { mutableStateOf(false) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (isDisplayedConnexionWifiVisible || !productDisplayController.isConnected) {
                ConnexionCard(
                    productDisplayController,
                    appViewModels,
                    onClickToStartAsClient = {
                        isNavBarVisible = false
                        isFabVisible = false
                    }
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                AppNavHost(
                    appViewModels = appViewModels,
                    navController = navController,
                    onToggleNavBar = { isNavBarVisible = !isNavBarVisible },
                    isFabVisible = isFabVisible,
                    onClickDonne = { isFabVisible = false },
                    onClickToDisplayeConexionWifi = {
                        isDisplayedConnexionWifiVisible = !isDisplayedConnexionWifiVisible
                    },
                )

                if (!productDisplayController.isHostPhone && productDisplayController.isConnected) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(enabled = false) { }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = productDisplayController.isHostPhone || !productDisplayController.isConnected,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            NavigationBarWithFab(
                items = items.filter { it != Screen.ToggleFab },
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                isFabVisible = isFabVisible,
                onToggleFabVisibility = {
                    isFabVisible = !isFabVisible
                    isDisplayedConnexionWifiVisible = false
                }
            )
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                progress = { uiState.loadingProgress },
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
        }
    }

    if (displayProductDataBase != null) {
        WindowsPresentationInfoProduct(
            displayController = productDisplayController,
            articleStatsDataBase = displayProductDataBase,
            colorsArticlesList = uiState.colorsArticlesTabelleModel,
            reloadTrigger = 0
        )
    }
}
