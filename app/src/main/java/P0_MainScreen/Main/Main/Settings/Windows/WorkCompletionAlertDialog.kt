package P0_MainScreen.Main.Main.Settings.Windows

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun WorkCompletionAlertDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit = {}, // Added callback for confirmation action
    nombreClientAvecCibleCommeLastBonAchat: Int = 0
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            text = {
                val nombreClientAvecCibleCommeLastBonAchat1 = if(nombreClientAvecCibleCommeLastBonAchat > 1)
                    nombreClientAvecCibleCommeLastBonAchat.toString() else ""

                Text(text = "يرجى تعيين تقارير  $nombreClientAvecCibleCommeLastBonAchat1 زبون لإكمال الخدمة .")
            },
            confirmButton = {
                // Only show confirm button when remaining clients < 3
                if (nombreClientAvecCibleCommeLastBonAchat < 5) {
                    TextButton(
                        onClick = {
                            onConfirm() // Execute confirmation action
                            onDismiss() // Then dismiss dialog
                        }
                    ) {
                        Text("موافق")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("العودة")
                }
            }
        )
    }
}
