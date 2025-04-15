// C_Couleurs.kt
package V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepository
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.View.A_GlideDisplayImageByKeyId_Proto_4_11
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

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

    // Add LazyRow state to detect if there are more items
    val lazyRowState = rememberLazyListState()
    val hasMoreItems by remember {
        derivedStateOf {
            val lastVisibleItem = lazyRowState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem < filteredColors.size - 1
        }
    }

    // Add a Row to combine the LazyRow and indicator
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            state = lazyRowState,
            modifier = Modifier.weight(1f)
        ) {
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

                // Calculate client quantities using a more reliable approach
                val allClientsMap = mutableMapOf<Long, Long>() // Maps client ID to quantity

                // For each color in our filtered list
                individualQuantities.forEach { colorEntry ->
                    // Find the parent product
                    val parentProduct =
                        models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
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
                        }
                    }
                }

                // Sum the client quantities
                val clientQuantitiesSum = allClientsMap.values.sum()

                // Get color name for this specific color
                val colorName = getColorNameByIndex(
                    Couleur.couleurIndex_ParentVID,
                    Produit.produitAcheterID
                )

                // Get product name as fallback
                val productName = models._2_1_ProduitsDataBase_Repository.modelDatasSnapList
                    .find { it.vid == Produit.produitAcheterID }?.nom
                    ?: "Produit #${Produit.produitAcheterID}"

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

        if (hasMoreItems) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Plus d'articles",
                    tint = Color.Red,
                    modifier = Modifier
                        .padding(8.dp)
                        .width(32.dp)
                        .height(32.dp)
                )
            }
        }
    }
}

// Fixed C_Couleurs.kt - Only the Acheteurs function needs to be updated
@Composable
fun Acheteurs(
    clientQuantities: Map<Long, Long>,
    models: _0_0_HeadOfRepositorys_Model,
    B_ClientDataBaseRepository: B_ClientDataBaseRepository = koinInject(),
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

            val clientDataBaseSnapList = B_ClientDataBaseRepository.modelDatas

            val client = clientDataBaseSnapList.find {
                it.id == clientId
            }
            val clientName = client?.nom ?: "Client #$clientId"

            // Find purchase operation VIDs for this client
            val bonAchatsForClient = models._1_3_BonAchat_Repository.modelDatasSnapList
                .filter { it.clientAcheteurID == clientId }
                .map { it.vid }

            val purchaseOperationVIDs = models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                .filter { it.parent_1_3_BonAchat in bonAchatsForClient }
                .map { it.vid }
                .take(1) // Take just the first one to display
                .joinToString()

            Text(
                text = "$clientName #$clientId (VID: ${purchaseOperationVIDs})",
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
