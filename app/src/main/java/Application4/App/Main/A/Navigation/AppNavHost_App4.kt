package Application4.App.Main.A.Navigation

import A_Main.Shared.Init.A_LoadingApp4_Init_Screen
import Application4.App.Fragment.ID1.Fragment.A_Compact_Presentoire_App_Produits_App4
import Application4.App.Fragment.ID2.Fragment.Screen_Panie_FragID2
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Main.A.Navigation.Component.Screen_NewProtoPattern
import Application4.App.Modules.Wi.Module.WifiTransferDatas_ControllerApp
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Modules.Base.AppDatabase
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_MapClients_A2FragID_1
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.centralDataBasesModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.classesHandlersModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.composRepositorysModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.factoryDataBaseProtoAvantJuin3Module
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.viewModelModule
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext
import java.util.concurrent.atomic.AtomicBoolean

private val heavyModules = listOf(
    viewModelModule,
    centralDataBasesModule,
    composRepositorysModule,
    factoryDataBaseProtoAvantJuin3Module,
    classesHandlersModule,
)

private val heavyModulesLoaded = AtomicBoolean(false)

@SuppressLint("RememberReturnType")
@Composable
fun  AppNavHost_App4(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    innerPadding: PaddingValues = PaddingValues(),
    fragmentNavigationHandler: FragmentNavigationHandler_NewProto,
    wifiTransferDatas_ControllerApp: WifiTransferDatas_ControllerApp,
    appDatabase: AppDatabase,
    on_update_M13TarificationInfos_par_ecriture: (M13TarificationInfos) -> Unit,
    affiche_buttons_lien_unite_couleur_au_couleut_parent: Boolean = false,
    affiche_ProduitDataBaseEdites_ComposableViews: Boolean = false,
    on_pour_update_affiche_ProduitDataBaseEdites_ComposableViews: (Boolean) -> Unit = {}
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        fragmentNavigationHandler.updateCurrentFragmentByRoute(currentRoute)
    }

    var list_M13TarificationInfos by remember {
        mutableStateOf<MutableList<M13TarificationInfos>>(mutableListOf())
    }
    LaunchedEffect(Unit) {
        list_M13TarificationInfos = appDatabase.dao_M13TarificationInfos().getAll()
    }

    val heavyReady = remember { mutableStateOf(heavyModulesLoaded.get()) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(route = Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route) {
                LaunchedEffect(Unit) {
                    if (!heavyModulesLoaded.get()) {
                        runCatching { GlobalContext.get().loadModules(heavyModules) }
                            .onSuccess {
                                heavyModulesLoaded.set(true)
                                heavyReady.value = true
                            }
                    } else {
                        heavyReady.value = true
                    }
                }

                if (heavyReady.value) {
                    var initDone by rememberSaveable { mutableStateOf(false) }

                    if (!initDone) {
                        A_LoadingApp4_Init_Screen(
                            innerPadding = PaddingValues(),
                            onInitDone = { initDone = true },
                            appDatabase = appDatabase
                        )
                    } else {
                        A_Compact_Presentoire_App_Produits_App4(
                            wifiTransferDatas_ControllerApp = wifiTransferDatas_ControllerApp,
                            appDatabase = appDatabase,
                            fragmentNavigationHandler = fragmentNavigationHandler,
                            affiche_buttons_lien_unite_couleur_au_couleut_parent = affiche_buttons_lien_unite_couleur_au_couleut_parent,
                            affiche_ProduitDataBaseEdites_ComposableViews = affiche_ProduitDataBaseEdites_ComposableViews,
                            on_pour_update_affiche_ProduitDataBaseEdites_ComposableViews = on_pour_update_affiche_ProduitDataBaseEdites_ComposableViews,
                            on_update_M13TarificationInfos_par_ecriture = { updated ->
                                on_update_M13TarificationInfos_par_ecriture(updated)
                                list_M13TarificationInfos = list_M13TarificationInfos
                                    .toMutableList()
                                    .apply {
                                        val index = indexOfFirst { it.keyID == updated.keyID }
                                        if (index >= 0) set(index, updated) else add(updated)
                                    }
                            },
                        )
                    }
                }
            }

            composable(route = Screen_NewProtoPattern.Panier.route) {
                LaunchedEffect(Unit) {
                    if (!heavyModulesLoaded.get()) {
                        runCatching { GlobalContext.get().loadModules(heavyModules) }
                            .onSuccess {
                                heavyModulesLoaded.set(true)
                                heavyReady.value = true
                            }
                    } else {
                        heavyReady.value = true
                    }
                }
                if (heavyReady.value) {
                    var initDone by rememberSaveable { mutableStateOf(false) }

                    if (!initDone) {
                        A_LoadingApp4_Init_Screen(
                            innerPadding = PaddingValues(),
                            onInitDone = { initDone = true },
                            appDatabase = koinInject()
                        )
                    } else {
                        Screen_Panie_FragID2(
                            fragmentNavigationHandler = fragmentNavigationHandler,
                            wifiTransferDatas_ControllerApp = wifiTransferDatas_ControllerApp,
                            list_M13TarificationInfos = list_M13TarificationInfos
                        )
                    }
                }
            }

            composable(route = Screen_NewProtoPattern.A_Clients_LocationGps.route) {
                LaunchedEffect(Unit) {
                    if (!heavyModulesLoaded.get()) {
                        runCatching { GlobalContext.get().loadModules(heavyModules) }
                            .onSuccess {
                                heavyModulesLoaded.set(true)
                                heavyReady.value = true
                            }
                    } else {
                        heavyReady.value = true
                    }
                }
                if (heavyReady.value) {
                    var initDone by rememberSaveable { mutableStateOf(false) }

                    if (!initDone) {
                        A_LoadingApp4_Init_Screen(
                            innerPadding = PaddingValues(),
                            onInitDone = { initDone = true },
                            appDatabase = koinInject()
                        )
                    } else {
                        A_MapClients_A2FragID_1(
                            wifiTransferDatas_ControllerApp = wifiTransferDatas_ControllerApp,
                            onUpdateLongAppSetting = {
                                fragmentNavigationHandler.navigateTo(
                                    Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route
                                )
                            },
                            onClear = {},
                            fragmentNavigationHandler_NewProto = fragmentNavigationHandler,
                        )
                    }
                }
            }
        }
    }
}
