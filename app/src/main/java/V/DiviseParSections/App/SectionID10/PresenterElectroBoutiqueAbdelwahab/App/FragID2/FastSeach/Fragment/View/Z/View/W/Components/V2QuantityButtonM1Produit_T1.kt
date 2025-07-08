package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.rememberQuantityButtonAnimations
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.Get.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur.Companion.ref
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
fun QuantityButtonM1Produit_T1(
    produit: ArticlesBasesStatsTable,
    modifier: Modifier = Modifier,
    viewModel: ViewModelsProduit_T1,
    newQuantity: Int,
    isSelected: Boolean,
    onClick: (Int) -> Unit = {}
) {
    val repo10OperationVentCouleur = viewModel.getter.repo10OperationVentCouleur
    val repo3CouleurProduitInfos = viewModel.getter.repo3CouleurProduitInfos

    val ventsDuProduit = viewModel.getterFocusedVarsHandlerFacade.onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent
        .filter { it.parentM1ProduitInfosKeyId == produit.keyID }

    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

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

                if (ventsDuProduit.isNotEmpty()) {
                    // Existing logic: distribute quantity among existing vents
                    val quantityPerItem = newQuantity / ventsDuProduit.size
                    val remainder = newQuantity % ventsDuProduit.size

                    ventsDuProduit.forEachIndexed { index, ventItem ->
                        val itemQuantity = quantityPerItem + if (index < remainder) 1 else 0
                        val updatedVent = ventItem.copy(
                            quantityAchete = itemQuantity,
                        )
                        repo10OperationVentCouleur.addOrUpdateData(updatedVent)
                    }
                } else {
                    val productColors = repo3CouleurProduitInfos.datasValue.filter {
                        it.parentBProduitInfosKeyID == produit.keyID
                    }

                    if (productColors.isNotEmpty() && newQuantity > 0) {
                        val defaultVent = viewModel.getterFocusedVarsHandlerFacade.getDefaultM10VentOperation()

                        if (defaultVent != null) {
                            val quantityPerColor = newQuantity / productColors.size
                            val remainder = newQuantity % productColors.size

                            productColors.forEachIndexed { index, color ->
                                val itemQuantity = quantityPerColor + if (index < remainder) 1 else 0

                                val newVent = defaultVent.copy(
                                    keyID = getPushFireBase(ref),
                                    parentM1ProduitInfosKeyId = produit.keyID,
                                    parentM1ProduitDebugInfos = produit.nom,
                                    parentM3CouleurProduitInfosKeyID = color.key,
                                    parentM3CouleurProduitDebugInfos = "${produit.nom}_${color.indexCouleurDansAncienProto}",
                                    quantityAchete = itemQuantity,
                                    etateActuellementEst = if (itemQuantity > 0) {
                                        M10OperationVentCouleur.EtateActuellementEst.ParentBonVentConfirme
                                    } else {
                                        M10OperationVentCouleur.EtateActuellementEst.SUPP_AU_PANIER_FINALE
                                    },
                                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                )

                                repo10OperationVentCouleur.addOrUpdateData(newVent)
                            }
                        }
                    }
                }

                viewModel.setterFocusedVarsHandlerFacade.fermeFocucePourPrixDeM1ProduitDialogChoisireQuantityFacade(produit)
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
