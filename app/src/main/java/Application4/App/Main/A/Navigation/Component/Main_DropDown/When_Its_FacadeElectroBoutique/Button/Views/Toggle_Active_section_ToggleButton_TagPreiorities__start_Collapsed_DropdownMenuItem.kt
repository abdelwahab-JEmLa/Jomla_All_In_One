package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Toggle_Active_section_ToggleButton_TagPreiorities__start_Collapsed_DropdownMenuItem(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
    onDismissDropdown: () -> Unit
) {                            //<--
//TODO(1): fait que ca toggle section_ToggleButton_TagPrioriter__start_Collapsed si null true si true null
    
    val activeCompt = viewModelNewProtoPatterns.active_Datas.active_M9Compt
    val showEditedProducts = activeCompt?.affiche_ProduitDataBaseEdites_ComposableViews ?: false

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = if (showEditedProducts) Icons.Default.Edit else Icons.Default.EditOff,
                contentDescription = if (showEditedProducts) "Masquer produits édités" else "Afficher produits édités",
                tint = if (showEditedProducts) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        text = {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "Produits édités",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Text(
                    text = if (showEditedProducts) "Afficher les produits modifiés" else "Masquer les produits modifiés",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (showEditedProducts) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        trailingIcon = {
            Switch(
                checked = showEditedProducts,
                onCheckedChange = { isChecked ->
                    activeCompt?.let { compt ->
                        val updatedCompt = compt.copy(
                            affiche_ProduitDataBaseEdites_ComposableViews = isChecked
                        )
                        viewModelNewProtoPatterns.update_active_Compt(updatedCompt)
                    }
                    onDismissDropdown()
                }
            )
        },
        onClick = {}
    )
}
