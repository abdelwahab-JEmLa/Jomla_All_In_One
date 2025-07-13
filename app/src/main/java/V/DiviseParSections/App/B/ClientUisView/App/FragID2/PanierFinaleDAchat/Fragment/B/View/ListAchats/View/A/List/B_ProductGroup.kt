package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ClickUpdate
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.C.MainItem.UI.Quantity.Ui.A.Screen.ModernQuantityDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.C.MainItem.UI.VentDisplayer_Sec2FragId2
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProductGroup(
    modifier: Modifier = Modifier,
    viewModel: ZViewModel_Sec1Frag3 = koinViewModel(),
    productKeyId: String,
    vents: List<M10OperationVentCouleur>,
) {
    val bProduitDataBase_SubClassFunctionality =
        viewModel.aCentralFacade.repositorysMainGetter.repoM1ProduitInfos
    val relative_M1Produit =
        bProduitDataBase_SubClassFunctionality.datasValue.find { it.keyID == productKeyId }
    val relative_M13Tariffication = viewModel.aCentralFacade.repositorysMainGetter.m13Tarification_By_KeyID(
        vents.first().parentM13TarificationKeyID
    )


    val haptic = LocalHapticFeedback.current
    var showDialog by remember { mutableStateOf(false) }

    val totalQuantity = vents.sumOf { it.quantity }
    val productName = relative_M1Produit?.nom?.takeIf { it.isNotBlank() }
        ?: relative_M1Produit?.nomMutable?.takeIf { it.isNotBlank() }
        ?: "Product #$productKeyId"


    val currentPrice = relative_M13Tariffication?.prixCurrency ?:0.0
    val hasNonTrouve =
        vents.any { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }
    val allNonTrouve =
        vents.isNotEmpty() && vents.all { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve }

    Card(
        modifier = modifier.fillMaxWidth(),
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
            if (relative_M1Produit != null) {
                ProductHeader_Modularized(relative_M1Produit , viewModel )
            }
            ProductHeader(
                productName = productName,
                allNonTrouve = allNonTrouve,
                hasNonTrouve = hasNonTrouve,
                totalQuantity = totalQuantity,
                onToggleDelivery = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    relative_M1Produit?.keyID?.let { viewModel.toggleEtateDeliveryNonTrouveVentOu(it) }
                },
                onQuantityClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showDialog = true
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            PriceEditorFragID2(
                currentPrice = currentPrice,
                label = "Prix Boit (toutes variantes)",
                onPriceUpdate = { price ->
                    if (!allNonTrouve) {
                        vents.forEach { vent ->
                            viewModel.uiStateCentralRepositorys.repo10OperationVentCouleur
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
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vents) { vent ->
                    viewModel.uiStateCentralRepositorys.repo3CouleurProduitInfos.datasValue
                        .find { it.keyID == vent.parentM3CouleurProduitInfosKeyID }?.let {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                tonalElevation = if (allNonTrouve) 1.dp else 2.dp,
                                modifier = Modifier
                                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
                                    .graphicsLayer(
                                        alpha = if (vent.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve) 0.5f else 1.0f
                                    )
                            ) {
                                VentDisplayer_Sec2FragId2(
                                    modifier = Modifier.padding(4.dp),
                                    ventKey = vent.keyID,
                                    size = 120.dp,
                                    purchasedQuantity = vent.quantity,
                                    viewModel = viewModel
                                )
                            }
                        }
                }
            }
        }
    }

    if (showDialog && vents.isNotEmpty() && !allNonTrouve) {
        ModernQuantityDialog(
            clickUpdate = ClickUpdate.TotalQua,
            colorName = "Total - $productName",
            currentQuantity = totalQuantity,
            onDissmiss_showQuantityDialog = { showDialog = false },
            onDismiss = { showDialog = false },
            viewModel = viewModel,
            vent = vents.first().copy(quantity = totalQuantity)
        )
    }
}

@Composable
private fun ProductHeader(
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
