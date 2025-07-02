package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.C.P.View.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ClickUpdate
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.C.P.View.List.C.MainItem.UI.Quantity.Ui.A.Screen.ModernQuantityDialog_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.C.P.View.List.C.MainItem.UI.ViewVentCouleur_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.C.P.View.Modules.PriceEditor_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun ViewProduit_T1(
    modifier: Modifier = Modifier,
    productKeyId: String,
    viewModel: ZViewModel_Sec1Frag3 = koinViewModel(),
) {
    val bProduitDataBase_SubClassFunctionality = viewModel.aCentral.getter.bProduitInfosRepository
    val produit =
        bProduitDataBase_SubClassFunctionality.datasValue.find { it.keyID == productKeyId }
    val getter = viewModel.aCentral.getter
    val onVentData = getter.gBonVentRepository.onVentData

    val relatedVents by remember {
        derivedStateOf {
            getter.fVentCouleurOperationRepository.datasValue
                .filter { it.parentBProduitInfosKeyId == productKeyId }
                .ifEmpty {
                    // If no vents found, create a default one
                    val currentAppCompt = getter.zAppComptRepositoryComposable.currentAppCompt
                    listOf(
                        FCouleurVentOperationInfos(
                            parentZAppComptID = currentAppCompt?.keyID
                                ?: "Non Definie",
                            parentDebugInfosID9AppCompt = currentAppCompt?.nom?: "Non Definie",

                            parentHVentPeriodKeyId = ParametresAppComptNonSaved().activePeriodKeyId,
                            parentDebugInfosID7VentPeriod = ParametresAppComptNonSaved().parentDebugInfosID7VentPeriod,

                            parentGBonVentKeyId = onVentData.keyID,
                            parentDebugInfosID8BonVent = onVentData.nomClientConcerned,

                            parentBProduitInfosKeyId = productKeyId  ,
                            parentDebugInfosID1Produit = produit?.nom?: "Non Definie",
                        )
                    )
                }
        }
    }

    val modifierAvecSemanticsTestTag = Modifier.semantics(mergeDescendants = true) {
        set(
            SemanticsPropertyKey("1 relativeVent"),
            relatedVents.first()
        )
        set(
            SemanticsPropertyKey("4 onVentData"),
            onVentData
        )
    }


    val haptic = LocalHapticFeedback.current
    var showDialog by remember { mutableStateOf(false) }

    val totalQuantity = relatedVents.sumOf { it.quantityAchete }
    val productName = produit?.nom?.takeIf { it.isNotBlank() }
        ?: produit?.nomMutable?.takeIf { it.isNotBlank() }
        ?: "Product #$productKeyId"
    val currentPrice = relatedVents.firstOrNull()?.provisoireMonPrix ?: 0.0
    val hasNonTrouve =
        relatedVents.any { it.etateDelivery == FCouleurVentOperationInfos.EtateDelivery.NonTrouve }
    val allNonTrouve =
        relatedVents.isNotEmpty() && relatedVents.all { it.etateDelivery == FCouleurVentOperationInfos.EtateDelivery.NonTrouve }

    Card(
        modifier = modifierAvecSemanticsTestTag
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(if (allNonTrouve) 2.dp else 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (allNonTrouve) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .graphicsLayer(alpha = if (allNonTrouve) 0.4f else 1.0f)
        ) {
            ProductHeader_T1(
                productName = productName,
                allNonTrouve = allNonTrouve,
                hasNonTrouve = hasNonTrouve,
                totalQuantity = totalQuantity,
                onToggleDelivery = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    produit?.keyID?.let { viewModel.toggleEtateDeliveryNonTrouveVentOu(it) }
                },
                onQuantityClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showDialog = true
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            PriceEditor_T1(
                currentPrice = currentPrice,
                label = "Prix unitaire (toutes variantes)",
                onPriceUpdate = { price ->
                    if (!allNonTrouve) {
                        relatedVents.forEach { vent ->
                            viewModel.uiStateCentralRepositorys.fVentCouleurOperationRepository
                                .addOrUpdateData(
                                    vent.copy(
                                        provisoireMonPrix = price,
                                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                    )
                                )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                textColor = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        set(SemanticsPropertyKey("1vents"), relatedVents.map { it })
                    },
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(relatedVents) { vent ->
                    viewModel.uiStateCentralRepositorys.b1CouleurOuGoutProduitDataBaseRepository.datasValue
                        .find { it.key == vent.parentCouleurInfosKeyID }?.let {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                tonalElevation = if (allNonTrouve) 1.dp else 2.dp,
                                modifier = Modifier
                                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
                                    .graphicsLayer(
                                        alpha = if (vent.etateDelivery == FCouleurVentOperationInfos.EtateDelivery.NonTrouve) 0.5f else 1.0f
                                    )
                            ) {
                                ViewVentCouleur_T1(
                                    modifier = Modifier.padding(4.dp),
                                    ventKey = vent.keyID,
                                    size = 120.dp,
                                    purchasedQuantity = vent.quantityAchete,
                                    viewModel = viewModel
                                )
                            }
                        }
                }
            }
        }
    }

    if (showDialog && relatedVents.isNotEmpty() && !allNonTrouve) {
        ModernQuantityDialog_T1(
            clickUpdate = ClickUpdate.TotalQua,
            colorName = "Total - $productName",
            currentQuantity = totalQuantity,
            onDissmiss_showQuantityDialog = { showDialog = false },
            onDismiss = { showDialog = false },
            viewModel = viewModel,
            vent = relatedVents.first().copy(quantityAchete = totalQuantity)
        )
    }
}

@Composable
private fun ProductHeader_T1(
    productName: String,
    allNonTrouve: Boolean,
    hasNonTrouve: Boolean,
    totalQuantity: Int,
    onToggleDelivery: () -> Unit,
    onQuantityClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (allNonTrouve) {
                        listOf(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    } else {
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                )
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (allNonTrouve) {
                    Text(
                        text = "Non disponible",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ToggleButton(allNonTrouve, hasNonTrouve, onToggleDelivery)
                QuantityDisplay(allNonTrouve, totalQuantity, onQuantityClick)
            }
        }
    }
}

@Composable
private fun ToggleButton(allNonTrouve: Boolean, hasNonTrouve: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (hasNonTrouve) MaterialTheme.colorScheme.errorContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f)
                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f)
            )
    ) {
        Icon(
            imageVector = if (hasNonTrouve) Icons.Default.Cancel else Icons.Default.CheckCircle,
            contentDescription = if (hasNonTrouve) "Mark as found" else "Mark as not found",
            tint = if (hasNonTrouve) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f)
            else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = if (allNonTrouve) 0.7f else 1.0f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun QuantityDisplay(allNonTrouve: Boolean, totalQuantity: Int, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (allNonTrouve) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.primary,
        modifier = Modifier.clickable(enabled = !allNonTrouve) { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Total quantity",
                tint = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = totalQuantity.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
