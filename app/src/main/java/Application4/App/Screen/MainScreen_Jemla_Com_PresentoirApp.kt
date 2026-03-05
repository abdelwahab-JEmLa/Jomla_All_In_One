package Application4.App.Screen

import Application4.App.Main.A.Navigation.AppNavHost_NewProtoPattern
import Application4.App.Main.A.Navigation.Component.NavigationBarWithFab_NPP
import Application4.App.Main.A.Navigation.Component.NavigationItems
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen_NewProtoPattern(
    modifier: Modifier = Modifier,
    fragmentNavigationHandler: FragmentNavigationHandler = koinInject(),
) {
    val navController = rememberNavController()

    // Give FragmentNavigationHandler ownership of the NavController
    fragmentNavigationHandler.setNavController(navController)

    // currentRoute comes from FragmentNavigationHandler's state, not navController directly
    val currentFragment by fragmentNavigationHandler.currentFragment.collectAsState()
    val currentRoute = currentFragment?.route

    var isFabVisible by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBarWithFab_NPP(
                items = NavigationItems.getItems(isAdmin = true),
                currentRoute = currentRoute,
                onNavigate = { route ->
                    fragmentNavigationHandler.navigateTo(route)
                },
            )
        }
    ) { innerPadding ->
        AppNavHost_NewProtoPattern(
            navController = navController,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
