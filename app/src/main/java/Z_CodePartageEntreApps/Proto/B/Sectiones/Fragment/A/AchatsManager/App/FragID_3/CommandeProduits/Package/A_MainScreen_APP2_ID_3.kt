package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.FragID_3.CommandeProduits.Package

import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.View.A_GlideDisplayImageByKeyId_Proto_4_11
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_MainScreen_APP2_FragID3(
    modifier: Modifier = Modifier,
    viewModel: ViewModelFragment_APP2_ID_3 = koinViewModel(),
) {
    val models = viewModel._0_0_HeadOfRepositorys_Repository.repositorys_Model

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn {
            items(
                models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                    .filter {
                        it.etateActuellementEst ==
                                _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
                    }
                    .distinctBy { it.produitAcheterID }
            )
            { Produit ->
                Card() {
                    Column {
                        Text("ProdID>${Produit.produitAcheterID}")
                        LazyRow {
                            items(
                                models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
                                    .filter {
                                        it.parentProduitAchateOperationVID == Produit.vid
                                                && it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                                                && it.totaleQuantity > 0
                                    }
                                    .distinctBy { it.couleurIndex_ParentVID }
                            )
                            { Couleur ->
                                VerticalDivider(
                                    thickness = 9.dp,
                                    color = Color.Red
                                )
                                // Calculate total quantity for all colors of this product
                                val totaleQuantity = models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
                                    .filter {
                                        it.parentProduitAchateOperationVID == Produit.vid
                                                && it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                                    }
                                    .sumOf { it.totaleQuantity }

                                Card(
                                    Modifier.background(Color.Red)
                                ) {
                                    Box {
                                        A_GlideDisplayImageByKeyId_Proto_4_11(
                                            Produit.produitAcheterID,
                                            Couleur.couleurIndex_ParentVID+1,
                                            100.dp
                                        )
                                        Text(
                                            "IDX>${Couleur.couleurIndex_ParentVID}" +
                                                    "=Qua>$totaleQuantity",
                                            Modifier
                                                .background(
                                                    color = Color.White.copy(alpha = 0.50f),
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //    A_OptionsControlsButtons_FragID3(viewModel)
    }
}
