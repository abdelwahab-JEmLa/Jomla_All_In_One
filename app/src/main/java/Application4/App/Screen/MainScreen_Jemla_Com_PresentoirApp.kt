package Application4.App.Screen

import Application4.App.Main.A.Navigation.AppNavHost_App4
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Main.A.Navigation.Component.NavigationBarWithFab_NewProto
import Application4.App.Main.A.Navigation.Component.NavigationItems
import Application4.App.Main.A.Navigation.Component.Screen_NewProtoPattern
import Application4.App.Modules.Wi.Module.ConnexionCardHost_App4
import Application4.App.Modules.Wi.Module.ProductDisplayController_NewProto
import Application4.App.Modules.Wi.Module.WifiTransferDatas_ControllerApp
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Modules.Base.AppDatabase
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen_NewProtoPattern(
    modifier: Modifier = Modifier,
    appDatabase: AppDatabase,
    wifiTransferDatas_ControllerApp: WifiTransferDatas_ControllerApp,
    fragmentNavigationHandler_passed: FragmentNavigationHandler_NewProto,
    on_update_M13TarificationInfos_par_ecriture: (M13TarificationInfos) -> Unit,
    on_clear_wifi_classe_cache: () -> Unit,
    on_pour_update_affiche_buttons_lien_unite_couleur_au_couleut_parent: (Boolean) -> Unit = {},
) {
    val wifiState = wifiTransferDatas_ControllerApp.state.collectAsState()

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
        wifiTransferDatas_ControllerApp = wifiTransferDatas_ControllerApp,
        appDatabase = appDatabase,
        on_update_M13TarificationInfos_par_ecriture = on_update_M13TarificationInfos_par_ecriture,
        on_clear_wifi_classe_cache = on_clear_wifi_classe_cache,
        on_pour_update_affiche_buttons_lien_unite_couleur_au_couleut_parent = on_pour_update_affiche_buttons_lien_unite_couleur_au_couleut_parent
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
    wifiTransferDatas_ControllerApp: WifiTransferDatas_ControllerApp,
    appDatabase: AppDatabase,
    on_update_M13TarificationInfos_par_ecriture: (M13TarificationInfos) -> Unit,
    on_clear_wifi_classe_cache: () -> Unit,
    on_pour_update_affiche_buttons_lien_unite_couleur_au_couleut_parent: (Boolean) -> Unit,
) {
    var affiche_ProduitDataBaseEdites by remember { mutableStateOf(false) }
    var affiche_buttons_lien_unite_couleur_au_couleut_parent by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBarWithFab_NewProto(
                wifiTransferDatas_ControllerApp = wifiTransferDatas_ControllerApp,
                items = NavigationItems.getItems(isAdmin = true),
                currentRoute = currentRoute,
                onNavigate = { route -> fragmentNavigationHandler_passed.navigateTo(route) },
                modifier = modifier,
                isFabVisible = true,
                onToggleFabVisibility = {},
                showWarningState = false,
                fragmentNavigationHandler = fragmentNavigationHandler_passed,
                affiche_ProduitDataBaseEdites = affiche_ProduitDataBaseEdites,
                onToggleProduitDataBaseEdites = { newVal -> affiche_ProduitDataBaseEdites = newVal },
                on_pour_update_affiche_buttons_lien_unite_couleur_au_couleut_parent = { newVal ->
                    affiche_buttons_lien_unite_couleur_au_couleut_parent = newVal
                    on_pour_update_affiche_buttons_lien_unite_couleur_au_couleut_parent(newVal)
                }
            )
        }
    ) { innerPadding ->
        Column {
            (!wifiState.isConnected).ifTrue {
                ConnexionCardHost_App4(
                    wifiTransferDatas_ControllerApp = wifiTransferDatas_ControllerApp,
                    on_clear_wifi_classe_cache = on_clear_wifi_classe_cache,
                )
            }
            AppNavHost_App4(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                innerPadding = innerPadding,
                fragmentNavigationHandler = fragmentNavigationHandler_passed,
                wifiTransferDatas_ControllerApp = wifiTransferDatas_ControllerApp,
                appDatabase = appDatabase,
                on_update_M13TarificationInfos_par_ecriture = on_update_M13TarificationInfos_par_ecriture,
                affiche_buttons_lien_unite_couleur_au_couleut_parent = affiche_buttons_lien_unite_couleur_au_couleut_parent
            )
        }
    }
}
