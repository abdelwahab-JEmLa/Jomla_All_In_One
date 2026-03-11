package Application4.App.Main.A.Navigation

import Application4.App.Fragment.Compact_Presentoire_App_Produits_FragID4
import Application4.App.Main.A.Navigation.Component.Screen_NewProtoPattern
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_MapClients_A2FragID_1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Ancien_PresenterApp_FragID5
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.centralDataBasesModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.classesHandlersModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.composRepositorysModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.factoryDataBaseProtoAvantJuin3Module
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.viewModelModule
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext

/** Modules that are only needed for the full "vendeur host" flow.
 *  They are loaded on demand when navigating to [Screen_NewProtoPattern.A_Clients_LocationGps]
 *  and unloaded when returning to [Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4],
 *  keeping memory lean while the lightweight presenter screen is active.
 */
private val heavyModules = listOf(
    viewModelModule,
    centralDataBasesModule,
    composRepositorysModule,
    factoryDataBaseProtoAvantJuin3Module,
    classesHandlersModule,
)

@Composable
fun AppNavHost_NewProtoPattern(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    fragmentNavigationHandler: FragmentNavigationHandler = koinInject(),
) {
    @Composable
    fun AnimatedContentScope.load_heavyModules() {
        remember {
            runCatching { GlobalContext.get().loadModules(heavyModules) }
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(route = Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route) {
                remember {
                    runCatching { GlobalContext.get().unloadModules(heavyModules) }
                }

                Compact_Presentoire_App_Produits_FragID4()
            }

            composable(route = Screen_NewProtoPattern.A_Clients_LocationGps.route) {
                load_heavyModules()
                A_MapClients_A2FragID_1(
                    onUpdateLongAppSetting = {
                        fragmentNavigationHandler.navigateTo(
                            Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route
                        )
                    },
                    onClear = {}
                )
            }

            composable(route = Screen_NewProtoPattern.Ancien_PresenterApp_FragID5.route) {
                load_heavyModules()
                Ancien_PresenterApp_FragID5()
            }
        }
    }
}
