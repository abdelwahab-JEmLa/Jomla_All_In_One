package P0_MainScreen

import P0_MainScreen.Ui.AppNavHost
import P0_MainScreen.Ui.ConnexionCard
import P0_MainScreen.Ui.NavigationBarWithFab
import P0_MainScreen.Ui.NavigationItems
import P0_MainScreen.Ui.Screen
import P2_EStorePresentationToClient.WindowsPresentationInfoProdect
import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.clientjetpack.AppViewModels

@Composable
fun MainScreen(
    appViewModels: AppViewModels
) {
    val headViewModel = appViewModels.headViewModel
    val uiState by headViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current
    val window = (context as? Activity)?.window

    // Handle fullscreen mode based on isHostPhone
    LaunchedEffect(uiState.isHostPhone) {
        if (!uiState.isHostPhone) {
            // Enable fullscreen and hide system bars
            window?.let {
                WindowCompat.setDecorFitsSystemWindows(it, false)
                it.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                WindowInsetsControllerCompat(it, view).let { controller ->
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                    controller.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        } else {
            // Restore normal mode
            window?.let {
                WindowCompat.setDecorFitsSystemWindows(it, true)
                it.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                WindowInsetsControllerCompat(it, view).show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    val displayProductDataBase = uiState.productDisplayController.windowsProductIdWhoInfoDisplayed?.let { id ->
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
            if (isDisplayedConnexionWifiVisible || !uiState.isConnected) {
                ConnexionCard(
                    uiState,
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

                if (!uiState.isHostPhone && uiState.isConnected) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(enabled = false) { }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = uiState.isHostPhone || !uiState.isConnected,
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
        WindowsPresentationInfoProdect(
            displayController = uiState.productDisplayController,
            articleStatsDataBase = displayProductDataBase,
            colorsArticlesList = uiState.colorsArticlesTabelleModel,
            reloadTrigger = 0
        )
    }
}
