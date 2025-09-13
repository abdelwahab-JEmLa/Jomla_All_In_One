package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.FloatingItems.Views

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject

@Composable
fun CheckList_ChoisiseurActiveFilter(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    val currentValues = focusedValuesGetter.active_Central_Values
    val activeFilter = currentValues.activeFilter

    // Define all available filters
    val availableFilters = listOf(
        ActiveCentralValues.ActiveFilter.Standart,
        ActiveCentralValues.ActiveFilter.NonTrouve,
        ActiveCentralValues.ActiveFilter.PrixAuGerant
    )

    fun updateActiveFilter(newFilter: ActiveCentralValues.ActiveFilter) {
        focusedValuesGetter.update_activeCentralValues(
            currentValues.copy(activeFilter = newFilter)
        )
    }

    fun getFilterDisplayName(filter: ActiveCentralValues.ActiveFilter): String {
        return when (filter) {
            is ActiveCentralValues.ActiveFilter.Standart -> "Standard"
            is ActiveCentralValues.ActiveFilter.NonTrouve -> "Non Trouvé"
            is ActiveCentralValues.ActiveFilter.PrixAuGerant -> "Prix au Gérant"
        }
    }

    @Composable
    fun getFilterColor(filter: ActiveCentralValues.ActiveFilter): Color {
        return when (filter) {
            is ActiveCentralValues.ActiveFilter.Standart -> MaterialTheme.colorScheme.primary
            is ActiveCentralValues.ActiveFilter.NonTrouve -> Color(0xFFFF5722) // Red-orange
            is ActiveCentralValues.ActiveFilter.PrixAuGerant -> Color(0xFF4CAF50) // Green
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Sélecteur de Filtres Actifs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter options
            availableFilters.forEach { filter ->
                val isSelected = when {
                    activeFilter is ActiveCentralValues.ActiveFilter.Standart &&
                            filter is ActiveCentralValues.ActiveFilter.Standart -> true

                    activeFilter is ActiveCentralValues.ActiveFilter.NonTrouve &&
                            filter is ActiveCentralValues.ActiveFilter.NonTrouve -> true

                    activeFilter is ActiveCentralValues.ActiveFilter.PrixAuGerant &&
                            filter is ActiveCentralValues.ActiveFilter.PrixAuGerant -> true

                    else -> false
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            getFilterColor(filter).copy(alpha = 0.1f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (isSelected) 4.dp else 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getFilterDisplayName(filter),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) {
                                getFilterColor(filter)
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )

                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    updateActiveFilter(filter)
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = getFilterColor(filter),
                                uncheckedColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Current status indicator
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = getFilterColor(activeFilter).copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = getFilterColor(activeFilter),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Filtre actuel: ${getFilterDisplayName(activeFilter)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = getFilterColor(activeFilter),
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
