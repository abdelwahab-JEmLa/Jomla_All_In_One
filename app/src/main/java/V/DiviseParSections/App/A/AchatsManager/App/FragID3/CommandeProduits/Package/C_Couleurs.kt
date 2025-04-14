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

private fun logColorEntryDetails(
    tag: String,
    models: _0_0_HeadOfRepositorys_Model,
    colorEntry: _1_1_CouleurAcheteOperation
) {
    val productVid = colorEntry.parentProduitAchateOperationVID
    val product = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
        .firstOrNull { it.vid == productVid }

    val bonAchatVid = product?.parent_1_3_BonAchat
    val bonAchat = bonAchatVid?.let {
        models._1_3_BonAchat_Repository.modelDatasSnapList
            .firstOrNull { it.vid == bonAchatVid }
    }

    val clientId = bonAchat?.clientAcheteurID
    val client = clientId?.let {
        models._3_ClientsDataBase_Repository.modelDatasSnapList
            .firstOrNull { it.vid == clientId }
    }

    android.util.Log.d(tag, "Color entry details for ID ${colorEntry.vid}:")
    android.util.Log.d(tag, "  - Product VID: $productVid")
    android.util.Log.d(tag, "  - BonAchat VID: $bonAchatVid")
    android.util.Log.d(tag, "  - Client ID: $clientId, Name: ${client?.nom ?: "Unknown"}")
    android.util.Log.d(tag, "  - Color Index: ${colorEntry.couleurIndex_ParentVID}")
    android.util.Log.d(tag, "  - Quantity: ${colorEntry.totaleQuantity}")
    android.util.Log.d(tag, "  - Status: ${colorEntry.etateActuellementEst}")
}

// Updated fix for C_Couleurs.kt
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
            // Calculate client quantities using a more reliable approach
            val allClientsMap = mutableMapOf<Long, Long>() // Maps client ID to quantity

            // For each color in our filtered list
            individualQuantities.forEach { colorEntry ->
                // Find the parent product
                val parentProduct = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                    .firstOrNull { it.vid == colorEntry.parentProduitAchateOperationVID }

                // Find the bonAchat
                val bonAchatVid = parentProduct?.parent_1_3_BonAchat

                // Find the client
                if (bonAchatVid != null) {
                    val bonAchat = models._1_3_BonAchat_Repository.modelDatasSnapList
                        .firstOrNull { it.vid == bonAchatVid }

                    val clientId = bonAchat?.clientAcheteurID

                    // Add to our client map if we found a valid client
                    if (clientId != null) {
                        val currentQuantity = allClientsMap.getOrDefault(clientId, 0L)
                        allClientsMap[clientId] = currentQuantity + colorEntry.totaleQuantity
                    } else {
                        // Log orphaned color entry (no client)
                        android.util.Log.d(tag, "Orphaned color entry found: ${colorEntry.vid}")
                        logColorEntryDetails(tag, models, colorEntry)
                    }
                } else {
                    // Log orphaned color entry (no bonAchat)
                    android.util.Log.d(tag, "Color entry with no BonAchat: ${colorEntry.vid}")
                    logColorEntryDetails(tag, models, colorEntry)
                }
            }

            // Sum the client quantities
            val clientQuantitiesSum = allClientsMap.values.sum()

            LaunchedEffect(Couleur.vid) {
                if (totaleQuantity.toLong() != clientQuantitiesSum) {
                    android.util.Log.d(tag, "Quantity mismatch for product ${Produit.produitAcheterID}, color ${Couleur.couleurIndex_ParentVID}:")
                    android.util.Log.d(tag, "  - Total quantity calculated: $totaleQuantity")
                    android.util.Log.d(tag, "  - Sum of client quantities: $clientQuantitiesSum")
                    android.util.Log.d(tag, "  - Difference: ${totaleQuantity - clientQuantitiesSum}")

                    // Log all clients we found
                    allClientsMap.forEach { (clientId, quantity) ->
                        val clientName = models._3_ClientsDataBase_Repository.modelDatasSnapList
                            .find { it.vid == clientId }?.nom ?: "Unknown"
                        android.util.Log.d(tag, "  - Client: $clientName (ID: $clientId), Quantity: $quantity")
                    }

                    // Detailed log of each color entry
                    individualQuantities.forEach { color ->
                        logColorEntryDetails(tag, models, color)
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
                                // Use the client sum for display to ensure UI consistency
                                text = "Qté: $clientQuantitiesSum",
                                fontSize = 50.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }

                    // Only display buyers if there are any and pass the updated client quantities map
                    if (allClientsMap.isNotEmpty()) {
                        Acheteurs(allClientsMap, models)
                    }
                }
            }
        }
    }
}

// Completely restructured Acheteurs to use the calculated client quantities
@Composable
fun Acheteurs(
    clientQuantities: Map<Long, Long>,
    models: _0_0_HeadOfRepositorys_Model
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Display each client with their quantity
        clientQuantities.forEach { (clientId, quantity) ->
            // Skip clients with zero quantity
            if (quantity <= 0) return@forEach

            // Get client name
            val clientName = models._3_ClientsDataBase_Repository.modelDatasSnapList
                .find { it.vid == clientId }?.nom ?: "Client #$clientId"

            Text(
                text = clientName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Qté: $quantity",
                fontWeight = FontWeight.Bold
            )

            // Add spacing between clients
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
