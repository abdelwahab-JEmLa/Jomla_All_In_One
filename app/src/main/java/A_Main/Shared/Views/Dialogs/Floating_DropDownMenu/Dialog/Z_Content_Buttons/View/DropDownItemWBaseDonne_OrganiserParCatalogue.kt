package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View

import V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite.SyncProgressIndicator
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DropDownItemWBaseDonne_OrganiserParCatalogue(
    progress: Float?,
    enabled:  Boolean,
    onClick:  () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector        = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint               = if (enabled) MaterialTheme.colorScheme.primary
                                         else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
            },
            text = {
                Text(
                    text = when {
                        progress == null -> "Organiser images par Catalogue (DropBox)"
                        progress < 1f    -> "Déplacement… ${(progress * 100).toInt()} %"
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
        if (progress != null) SyncProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }
}
