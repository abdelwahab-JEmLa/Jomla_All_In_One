package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.FragID_3.CommandeProduits.Package

import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App._1.Shared.Views.LoadingContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel


@Composable
fun A_MainScreen_APP2_FragID3(
    modifier: Modifier = Modifier,
    viewModel: ViewModelFragment_APP2_ID_3 = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsState()
    val progressValue by viewModel.
    _0_0_HeadOfRepositorys_Repository.progressRepo.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    // Show loading indicator while data is being loaded
                    if (progressValue < 1.0f) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingContent(message = "Loading data...")
                        }
                    } else {
                        A_OptionsControlsButtons_FragID3(viewModel)
                    }
                }
            }
        }
    }
}
