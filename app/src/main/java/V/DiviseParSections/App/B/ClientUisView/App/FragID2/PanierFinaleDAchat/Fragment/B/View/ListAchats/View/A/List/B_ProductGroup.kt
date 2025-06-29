package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ClickUpdate
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.C.MainItem.UI.Quantity.Ui.A.Screen.ModernQuantityDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.C.MainItem.UI.VentDisplayer_Sec2FragId2
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.BProduitInfosRepository
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.FCouleurVentOperationInfos
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

@Composable
fun ProductGroup(
    viewModel: ZViewModel_Sec1Frag3,
    productKeyId: String,
    achats: List<FCouleurVentOperationInfos>,
    modifier: Modifier = Modifier,
    bProduitDataBase_SubClassFunctionality: BProduitInfosRepository
) {
    val produit = bProduitDataBase_SubClassFunctionality.datasValue.find { it.keyID == productKeyId }
    val haptic = LocalHapticFeedback.current
    var showTotalQuantityDialog by remember { mutableStateOf(false) }

    val totalQuantity = achats.sumOf { it.quantityAchete }
    val productName = produit?.nom?.takeIf { it.isNotBlank() }
        ?: produit?.nomMutable?.takeIf { it.isNotBlank() }
        ?: "Product #$productKeyId"
    val currentPrice = achats.firstOrNull()?.provisoireMonPrix ?: 0.0

    // Check if any of the items have delivery state "NonTrouve"
    val hasNonTrouveItems = achats.any { it.etateDelivery == FCouleurVentOperationInfos.EtateDelivery.NonTrouve }

    // Check if ALL items have delivery state "NonTrouve" - for graying out the entire product
    val allItemsNonTrouve = achats.isNotEmpty() && achats.all { it.etateDelivery == FCouleurVentOperationInfos.EtateDelivery.NonTrouve }

    // Calculate alpha and colors based on delivery state
    val contentAlpha = if (allItemsNonTrouve) 0.4f else 1.0f
    val cardContainerColor = if (allItemsNonTrouve) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (allItemsNonTrouve) 2.dp else 6.dp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardContainerColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .graphicsLayer(alpha = contentAlpha)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = if (allItemsNonTrouve) {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.secondaryContainer
                                )
                            )
                        }
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
                            color = if (allItemsNonTrouve) {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            },
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Add status indicator text for non-trouve items
                        if (allItemsNonTrouve) {
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
                        // Toggle delivery state button
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                produit?.keyID?.let { key ->
                                    viewModel.toggleEtateDeliveryNonTrouveVentOu(key)
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (hasNonTrouveItems)
                                        MaterialTheme.colorScheme.errorContainer.copy(
                                            alpha = if (allItemsNonTrouve) 0.7f else 1.0f
                                        )
                                    else
                                        MaterialTheme.colorScheme.primaryContainer.copy(
                                            alpha = if (allItemsNonTrouve) 0.7f else 1.0f
                                        )
                                )
                        ) {
                            Icon(
                                imageVector = if (hasNonTrouveItems) Icons.Default.Cancel else Icons.Default.CheckCircle,
                                contentDescription = if (hasNonTrouveItems) "Mark as found" else "Mark as not found",
                                tint = if (hasNonTrouveItems) {
                                    MaterialTheme.colorScheme.onErrorContainer.copy(
                                        alpha = if (allItemsNonTrouve) 0.7f else 1.0f
                                    )
                                } else {
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                        alpha = if (allItemsNonTrouve) 0.7f else 1.0f
                                    )
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Total quantity display
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (allItemsNonTrouve) {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier
                                .clickable(enabled = !allItemsNonTrouve) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    showTotalQuantityDialog = true
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Total quantity",
                                    tint = if (allItemsNonTrouve) {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    } else {
                                        MaterialTheme.colorScheme.onPrimary
                                    },
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = totalQuantity.toString(),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (allItemsNonTrouve) {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    } else {
                                        MaterialTheme.colorScheme.onPrimary
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price editor - disabled when all items are non-trouve
            PriceEditorFragID2(
                currentPrice = currentPrice,
                label = "Prix unitaire (toutes variantes)",
                onPriceUpdate = { newPrice ->
                    if (!allItemsNonTrouve) {
                        achats.forEach { vent ->
                            val updatedVent = vent.copy(
                                provisoireMonPrix = newPrice,
                                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                            )
                            viewModel.uiStateCentralRepositorys.fVentCouleurOperationRepository
                                .addOrUpdateData(updatedVent)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                textColor = if (allItemsNonTrouve) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(achats) { vent ->
                    viewModel.uiStateCentralRepositorys.b1CouleurOuGoutProduitDataBaseRepository.datasValue
                        .find { it.key == vent.parentCouleurInfosKeyID }?.let {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                tonalElevation = if (allItemsNonTrouve) 1.dp else 2.dp,
                                modifier = Modifier
                                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
                                    .graphicsLayer(
                                        alpha = if (vent.etateDelivery == FCouleurVentOperationInfos.EtateDelivery.NonTrouve) 0.5f else 1.0f
                                    )
                            ) {
                                VentDisplayer_Sec2FragId2(
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

    // Disable dialog when all items are non-trouve
    if (showTotalQuantityDialog && achats.isNotEmpty() && !allItemsNonTrouve) {
        ModernQuantityDialog(
            clickUpdate = ClickUpdate.TotalQua,
            colorName = "Total - $productName",
            currentQuantity = totalQuantity,
            onDissmiss_showQuantityDialog = { showTotalQuantityDialog = false },
            onDismiss = { showTotalQuantityDialog = false },
            viewModel = viewModel,
            vent = achats.first().copy(quantityAchete = totalQuantity)
        )
    }
}
