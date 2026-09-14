package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views

import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Modules.Wi.Module.WifiTransferDatas_ControllerApp
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Ui.LoadingProgressOverlay
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun A_MapClients_A2FragID_1(
    modifier: Modifier = Modifier,
    fragmentNavigationHandler_NewProto: FragmentNavigationHandler_NewProto,
    viewModel: MapClientsViewModel = koinViewModel(),
    onUpdateLongAppSetting: () -> Unit = {},
    onClear: () -> Unit = {},
    wifiTransferDatas_ControllerApp: WifiTransferDatas_ControllerApp,
) {

    //<--
    //TODO(1): fait que si m9 .lance_dialoge_client_et_ne_lance_pas_le_map_pour_ressources
    val uiState by viewModel.uiState.collectAsState()
    val progress = uiState.mainLoadingProgress

    var isTimeout by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2000)
        isTimeout = true
    }

    // Clean up resources when fragment is disposed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.cleanupResources()
        }
    }


    Box(modifier = modifier.fillMaxSize()) {
        if (progress < 1.0f && !isTimeout) {
            LoadingProgressOverlay(progress = progress)
        } else {
            MapContent(
                viewModel = viewModel,
                fragmentNavigationHandler_NewProto=
                    fragmentNavigationHandler_NewProto,
                onUpdateLongAppSetting = onUpdateLongAppSetting,
                onClear = onClear,
                wifiTransferDatas_ControllerApp=wifiTransferDatas_ControllerApp,
            )
        }
    }
}



