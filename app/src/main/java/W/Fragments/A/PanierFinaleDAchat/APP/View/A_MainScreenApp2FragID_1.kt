package W.Fragments.A.PanierFinaleDAchat.APP.View

import W.Fragments.A.PanierFinaleDAchat.APP.ViewModel.ViewModelFragment_APP2_ID_1
import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.ViewModel.ViewModelFragment_APP2_ID_2
import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App._1.Shared.Views.LoadingContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val composeModules = module {
    viewModel { ViewModelFragment_APP2_ID_1(get(), get(), get()
        , get()
        , get()
    ) }
    viewModel { ViewModelFragment_APP2_ID_2(get(), get(), get(), get()) }
}

@Composable
fun A_MainScreenApp2FragID_1(
    modifier: Modifier = Modifier,
    viewModel: ViewModelFragment_APP2_ID_1 = koinViewModel(),
) {
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
            } else if (uiState._1_4_PeriodeVentList.isEmpty()) {
                    // Show a message when no periods are available
                    Box(
                        modifier = modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No sales periods available")
                    }
                    return
            } else {
                // Data is loaded, show the main content
                B_MainList_FragID_2(uiState = uiState)
            }
        }
    }
}
