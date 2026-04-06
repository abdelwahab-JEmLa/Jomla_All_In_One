package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Buttons.View.Ui

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.SyncReport
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Displays a summary of what [A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.DropBox_Init_3.syncFromImages2] did:
 * - how many files were newly added
 * - how many files were overwritten
 * with the individual file names listed under each heading.
 */
@Composable
 fun SyncReportDialog(
    report: SyncReport,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50)   // green
            )
        },
        title = {
            Text(
                text = "Synchronisation terminée",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        text = {
            Column {
                if (report.isEmpty) {
                    Text(
                        text = "Aucun fichier modifié — tout est déjà à jour.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    if (report.added.isNotEmpty()) {
                        Text(
                            text = "✅ Ajoutés (${report.added.size})",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        report.added.forEach { name ->
                            Text(
                                text = "  • $name",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    if (report.added.isNotEmpty() && report.overwritten.isNotEmpty()) {
                        Spacer(modifier = Modifier.Companion.height(8.dp))
                    }

                    if (report.overwritten.isNotEmpty()) {
                        Text(
                            text = "🔄 Écrasés (${report.overwritten.size})",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                        report.overwritten.forEach { name ->
                            Text(
                                text = "  • $name",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "OK",
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    )
}
