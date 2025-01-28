package P0_MainScreen.Ui.Main.AppNavHost

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.Packages._2._2LocationGpsClients.App.NH_1.id1_ClientsLocationGps.A_id1_ClientsLocationGps
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.app2(
    viewModelInitApp: ViewModelInitApp,
    clientEnCourDeVent: Long,
    navController: NavHostController,
    xmlResources: List<Pair<String, Int>>
) {
    composable(Screen.A_ClientsLocationGps.route) {
        A_id1_ClientsLocationGps(
            viewModel = viewModelInitApp,
            clientEnCourDeVent = clientEnCourDeVent,
            onUpdateLongAppSetting = {
                navController.navigate(Screen.EditDatabaseWithCreateNewArticles.route) {
                    // Pop the current fragment off the back stack
                    popUpTo(Screen.A_ClientsLocationGps.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            } ,
            xmlResources=xmlResources
        )
    }
}

object ScreensApp2 {
    val A_ClientsLocationGps = Screen.A_ClientsLocationGps
}

@Preview
@Composable
private fun PreviewApp2_F1() {
    val viewModelInitApp: ViewModelInitApp  = viewModel()
    if (viewModelInitApp.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                progress = {
                    viewModelInitApp.loadingProgress
                },
                modifier = Modifier.align(Alignment.Center),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
        }
        return
    }
 /*   // Preview version without real ViewModel
    A_ClientsLocationGps(
        modifier = Modifier.fillMaxSize()
    , viewModelInitApp, onUpdateLongAppSetting = {}
    )    */
}



