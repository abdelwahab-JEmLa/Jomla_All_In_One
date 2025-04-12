package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.FragID_3.CommandeProduits.Package

import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_MainScreen_APP2_FragID3(
    modifier: Modifier = Modifier,
    viewModel: ViewModelFragment_APP2_ID_3 = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsState()
    val progressValue by viewModel._0_0_HeadOfRepositorys_Repository.progressRepo.collectAsState()
    val models = viewModel._0_0_HeadOfRepositorys_Repository.repositorys_Model

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn {
            items(models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList.filter {
                    it.parent_1_3_BonAchat == 1L && it.etateActuellementEst == _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
                }.distinctBy { it.produitAcheterID }.map { it.vid }) { OperationAchateProduitVID ->
                Card() {
                    Column {
                        Text("OperationAchateProduitVID$OperationAchateProduitVID")
                        LazyRow {
                            items(models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList.filter {
                                    it.parentProduitAchateOperationVID == OperationAchateProduitVID && it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI && it.totaleQuantity > 0
                                }.distinctBy { it.couleurIndex_ParentVID }
                                .map { it.vid }) { OperationAchateCouleurVID ->
                                 VerticalDivider(
                                     thickness= 9.dp,
                                     color = Color.Red
                                 )
                                Card(
                                    Modifier.background(Color.Red)
                                ) {
                                    Text("OperationAchateCouleurVID$OperationAchateCouleurVID"
                                    ,Modifier.background(Color.White))
                                }
                            }
                        }
                    }
                }
            }
        }
        A_OptionsControlsButtons_FragID3(viewModel)
    }
}


