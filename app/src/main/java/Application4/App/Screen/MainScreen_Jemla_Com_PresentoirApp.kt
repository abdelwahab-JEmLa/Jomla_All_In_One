package Application4.App.Screen

import Application4.App.Fragment.ID1.Fragment.Compact_Presentoire_App_Produits_FragID4
import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import Application4.App.Main.A.Navigation.AppNavHost_NewProtoPattern
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Main.A.Navigation.Component.NavigationBarWithFab_NewProto
import Application4.App.Main.A.Navigation.Component.NavigationItems
import Application4.App.Modules.Wi.Module.ConnexionCardHost_App4
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen_NewProtoPattern(
    modifier: Modifier = Modifier,
    fragmentNavigationHandler: FragmentNavigationHandler_NewProto = koinInject(),
    viewModelNewProtoPatterns: ViewModel_NewProtoPatterns = koinViewModel(),
) {
    val wifiState by viewModelNewProtoPatterns.wifiState.collectAsState()

    // currentRoute comes from FragmentNavigationHandler's state, not navController directly
    val currentFragment by fragmentNavigationHandler.currentFragment.collectAsState()
    val currentRoute = currentFragment?.route

    var isFabVisible by rememberSaveable { mutableStateOf(true) }

    if (M18CentralParametresOfAllApps.get_Default().no_loadKoin_CrachComposReglement) {
        Compact_Presentoire_App_Produits_FragID4(viewModelNewProtoPatterns = viewModelNewProtoPatterns)
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                NavigationBarWithFab_NewProto(
                    items = NavigationItems.getItems(isAdmin = true),
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        fragmentNavigationHandler.navigateTo(route)
                    },
                    modifier = modifier,
                    isFabVisible = true,
                    onToggleFabVisibility = {},
                    showWarningState = false,
                    onClickImageToShowControles = {},
                )
            }
        ) {innerPadding ->
            Column() {
                (!wifiState.isConnected).ifTrue {
                    ConnexionCardHost_App4(vm = viewModelNewProtoPatterns)
                }
                AppNavHost_NewProtoPattern(
                    modifier = Modifier.fillMaxSize(),
                    viewModelNewProtoPatterns = viewModelNewProtoPatterns,
                )
            }
        }
    }
}
