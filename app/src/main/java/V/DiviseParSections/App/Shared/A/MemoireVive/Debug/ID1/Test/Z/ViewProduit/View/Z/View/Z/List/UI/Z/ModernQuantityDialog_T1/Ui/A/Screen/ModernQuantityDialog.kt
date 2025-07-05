package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ClickUpdate
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Z.ViewProduit.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.B.List.QuantityGrid_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.GetterFocusedVars.Companion.getSemanticsTagFocucedVars
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties

@Composable
fun VentCouleurQuantityDialog_T1(
    vent: M10OperationVentCouleur,
    viewModel: ViewModelsProduit_T1,
    clickUpdate: ClickUpdate = ClickUpdate.CouleurQua,
    colorName: String,
    currentQuantity: Int,
) {
    var selectedQuantity by remember { mutableStateOf(currentQuantity) }
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    fun closeDialogChoisireQuantity(): Unit {
        viewModel.setterFocusedVarsHandlerFacade.closeDialogChoisireQuantity()
    }

    AlertDialog(
        onDismissRequest = { closeDialogChoisireQuantity() },
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
                QuantityGrid_T1(
                    clickUpdate = clickUpdate,
                    vent = vent,
                    currentQuantity = selectedQuantity,
                    onQuantitySelected = { newQuantity ->
                        selectedQuantity = newQuantity
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                        val message = if (newQuantity == 0) {
                            "Removed $colorName from cart"
                        } else {
                            "Updated $colorName quantity to $newQuantity"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                        closeDialogChoisireQuantity()
                    },
                    viewModel = viewModel
                )
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    closeDialogChoisireQuantity()
                },
                modifier = Modifier.getSemanticsTagFocucedVars(viewModel.getterFocusedVarsHandlerFacade)
            ) {
                Text("Close")
            }
        }
    )
}
