package Application4.App.Fragment.ID1.Fragment.Dialogs.Dialog.Buttons.View

import V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite.SyncProgressIndicator
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DropDownItemWBaseDonne_SyncDepuisImages2(
    progress:     Float?,
    enabled:      Boolean,
    onClick:      () -> Unit,
    currentLabel: String = "",
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        DropdownMenuItem(
            text = {
                Text(
                    text = when {
                        progress == null -> "Sync local ← DropBox Images_2"
                        progress < 1f    -> "Téléchargement… ${(progress * 100).toInt()} %"
                        else             -> "Terminé ✓"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            },
            enabled = enabled,
            onClick = onClick
        )

        if (progress != null) {
            // Current file label (product name + index)
            if (currentLabel.isNotBlank()) {
                Text(
                    text     = currentLabel,
                    style    = MaterialTheme.typography.labelSmall,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            SyncProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}
