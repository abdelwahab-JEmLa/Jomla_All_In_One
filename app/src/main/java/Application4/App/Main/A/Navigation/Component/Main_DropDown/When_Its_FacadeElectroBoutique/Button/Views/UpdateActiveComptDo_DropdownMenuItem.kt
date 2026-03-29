package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import EntreApps.Shared.Models.Do
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UpdateActiveComptDo_DropdownMenuItem(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
    onDismissDropdown: () -> Unit
) {
    val activeCompt = viewModelNewProtoPatterns.active_Datas.active_M9Compt
    val currentDo = activeCompt?.next_start ?: Do.StandartInit_Sans_RienFair
    var isExpanded by remember { mutableStateOf(false) }

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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mode synchronisation",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isExpanded) {
                    Do.entries.forEach { doOption ->
                        val label = when (doOption) {
                            Do.StandartInit_Sans_RienFair                                -> "Standard Init"
                            Do.DeleteInsertAll_Active_Key                                -> "Réinit clé active"
                            Do.DeleteAll_To_Let_Ancien_Repositorys_GetAll                -> "Réinit toutes les données"
                            Do.DeleteInsertAll_Ref_All_Datas                             -> "Réinit toutes les données de référence"
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
                } else {
                    // Show current selection when collapsed
                    val currentLabel = when (currentDo) {
                        Do.StandartInit_Sans_RienFair                                -> "Standard Init"
                        Do.DeleteInsertAll_Active_Key                                -> "Réinit clé active"
                        Do.DeleteAll_To_Let_Ancien_Repositorys_GetAll                -> "Réinit toutes les données"
                        Do.DeleteInsertAll_Ref_All_Datas                             -> "Réinit toutes les données de référence"
                    }
                    Text(
                        text = currentLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        onClick = {}
    )
}
