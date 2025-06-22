package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.Preview
    /*
import Z_CodePartageEntreApps.DataBase.Juin17.Proto.D_AchatOperationRepository.Base.Preview.D_AchatOperationTestDatasViewModel
import Z_CodePartageEntreApps.Ui.LoadingScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun D_AchatOperationTestDatas(
    viewModel: D_AchatOperationTestDatasViewModel = koinViewModel()
) {
    val datas = viewModel.achatOperationComposeRepository.datasValue
    val loadingProgress =
        viewModel.appComptComposeRepository.mainInitDataBaseProgressEtate_appComptActuelle

    if (loadingProgress < 1.0f) {
        LoadingScreen(loadingProgress)
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(datas.size) { index ->
                    val data = datas[index]
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(data.bsonObjectId)

                            VerticalDivider(thickness=10.dp)

                            Text(data.etateActuellementEst.name)
                        }
                    }
                }
            }
        }
    }
}
                            */
