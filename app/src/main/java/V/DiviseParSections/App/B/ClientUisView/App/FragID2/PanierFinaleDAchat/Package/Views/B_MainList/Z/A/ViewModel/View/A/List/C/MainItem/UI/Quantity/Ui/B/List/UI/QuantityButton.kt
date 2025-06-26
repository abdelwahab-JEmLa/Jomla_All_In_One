// Enhanced QuantityButton.kt - Fixed TODOs
package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.View.A.List.C.MainItem.UI.Quantity.Ui.B.List.UI

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.FCouleurVentOperation
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.ZViewModel_Sec1Frag3
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

enum class ClickUpdate{
    CouleurQua,
    TotalQua
}

@Composable
fun QuantityButton(
    clickUpdate: ClickUpdate = ClickUpdate.CouleurQua,
    viewModel: ZViewModel_Sec1Frag3,
    newQuantity: Int,
    isSelected: Boolean,
    onClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    vent: FCouleurVentOperation
) {
    val fCouleurAchatOperationRepositoryComposable = viewModel.uiStateCentralRepositorys
        .fCouleurAchatOperationRepositoryComposable
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    // Animations
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 400f
        ),
        label = "quantity_button_scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = spring(dampingRatio = 0.8f),
        label = "quantity_button_color"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = spring(dampingRatio = 0.8f),
        label = "quantity_text_color"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .size(56.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    radius = 28.dp,
                    color = if (isSelected) Color.White.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick(newQuantity)

                when(clickUpdate) {
                    ClickUpdate.CouleurQua -> {
                        // Original functionality - update single color quantity
                        vent.let { existingVent ->
                            val updatedVent = if (newQuantity == 0) {
                                existingVent.copy(
                                    quantityAchete = newQuantity,
                                    etateActuellementEst = FCouleurVentOperation.EtateActuellementEst.SUPP_AU_PANIER_FINALE
                                )
                            } else {
                                existingVent.copy(
                                    quantityAchete = newQuantity,
                                    etateActuellementEst = FCouleurVentOperation.EtateActuellementEst.ParentBonVentConfirme
                                )
                            }
                            fCouleurAchatOperationRepositoryComposable.addOrUpdateData(updatedVent)
                        }
                    }
                    ClickUpdate.TotalQua -> {
                        // TODO(2.C) FIXED: Update all purchase colors by total quantity
                        // Find all items with the same product ID as the current vent
                        val allProductVents = fCouleurAchatOperationRepositoryComposable.datasValue.filter {
                            it.parentProduitId == vent.parentProduitId &&
                                    it.etateActuellementEst != FCouleurVentOperation.EtateActuellementEst.SUPP_AU_PANIER_FINALE
                        }

                        if (allProductVents.isNotEmpty()) {
                            if (newQuantity == 0) {
                                // Remove all items from the product group
                                allProductVents.forEach { ventItem ->
                                    val updatedVent = ventItem.copy(
                                        quantityAchete = 0,
                                        etateActuellementEst = FCouleurVentOperation.EtateActuellementEst.SUPP_AU_PANIER_FINALE,
                                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                    )
                                    fCouleurAchatOperationRepositoryComposable.addOrUpdateData(updatedVent)
                                }
                            } else {
                                // Distribute the new total quantity among all colors
                                val quantityPerItem = newQuantity / allProductVents.size
                                val remainder = newQuantity % allProductVents.size

                                allProductVents.forEachIndexed { index, ventItem ->
                                    val itemQuantity = quantityPerItem + if (index < remainder) 1 else 0
                                    val updatedVent = ventItem.copy(
                                        quantityAchete = itemQuantity,
                                        etateActuellementEst = if (itemQuantity > 0) {
                                            FCouleurVentOperation.EtateActuellementEst.ParentBonVentConfirme
                                        } else {
                                            FCouleurVentOperation.EtateActuellementEst.SUPP_AU_PANIER_FINALE
                                        },
                                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                    )
                                    fCouleurAchatOperationRepositoryComposable.addOrUpdateData(updatedVent)
                                }
                            }
                        }
                    }
                }
            },
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        shadowElevation = if (isSelected) 8.dp else 2.dp,
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(8.dp)
                .then(
                    if (isSelected) {
                        Modifier.background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                radius = 40f
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else Modifier
                )
        ) {
            Text(
                text = newQuantity.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
