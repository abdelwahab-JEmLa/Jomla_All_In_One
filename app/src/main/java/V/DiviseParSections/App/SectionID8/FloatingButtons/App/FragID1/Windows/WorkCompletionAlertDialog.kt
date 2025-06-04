package V.DiviseParSections.App.SectionID8.FloatingButtons.App.FragID1.Windows

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun WorkCompletionAlertDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    nombreClientAvecCibleCommeLastBonAchat: Int = 0
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            text = {
                val nombreClientAvecCibleCommeLastBonAchat1 = if(nombreClientAvecCibleCommeLastBonAchat>1)
                    nombreClientAvecCibleCommeLastBonAchat.toString() else ""

                Text(text = "يرجى تعيين تقارير  $nombreClientAvecCibleCommeLastBonAchat1 زبون لإكمال الخدمة .")
            },
            confirmButton = {
                // Removed - only showing information
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
