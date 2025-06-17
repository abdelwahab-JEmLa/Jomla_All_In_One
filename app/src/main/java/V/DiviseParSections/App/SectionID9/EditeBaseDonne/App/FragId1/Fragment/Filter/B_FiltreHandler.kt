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
import androidx.compose.material3.Divider
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

enum class SortOrder {
    ID_DESC,
    ID_ASC,
    NAME_ASC,
    NAME_DESC,
    CATEGORY_GROUPED
}

data class FilterState(
    val hideNonDispo: Boolean = false,
    val hideDispoOnly: Boolean = false,
    val hidePetiteProbability: Boolean = false,
    val hidePrixAchatZero: Boolean = false,
    val hidePrixAchatPositif: Boolean = false,
    val searchText: String = "",
    val sortOrder: SortOrder = SortOrder.CATEGORY_GROUPED,
    val enableCategoryGrouping: Boolean = true
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
                text = "Filtres et tri",
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

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Sorting options section
            Text(
                text = "Options de tri",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            FilterOption(
                label = "Grouper par catégories (comme EditeCategoriesMainList)",
                checked = filterState.enableCategoryGrouping,
                onCheckedChange = {
                    onFilterChanged(
                        filterState.copy(
                            enableCategoryGrouping = it,
                            sortOrder = if (it) SortOrder.CATEGORY_GROUPED else SortOrder.ID_DESC
                        )
                    )
                }
            )

            // Sort order options (only show when not grouping by categories)
            if (!filterState.enableCategoryGrouping) {
                Text(
                    text = "Ordre de tri:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                SortOption(
                    label = "ID décroissant (plus récent)",
                    selected = filterState.sortOrder == SortOrder.ID_DESC,
                    onClick = { onFilterChanged(filterState.copy(sortOrder = SortOrder.ID_DESC)) }
                )

                SortOption(
                    label = "ID croissant (plus ancien)",
                    selected = filterState.sortOrder == SortOrder.ID_ASC,
                    onClick = { onFilterChanged(filterState.copy(sortOrder = SortOrder.ID_ASC)) }
                )

                SortOption(
                    label = "Nom A-Z",
                    selected = filterState.sortOrder == SortOrder.NAME_ASC,
                    onClick = { onFilterChanged(filterState.copy(sortOrder = SortOrder.NAME_ASC)) }
                )

                SortOption(
                    label = "Nom Z-A",
                    selected = filterState.sortOrder == SortOrder.NAME_DESC,
                    onClick = { onFilterChanged(filterState.copy(sortOrder = SortOrder.NAME_DESC)) }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Filtres de disponibilité",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
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

                androidx.compose.material3.TextButton(
                    onClick = {
                        onFilterChanged(
                            FilterState(
                                hideNonDispo = true,
                                hideDispoOnly = true,
                                hidePetiteProbability = true,
                                hidePrixAchatZero = true,
                                hidePrixAchatPositif = true,
                                searchText = filterState.searchText,
                                sortOrder = filterState.sortOrder,
                                enableCategoryGrouping = filterState.enableCategoryGrouping
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

@Composable
private fun SortOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        androidx.compose.material3.RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}
