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
            title = {
                Text(text = "اعلام")
            },
            text = {
                Text(text = "بقي $nombreClientAvecCibleCommeLastBonAchat لإكمال الخدمة معهم. هل تمت وظيفة اليوم؟")
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
