package Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog.Buttons.View.Ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Generic warning/confirmation dialog.
 *
 * @param title        Title shown in bold at the top.
 * @param message      Body text describing what will happen.
 * @param confirmLabel Label for the destructive confirm button (default "Confirmer").
 * @param cancelLabel  Label for the cancel button (default "Annuler").
 * @param onConfirm    Called when the user accepts.
 * @param onDismiss    Called when the user cancels or taps outside.
 */
@Composable
fun AvertissementDialog(
    title:        String,
    message:      String,
    confirmLabel: String = "Confirmer",
    cancelLabel:  String = "Annuler",
    onConfirm:    () -> Unit,
    onDismiss:    () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector        = Icons.Default.Warning,
                contentDescription = null,
                tint               = Color(0xFFFFA000)   // amber warning colour
            )
        },
        title = {
            Text(
                text  = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        text = {
            Text(
                text  = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(
                    text  = confirmLabel,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text  = cancelLabel,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
    )
}
