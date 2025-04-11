package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views

import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.ViewModel.ViewModelFragment_APP2_ID_2
import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App._1.Shared.Views.LoadingContent
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun MainScreen_APP2_ID_2(
    modifier: Modifier = Modifier,
    viewModel: ViewModelFragment_APP2_ID_2 = koinViewModel(),
    _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository = koinInject(),
) {
    val progressValue by _0_0_HeadOfRepositorys_Repository.progressRepo.collectAsState()
    val _0_HeadOfRepositorys_Repository_Model = _0_0_HeadOfRepositorys_Repository
        .repositorys_Model


    val composeKeyVID =
        _0_0_HeadOfRepositorys_Repository.activeVID_1_3_BonAchat

    Column(
        modifier = modifier.fillMaxSize()
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
                MainList_APP2_ID_2(
                    composeKeyVID = composeKeyVID,
                    _0_HeadOfRepositorys_Repository_Model = _0_HeadOfRepositorys_Repository_Model
                )
            }
        }
    }
}
