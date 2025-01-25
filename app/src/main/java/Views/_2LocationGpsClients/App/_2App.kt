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
import androidx.navigation.compose.composable

// _2App.kt
fun NavGraphBuilder._2App(viewModelInitApp: ViewModelInitApp, clientEnCourDeVent: Long = 1) {
    composable(ScreensApp2.Fragment1Screen.route) {
        A_ClientsLocationGps(
            viewModel = viewModelInitApp ,
            clientEnCourDeVent =  clientEnCourDeVent
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
    , viewModelInitApp
    )
}

object ScreensApp2 {
    val Fragment1Screen = Screen.Fragment1ScreenDataObject
}


