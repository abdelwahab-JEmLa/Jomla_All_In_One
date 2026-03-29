package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Prioriter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PrioriterToggle_DropdownMenuItem(viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns) {
    val activeFilter = viewModelNewProtoPatterns.active_Datas.affiche_produits_Ou_On_TagPrioriter
    var isExpanded by remember { mutableStateOf(false) }

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                // Header with click to expand/collapse
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Priorité affichage produits",
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

                // Show chips only when expanded
                if (isExpanded) {
                    Prioriter.entries.forEach { prioriter ->
                        val isSelected = activeFilter?.contains(prioriter) == true
                        val label = when (prioriter) {
                            Prioriter.Dernier_VentAchat_Est_Trop_Luin    -> "Dernier vente/achat trop loin"
                            Prioriter.Dernier_VentAchat_Est_Moin_Mois    -> "Dernier vente/achat < 1 mois"
                            Prioriter.Dernier_VentAchat_Est_Moin_Semain  -> "Dernier vente/achat < 1 semaine"
                            Prioriter.PlusDe80P_Ne_Le_Voit_Pas           -> "Plus de 80% ne le voient pas"
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val current = activeFilter?.toMutableSet() ?: mutableSetOf()
                                if (isSelected) current.remove(prioriter) else current.add(prioriter)
                                viewModelNewProtoPatterns.active_Datas
                                    .affiche_produits_Ou_On_TagPrioriter = current.ifEmpty { null }
                            },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.Red else MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Red.copy(alpha = 0.12f),
                                selectedLabelColor = Color.Red,
                            ),
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                } else {
                    // Show summary of active filters when collapsed
                    val activeCount = activeFilter?.size ?: 0
                    if (activeCount > 0) {
                        Text(
                            text = "$activeCount filtre${if (activeCount > 1) "s" else ""} actif${if (activeCount > 1) "s" else ""}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        },
        onClick = {}
    )
}
