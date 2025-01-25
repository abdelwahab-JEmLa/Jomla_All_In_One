package Views._2LocationGpsClients.App

import P0_MainScreen.Ui.Main.AppNavHost.Screen
import Views._2LocationGpsClients.App.MainApp.A_ClientsLocationGps
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
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
    navController: NavHostController
) {
    composable(Screen.A_ClientsLocationGps.route) {
        A_ClientsLocationGps(
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
            }
        )
    }
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
    // Preview version without real ViewModel
    A_ClientsLocationGps(
        modifier = Modifier.fillMaxSize()
    , viewModelInitApp, onUpdateLongAppSetting = {}
    )
}

object ScreensApp2 {
    val A_ClientsLocationGps = Screen.A_ClientsLocationGps
}


