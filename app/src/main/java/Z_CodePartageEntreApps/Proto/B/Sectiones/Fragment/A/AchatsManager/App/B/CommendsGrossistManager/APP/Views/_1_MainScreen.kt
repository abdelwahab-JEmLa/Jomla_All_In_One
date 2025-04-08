package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views

import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.ViewModel.ViewModelFragment_APP2_ID_2
import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App._1.Shared.Views.LoadingContent
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun MainScreen_APP2_ID_2(
    modifier: Modifier = Modifier,
    viewModel: ViewModelFragment_APP2_ID_2 = koinViewModel(),
    _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository = koinInject(),
) {
    // Collect the UI state from the ViewModel
    val _0_HeadOfRepositorys_Repository_Model = _0_0_HeadOfRepositorys_Repository
        .repositorys_Model

    val progress1 by _0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository.progressRepo.collectAsState()
    val progress2 by _0_HeadOfRepositorys_Repository_Model._1_2_ProduitAcheteOperation_Repository.progressRepo.collectAsState()
    val progress3 by _0_HeadOfRepositorys_Repository_Model._1_3_BonAchat_Repository.progressRepo.collectAsState()
    val progress4 by _0_HeadOfRepositorys_Repository_Model._1_4_PeriodeVent_Repository.progressRepo.collectAsState()
    val progress5 by _0_HeadOfRepositorys_Repository_Model._1_5_Vendeur_Repository.progressRepo.collectAsState()

    val isLoading =
        progress1 < 1.0f || progress2 < 1.0f || progress3 < 1.0f || progress4 < 1.0f || progress5 < 1.0f

    val composeKeyVID = _0_HeadOfRepositorys_Repository_Model
        ._1_3_BonAchat_Repository
        .activeId

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.weight(1f),
        ) {
            // Show loading indicator while data is being loaded
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingContent(message = "Loading data...")
                }
            } else if (_0_HeadOfRepositorys_Repository_Model
                    ._1_4_PeriodeVent_Repository.modelDatasSnapList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyListContent(message = "No data available")
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

@Composable
fun EmptyListContent(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.Info,
            contentDescription = "Empty List",
            tint = androidx.compose.ui.graphics.Color.Gray,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = message,
            color = androidx.compose.ui.graphics.Color.Gray,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
