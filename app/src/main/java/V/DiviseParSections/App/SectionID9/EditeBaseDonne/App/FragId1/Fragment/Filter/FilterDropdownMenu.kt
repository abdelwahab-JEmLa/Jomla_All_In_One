package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.Models.FilterState
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.Models.SortOrder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

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
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Filtres et tri",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
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
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                // Sorting options section
                Text(
                    text = "Options de tri",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
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
            }

            // Sort order options (only show when not grouping by categories)
            if (!filterState.enableCategoryGrouping) {
                item {
                    Text(
                        text = "Ordre de tri:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    SortOption(
                        label = "ID décroissant (plus récent)",
                        selected = filterState.sortOrder == SortOrder.ID_DESC,
                        onClick = { onFilterChanged(filterState.copy(sortOrder = SortOrder.ID_DESC)) }
                    )
                }

                item {
                    SortOption(
                        label = "ID croissant (plus ancien)",
                        selected = filterState.sortOrder == SortOrder.ID_ASC,
                        onClick = { onFilterChanged(filterState.copy(sortOrder = SortOrder.ID_ASC)) }
                    )
                }

                item {
                    SortOption(
                        label = "Nom A-Z",
                        selected = filterState.sortOrder == SortOrder.NAME_ASC,
                        onClick = { onFilterChanged(filterState.copy(sortOrder = SortOrder.NAME_ASC)) }
                    )
                }

                item {
                    SortOption(
                        label = "Nom Z-A",
                        selected = filterState.sortOrder == SortOrder.NAME_DESC,
                        onClick = { onFilterChanged(filterState.copy(sortOrder = SortOrder.NAME_DESC)) }
                    )
                }
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                Text(
                    text = "Filtres de niveau d'arrivage",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                FilterOption(
                    label = "Masquer produits sans niveau d'arrivage",
                    checked = filterState.hideQuiNeSontPas_cUnNeveauArrivage,
                    onCheckedChange = {
                        onFilterChanged(filterState.copy(hideQuiNeSontPas_cUnNeveauArrivage = it))
                    }
                )
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                Text(
                    text = "Filtres de disponibilité",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                FilterOption(
                    label = "Masquer produits non disponibles",
                    checked = filterState.hideNonDispo,
                    onCheckedChange = {
                        onFilterChanged(filterState.copy(hideNonDispo = it))
                    }
                )
            }

            item {
                FilterOption(
                    label = "Masquer produits disponibles",
                    checked = filterState.hideDispoOnly,
                    onCheckedChange = {
                        onFilterChanged(filterState.copy(hideDispoOnly = it))
                    }
                )
            }

            item {
                FilterOption(
                    label = "Masquer produits possibles",
                    checked = filterState.hidePetiteProbability,
                    onCheckedChange = {
                        onFilterChanged(filterState.copy(hidePetiteProbability = it))
                    }
                )
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                Text(
                    text = "Filtres de prix d'achat",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                FilterOption(
                    label = "Masquer produits sans prix d'achat",
                    checked = filterState.hidePrixAchatZero,
                    onCheckedChange = {
                        onFilterChanged(filterState.copy(hidePrixAchatZero = it))
                    }
                )
            }

            item {
                FilterOption(
                    label = "Masquer produits avec prix d'achat",
                    checked = filterState.hidePrixAchatPositif,
                    onCheckedChange = {
                        onFilterChanged(filterState.copy(hidePrixAchatPositif = it))
                    }
                )
            }

            // NEW: Time-based filter for prixAchatDernierTimeTempUpdate
            item {
                FilterOption(
                    label = "Filtrer par ancienneté de prix d'achat",
                    checked = filterState.enablePrixAchatTimeFilter,
                    onCheckedChange = {
                        onFilterChanged(filterState.copy(enablePrixAchatTimeFilter = it))
                    }
                )
            }

            if (filterState.enablePrixAchatTimeFilter) {
                item {
                    OutlinedTextField(
                        value = filterState.prixAchatTimeFilterDays,
                        onValueChange = {
                            // Only allow numeric input
                            val cleanedInput = it.filter { char -> char.isDigit() }
                            onFilterChanged(filterState.copy(prixAchatTimeFilterDays = cleanedInput))
                        },
                        label = { Text("Nombre de jours (ex: 1, 7, 30)") },
                        placeholder = { Text("1") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        supportingText = {
                            Text(
                                text = "Affiche seulement les produits dont le prix d'achat a été mis à jour il y a X jours ou plus",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // New selling price filters section
            item {
                Text(
                    text = "Filtres de prix de vente",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                FilterOption(
                    label = "Masquer produits sans prix de vente",
                    checked = filterState.hidePrixVenteZero,
                    onCheckedChange = {
                        onFilterChanged(filterState.copy(hidePrixVenteZero = it))
                    }
                )
            }

            item {
                FilterOption(
                    label = "Masquer produits avec prix de vente",
                    checked = filterState.hidePrixVentePositif,
                    onCheckedChange = {
                        onFilterChanged(filterState.copy(hidePrixVentePositif = it))
                    }
                )
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                Text(
                    text = "Filtres de priorité grossiste",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                FilterOption(
                    label = "Masquer produits avec priorité grossiste",
                    checked = filterState.hideHeldPrioriteDemandAuGrossist,
                    onCheckedChange = {
                        onFilterChanged(filterState.copy(hideHeldPrioriteDemandAuGrossist = it))
                    }
                )
            }

            item {
                FilterOption(
                    label = "Masquer produits sans priorité grossiste",
                    checked = filterState.hideNonHeldPrioriteDemandAuGrossist,
                    onCheckedChange = {
                        onFilterChanged(filterState.copy(hideNonHeldPrioriteDemandAuGrossist = it))
                    }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = {
                            onFilterChanged(FilterState())
                            onDismiss()
                        }
                    ) {
                        Text("Réinitialiser")
                    }

                    TextButton(
                        onClick = {
                            onFilterChanged(
                                FilterState(
                                    hideQuiNeSontPas_cUnNeveauArrivage = true,
                                    hideNonDispo = true,
                                    hideDispoOnly = true,
                                    hidePetiteProbability = true,
                                    hidePrixAchatZero = true,
                                    hidePrixAchatPositif = true,
                                    hidePrixVenteZero = true,
                                    hidePrixVentePositif = true,
                                    hideHeldPrioriteDemandAuGrossist = true,
                                    hideNonHeldPrioriteDemandAuGrossist = true,
                                    searchText = filterState.searchText,
                                    sortOrder = filterState.sortOrder,
                                    enableCategoryGrouping = filterState.enableCategoryGrouping,
                                    enablePrixAchatTimeFilter = true,
                                    prixAchatTimeFilterDays = "1"
                                )
                            )
                        }
                    ) {
                        Text("Tout Activer")
                    }

                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Fermer")
                    }
                }
            }
        }
    }
}
