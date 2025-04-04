package W.Fragments.A.PanierFinaleDAchat.APP

import W.Fragments.A.PanierFinaleDAchat.APP.ViewModel.ViewModelFragment_APP2_FragID_1
import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.ViewModel.ViewModelFragment_APP2_ID_2
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val composeModules = module {
    viewModel { ViewModelFragment_APP2_FragID_1(get(), get(), get(), get()) }
    viewModel { ViewModelFragment_APP2_ID_2(get(), get(), get(), get()) }
}

@Composable
fun A_MainScreenApp2FragID_1(
    modifier: Modifier = Modifier,
    viewModel: ViewModelFragment_APP2_FragID_1 = koinViewModel(),
) {
    // Collect the UI state from the ViewModel
    val uiState by viewModel.uiStateFlow.collectAsState()
        Box(
        ) {
            MainList(uiState = uiState)
        }
}
