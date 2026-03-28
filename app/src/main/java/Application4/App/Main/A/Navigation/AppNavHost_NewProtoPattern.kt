package Application4.App.Main.A.Navigation

import Application4.App.A.Start.Init.Proto.A_LoadingApp4_Init_Screen
import Application4.App.Fragment.ID1.Fragment.A_Compact_Presentoire_App_Produits_App4
import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID2.Fragment.Screen_Panie_FragID2
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Main.A.Navigation.Component.Screen_NewProtoPattern
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
fun AppNavHost_NewProtoPattern(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    innerPadding: PaddingValues = PaddingValues(),
    fragmentNavigationHandler: FragmentNavigationHandler_NewProto = koinInject(),
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        fragmentNavigationHandler.updateCurrentFragmentByRoute(currentRoute)
    }

    // State flag: true only after heavy modules are confirmed loaded in Koin
    val heavyReady = androidx.compose.runtime.remember { mutableStateOf(heavyModulesLoaded.get()) }

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
                    if (heavyModulesLoaded.get()) {
                        runCatching { GlobalContext.get().unloadModules(heavyModules) }
                            .onSuccess {
                                heavyModulesLoaded.set(false)
                                heavyReady.value = false
                            }
                    }
                }

                // FIX(TODO-1): Gate between loading screen and A_Compact here in the nav graph.
                // A_LoadingApp4_Init_Screen no longer embeds A_Compact directly — it fires
                // onInitDone() when done, and we switch to A_Compact inside this composable.
                var initDone by rememberSaveable { mutableStateOf(false) }
                if (!initDone) {
                    A_LoadingApp4_Init_Screen(
                        innerPadding = innerPadding,
                        onInitDone = { initDone = true },
                    )
                } else {
                    A_Compact_Presentoire_App_Produits_App4()
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
                    Screen_Panie_FragID2()
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
                    A_MapClients_A2FragID_1(
                        onUpdateLongAppSetting = {
                            fragmentNavigationHandler.navigateTo(
                                Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route
                            )
                        },
                        onClear = {}
                    )
                }
            }
        }
    }
}
