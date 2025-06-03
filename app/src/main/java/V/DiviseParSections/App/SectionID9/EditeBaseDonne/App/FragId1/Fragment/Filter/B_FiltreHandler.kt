package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class FilterState(
    val hideNonDispo: Boolean = false,
    val hideDispoOnly: Boolean = false,
    val hidePetiteProbability: Boolean = false
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
        filterState.hideNonDispo || filterState.hideDispoOnly || filterState.hidePetiteProbability

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
