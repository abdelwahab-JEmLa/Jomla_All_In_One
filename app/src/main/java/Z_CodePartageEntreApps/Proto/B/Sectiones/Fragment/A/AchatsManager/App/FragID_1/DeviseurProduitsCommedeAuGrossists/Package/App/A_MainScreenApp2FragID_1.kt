package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.FragID_1.DeviseurProduitsCommedeAuGrossists.Package.App

import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.ViewModel.ViewModelFragment_APP2_ID_2
import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App._1.Shared.Views.LoadingContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

val composeModules = module {
    viewModel { ViewModelFragment_APP2_FragID_1(get(),get(),get(),get()) }
    viewModel { ViewModelFragment_APP2_ID_2(get(),get(),get(),get()) }
}

// Load the module when the composable is first used
fun loadComposAPP2Modules() {
    loadKoinModules(composeModules)
}

@Composable
fun A_MainScreenApp2FragID_1(
    modifier: Modifier = Modifier,
    viewModel: ViewModelFragment_APP2_FragID_1 = koinViewModel(),
) {

    // Collect the UI state from the ViewModel
    val uiState by viewModel.uiStateFlow.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(1f),
        ) {
            // Show loading indicator while data is being loaded
            if (uiState.isDataLoading && !uiState.isInitialized) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingContent(message = "Loading data...")
                }
            } else if (uiState.errorMessage != null) {
                // Show error message if any
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${uiState.errorMessage}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                // Data is loaded, show the main content
                MainList(uiState = uiState)
                A_OptionsControlsButtons_FragId_7(viewModel = viewModel)
            }
        }
    }
}
