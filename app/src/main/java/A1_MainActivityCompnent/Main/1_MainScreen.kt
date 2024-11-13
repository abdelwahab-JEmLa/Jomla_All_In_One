package A1_MainActivityCompnent.Main

import A1_MainActivityCompnent.Ui.AppNavHost
import A1_MainActivityCompnent.Ui.ConnexionCard
import A1_MainActivityCompnent.Ui.NavigationBarWithFab
import A1_MainActivityCompnent.Ui.NavigationItems
import A1_MainActivityCompnent.Ui.Screen
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
    val startUpViewModel = appViewModels.startUpNewArticlesViewModels
    val uiState by startUpViewModel.uiState.collectAsState()
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
            // Main Navigation Content with Overlay
            Box(modifier = Modifier.weight(1f)) {
                AppNavHost(
                    appViewModels = appViewModels,
                    navController = navController,
                    onToggleNavBar = { isNavBarVisible = !isNavBarVisible },
                    isFabVisible = isFabVisible,
                    onClickDonne = { isFabVisible = false

                    },
                    onClickToDisplayeConexionWifi = { isDisplayeConexionWifiVisible = !isDisplayeConexionWifiVisible },
                )

                // Overlay when not host phone
                if (!isHostPhone) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(enabled = false) { }
                    ) {
                    }
                }
            }
        }

        // Bottom Navigation Bar
        AnimatedVisibility(
            visible = isNavBarVisible,
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
                onToggleFabVisibility = { isFabVisible = !isFabVisible
                    isDisplayeConexionWifiVisible=false}
            )
        }

        // Loading Indicator
        if (uiState.isLoading) {
            CircularProgressIndicator(
                progress = {
                    uiState.loadingProgress
                },
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
        }
    }
}
