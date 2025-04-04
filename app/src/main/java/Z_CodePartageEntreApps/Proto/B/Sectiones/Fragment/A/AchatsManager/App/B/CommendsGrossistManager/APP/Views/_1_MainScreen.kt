package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views

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

@Composable
fun MainScreen_APP2_ID_2(
    modifier: Modifier = Modifier,
    viewModel: ViewModelFragment_APP2_ID_2 = koinViewModel(),
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
                MainList_APP2_ID_2(uiState = uiState)
            }
        }
    }
}
