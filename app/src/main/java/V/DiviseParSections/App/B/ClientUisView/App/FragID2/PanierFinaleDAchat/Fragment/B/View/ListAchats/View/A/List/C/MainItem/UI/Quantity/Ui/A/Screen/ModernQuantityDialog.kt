package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.C.MainItem.UI.Quantity.Ui.A.Screen

import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.C.MainItem.UI.Quantity.Ui.B.List.QuantityGrid
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ClickUpdate
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties

@Composable
fun ModernQuantityDialog(
    clickUpdate: ClickUpdate = ClickUpdate.CouleurQua, // FIXED: Added parameter with default value
    colorName: String,
    currentQuantity: Int,
    onDissmiss_showQuantityDialog: () -> Unit,
    onDismiss: () -> Unit,
    viewModel: ZViewModel_Sec1Frag3,
    vent: FCouleurVentOperationInfos
) {
    var selectedQuantity by remember { mutableStateOf(currentQuantity) }
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
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
                    text = colorName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column {
                QuantityGrid(
                    clickUpdate = clickUpdate, // FIXED: Pass the click update mode parameter
                    vent = vent,
                    currentQuantity = selectedQuantity,
                    onQuantitySelected = { newQuantity ->
                        selectedQuantity = newQuantity
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                        onDissmiss_showQuantityDialog()

                        val message = if (newQuantity == 0) {
                            "Removed $colorName from cart"
                        } else {
                            "Updated $colorName quantity to $newQuantity"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                        onDismiss()
                    },
                    viewModel = viewModel
                )
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = onDismiss
            ) {
                Text("Close")
            }
        }
    )
}
