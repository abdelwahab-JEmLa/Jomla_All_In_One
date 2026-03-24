package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun UpdateActiveCompt_DropdownMenuItem(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
    onDismissDropdown: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    
    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "Compte actif",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                
                val activeCompt = viewModelNewProtoPatterns.active_Datas.active_M9Compt
                Text(
                    text = activeCompt?.nom?.takeIf { it.isNotBlank() } ?: "Aucun compte sélectionné",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (activeCompt != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                
                TextButton(
                    onClick = { 
                        showDialog = true
                        onDismissDropdown()
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Changer de compte")
                }
            }
        },
        onClick = {}
    )
    
    if (showDialog) {
        ActiveComptSelectionDialog(
            viewModelNewProtoPatterns = viewModelNewProtoPatterns,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun ActiveComptSelectionDialog(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    MaterialTheme.shapes.medium
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Sélectionner un compte",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Get all available comptes from the state
            val allComptes = viewModelNewProtoPatterns._uiStateNewProtoPatterns.value.list_Datas?.m9AppCompt ?: emptyList()
            
            allComptes.forEach { compt ->
                TextButton(
                    onClick = {
                        viewModelNewProtoPatterns.update_active_Compt(compt)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = compt.nom.ifBlank { compt.keyID.take(8) },
                        color = if (viewModelNewProtoPatterns.active_Datas.active_M9Compt?.keyID == compt.keyID) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
