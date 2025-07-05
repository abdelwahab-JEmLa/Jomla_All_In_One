// Enhanced QuantityButton.kt - Fixed TODOs
package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.B.List.UI

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ClickUpdate
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.rememberQuantityButtonAnimations
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
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

@Composable
fun QuantityButton_T1(
    modifier: Modifier = Modifier,
    clickUpdate: ClickUpdate = ClickUpdate.CouleurQua,
    viewModel: ViewModelsProduit_T1,
    newQuantity: Int,
    isSelected: Boolean,
    onClick: (Int) -> Unit = {},
    vent: M10OperationVentCouleur
) {
    val fCouleurAchatOperationRepositoryComposable = viewModel.getter
        .repo10OperationVentCouleur
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    // Use the separated animation functions
    val animations = rememberQuantityButtonAnimations(isSelected)

    Surface(
        modifier = modifier
            .scale(animations.scale)
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
                                    etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.SUPP_AU_PANIER_FINALE
                                )
                            } else {
                                existingVent.copy(
                                    quantityAchete = newQuantity,
                                    etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ParentBonVentConfirme
                                )
                            }
                            fCouleurAchatOperationRepositoryComposable.addOrUpdateData(updatedVent)
                        }
                    }
                    ClickUpdate.TotalQua -> {
                        val allProductVents = fCouleurAchatOperationRepositoryComposable.datasValue.filter {
                            it.parentM1ProduitInfosKeyId == vent.parentM1ProduitInfosKeyId &&
                                    it.etateActuellementEst != M10OperationVentCouleur.EtateActuellementEst.SUPP_AU_PANIER_FINALE
                        }

                        if (allProductVents.isNotEmpty()) {
                            if (newQuantity == 0) {
                                allProductVents.forEach { ventItem ->
                                    val updatedVent = ventItem.copy(
                                        quantityAchete = 0,
                                        etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.SUPP_AU_PANIER_FINALE,
                                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                    )
                                    fCouleurAchatOperationRepositoryComposable.addOrUpdateData(updatedVent)
                                }
                            } else {
                                val quantityPerItem = newQuantity / allProductVents.size
                                val remainder = newQuantity % allProductVents.size

                                allProductVents.forEachIndexed { index, ventItem ->
                                    val itemQuantity = quantityPerItem + if (index < remainder) 1 else 0
                                    val updatedVent = ventItem.copy(
                                        quantityAchete = itemQuantity,
                                        etateActuellementEst = if (itemQuantity > 0) {
                                            M10OperationVentCouleur.EtateActuellementEst.ParentBonVentConfirme
                                        } else {
                                            M10OperationVentCouleur.EtateActuellementEst.SUPP_AU_PANIER_FINALE
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
        color = animations.backgroundColor,
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
                color = animations.textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
