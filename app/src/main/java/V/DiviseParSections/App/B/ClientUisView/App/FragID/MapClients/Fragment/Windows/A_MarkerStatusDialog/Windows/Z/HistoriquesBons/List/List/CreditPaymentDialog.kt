package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CreditPaymentDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit,
    sumBonVents: Double = 0.0
) {
    var versementText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Auto-focus on the text field when dialog opens
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("إدخال مبلغ الدفع")
        },
        text = {
            Column {
                Text(
                    text = "أدخل مبلغ الدفع للمعاملة الائتمانية",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = versementText,
                    onValueChange = { newValue ->
                        versementText = newValue
                        // Auto-confirm when user enters a value or when field becomes empty
                        val amount = if (newValue.isEmpty()) {
                            sumBonVents
                        } else {
                            newValue.toDoubleOrNull()
                        }
                        
                        if (amount != null && amount > 0) {
                            onConfirm(amount)
                        }
                    },
                    label = { Text("مبلغ الدفع") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val amount = if (versementText.isEmpty()) {
                                sumBonVents
                            } else {
                                versementText.toDoubleOrNull()
                            }
                            
                            if (amount != null && amount > 0) {
                                onConfirm(amount)
                            }
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }
        },
        confirmButton = {
            // Remove the confirm button as requested
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
