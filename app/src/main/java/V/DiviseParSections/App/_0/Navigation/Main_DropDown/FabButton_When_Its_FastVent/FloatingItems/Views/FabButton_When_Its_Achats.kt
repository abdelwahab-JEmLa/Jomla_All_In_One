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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
    val currentValues by remember { focusedValuesGetter::active_Central_Values }
    val activeFilters = currentValues.activeFilters
    val isVisible = currentValues.affiche_CheckList_ChoisiseurActiveFilter

    // Afficher le bouton flottant si le sélecteur n'est pas visible
    if (!isVisible) {
        FloatingButton_ToggleFilterSelector()
        return
    }

    // FIXED: Added non_premier_Check_Donne to available filters
    val availableFilters = listOf(
        ActiveCentralValues.ActiveFilter.NonTrouve,
        ActiveCentralValues.ActiveFilter.PrixAuGerant,
        ActiveCentralValues.ActiveFilter.premier_Check_Donne,
        ActiveCentralValues.ActiveFilter.non_premier_Check_Donne
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    // Calculate card dimensions (90% width, estimated height ~350dp)
    val cardWidth = screenWidth.value * 0.9f
    val cardHeight = 400f // Increased for 4 filters

    var offsetX by remember { mutableFloatStateOf((screenWidth.value - cardWidth) / 2) }
    var offsetY by remember { mutableFloatStateOf((screenHeightDp.value - cardHeight) / 2) }

    fun toggleFilter(filter: ActiveCentralValues.ActiveFilter) {
        val newFilters = if (activeFilters.contains(filter)) {
            activeFilters - filter
        } else {
            activeFilters + filter
        }

        focusedValuesGetter.update_activeCentralValues(
            currentValues.copy(activeFilters = newFilters)
        )
    }

    fun clearAllFilters() {
        focusedValuesGetter.update_activeCentralValues(
            currentValues.copy(activeFilters = emptySet())
        )
    }

    fun hideFilterSelector() {
        focusedValuesGetter.update_activeCentralValues(
            currentValues.copy(affiche_CheckList_ChoisiseurActiveFilter = false)
        )
    }

    // FIXED: Added display name for non_premier_Check_Donne filter
    fun getFilterDisplayName(filter: ActiveCentralValues.ActiveFilter): String {
        return when (filter) {
            is ActiveCentralValues.ActiveFilter.NonTrouve -> "Masquer Non Trouvé"
            is ActiveCentralValues.ActiveFilter.PrixAuGerant -> "Prix au Gérant"
            is ActiveCentralValues.ActiveFilter.premier_Check_Donne -> "Premier Check Donné"
            is ActiveCentralValues.ActiveFilter.non_premier_Check_Donne -> "Non Premier Check Donné"
        }
    }

    // FIXED: Added color for non_premier_Check_Donne filter
    @Composable
    fun getFilterColor(filter: ActiveCentralValues.ActiveFilter): Color {
        return when (filter) {
            is ActiveCentralValues.ActiveFilter.NonTrouve -> Color(0xFFFF5722) // Red-orange
            is ActiveCentralValues.ActiveFilter.PrixAuGerant -> Color(0xFF4CAF50) // Green
            is ActiveCentralValues.ActiveFilter.premier_Check_Donne -> Color(0xFF2196F3) // Blue
            is ActiveCentralValues.ActiveFilter.non_premier_Check_Donne -> Color(0xFF9C27B0) // Purple
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
                        // Allow dragging across entire screen with proper bounds
                        offsetX = offsetX.coerceIn(0f, (screenWidth.value - cardWidth).coerceAtLeast(0f))
                        offsetY = offsetY.coerceIn(0f, (screenHeightDp.value - cardHeight).coerceAtLeast(0f))
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
                // Header avec bouton de fermeture
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Sélecteur de Filtres",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { hideFilterSelector() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Liste des filtres
                availableFilters.forEach { filter ->
                    val isSelected = activeFilters.contains(filter)

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
                                onCheckedChange = { toggleFilter(filter) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = getFilterColor(filter),
                                    uncheckedColor = MaterialTheme.colorScheme.outline
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Affichage des filtres actifs
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

@Composable
private fun FloatingButton_ToggleFilterSelector(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    val currentValues by remember { focusedValuesGetter::active_Central_Values }
    val hasActiveFilters = currentValues.activeFilters.isNotEmpty()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Badge pour indiquer le nombre de filtres actifs
        if (hasActiveFilters) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        color = Color(0xFFFF5722),
                        shape = CircleShape
                    )
                    .shadow(4.dp, CircleShape)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${currentValues.activeFilters.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp
                )
            }
        }

        FloatingActionButton(
            onClick = {
                focusedValuesGetter.update_activeCentralValues(
                    currentValues.copy(affiche_CheckList_ChoisiseurActiveFilter = true)
                )
            },
            containerColor = if (hasActiveFilters) {
                Color(0xFFFF9800) // Orange quand il y a des filtres actifs
            } else {
                MaterialTheme.colorScheme.primaryContainer
            },
            contentColor = if (hasActiveFilters) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onPrimaryContainer
            },
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = if (hasActiveFilters) 8.dp else 6.dp
            ),
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Afficher le sélecteur de filtres",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
