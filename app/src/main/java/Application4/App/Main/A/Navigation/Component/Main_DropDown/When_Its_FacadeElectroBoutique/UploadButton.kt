package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// Upload button (unchanged logic)
// ---------------------------------------------------------------------------
@Composable
 fun UploadButton(
    isUploading: Boolean,
    coroutineScope: CoroutineScope,
    onDismissDropdown: () -> Unit
) {
    DropdownMenuItem(
        leadingIcon = {
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.Companion.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        },
        text = {
            Text(
                text = if (isUploading) "Envoi en cours..." else "Envoyer produits filtrés → Firestore",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        enabled = !isUploading,
        onClick = {
            coroutineScope.launch(Dispatchers.IO) {
                onDismissDropdown()
            }
        }
    )
}
