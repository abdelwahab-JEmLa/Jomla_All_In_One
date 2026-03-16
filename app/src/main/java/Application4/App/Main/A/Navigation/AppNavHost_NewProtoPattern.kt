package Application4.App.Main.A.Navigation

import Application4.App.Fragment.ID1.Fragment.Compact_Presentoire_App_Produits_FragID4
import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
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
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    fragmentNavigationHandler: FragmentNavigationHandler_NewProto = koinInject(),
    viewModelNewProtoPatterns: ViewModel_NewProtoPatterns,
) {
    // ✅ FIX: Le LaunchedEffect(Unit) qui enregistrait setOnLeaveCompactPresentoireCallback
    // ici a été SUPPRIMÉ. Il créait un double enregistrement avec MainScreen et risquait
    // d'écraser le callback déjà configuré depuis MainScreen_NewProtoPattern.
    // L'enregistrement unique des callbacks est fait dans MainScreen via ses propres LaunchedEffect.

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        fragmentNavigationHandler.updateCurrentFragmentByRoute(currentRoute)
    }

    @Composable
    fun AnimatedContentScope.load_heavyModules() {
        remember {
            if (!heavyModulesLoaded.get()) {
                runCatching { GlobalContext.get().loadModules(heavyModules) }
                    .onSuccess { heavyModulesLoaded.set(true) }
            }
        }
    }

    @Composable
    fun AnimatedContentScope.unload_heavyModules() {
        remember {
            if (heavyModulesLoaded.get()) {
                runCatching { GlobalContext.get().unloadModules(heavyModules) }
                    .onSuccess { heavyModulesLoaded.set(false) }
            }
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(route = Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route) {
                unload_heavyModules()
                Compact_Presentoire_App_Produits_FragID4(viewModelNewProtoPatterns = viewModelNewProtoPatterns)
            }

            composable(route = Screen_NewProtoPattern.Panier.route) {
                load_heavyModules()
                Screen_Panie_FragID2()
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
        }
    }
}
