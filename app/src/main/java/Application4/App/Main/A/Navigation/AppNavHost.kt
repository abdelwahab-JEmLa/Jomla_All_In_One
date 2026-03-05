package Application4.App.Main.A.Navigation

import Application4.App.Fragment.Compact_Presentoire_App_Produits_FragID4
import Application4.App.Main.A.Navigation.Component.Screen_NewProtoPattern
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_MapClients_A2FragID_1
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.koin.compose.koinInject

@Composable
fun AppNavHost_NewProtoPattern(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    fragmentNavigationHandler: FragmentNavigationHandler = koinInject(),
) {
    Surface(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(route = Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route) {
                Compact_Presentoire_App_Produits_FragID4()
            }

            composable(route = Screen_NewProtoPattern.A_Clients_LocationGps.route) {
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
