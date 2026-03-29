package Application4.App.Screen

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Main.A.Navigation.AppNavHost_NewProtoPattern
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Main.A.Navigation.Component.NavigationBarWithFab_NewProto
import Application4.App.Main.A.Navigation.Component.NavigationItems
import Application4.App.Main.A.Navigation.Component.Screen_NewProtoPattern
import Application4.App.Modules.Wi.Module.ConnexionCardHost_App4
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen_NewProtoPattern(
    modifier: Modifier = Modifier,
    fragmentNavigationHandler: FragmentNavigationHandler_NewProto = koinInject(),
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns = koinViewModel(),
) {
    val wifiState by viewModelNewProtoPatterns.wifiState.collectAsState()

    val navController = rememberNavController()
    LaunchedEffect(navController) {
        fragmentNavigationHandler.setNavController(navController)
        fragmentNavigationHandler.setStartupScreen_NewProtoPattern(
            Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4
        )
    }

    val currentFragment by fragmentNavigationHandler.currentFragment.collectAsState()
    val currentRoute = currentFragment?.route

    MainScaffold(
        modifier = modifier,
        wifiState = wifiState,
        currentRoute = currentRoute,
        navController = navController,
        fragmentNavigationHandler = fragmentNavigationHandler,
        viewModelNewProtoPatterns = viewModelNewProtoPatterns,
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun MainScaffold(
    modifier: Modifier,
    wifiState: Any,
    currentRoute: String?,
    navController: androidx.navigation.NavHostController,
    fragmentNavigationHandler: FragmentNavigationHandler_NewProto,
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBarWithFab_NewProto(
                items = NavigationItems.getItems(isAdmin = true),
                currentRoute = currentRoute,
                onNavigate = { route -> fragmentNavigationHandler.navigateTo(route) },
                modifier = modifier,
                isFabVisible = true,
                onToggleFabVisibility = {},
                showWarningState = false,
                onClickImageToShowControles = {},
            )
        }
    ) { innerPadding ->
        Column {
            (!(wifiState as Application4.App.Modules.Wi.Module.ProductDisplayController_NewProto).isConnected).ifTrue {
                ConnexionCardHost_App4(vm = viewModelNewProtoPatterns)
            }
            AppNavHost_NewProtoPattern(
                modifier = Modifier.fillMaxSize(),
                viewModelNewProtoPatterns = viewModelNewProtoPatterns,
                navController = navController,
                innerPadding = innerPadding,
            )
        }
    }
}
