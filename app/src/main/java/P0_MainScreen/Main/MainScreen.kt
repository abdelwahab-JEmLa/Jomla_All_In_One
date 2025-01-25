package P0_MainScreen.Main

import P0_MainScreen.Ui.Main.AppNavHost.AppNavHost
import P0_MainScreen.Ui.Main.AppNavHost.NavigationBarWithFab
import P0_MainScreen.Ui.Main.AppNavHost.NavigationItems
import P0_MainScreen.Ui.Main.AppNavHost.Screen
import Views._2LocationGpsClients.App.ScreensApp2
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.clientjetpack.AppViewModels

@Composable
fun MainScreen(appViewModels: AppViewModels, viewModelInitApp: ViewModelInitApp = viewModel()) {
    val navController = rememberNavController()
    val uiState by appViewModels.headViewModel.uiState.collectAsState()
    val currentClientId = uiState.appSettingsSaverModel.find { it.name == "clientBuyerNowId" }?.valueLong ?: 0
    var isNavBarVisible by remember { mutableStateOf(true) }

    // Gestion de la navigation forcée
    LaunchedEffect(currentClientId) {
        if (currentClientId == 0L) {
            isNavBarVisible = false
            navController.navigate(ScreensApp2.Fragment1Screen.route)

            {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                AppNavHost(
                    appViewModels = appViewModels,
                    viewModelInitApp = viewModelInitApp,
                    navController = navController,
                    onToggleNavBar = { isNavBarVisible = !isNavBarVisible },
                    isFabVisible = false,
                    onClickDonne = {},
                    onClickToDisplayeConexionWifi = {},
                    onToggleLockHost = {}
                )
            }

            // Barre de navigation conditionnelle
            AnimatedVisibility(visible = isNavBarVisible && currentClientId != 0L) {
                NavigationBarWithFab(
                    items = NavigationItems.getItems().filter { it != Screen.ToggleFab },
                    currentRoute = navController.currentBackStackEntry?.destination?.route,
                    onNavigate = { route ->
                        if (currentClientId != 0L) navController.navigate(route)
                    },
                    isFabVisible = false,
                    onToggleFabVisibility = {}
                )
            }
        }
    }
}

