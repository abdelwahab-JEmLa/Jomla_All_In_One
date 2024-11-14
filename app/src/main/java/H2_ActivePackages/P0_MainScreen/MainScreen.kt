package H2_ActivePackages.P0_MainScreen

import H2_ActivePackages.P0_MainScreen.Ui.AppNavHost
import H2_ActivePackages.P0_MainScreen.Ui.ConnexionCard
import H2_ActivePackages.P0_MainScreen.Ui.NavigationBarWithFab
import H2_ActivePackages.P0_MainScreen.Ui.NavigationItems
import H2_ActivePackages.P0_MainScreen.Ui.Screen
import H2_ActivePackages.P2_EStorePresentationToClient.WindowsPresentationInfoProdect
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
fun MainScreen(     //TODO fit que l app soit on mode prentation plien ecran
    //le top bar du time ert button du telphone ce cachon quand   !isHostPhone
    appViewModels: AppViewModels
) {
    val startUpViewModel = appViewModels.headViewModel
    val uiState by startUpViewModel.uiState.collectAsState()
    val displayerStats by appViewModels.clientPresentationViewModel.displayerStats.collectAsState()

    // Safely find matching article based on displayed product ID
    val displayProductDataBase = displayerStats.windowsProductIdWhoInfoDisplayed?.let { id ->
        uiState.articlesBasesStatTables.find { it.idArticle.toLong() == id }
    }

    val isHostPhone = uiState.isHostPhone
    val navController = rememberNavController()
    val items = NavigationItems.getItems()
    var isNavBarVisible by remember { mutableStateOf(true) }
    var isFabVisible by remember { mutableStateOf(false) }
    var isDisplayeConexionWifiVisible by remember { mutableStateOf(false) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (isDisplayeConexionWifiVisible) {
                ConnexionCard(uiState, appViewModels)
            }
            Box(modifier = Modifier.weight(1f)) {
                AppNavHost(
                    appViewModels = appViewModels,
                    navController = navController,
                    onToggleNavBar = { isNavBarVisible = !isNavBarVisible },
                    isFabVisible = isFabVisible,
                    onClickDonne = { isFabVisible = false },
                    onClickToDisplayeConexionWifi = {
                        isDisplayeConexionWifiVisible = !isDisplayeConexionWifiVisible
                    },
                )

                if (!isHostPhone) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(enabled = false) { }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isNavBarVisible && isHostPhone,
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
                    isDisplayeConexionWifiVisible = false
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

    // Show product info window only when we have both an ID and matching product data
    if ( displayProductDataBase != null) {
        WindowsPresentationInfoProdect(
            displayController = displayerStats,
            articleStatsDataBase = displayProductDataBase,
            colorsArticlesList = uiState.colorsArticlesTabelleModel,
            reloadTrigger = 0
        )
    }
}
