package W.App.A.PanierFinaleDAchat.APP.View

import W.App.A.PanierFinaleDAchat.APP.ViewModel.ViewModelFragment_APP2_ID_1
import Z_CodePartageEntreApps.Proto.B.ParSections.Fragment.A.AchatsManager.App.B.PanieAchates.APP.ViewModel.ViewModelFragment_APP2_ID_2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val composeModules = module {
    viewModel { ViewModelFragment_APP2_ID_1(get(), get(), get(), get(), get()) }
    viewModel { ViewModelFragment_APP2_ID_2(get()) }
}

@Composable
fun A_MainScreenApp2FragID_1(
    modifier: Modifier = Modifier,
    viewModel: ViewModelFragment_APP2_ID_1 = koinViewModel(),
) {   /*
    // Get repository progress states in a simpler way
    val isLoading by viewModel.isDataLoading.collectAsState(initial = true)


    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(1f),
        ) {
            when {
                // Show loading indicator while data is being loaded
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingContent(message = "Loading data...")
                    }
                }
                // Show message when no periods are available
                viewModel._1_4_PeriodeVent_Repository.modelDatasSnapList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No sales periods available")
                    }
                }
                // Show main content when data is loaded
                else -> {
                    // Pass the relevant data to B_MainList_FragID_2
                    B_MainList_FragID_2()
                }
            }
        }
    }        */
}
