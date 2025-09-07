// Enhanced QuantityButton.kt - Fixed TODOs
package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.ViewVentCouleur_T1.View.Z.Components.PickQantity.Dialog.List.UI

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.rememberQuantityButtonAnimations
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
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
    newQuantity: Int,
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    setIN_Vent_Its_Quantity_Represent: M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent,
    quantite_Boit_Par_Carton: Int,
    onClick: (Int) -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    val animations = rememberQuantityButtonAnimations(isSelected)

    val quantity_Finale = when (setIN_Vent_Its_Quantity_Represent) {
        M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton -> {
            newQuantity * quantite_Boit_Par_Carton
        }

        M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit -> {
            newQuantity
        }
    }

    Surface(
        modifier = modifier
            .getSemanticsTag(nomVal = "quantity_Finale", data = quantity_Finale)
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
                onClick(quantity_Finale)
            }
            .scale(animations.scale)
            .size(56.dp)
            ,
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
