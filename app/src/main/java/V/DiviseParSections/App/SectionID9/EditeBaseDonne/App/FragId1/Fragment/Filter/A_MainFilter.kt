package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.Models.FilterState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun A_MainFilter(
    filterState: FilterState,
    onFilterChanged: (FilterState) -> Unit,
    totalCount: Int,
    filteredCount: Int,
    modifier: Modifier = Modifier
) {
    val hasActiveFilters =
        filterState.hideQuiNeSontPas_cUnNeveauArrivage ||
                filterState.hideNonDispo ||
                filterState.hideDispoOnly ||
                filterState.hidePetiteProbability ||
                filterState.hidePrixAchatZero ||
                filterState.hidePrixAchatPositif ||
                filterState.hideHeldPrioriteDemandAuGrossist ||
                filterState.hideNonHeldPrioriteDemandAuGrossist ||
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
                    if (filterState.hideQuiNeSontPas_cUnNeveauArrivage) {
                        FilterChip(
                            label = "Masquer Sans Neveau Arrivage",
                            onRemove = { onFilterChanged(filterState.copy(hideQuiNeSontPas_cUnNeveauArrivage = false)) }
                        )
                    }

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

                    if (filterState.hideHeldPrioriteDemandAuGrossist) {
                        FilterChip(
                            label = "Masquer Priorité Grossiste",
                            onRemove = { onFilterChanged(filterState.copy(hideHeldPrioriteDemandAuGrossist = false)) }
                        )
                    }

                    if (filterState.hideNonHeldPrioriteDemandAuGrossist) {
                        FilterChip(
                            label = "Masquer Non-Priorité Grossiste",
                            onRemove = { onFilterChanged(filterState.copy(hideNonHeldPrioriteDemandAuGrossist = false)) }
                        )
                    }
                }
            }
        }
    }
}
