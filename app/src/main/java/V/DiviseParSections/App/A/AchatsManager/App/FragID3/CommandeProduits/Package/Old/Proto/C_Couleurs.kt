// C_Couleurs.kt
package V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Old.Proto

import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.View.B.List.B.Main.VIEW.AcheteursDeCetteProduit
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Modules.D.Glide.Proto.CalculeCouleurHandler
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.View.A_GlideDisplayImageByKeyId_Proto_4_11
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

@Composable
fun Couleurs(
    calculeCouleurHandler: CalculeCouleurHandler = koinInject(),
    Produit: _1_2_ProduitAcheteOperation,
    colorsForProduct: List<_1_1_CouleurAcheteOperation>,
    models: GroupeRepositorysProtoAvJuin3Model,
    periodFilter: Long? = null,
) {
    if (Produit.etateActuellementEst != _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME) {
        return
    }

    val database = koinInject<AppDatabase>()
    var articlesBasesStatsModel by remember { mutableStateOf<List<Any>?>(null) }

    LaunchedEffect(Produit.vid) {
        models.repositoryC2_ProduitAcheteOperation
            .repositoryScope
            .launch {
                articlesBasesStatsModel = database.ArticlesBasesStatsModelDao().getAll()
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

    val filteredColors = colorsForProduct
        .filter { color ->
            val basicCondition = color.totaleQuantity > 0 &&
                    color.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI

            if (!basicCondition) return@filter false
            if (periodFilter == null) return@filter true

            val parentProduct = models.repositoryC2_ProduitAcheteOperation.modelDatasSnapList
                .firstOrNull { it.vid == color.parentProduitAchateOperationVID }

            val bonAchatId = parentProduct?.parent_1_3_TransactionCommercial

            val bonAchatPeriod = bonAchatId?.let { id ->
                models.c3TransactionCommercialRepository.modelDatasSnapList
                    .firstOrNull { it.vid == id }?.parentPeriodeVentOldID
            }

            bonAchatPeriod == periodFilter
        }
        .distinctBy { it.couleurIndex_ParentVID }

    if (filteredColors.isEmpty()) {
        return
    }

    val lazyRowState = rememberLazyListState()
    val hasMoreItems by remember {
        derivedStateOf {
            val lastVisibleItem = lazyRowState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem < filteredColors.size - 1
        }
    }

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

                val individualQuantities = colorsForProduct
                    .filter { color ->
                        val colorMatch =
                            color.couleurIndex_ParentVID == Couleur.couleurIndex_ParentVID &&
                                    color.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI

                        if (!colorMatch) return@filter false
                        if (periodFilter == null) return@filter true

                        val parentProduct =
                            models.repositoryC2_ProduitAcheteOperation.modelDatasSnapList
                                .firstOrNull { it.vid == color.parentProduitAchateOperationVID }

                        val bonAchatId = parentProduct?.parent_1_3_TransactionCommercial

                        val bonAchatPeriod = bonAchatId?.let { id ->
                            models.c3TransactionCommercialRepository.modelDatasSnapList
                                .firstOrNull { it.vid == id }?.parentPeriodeVentOldID
                        }

                        bonAchatPeriod == periodFilter
                    }

                val allClientsMap = mutableMapOf<Long, Long>()

                individualQuantities.forEach { colorEntry ->
                    val parentProduct =
                        models.repositoryC2_ProduitAcheteOperation.modelDatasSnapList
                            .firstOrNull { it.vid == colorEntry.parentProduitAchateOperationVID }

                    val bonAchatVid = parentProduct?.parent_1_3_TransactionCommercial

                    if (bonAchatVid != null) {
                        val bonAchat = models.c3TransactionCommercialRepository.modelDatasSnapList
                            .firstOrNull { it.vid == bonAchatVid }

                        val clientId = bonAchat?.parentHClientOldID

                        if (clientId != null) {
                            val currentQuantity = allClientsMap.getOrDefault(clientId, 0L)
                            allClientsMap[clientId] = currentQuantity + colorEntry.totaleQuantity
                        }
                    }
                }

                val clientQuantitiesSum = allClientsMap.values.sum()

                val colorName = remember(Produit.produitAcheterID, Couleur.couleurIndex_ParentVID) {
                    val product = calculeCouleurHandler.findProductById(Produit.produitAcheterID)
                    product?.let {
                        val productImageInfos =
                            calculeCouleurHandler.getProduitInfoImageParIndex(it)
                        val colorIndex = Couleur.couleurIndex_ParentVID.toInt()
                        productImageInfos.getOrNull(colorIndex)?.colorName?.takeIf { name -> name.isNotBlank() }
                    }
                }

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
                                        text = colorName ?: "NonTrouve",
                                        fontSize = 55.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(40.dp)
                                            .graphicsLayer(rotationZ = 45f)
                                    )
                                }, qualityImage = 1
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
                                    text = "Qté: $clientQuantitiesSum",
                                    fontSize = 50.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }

                        if (allClientsMap.isNotEmpty()) {
                            AcheteursDeCetteProduit(clientQuantities = allClientsMap)
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
