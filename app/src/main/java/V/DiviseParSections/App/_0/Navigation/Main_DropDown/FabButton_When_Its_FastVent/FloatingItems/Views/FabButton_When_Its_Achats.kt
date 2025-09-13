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
    // FIXED: Support pour plusieurs filtres simultanés
    val currentValues by remember { focusedValuesGetter::active_Central_Values }
    val activeFilters = currentValues.activeFilters // Set<ActiveFilter> au lieu de ActiveFilter?

    val availableFilters = listOf(
        ActiveCentralValues.ActiveFilter.NonTrouve,
        ActiveCentralValues.ActiveFilter.PrixAuGerant
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 400f).coerceAtLeast(0f)) }
    var offsetY by remember { mutableFloatStateOf((screenHeightDp.value - 400f).coerceAtLeast(0f)) }

    // FIXED: Nouvelle fonction pour toggler un filtre (ajouter/retirer)
    fun toggleFilter(filter: ActiveCentralValues.ActiveFilter) {
        val newFilters = if (activeFilters.contains(filter)) {
            // Retirer le filtre
            activeFilters - filter
        } else {
            // Ajouter le filtre
            activeFilters + filter
        }

        focusedValuesGetter.update_activeCentralValues(
            currentValues.copy(activeFilters = newFilters)
        )
    }

    // FIXED: Fonction pour effacer tous les filtres
    fun clearAllFilters() {
        focusedValuesGetter.update_activeCentralValues(
            currentValues.copy(activeFilters = emptySet())
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
                        text = "Sélecteur de Filtres Multiples",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // FIXED: Chaque filtre peut maintenant être sélectionné indépendamment
                availableFilters.forEach { filter ->
                    val isSelected = activeFilters.contains(filter) // Simple vérification dans le Set

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
                                onCheckedChange = {
                                    // FIXED: Simple toggle - ajouter si pas présent, retirer si présent
                                    toggleFilter(filter)
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

                // FIXED: Affichage des filtres actifs (peut être plusieurs maintenant)
                if (activeFilters.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Filtres actifs (${activeFilters.size}):",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }

                            // Liste des filtres actifs
                            activeFilters.forEach { filter ->
                                Text(
                                    text = "• ${getFilterDisplayName(filter)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = getFilterColor(filter),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 24.dp, top = 2.dp)
                                )
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = "Aucun filtre actif",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}
