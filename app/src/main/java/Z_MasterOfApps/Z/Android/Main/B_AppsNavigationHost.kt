package Z_MasterOfApps.Z.Android.Main

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_1.id4_DeplaceProduitsVerGrossist.A_id4_DeplaceProduitsVerGrossist
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_2.id1_GerantDefinirePosition.A_id1_GerantDefinirePosition
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_3.id2_TravaillieurListProduitAchercheChezLeGrossist.A_Id2_TravaillieurListProduitAchercheChezLeGrossist
import Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps.A_id1_ClientsLocationGps
import Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.A_StartupScreen
import Z_MasterOfApps.Z.Android.Main.Utils.NavigationBarWithFab
import Z_MasterOfApps.Z.Android.Packages._1.GerantAfficheurGrossistCommend.App.NH_4.id3_AfficheurDesProduitsPourLeColecteur.A_id3_AfficheurDesProduitsPourLeColecteur
import Z_MasterOfApps.Z.Android.Packages._1.GerantAfficheurGrossistCommend.App.NH_5.ID5_VerificationProduitAcGrossist.A_ID5_VerificationProduitAcGrossist
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Moving
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhonelinkRing
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigationHost(
    viewModelInitApp: ViewModelInitApp,
    modifier: Modifier,
) {
    val navController = rememberNavController()
    val items = NavigationItems.items
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                Box(modifier = modifier.fillMaxSize()) {
                    if (viewModelInitApp.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        NavHost(
                            navController = navController,
                            startDestination = Screens.Startup.route,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable(Screens.Startup.route) {
                                A_StartupScreen(viewModelInitApp = viewModelInitApp)
                            }
                            composable(Screens.NavHost_1.route) {
                                A_id4_DeplaceProduitsVerGrossist(viewModelInitApp = viewModelInitApp)
                            }
                            composable(Screens.NavHost_2.route) {
                                A_id1_GerantDefinirePosition(viewModelInitApp = viewModelInitApp)
                            }
                             composable(Screens.NavHost_3.route) {
                                A_Id2_TravaillieurListProduitAchercheChezLeGrossist(viewModelInitApp)
                            }
                            composable(Screens.NavHost_4.route) {
                                A_ID5_VerificationProduitAcGrossist(viewModelInitApp)
                            }
                            composable(Screens.NavHost_5.route) {
                                A_id3_AfficheurDesProduitsPourLeColecteur(viewModelInitApp = viewModelInitApp)
                            }
                            composable(Screens.NavHostA2_1.route) {
                                A_id1_ClientsLocationGps(viewModel = viewModelInitApp)
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = true,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            NavigationBarWithFab(
                items = items,
                viewModelInitApp = viewModelInitApp,
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

object NavigationItems {
    val items @SuppressLint("SuspiciousIndentation") @Composable get() = buildList {
        val viewModelInitApp: ViewModelInitApp = viewModel()
        val cLeTelephoneDuGerant = viewModelInitApp._paramatersAppsViewModelModel.cLeTelephoneDuGerant == true

            add(Screens.Startup)
        if (cLeTelephoneDuGerant) { add(Screens.NavHost_1) }
            add(Screens.NavHost_2)
            add(Screens.NavHost_3)
            add(Screens.NavHost_5)
            add(Screens.NavHost_4)
        if (cLeTelephoneDuGerant) { add(Screens.NavHostA2_1) }
    }
}

@Preview
@Composable
private fun Preview_Fragment() {
    val viewModelInitApp: ViewModelInitApp = viewModel()
    Box(modifier = Modifier.fillMaxSize()) {
        if (viewModelInitApp.isLoading) {
            // Loading indicator centered in the box
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            A_id1_GerantDefinirePosition(viewModelInitApp = viewModelInitApp)
        }
    }
}

object Screens {
    val Startup = StartupIcon_Start
    val NavHost_1 = MainScreenDataObject_F4
    val NavHost_2 = MainScreenDataObject_F1
    val NavHost_3 = MainScreenDataObject_F2
    val NavHost_5 = MainScreenDataObject_F3
    val NavHost_4 = ID4Icon_Done
    val NavHostA2_1 = ID1Icon_Person
}


data object StartupIcon_Start : Screen(
    icon = Icons.Default.Start,
    color = Color(0xFFFF5722),
    route = "StartupIcon_Start", title = "StartupIcon_Start"
)

data object MainScreenDataObject_F1 : Screen(
    icon = Icons.Default.Tab,
    route = "fragment_main_screen_1",
    title = "Serveur Grossist",
    color = Color(0xFFFF5722)
)

data object MainScreenDataObject_F2 : Screen(
    icon = Icons.Default.PhonelinkRing,
    route = "main_screen_f2",
    title = "Phone Client Grossist",
    color = Color(0xFFFFEB3B)
)

data object MainScreenDataObject_F3 : Screen(
    route = "main_screen_f3",
    icon = Icons.Default.Person,
    title = "Phone Client Client",
    color = Color(0xFFFF5722)
)

data object MainScreenDataObject_F4 : Screen(
    route = "main_screen_f4",
    icon = Icons.Default.Moving,
    title = "main_screen_f4",
    color = Color(0xFF3F51B5)
)

data object ID4Icon_Done : Screen(
    icon = Icons.Default.Done,
    route = "A_ID5_VerificationProduitAcGrossist", title = "A_ID5_VerificationProduitAcGrossist",
    color = Color(0xFFFF5892)
)

data object ID1Icon_Person : Screen(
    icon = Icons.Default.Person,
    route = "Id_App2Fragment1", title = "A_id1_ClientsLocationGps",
    color = Color(0xFF03A9F4)
)
abstract class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val color: Color
)


