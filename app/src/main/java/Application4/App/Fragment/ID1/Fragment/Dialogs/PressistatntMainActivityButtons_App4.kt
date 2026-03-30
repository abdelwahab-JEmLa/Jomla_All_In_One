package Application4.App.Fragment.ID1.Fragment.Dialogs

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Prioriter
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

    // FIX TODO(1): start at center — offsets begin at 0 so the FAB appears
    // exactly where the Box's contentAlignment = Center places it.
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val fabColor = if (isEchatillantsFilter)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surfaceVariant

    // FIX TODO(1): use Alignment.Center so the FAB starts in the middle of the screen.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // FIX TODO(1) label: wrap FAB + label in a draggable Row so both move together.
        Row(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .padding(16.dp)
                    .size(56.dp),
                onClick = {
                    viewModelNewProtoPatterns.active_Datas.affiche_produits_Ou_On_TagPrioriter =
                        if (isEchatillantsFilter) {
                            null  // show all products
                        } else {
                            setOf(Prioriter.Affiche_Que_Les_Produits_De_Jomla_Clients_ECHATILLANTS)
                        }
                },
                containerColor = fabColor,
            ) {
                Icon(
                    imageVector = if (isEchatillantsFilter) Icons.Default.Check else Icons.Default.FilterList,
                    contentDescription = "Toggle Echatillants Filter",
                    tint = if (isEchatillantsFilter) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // FIX TODO(1) label: label with matching background color next to the FAB.
            Text(
                text = if (isEchatillantsFilter) "Échantillons" else "Tous les produits",
                modifier = Modifier
                    .background(
                        color = fabColor,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                color = if (isEchatillantsFilter) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
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
