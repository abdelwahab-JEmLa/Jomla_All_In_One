package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List.MainList
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.Ui.EmptyDataMessage
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.Ui.GenerationProgressScreen
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.A.Main.B1CouleurOuGoutProduitDataBaseTestDatasViewModel
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.B1CouleurOuGoutProduitDataBase
import Z_CodePartageEntreApps.Ui.LoadingScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun B1CouleurOuGoutProduitDataBaseTestDatas(
    viewModel: B1CouleurOuGoutProduitDataBaseTestDatasViewModel = koinViewModel()
) {
    val datas = viewModel.b1CouleurOuGoutProduitDataBaseRepository.datasValue
    val uiState by viewModel.uiState.collectAsState()
    val loadingProgress = viewModel.a_CentralDatasHandlerProtoJuin9.loadingProgress ?: 0f

    when {
        loadingProgress < 1.0f -> LoadingScreen(loadingProgress)
        uiState.isGeneratingData -> GenerationProgressScreen(uiState.progressCount, uiState.totalItems)
        else -> MainScreen(viewModel,datas, uiState, viewModel::genereDatasDepuitParent)
    }
}

@Composable
private fun MainScreen(
    viewModel: B1CouleurOuGoutProduitDataBaseTestDatasViewModel,
    datas: List<B1CouleurOuGoutProduitDataBase>,
    uiState: B1CouleurOuGoutProduitDataBaseTestDatasViewModel.UiState,
    onRefresh: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total items: ${datas.size}", modifier = Modifier.padding(bottom = 8.dp))
            if (uiState.progressCount > 0) {
                Text("Generated ${uiState.progressCount} color variants", modifier = Modifier.padding(bottom = 8.dp))
            }

            if (datas.isEmpty()) {
                EmptyDataMessage()
            } else {
                MainList(viewModel.b1CouleurOuGoutProduitDataBaseRepository)
            }
        }

        FloatingActionButton(
            onClick = onRefresh,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Generate Data")
        }
    }
}

