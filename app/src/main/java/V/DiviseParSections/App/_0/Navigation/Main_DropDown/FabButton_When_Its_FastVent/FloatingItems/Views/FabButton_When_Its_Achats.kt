package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.FloatingItems.Views

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@Composable
fun CheckList_ChoisiseurActiveFilter(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    val currentValues = focusedValuesGetter.active_Central_Values
    val activeFilter = currentValues.activeFilter

    // FIXED TODO(1): Removed Standart - only available filters
    val availableFilters = listOf(
        ActiveCentralValues.ActiveFilter.NonTrouve,
        ActiveCentralValues.ActiveFilter.PrixAuGerant
    )

    // FIXED TODO(1): Make it draggable like FragAchats_FloatingOutlinedSearcher_4
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 400f).coerceAtLeast(0f)) }
    var offsetY by remember { mutableFloatStateOf((screenHeightDp.value - 400f).coerceAtLeast(0f)) }

    fun updateActiveFilter(newFilter: ActiveCentralValues.ActiveFilter) {
        focusedValuesGetter.update_activeCentralValues(
            currentValues.copy(activeFilter = newFilter)
        )
    }

    fun getFilterDisplayName(filter: ActiveCentralValues.ActiveFilter): String {
        return when (filter) {
            is ActiveCentralValues.ActiveFilter.NonTrouve -> "Non Trouvé"
            is ActiveCentralValues.ActiveFilter.PrixAuGerant -> "Prix au Gérant"
        }
    }

    @Composable
    fun getFilterColor(filter: ActiveCentralValues.ActiveFilter): Color {
        return when (filter) {
            is ActiveCentralValues.ActiveFilter.NonTrouve -> Color(0xFFFF5722) // Red-orange
            is ActiveCentralValues.ActiveFilter.PrixAuGerant -> Color(0xFF4CAF50) // Green
        }
    }

    // FIXED TODO(1): Made the entire component draggable
    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .fillMaxWidth(0.9f)
                .background(
                    color = Color.White.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(12.dp)
                )
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        // Keep within screen bounds with some padding
                        offsetX = offsetX.coerceIn(0f, screenWidth.value - 350f)
                        offsetY = offsetY.coerceIn(0f, screenHeightDp.value - 300f)
                    }
                },
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

                // Filter options - Only allow selection, no deselection
                availableFilters.forEach { filter ->
                    val isSelected = when {
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
                                    // Only allow selection, not deselection - switch between available filters only
                                    if (checked && !isSelected) {
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
}
