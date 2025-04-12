package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.FragID_3.CommandeProduits.Package

import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.View.A_GlideDisplayImageByKeyId_Proto_4_11
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

    Box(modifier = modifier
        .fillMaxSize()
        .padding(4.dp)
    ) {
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
                        Text( models._2_1_ProduitsDataBase_Repository.modelDatasSnapList
                            .find { it.vid==Produit.produitAcheterID }?.nom ?: "Produit inconnu"
                            ,Modifier.padding(4.dp)
                        )

                        // Instead of filtering by Produit.vid, we should filter by produitAcheterID
                        val colorsForProduct =
                            models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
                                .filter {
                                    // Find the parent product vid that this color belongs to
                                    val parentProduct =
                                        models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                                            .firstOrNull { prod -> prod.vid == it.parentProduitAchateOperationVID }

                                    // Check if this color belongs to a product with the same ID as our current product
                                    parentProduct?.produitAcheterID == Produit.produitAcheterID &&
                                            it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                                }

                        val buyerIds = remember {
                            // Find all BonAchat IDs associated with this product
                            val bonAchatIds =
                                models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                                    .filter {
                                        it.produitAcheterID == Produit.produitAcheterID &&
                                                it.etateActuellementEst == _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
                                    }
                                    .map { it.parent_1_3_BonAchat }
                                    .distinct()

                            // Get all client IDs from those BonAchat entries
                            models._1_3_BonAchat_Repository.modelDatasSnapList
                                .filter { it.vid in bonAchatIds }
                                .map { it.clientAcheteurID }
                                .distinct()
                        }

                        LazyRow {
                            items(
                                colorsForProduct
                                    .filter { it.totaleQuantity > 0 }
                                    .distinctBy { it.couleurIndex_ParentVID }
                            )
                            { Couleur ->
                                VerticalDivider(
                                    thickness = 9.dp,
                                    color = Color.Red
                                )

                                val totaleQuantity = colorsForProduct
                                    .filter { it.couleurIndex_ParentVID == Couleur.couleurIndex_ParentVID }
                                    .sumOf { it.totaleQuantity }

                                Card(
                                    Modifier.background(Color.Red)
                                ) {
                                    Column {
                                        A_GlideDisplayImageByKeyId_Proto_4_11(
                                            Produit.produitAcheterID,
                                            Couleur.couleurIndex_ParentVID + 1,
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
                                        // Fixed - Safely handle potentially null client names
                                        Column {
                                            buyerIds.forEach { buyerId ->
                                                // Get the client by ID and display name with a fallback
                                                val clientName = models._3_ClientsDataBase_Repository.modelDatasSnapList
                                                    .find { it.vid == buyerId }?.nom ?: "Client inconnu"

                                                Text(
                                                    text = clientName,
                                                    modifier = Modifier
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
            }
        }
        //    A_OptionsControlsButtons_FragID3(viewModel)
    }
}
