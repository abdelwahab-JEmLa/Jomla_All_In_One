package V.DiviseParSections.App._0.Navigation

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CatalogSelectionDialog(
    onDismiss: () -> Unit,
    onCatalogSelected: (Long) -> Unit,
    viewModelInitApp: ViewModelInitApp
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sélectionner un catalogue") },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    TextButton(
                        onClick = { onCatalogSelected(148L) }
                    ) {
                        Text("Catalogue Cosmétiques")
                    }

                    TextButton(
                        onClick = { onCatalogSelected(149L) }
                    ) {
                        Text("Catalogue Confiseries")
                    }

                    TextButton(
                        onClick = { onCatalogSelected(150L) }
                    ) {
                        Text("Catalogue Téléphones")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
