package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.B.List.QuantityGrid_T1
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties

@Composable
fun Dialog_Choisire_Quantity_Modularized(
    old_quantity: Int,
    setIN_Vent_Its_Quantity_Represent: M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent=
        M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit,
    quantite_Boit_Par_Carton: Int= 1,
    label: String,
    onClick_Quantity_Button: (Int?) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = {
            onClick_Quantity_Button(null)
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        title = {
            Column {
                Text(
                    text = "Select Quantity",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column {
                QuantityGrid_T1(
                    old_quantity = old_quantity,
                    setIN_Vent_Its_Quantity_Represent = setIN_Vent_Its_Quantity_Represent,
                    quantite_Boit_Par_Carton = quantite_Boit_Par_Carton,
                    on_Dismiss_Confirme_New_Quantity = { newQuantity ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                        val message = if (newQuantity == 0) {
                            "Removed $label from cart"
                        } else {
                            "Updated $label quantity to $newQuantity"
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
                    onClick_Quantity_Button(null)
                },
            ) {
                Text("Close")
            }
        }
    )
}
