package V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_MainPresenter.Filter

// FIXED: Import FilterState and SortOrder from ActiveCentralValues, not from a separate Models package
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues.FilterState_Facad_Boutique
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues.SortOrder_Facade_Boutique
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
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
import org.koin.compose.koinInject

/**
 * FIXED: FilterDropdownMenu now properly integrates with FocusedValuesGetter
 * This version retrieves the current FilterState from FocusedValuesGetter and
 * updates it through the proper channel.
 *
 * FIXED: Correct imports - FilterState and SortOrder are defined inside ActiveCentralValues
 */
@Composable
fun FilterDropdownMenu(
    onDismiss: () -> Unit,
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    // FIXED: Get current FilterState from FocusedValuesGetter
    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val currentFilterState = activeCentralValues.filterStateFacadBoutique ?: FilterState_Facad_Boutique()

    // FIXED: Update FilterState through FocusedValuesGetter
    val onFilterChanged: (FilterState_Facad_Boutique) -> Unit = { newFilterState ->
        focusedValuesGetter.update_activeCentralValues(
            activeCentralValues.copy(filterStateFacadBoutique = newFilterState)
        )
    }

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
                // Search field - FIXED: searchText now resolves correctly
                OutlinedTextField(
                    value = currentFilterState.searchText,
                    onValueChange = {
                        onFilterChanged(currentFilterState.copy(searchText = it))
                    },
                    label = { Text("Rechercher par nom") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Rechercher"
                        )
                    },
                    trailingIcon = {
                        if (currentFilterState.searchText.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    onFilterChanged(currentFilterState.copy(searchText = ""))
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
                    checked = currentFilterState.enableCategoryGrouping,
                    onCheckedChange = {
                        onFilterChanged(
                            currentFilterState.copy(
                                enableCategoryGrouping = it,
                                sortOrderFacadeBoutique = if (it) SortOrder_Facade_Boutique.CATEGORY_GROUPED else SortOrder_Facade_Boutique.ID_DESC
                            )
                        )
                    }
                )
            }

            // Sort order options (only show when not grouping by categories)
            if (!currentFilterState.enableCategoryGrouping) {
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
                        selected = currentFilterState.sortOrderFacadeBoutique == SortOrder_Facade_Boutique.ID_DESC,
                        onClick = { onFilterChanged(currentFilterState.copy(sortOrderFacadeBoutique = SortOrder_Facade_Boutique.ID_DESC)) }
                    )
                }

                item {
                    SortOption(
                        label = "ID croissant (plus ancien)",
                        selected = currentFilterState.sortOrderFacadeBoutique == SortOrder_Facade_Boutique.ID_ASC,
                        onClick = { onFilterChanged(currentFilterState.copy(sortOrderFacadeBoutique = SortOrder_Facade_Boutique.ID_ASC)) }
                    )
                }

                // Prix Achat Time Sort Options
                item {
                    SortOption(
                        label = "Prix achat récemment mis à jour",
                        selected = currentFilterState.sortOrderFacadeBoutique == SortOrder_Facade_Boutique.PRIX_ACHAT_TIME_DESC,
                        onClick = { onFilterChanged(currentFilterState.copy(sortOrderFacadeBoutique = SortOrder_Facade_Boutique.PRIX_ACHAT_TIME_DESC)) }
                    )
                }

                item {
                    SortOption(
                        label = "Prix achat anciennement mis à jour",
                        selected = currentFilterState.sortOrderFacadeBoutique == SortOrder_Facade_Boutique.PRIX_ACHAT_TIME_ASC,
                        onClick = { onFilterChanged(currentFilterState.copy(sortOrderFacadeBoutique = SortOrder_Facade_Boutique.PRIX_ACHAT_TIME_ASC)) }
                    )
                }

                item {
                    SortOption(
                        label = "Nom A-Z",
                        selected = currentFilterState.sortOrderFacadeBoutique == SortOrder_Facade_Boutique.NAME_ASC,
                        onClick = { onFilterChanged(currentFilterState.copy(sortOrderFacadeBoutique = SortOrder_Facade_Boutique.NAME_ASC)) }
                    )
                }

                item {
                    SortOption(
                        label = "Nom Z-A",
                        selected = currentFilterState.sortOrderFacadeBoutique == SortOrder_Facade_Boutique.NAME_DESC,
                        onClick = { onFilterChanged(currentFilterState.copy(sortOrderFacadeBoutique = SortOrder_Facade_Boutique.NAME_DESC)) }
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
                    checked = currentFilterState.hideQuiNeSontPas_cUnNeveauArrivage,
                    onCheckedChange = {
                        onFilterChanged(currentFilterState.copy(hideQuiNeSontPas_cUnNeveauArrivage = it))
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
                    checked = currentFilterState.hideNonDispo,
                    onCheckedChange = {
                        onFilterChanged(currentFilterState.copy(hideNonDispo = it))
                    }
                )
            }

            item {
                FilterOption(
                    label = "Masquer produits disponibles",
                    checked = currentFilterState.hideDispoOnly,
                    onCheckedChange = {
                        onFilterChanged(currentFilterState.copy(hideDispoOnly = it))
                    }
                )
            }

            item {
                FilterOption(
                    label = "Masquer produits possibles",
                    checked = currentFilterState.hidePetiteProbability,
                    onCheckedChange = {
                        onFilterChanged(currentFilterState.copy(hidePetiteProbability = it))
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
                    checked = currentFilterState.hidePrixAchatZero,
                    onCheckedChange = {
                        onFilterChanged(currentFilterState.copy(hidePrixAchatZero = it))
                    }
                )
            }

            item {
                FilterOption(
                    label = "Masquer produits avec prix d'achat",
                    checked = currentFilterState.hidePrixAchatPositif,
                    onCheckedChange = {
                        onFilterChanged(currentFilterState.copy(hidePrixAchatPositif = it))
                    }
                )
            }

            // Time-based filter for prixAchatDernierTimeTempUpdate
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    FilterOption(
                        label = "🟡 Filtrer par ancienneté de prix d'achat",
                        checked = currentFilterState.enablePrixAchatTimeFilter,
                        onCheckedChange = {
                            onFilterChanged(currentFilterState.copy(enablePrixAchatTimeFilter = it))
                        }
                    )
                }
            }

            // The days input field
            if (currentFilterState.enablePrixAchatTimeFilter) {
                item {
                    OutlinedTextField(
                        value = currentFilterState.prixAchatTimeFilterDays,
                        onValueChange = {
                            // Only allow numeric input
                            val cleanedInput = it.filter { char -> char.isDigit() }
                            onFilterChanged(currentFilterState.copy(prixAchatTimeFilterDays = cleanedInput))
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

            // Selling price filters section
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
                    checked = currentFilterState.hidePrixVenteZero,
                    onCheckedChange = {
                        onFilterChanged(currentFilterState.copy(hidePrixVenteZero = it))
                    }
                )
            }

            item {
                FilterOption(
                    label = "Masquer produits avec prix de vente",
                    checked = currentFilterState.hidePrixVentePositif,
                    onCheckedChange = {
                        onFilterChanged(currentFilterState.copy(hidePrixVentePositif = it))
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
                    checked = currentFilterState.hideHeldPrioriteDemandAuGrossist,
                    onCheckedChange = {
                        onFilterChanged(currentFilterState.copy(hideHeldPrioriteDemandAuGrossist = it))
                    }
                )
            }

            item {
                FilterOption(
                    label = "Masquer produits sans priorité grossiste",
                    checked = currentFilterState.hideNonHeldPrioriteDemandAuGrossist,
                    onCheckedChange = {
                        onFilterChanged(currentFilterState.copy(hideNonHeldPrioriteDemandAuGrossist = it))
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
                            onFilterChanged(FilterState_Facad_Boutique())
                            onDismiss()
                        }
                    ) {
                        Text("Réinitialiser")
                    }

                    TextButton(
                        onClick = {
                            onFilterChanged(
                                FilterState_Facad_Boutique(
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
                                    searchText = currentFilterState.searchText,
                                    sortOrderFacadeBoutique = currentFilterState.sortOrderFacadeBoutique,
                                    enableCategoryGrouping = currentFilterState.enableCategoryGrouping,
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
