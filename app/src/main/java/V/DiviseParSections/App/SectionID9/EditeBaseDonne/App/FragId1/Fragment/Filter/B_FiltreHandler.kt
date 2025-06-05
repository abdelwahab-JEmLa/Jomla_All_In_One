package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class FilterState(
    val hideNonDispo: Boolean = false,
    val hideDispoOnly: Boolean = false,
    val hidePetiteProbability: Boolean = false,
    val hidePrixAchatZero: Boolean = false,
    val hidePrixAchatPositif: Boolean = false,
    val searchText: String = ""
)

@Composable
fun MainFilter(
    filterState: FilterState,
    onFilterChanged: (FilterState) -> Unit,
    totalCount: Int,
    filteredCount: Int,
    modifier: Modifier = Modifier
) {
    val hasActiveFilters =
        filterState.hideNonDispo ||
                filterState.hideDispoOnly ||
                filterState.hidePetiteProbability ||
                filterState.hidePrixAchatZero ||
                filterState.hidePrixAchatPositif ||
                filterState.searchText.isNotEmpty()

    if (hasActiveFilters) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Filtres actifs - Affichage: $filteredCount/$totalCount produits",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Search text filter chip
                if (filterState.searchText.isNotEmpty()) {
                    FilterChip(
                        label = "Recherche: \"${filterState.searchText}\"",
                        onRemove = { onFilterChanged(filterState.copy(searchText = "")) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (filterState.hideNonDispo) {
                        FilterChip(
                            label = "Masquer Non Dispo",
                            onRemove = { onFilterChanged(filterState.copy(hideNonDispo = false)) }
                        )
                    }

                    if (filterState.hideDispoOnly) {
                        FilterChip(
                            label = "Masquer Dispo",
                            onRemove = { onFilterChanged(filterState.copy(hideDispoOnly = false)) }
                        )
                    }

                    if (filterState.hidePetiteProbability) {
                        FilterChip(
                            label = "Masquer Possible",
                            onRemove = { onFilterChanged(filterState.copy(hidePetiteProbability = false)) }
                        )
                    }

                    if (filterState.hidePrixAchatZero) {
                        FilterChip(
                            label = "Masquer Prix Achat = 0",
                            onRemove = { onFilterChanged(filterState.copy(hidePrixAchatZero = false)) }
                        )
                    }

                    if (filterState.hidePrixAchatPositif) {
                        FilterChip(
                            label = "Masquer Prix Achat > 0",
                            onRemove = { onFilterChanged(filterState.copy(hidePrixAchatPositif = false)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(16.dp)
            ) {
                Text(
                    text = "×",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun FilterDropdownMenu(
    filterState: FilterState,
    onFilterChanged: (FilterState) -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.padding(top = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Filtres de disponibilité",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            // Search field
            OutlinedTextField(
                value = filterState.searchText,
                onValueChange = {
                    onFilterChanged(filterState.copy(searchText = it))
                },
                label = { Text("Rechercher par nom") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Rechercher"
                    )
                },
                trailingIcon = {
                    if (filterState.searchText.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                onFilterChanged(filterState.copy(searchText = ""))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Effacer"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            FilterOption(
                label = "Masquer produits non disponibles",
                checked = filterState.hideNonDispo,
                onCheckedChange = {
                    onFilterChanged(filterState.copy(hideNonDispo = it))
                }
            )

            FilterOption(
                label = "Masquer produits disponibles",
                checked = filterState.hideDispoOnly,
                onCheckedChange = {
                    onFilterChanged(filterState.copy(hideDispoOnly = it))
                }
            )

            FilterOption(
                label = "Masquer produits possibles",
                checked = filterState.hidePetiteProbability,
                onCheckedChange = {
                    onFilterChanged(filterState.copy(hidePetiteProbability = it))
                }
            )

            FilterOption(
                label = "Masquer produits sans prix d'achat",
                checked = filterState.hidePrixAchatZero,
                onCheckedChange = {
                    onFilterChanged(filterState.copy(hidePrixAchatZero = it))
                }
            )

            FilterOption(
                label = "Masquer produits avec prix d'achat",
                checked = filterState.hidePrixAchatPositif,
                onCheckedChange = {
                    onFilterChanged(filterState.copy(hidePrixAchatPositif = it))
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                androidx.compose.material3.TextButton(
                    onClick = {
                        onFilterChanged(FilterState())
                        onDismiss()
                    }
                ) {
                    Text("Réinitialiser")
                }

                // Added: Button to activate all filters
                androidx.compose.material3.TextButton(
                    onClick = {
                        onFilterChanged(
                            FilterState(
                                hideNonDispo = true,
                                hideDispoOnly = true,
                                hidePetiteProbability = true,
                                hidePrixAchatZero = true,
                                hidePrixAchatPositif = true,
                                searchText = filterState.searchText // Keep current search text
                            )
                        )
                    }
                ) {
                    Text("Tout Activer")
                }

                androidx.compose.material3.TextButton(
                    onClick = onDismiss
                ) {
                    Text("Fermer")
                }
            }
        }
    }
}

@Composable
private fun FilterOption(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        androidx.compose.material3.Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
