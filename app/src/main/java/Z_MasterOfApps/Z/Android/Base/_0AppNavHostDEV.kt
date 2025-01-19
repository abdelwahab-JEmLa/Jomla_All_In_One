package Z_MasterOfApps.Z.Android.Base

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Moving
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import Z_MasterOfApps.Z.Android.Dev.Views._1NavHost.Fragment_IdDEV.A_DeplaceProduitsVerGrossist_F5

@Composable
fun AppNavHostDEV(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp,
    navController: NavHostController,
) {
    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Fragment_5.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Fragment_5.route) {
                A_DeplaceProduitsVerGrossist_F5(viewModelInitApp = viewModelInitApp)
            }

        }
    }
}

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val color: Color
) {
    data object Fragment_5 : Screen(
        route = "main_screen_F5",
        icon = Icons.Default.Moving,
        title = "main_screen_F5",
        color = Color(0xFF3F51B5)
    )

}

object NavigationItems {
    val items = listOf(
        Screen.Fragment_5,

        )
}

@Preview
@Composable
private fun Preview_Fragment4() {
    A_DeplaceProduitsVerGrossist_F5(modifier = Modifier.fillMaxSize())
}
