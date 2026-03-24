package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import EntreApps.Shared.Models.Prioriter
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// ---------------------------------------------------------------------------
// Three-chip toggle row for affiche_produits_Ou_On_TagPrioriter
// ---------------------------------------------------------------------------
@Composable
 fun PrioriterToggleItem(viewModelNewProtoPatterns: ViewModel_NewProtoPatterns) {
    val activeFilter = viewModelNewProtoPatterns.active_Datas.affiche_produits_Ou_On_TagPrioriter
    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(modifier = Modifier.Companion.padding(vertical = 4.dp)) {
                Text(
                    text = "Priorité affichage produits",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.Companion.padding(bottom = 6.dp)
                )
                Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                    Prioriter.entries.forEach { prioriter ->
                        val isSelected = activeFilter?.contains(prioriter) == true
                        val label = when (prioriter) {
                            Prioriter.Dernier_VentAchat_Est_Moin_Mois -> "< Mois"
                            Prioriter.Dernier_VentAchat_Est_Moin_Semain -> "< Sem"
                            Prioriter.PlusDe80P_Ne_Le_Voit_Pas -> "80%"
                            else -> {
                                ""
                            }
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val current = activeFilter?.toMutableSet() ?: mutableSetOf()
                                if (isSelected) current.remove(prioriter) else current.add(prioriter)
                                viewModelNewProtoPatterns.active_Datas
                                    .affiche_produits_Ou_On_TagPrioriter =
                                    current.ifEmpty { null }
                            },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                            modifier = Modifier.Companion.padding(end = 4.dp)
                        )
                    }
                }
            }
        },
        onClick = { /* row is non-clickable; chips handle their own clicks */ }
    )
}
