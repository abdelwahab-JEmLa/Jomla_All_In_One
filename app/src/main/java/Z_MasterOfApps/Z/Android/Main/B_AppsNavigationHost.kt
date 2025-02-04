package Z_MasterOfApps.Z.Android.Main

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_1.id4_DeplaceProduitsVerGrossist.A_id4_DeplaceProduitsVerGrossist
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_2.id1_GerantDefinirePosition.A_id1_GerantDefinirePosition
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_3.id2_TravaillieurListProduitAchercheChezLeGrossist.A_Id2_TravaillieurListProduitAchercheChezLeGrossist
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_4.id3_AfficheurDesProduitsPourLeColecteur.A_id3_AfficheurDesProduitsPourLeColecteur
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_5.ID5_VerificationProduitAcGrossist.A_ID5_VerificationProduitAcGrossist
import Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps.A_id1_ClientsLocationGps
import Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.A_StartupScreen
import Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.NavigationBarWithFab
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    val isManagerPhone = viewModelInitApp._paramatersAppsViewModelModel.cLeTelephoneDuGerant ?: false
    val items = remember(isManagerPhone) { NavigationItems.getItems(isManagerPhone) }

    val startDestination = InfosDatas_FramgmentId4.route
    val currentRoute = navController.currentBackStackEntryAsState()
        .value?.destination?.route

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
                            startDestination = startDestination,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable(InfosDatas_FramgmentId4.route) {
                                A_id4_DeplaceProduitsVerGrossist(viewModelInitApp = viewModelInitApp)
                            }
                            composable(InfosDatas_FramgmentId1.route) {
                                A_id1_GerantDefinirePosition(viewModel = viewModelInitApp)
                            }
                            composable(InfosDatas_FramgmentId5.route) {
                                A_ID5_VerificationProduitAcGrossist(viewModel = viewModelInitApp)
                            }
                            composable(Screens.NavHost_3.route) {
                                A_Id2_TravaillieurListProduitAchercheChezLeGrossist(viewModelInitApp = viewModelInitApp)
                            }
                            composable(Screens.NavHost_5.route) {
                                A_id3_AfficheurDesProduitsPourLeColecteur(viewModelInitApp = viewModelInitApp)
                            }
                            composable(Screens.NavHostA2_1.route) {
                                A_id1_ClientsLocationGps(viewModel = viewModelInitApp)
                            }
                            composable(Screens.Startup_0.route) {
                                A_StartupScreen(viewModelInitApp, { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                })
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
object Screens {
    val Startup_0 = StartupIcon_Start
    val NavHost_3 = MainScreenDataObject_F2
    val NavHost_5 = MainScreenDataObject_F3
    val NavHostA2_1 = ID1Icon_Person
}
object NavigationItems {
    fun getItems(isManagerPhone: Boolean) = buildList {
        add(Screens.Startup_0)

        //Manageur_Fragments
        if (isManagerPhone) { add(InfosDatas_FramgmentId4) }
        add(InfosDatas_FramgmentId1)
        add(InfosDatas_FramgmentId5)

        //Clients_Fragments
        add(Screens.NavHost_3)
        add(Screens.NavHost_5)

        //MapApp_Fragments
        if (isManagerPhone) { add(Screens.NavHostA2_1) }
    }
}

data object InfosDatas_FramgmentId1 : Screen(
    id =1,
    icon = Icons.Default.LocationOn,
    route = "fragment_main_screen_1",
    titleArab = "محدد اماكن المنتجات عند الجمال",
    color = Color(0xFFFF5722)
)

data object MainScreenDataObject_F2 : Screen(
    id =2,
    icon = Icons.Default.Visibility,
    route = "main_screen_f2",
    titleArab = "مظهر اماكن المنتجات عند الجمال",
    color = Color(0xFFA48E39)
)

data object MainScreenDataObject_F3 : Screen(
    id =3,
    route ="مظهر الاماكن لمقسم المنتجات على الزبائن",
    icon = Icons.Default.Groups,
    titleArab = "مظهر الاماكن لمقسم المنتجات على الزبائن",
    color = Color(0xFF9C27B0)
)

data object InfosDatas_FramgmentId4 : Screen(
    id =4,
    route = "main_screen_f4",
    icon = Icons.Default.LocalShipping, // Changed from Moving to LocalShipping for product distribution
    titleArab = "مقسم المنتجات الى الجمالين",
    color = Color(0xFF3F51B5)
)

data object InfosDatas_FramgmentId5 : Screen(
    id =5,
    icon = Icons.AutoMirrored.Filled.FactCheck, // Changed from Done to FactCheck for invoice verification
    route = "A_ID5_VerificationProduitAcGrossist",
    titleArab = "التاكد من فواتير مع المنتجات عند الجمال",
    color = Color(0xFFFF5892)
)

data object ID1Icon_Person : Screen(
    id =6,
    icon = Icons.Default.PinDrop, // Changed from Person to PinDrop for GPS location
    route = "Id_App2Fragment1",
    titleArab = "محدد اماكن الزبائن GPS",
    color = Color(0xFFFF9800)

)
data object StartupIcon_Start : Screen(
    id =7,
    icon = Icons.Default.Home,
    color = Color(0xFF3A3533),
    route = "StartupIcon_Start",
    titleArab = "المدخل الرئيسي"
)

abstract class Screen(
    val id: Long,
    val route: String,
    val icon: ImageVector,
    val titleArab: String,
    val color: Color
)


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
            A_id1_GerantDefinirePosition(viewModel = viewModelInitApp)
        }
    }
}
