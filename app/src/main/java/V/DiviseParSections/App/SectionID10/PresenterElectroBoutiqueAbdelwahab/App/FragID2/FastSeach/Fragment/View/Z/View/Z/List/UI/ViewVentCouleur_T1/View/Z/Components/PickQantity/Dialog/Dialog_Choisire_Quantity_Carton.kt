package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.ViewVentCouleur_T1.View.Z.Components.PickQantity.Dialog

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.B.List.QuantityGrid_Carton
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/**
 * Specialized dialog for updating quantities by carton only.
 * This dialog is specifically designed for carton-based quantity operations.
 */
@Composable
fun Dialog_Choisire_Quantity_Carton(
    old_quantity: Int,
    quantite_Boit_Par_Carton: Int,
    label: String,
    onClick_Quantity_Button: (Int?) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    // Force carton-based quantity representation for this specialized dialog
    val setIN_Vent_Its_Quantity_Represent = M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton

    AlertDialog(
        onDismissRequest = {
            // Fixed: Provide haptic feedback on dismiss
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick_Quantity_Button(null)
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        title = {
            Column {
                Text(
                    text = "Select Quantity (By Carton)",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Show carton information
                if (quantite_Boit_Par_Carton > 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1 carton = $quantite_Boit_Par_Carton units",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        text = {
            Column {
                QuantityGrid_Carton(
                    old_quantity = old_quantity,
                    setIN_Vent_Its_Quantity_Represent = setIN_Vent_Its_Quantity_Represent,
                    quantite_Boit_Par_Carton = quantite_Boit_Par_Carton,
                    on_Dismiss_Confirme_New_Quantity = { newQuantity ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                        // Calculate carton quantity for display message
                        val cartonQuantity = newQuantity / quantite_Boit_Par_Carton

                        val message = if (newQuantity == 0) {
                            "Removed $label from cart"
                        } else {
                            "Updated $label: $cartonQuantity carton${if (cartonQuantity != 1) "s" else ""} ($newQuantity units total)"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                        onClick_Quantity_Button(newQuantity)
                    }
                )
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    // Fixed: Add haptic feedback and ensure proper dismissal
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick_Quantity_Button(null)
                }
            ) {
                Text("Close")
            }
        }
    )
}
