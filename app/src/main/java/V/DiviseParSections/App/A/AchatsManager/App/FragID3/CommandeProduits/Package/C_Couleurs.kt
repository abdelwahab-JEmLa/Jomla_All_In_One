package V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.View.A_GlideDisplayImageByKeyId_Proto_4_11
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun Couleurs(
    Produit: _1_2_ProduitAcheteOperation,
    colorsForProduct: List<_1_1_CouleurAcheteOperation>,
    buyerIds: List<Long>,
    models: _0_0_HeadOfRepositorys_Model,
) {
    // Only proceed if the product is in CONFIRME state
    if (Produit.etateActuellementEst != _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME) {
        return
    }

    val database = koinInject<AppDatabase>()
    val tag = "CommandeProduitsLog" // Log tag for tracking

    var articlesBasesStatsModel by remember { mutableStateOf<List<Any>?>(null) }

    LaunchedEffect(Produit.vid) {
        models._1_2_ProduitAcheteOperation_Repository
            .repositoryScope
            .launch {
                articlesBasesStatsModel = database.articlesBasesStatsModelDao().getAll()
            }
    }

    fun getColorNameByIndex(colorIndex: Long?, productId: Long?): String? {
        if (colorIndex == null || productId == null) return null

        val article = articlesBasesStatsModel?.find {
            try {
                it.javaClass.getMethod("getIdArticle").invoke(it) == productId.toInt()
            } catch (e: Exception) {
                false
            }
        } ?: return null

        return try {
            when (colorIndex) {
                0L -> article.javaClass.getMethod("getCouleur1").invoke(article) as? String
                1L -> article.javaClass.getMethod("getCouleur2").invoke(article) as? String
                2L -> article.javaClass.getMethod("getCouleur3").invoke(article) as? String
                3L -> article.javaClass.getMethod("getCouleur4").invoke(article) as? String
                else -> "Unknown color"
            }
        } catch (e: Exception) {
            "Color name not available"
        }
    }

    // Filter colors to only include those with quantity > 0 and QUANTITY_CHOISI status
    val filteredColors = colorsForProduct
        .filter {
            it.totaleQuantity > 0 &&
                    it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
        }
        .distinctBy { it.couleurIndex_ParentVID }

    // Don't display anything if no colors meet the criteria
    if (filteredColors.isEmpty()) {
        return
    }

    LazyRow {
        items(filteredColors) { Couleur ->
            VerticalDivider(
                thickness = 9.dp,
                color = Color.Red
            )

            // Calculate expected total quantity - sum of all individual quantities for this color
            val individualQuantities = colorsForProduct
                .filter {
                    it.couleurIndex_ParentVID == Couleur.couleurIndex_ParentVID &&
                            it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                }

            // Calculate sum of all quantities
            val totaleQuantity = individualQuantities.sumOf { it.totaleQuantity }

            // Log discrepancies - Fix for TODO(1)
            // Check if the total quantity matches the sum of client quantities
            val clientQuantitiesSum = buyerIds.sumOf { buyerId ->
                // Get the BonAchat VIDs for this client
                val clientBonAchatVids =
                    models._1_3_BonAchat_Repository.modelDatasSnapList
                        .filter { it.clientAcheteurID == buyerId }
                        .map { it.vid }

                // Get all product VIDs associated with this client's BonAchat entries
                val clientProductVids =
                    models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                        .filter { it.parent_1_3_BonAchat in clientBonAchatVids }
                        .map { it.vid }

                // Sum the quantities for this client and this color
                colorsForProduct
                    .filter {
                        it.parentProduitAchateOperationVID in clientProductVids &&
                                it.couleurIndex_ParentVID == Couleur.couleurIndex_ParentVID &&
                                it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                    }
                    .sumOf { it.totaleQuantity }
            }

            LaunchedEffect(Couleur.vid) {
                if (totaleQuantity != clientQuantitiesSum) {
                    android.util.Log.d(tag, "Quantity mismatch for product ${Produit.produitAcheterID}, color ${Couleur.couleurIndex_ParentVID}:")
                    android.util.Log.d(tag, "  - Total quantity calculated: $totaleQuantity")
                    android.util.Log.d(tag, "  - Sum of client quantities: $clientQuantitiesSum")
                    android.util.Log.d(tag, "  - Difference: ${totaleQuantity - clientQuantitiesSum}")

                    // Log individual quantities for debugging
                    individualQuantities.forEach { color ->
                        android.util.Log.d(tag, "  - Color entry ID: ${color.vid}, Quantity: ${color.totaleQuantity}")
                    }
                }
            }

            // Get color name for this specific color
            val colorName = getColorNameByIndex(
                Couleur.couleurIndex_ParentVID,
                Produit.produitAcheterID
            )

            // Get product name as fallback
            val productName = models._2_1_ProduitsDataBase_Repository.modelDatasSnapList
                .find { it.vid == Produit.produitAcheterID }?.nom ?: "Produit #${Produit.produitAcheterID}"

            Card(
                modifier = Modifier.background(Color.Red)
            ) {
                Column {
                    Box {
                        A_GlideDisplayImageByKeyId_Proto_4_11(
                            Produit.produitAcheterID,
                            Couleur.couleurIndex_ParentVID + 1,
                            360.dp,
                            onImageNeExistePas = {
                                Text(
                                    text = colorName ?: productName,
                                    fontSize = 55.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp)
                                        .graphicsLayer(rotationZ = 45f)
                                )
                            }
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.70f),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                        ) {
                            Text(
                                text = "Qté: $totaleQuantity",
                                fontSize = 50.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }

                    // Only display buyers if there are any
                    if (buyerIds.isNotEmpty()) {
                        Acheteurs(buyerIds, models, colorsForProduct, Couleur)
                    }
                }
            }
        }
    }
}

@Composable
fun Acheteurs(
    buyerIds: List<Long>,
    models: _0_0_HeadOfRepositorys_Model,
    colorsForProduct: List<_1_1_CouleurAcheteOperation>,
    Couleur: _1_1_CouleurAcheteOperation,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        buyerIds.forEach { buyerId ->
            // Get the client by ID and display name with a fallback
            val clientName =
                models._3_ClientsDataBase_Repository.modelDatasSnapList
                    .find { it.vid == buyerId }?.nom
                    ?: "Client #$buyerId"

            // Get the BonAchat VIDs for this client
            val clientBonAchatVids =
                models._1_3_BonAchat_Repository.modelDatasSnapList
                    .filter { it.clientAcheteurID == buyerId }
                    .map { it.vid }

            // Get all product VIDs associated with this client's BonAchat entries
            val clientProductVids =
                models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                    .filter { it.parent_1_3_BonAchat in clientBonAchatVids }
                    .map { it.vid }

            // Fixed calculation for clientColorQuantity - Fix for TODO(2)
            // Only sum colors with QUANTITY_CHOISI status
            val clientColorQuantity = colorsForProduct
                .filter {
                    it.parentProduitAchateOperationVID in clientProductVids &&
                            it.couleurIndex_ParentVID == Couleur.couleurIndex_ParentVID &&
                            it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                }
                .sumOf { it.totaleQuantity }

            // Only display if this client has a quantity for this color
            if (clientColorQuantity > 0) {
                Text(
                    text = clientName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Qté: $clientColorQuantity",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Add a small spacing between client entries
        Spacer(modifier = Modifier.height(4.dp))
    }
}
