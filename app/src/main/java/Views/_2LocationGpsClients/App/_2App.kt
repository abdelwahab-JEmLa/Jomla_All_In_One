package Views._2LocationGpsClients.App

import P0_MainScreen.Ui.Main.AppNavHost.Screen
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.A_ClientsLocationGps

// _2App.kt
fun NavGraphBuilder._2App(viewModelInitApp: ViewModelInitApp) {
    composable(ScreensApp2.Fragment1Screen.route) {
        A_ClientsLocationGps(
            viewModel = viewModelInitApp
        )
    }
}

@Preview
@Composable
private fun PreviewApp2_F1() {
    // Preview version without real ViewModel
    A_ClientsLocationGps(modifier = Modifier.fillMaxSize())
}

object ScreensApp2 {
    val Fragment1Screen = Screen.Fragment1ScreenDataObject
}


