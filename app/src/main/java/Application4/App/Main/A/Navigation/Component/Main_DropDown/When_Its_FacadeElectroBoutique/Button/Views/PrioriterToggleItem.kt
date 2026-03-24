package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Model.Prioriter
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PrioriterToggleItem(viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns) {
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
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "Priorité affichage produits",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
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
            }
        },
        onClick = {}
    )
}
