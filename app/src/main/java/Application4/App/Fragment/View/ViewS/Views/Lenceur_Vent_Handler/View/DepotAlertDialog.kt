package Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import kotlin.math.abs

@Composable
 fun DepotAlertDialog(
    alertInfo: DepotUpdateResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Alerte Stock", style = MaterialTheme.typography.titleMedium) },
        text = {
            Text(
                text = buildString {
                    append(alertInfo.message)
                    append("\n\n")
                    append("Stock actuel: ${alertInfo.currentCount}")
                    append("\n")
                    append("Quantité demandée: ${abs(alertInfo.requestedChange)}")
                }
            )
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } }
    )
}
