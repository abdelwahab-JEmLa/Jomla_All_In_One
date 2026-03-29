package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import EntreApps.Shared.Models.Do
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UpdateActiveComptDo_DropdownMenuItem(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
    onDismissDropdown: () -> Unit
) {
    val activeCompt = viewModelNewProtoPatterns.active_Datas.active_M9Compt
    val currentDo = activeCompt?.next_start ?: Do.StandartInit

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = "Mode synchronisation",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "Mode synchronisation",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Do.entries.forEach { doOption ->
                    val label = when (doOption) {
                        Do.StandartInit                                   -> "Standard Init"
                        Do.DeleteInsertAll_Active_Key                     -> "Réinit clé active"
                        Do.DeleteAll_To_Let_Ancien_Repositorys_GetAll     -> "Réinit toutes les données"
                        Do.DeleteInsertAll_Ref_All_Datas                  -> "Réinit toutes les données de référence"
                    }
                    FilterChip(
                        selected = currentDo == doOption,
                        onClick = {
                            activeCompt?.let { compt ->
                                val updatedCompt = compt.copy(next_start = doOption)
                                viewModelNewProtoPatterns.update_active_Compt(updatedCompt)
                            }
                            onDismissDropdown()
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        },
        onClick = {}
    )
}
