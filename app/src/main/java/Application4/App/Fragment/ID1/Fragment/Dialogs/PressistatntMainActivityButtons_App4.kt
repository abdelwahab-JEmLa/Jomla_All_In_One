package Application4.App.Fragment.ID1.Fragment.Dialogs

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Prioriter
import EntreApps.Shared.Models.Home.ActiveCentralValues
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun PressistatntMainActivityButtons_App4(viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns) {
    val isEchatillantsFilter by remember {
        derivedStateOf {
            viewModelNewProtoPatterns.active_Datas.affiche_produits_Ou_On_TagPrioriter
                ?.contains(Prioriter.Affiche_Que_Les_Produits_De_Jomla_Clients_ECHATILLANTS) == true
        }
    }

    var offsetX by remember { mutableFloatStateOf(-50f) }
    var offsetY by remember { mutableFloatStateOf(-50f) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {
        FloatingActionButton(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .padding(16.dp)
                .size(56.dp),
            onClick = {
                viewModelNewProtoPatterns.active_Datas.affiche_produits_Ou_On_TagPrioriter =
                    if (isEchatillantsFilter) {
                        Prioriter.entries.toSet() - Prioriter.Affiche_Que_Les_Produits_De_Jomla_Clients_ECHATILLANTS
                    } else {
                        setOf(Prioriter.Affiche_Que_Les_Produits_De_Jomla_Clients_ECHATILLANTS)
                    }
            },
            containerColor = if (isEchatillantsFilter)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Icon(
                imageVector = if (isEchatillantsFilter) Icons.Default.Check else Icons.Default.FilterList,
                contentDescription = "Toggle Echatillants Filter",
                tint = if (isEchatillantsFilter) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FloatingFilterToggleFAB(
    activeFilters: Set<ActiveCentralValues.ActiveFilter>,
    onToggleFilter: () -> Unit,
    showLabels: Boolean,
    modifier: Modifier = Modifier
) {
    val currentFilterState = when {
        activeFilters.contains(ActiveCentralValues.ActiveFilter.premier_Check_Donne) ->
            FilterState.PREMIER_CHECK
        activeFilters.contains(ActiveCentralValues.ActiveFilter.non_premier_Check_Donne) ->
            FilterState.NON_PREMIER_CHECK
        else -> FilterState.AUCUN
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        FloatingActionButton(
            modifier = Modifier.size(40.dp),
            onClick = onToggleFilter,
            containerColor = when (currentFilterState) {
                FilterState.PREMIER_CHECK -> Color(0xFF2196F3)
                FilterState.NON_PREMIER_CHECK -> Color(0xFF9C27B0)
                FilterState.AUCUN -> MaterialTheme.colorScheme.surfaceVariant
            },
        ) {
            Icon(
                imageVector = when (currentFilterState) {
                    FilterState.PREMIER_CHECK -> Icons.Default.Check
                    FilterState.NON_PREMIER_CHECK -> Icons.Default.Close
                    FilterState.AUCUN -> Icons.Default.FilterList
                },
                contentDescription = "Toggle Filter",
                tint = when (currentFilterState) {
                    FilterState.PREMIER_CHECK, FilterState.NON_PREMIER_CHECK -> Color.White
                    FilterState.AUCUN -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        if (showLabels) {
            Text(
                text = when (currentFilterState) {
                    FilterState.PREMIER_CHECK -> "Premier Check Donné"
                    FilterState.NON_PREMIER_CHECK -> "Non Premier Check Donné"
                    FilterState.AUCUN -> "Aucun Filtre"
                },
                modifier = Modifier
                    .background(
                        color = when (currentFilterState) {
                            FilterState.PREMIER_CHECK -> Color(0xFF2196F3)
                            FilterState.NON_PREMIER_CHECK -> Color(0xFF9C27B0)
                            FilterState.AUCUN -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = when (currentFilterState) {
                    FilterState.PREMIER_CHECK, FilterState.NON_PREMIER_CHECK -> Color.White
                    FilterState.AUCUN -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private enum class FilterState {
    AUCUN,
    PREMIER_CHECK,
    NON_PREMIER_CHECK
}
