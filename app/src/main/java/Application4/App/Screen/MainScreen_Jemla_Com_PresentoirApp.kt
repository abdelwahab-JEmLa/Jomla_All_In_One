package Application4.App.Screen

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Main.A.Navigation.AppNavHost_NewProtoPattern
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Main.A.Navigation.Component.NavigationBarWithFab_NewProto
import Application4.App.Main.A.Navigation.Component.NavigationItems
import Application4.App.Main.A.Navigation.Component.Screen_NewProtoPattern
import Application4.App.Modules.Wi.Module.ConnexionCardHost_App4
import Application4.App.Modules.Wi.Module.ProductDisplayController_NewProto
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen_NewProtoPattern(
    modifier: Modifier = Modifier,
    viewModelNewProtoPatterns_passed: A_ViewModel_NewProtoPatterns,
    fragmentNavigationHandler_passed: FragmentNavigationHandler_NewProto
) {

    val wifiState = viewModelNewProtoPatterns_passed.wifiState.collectAsState()

    val navController = rememberNavController()
    LaunchedEffect(navController) {
        fragmentNavigationHandler_passed.setNavController(navController)
        fragmentNavigationHandler_passed.setStartupScreen_NewProtoPattern(
            Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4
        )
    }

    val currentFragment by fragmentNavigationHandler_passed.currentFragment.collectAsState()
    val currentRoute = currentFragment?.route

    MainScaffold(
        modifier = modifier,
        wifiState = wifiState.value,
        currentRoute = currentRoute,
        navController = navController,
        fragmentNavigationHandler_passed = fragmentNavigationHandler_passed,
        viewModelNewProtoPatterns_passed = viewModelNewProtoPatterns_passed,
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun MainScaffold(
    modifier: Modifier,
    wifiState: ProductDisplayController_NewProto,
    currentRoute: String?,
    navController: NavHostController,
    fragmentNavigationHandler_passed: FragmentNavigationHandler_NewProto,
    viewModelNewProtoPatterns_passed: A_ViewModel_NewProtoPatterns,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBarWithFab_NewProto(
                viewModelNewProtoPatterns_passed=viewModelNewProtoPatterns_passed,
                items = NavigationItems.getItems(isAdmin = true),
                currentRoute = currentRoute,
                onNavigate = { route -> fragmentNavigationHandler_passed.navigateTo(route) },
                modifier = modifier,
                isFabVisible = true,
                onToggleFabVisibility = {},
                showWarningState = false,
                onClickImageToShowControles = {},
            )
        }
    ) { innerPadding ->
        Column {
            (!wifiState.isConnected).ifTrue {
                ConnexionCardHost_App4(vm = viewModelNewProtoPatterns_passed)
            }
            AppNavHost_NewProtoPattern(
                fragmentNavigationHandler=fragmentNavigationHandler_passed,
                modifier = Modifier.fillMaxSize(),
                viewModelNewProtoPatterns_passed = viewModelNewProtoPatterns_passed,
                navController = navController,
                innerPadding = innerPadding,
            )
        }
    }
}
