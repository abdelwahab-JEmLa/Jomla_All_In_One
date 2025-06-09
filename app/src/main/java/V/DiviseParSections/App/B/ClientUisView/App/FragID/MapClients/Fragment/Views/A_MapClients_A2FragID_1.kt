package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Ui.LoadingProgressOverlay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_MapClients_A2FragID_1(
    modifier: Modifier = Modifier,
    viewModel: MapClientsViewModel = koinViewModel(),
    clientEnCourDeVent: Long = 0,
    onUpdateLongAppSetting: () -> Unit = {},
    onClear: () -> Unit = {},
    mapReloadTrigger: Int = 0,
) {
    val uiState by viewModel.uiState.collectAsState()
    val progress = uiState.mainLoadingProgress

    // Clean up resources when fragment is disposed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.cleanupResources()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (progress < 1.0f) {
            LoadingProgressOverlay(progress = progress)
        } else {
            MapContent(
                viewModel = viewModel,
                onUpdateLongAppSetting = onUpdateLongAppSetting,
                onClear = onClear,
                mapReloadTrigger = mapReloadTrigger
            )
        }
    }
}


