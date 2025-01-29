package Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.B.Dialogs.A_OptionsControlsButtons
import Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.ViewModel.Startup_Extension
import Z_MasterOfApps.Z_AppsFather.Kotlin._4.Modules.GlideDisplayImageBykeyId
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal fun A_StartupScreen(
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp = viewModel(),
) {
    val extensionVM = Startup_Extension( viewModelInitApp,)
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                GlideDisplayImageBykeyId(
                    imageGlidReloadTigger = 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp),
                    size = 500.dp,
                )
            }
            if (viewModelInitApp
                    ._paramatersAppsViewModelModel
                    .fabsVisibility
            ) {
                A_OptionsControlsButtons(
                    extensionVM=extensionVM,
                    viewModelInitApp=viewModelInitApp,
                    paddingValues=paddingValues,
                )
            }
        }
    }
}

