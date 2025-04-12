package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.FragID_3.CommandeProduits.Package

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.View.A_GlideDisplayImageByKeyId_Proto_4_11
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_MainScreen_APP2_FragID3(
    modifier: Modifier = Modifier.padding(2.dp),
    viewModel: ViewModelFragment_APP2_ID_3 = koinViewModel(),
) {
    val models = viewModel._0_0_HeadOfRepositorys_Repository.repositorys_Model

    Box(
        modifier = modifier
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
                HorizontalDivider(Modifier.padding(10.dp), thickness = 2.dp)
                ProduitCommande(models, Produit)
            }
        }
        //    A_OptionsControlsButtons_FragID3(viewModel)
    }
}

@Composable
private fun ProduitCommande(
    models: _0_0_HeadOfRepositorys_Model,
    Produit: _1_2_ProduitAcheteOperation,
) {
    Card() {
        Column {
            Text(
                models._2_1_ProduitsDataBase_Repository.modelDatasSnapList
                    .find { it.vid == Produit.produitAcheterID }?.nom
                    ?: "ProduitCommande inconnu", Modifier.padding(4.dp)
            )

            // Instead of filtering by ProduitCommande.vid, we should filter by produitAcheterID
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

            Couleurs(colorsForProduct, Produit, buyerIds, models)
        }
    }
}

@Composable
private fun Couleurs(
    colorsForProduct: List<_1_1_CouleurAcheteOperation>,
    Produit: _1_2_ProduitAcheteOperation,
    buyerIds: List<Long>,
    models: _0_0_HeadOfRepositorys_Model,
) {
    LazyRow {
        items(
            colorsForProduct
                .filter { it.totaleQuantity > 0 }
                .distinctBy { it.couleurIndex_ParentVID }
        ) { Couleur ->
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
                        360.dp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White.copy(alpha = 0.70f),
                                shape = RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "=Qua>$totaleQuantity",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(4.dp)
                        )
                    }

                    Column(Modifier.padding(3.dp)) {
                        buyerIds.forEach { buyerId ->
                            // Get the client by ID and display name with a fallback
                            val clientName =
                                models._3_ClientsDataBase_Repository.modelDatasSnapList
                                    .find { it.vid == buyerId }?.nom
                                    ?: "Client #$buyerId"

                            // Get the BonAchat VIDs for this client
                            val clientBonAchatVids = models._1_3_BonAchat_Repository.modelDatasSnapList
                                .filter { it.clientAcheteurID == buyerId }
                                .map { it.vid }

                            // Get all product VIDs associated with this client's BonAchat entries
                            val clientProductVids = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                                .filter { it.parent_1_3_BonAchat in clientBonAchatVids }
                                .map { it.vid }

                            // Sum the quantities for this client and this color
                            val clientColorQuantity = colorsForProduct
                                .filter {
                                    it.parentProduitAchateOperationVID in clientProductVids &&
                                            it.couleurIndex_ParentVID == Couleur.couleurIndex_ParentVID
                                }
                                .sumOf { it.totaleQuantity }

                            HorizontalDivider(Modifier.padding(3.dp))

                            // Instead of using a separate composable function
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = Color.White.copy(alpha = 0.50f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = clientName,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "Qté: $clientColorQuantity",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
