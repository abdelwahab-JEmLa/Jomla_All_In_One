package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun ClientsMapFilter(
    viewModel: ClientsMapFilterViewModel = koinInject(),
) {
    /*
    var showMenu by remember { mutableStateOf(true) }
    var showLabels by remember { mutableStateOf(true) }

    // États pour le drag
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
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
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                if (showMenu) {
                    // Create buttons for each day of the week from viewModel
                    viewModel.dayNames.forEach { day ->
                        val isSelected = viewModel.filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList.contains(day)
                        DayFilterButton(
                            day = day,
                            isSelected = isSelected,
                            showLabels = showLabels,
                            onToggle = {
                                // Toggle this day in the filter list
                                val currentList = viewModel.filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList.toMutableList()
                                if (isSelected) {
                                    currentList.remove(day)
                                } else {
                                    currentList.add(day)
                                }
                                viewModel.filterLesClientsOuLeurDernierjourAchatsEstDonsCetteList = currentList
                            }
                        )
                    }
                }

                // Integrated LabelsButton function
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val containerColor = Color(0xFF3F51B5)
                    FloatingActionButton(
                        onClick = { showLabels = !showLabels },
                        modifier = Modifier.size(40.dp),
                        containerColor = containerColor
                    ) {
                        Icon(Icons.Default.Info, if (showLabels) "Hide labels" else "Show labels")
                    }

                    if (showLabels) {
                        Text(
                            if (showLabels) "Hide labels" else "Show labels",
                            modifier = Modifier
                                .background(containerColor)
                                .padding(4.dp),
                            color = Color.White
                        )
                    }
                }

                // Integrated MenuButton function
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val containerColor = Color(0xFF3F51B5)
                    FloatingActionButton(
                        onClick = { showMenu = !showMenu },
                        modifier = Modifier.size(40.dp),
                        containerColor = containerColor
                    ) {
                        Icon(
                            if (showMenu) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            if (showMenu) "Hide menu" else "Show menu"
                        )
                    }

                    if (showLabels) {
                        Text(
                            if (showMenu) "Hide" else "Options",
                            modifier = Modifier
                                .background(containerColor)
                                .padding(4.dp),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }          */
}

@Composable
fun DayFilterButton(
    day: String,
    isSelected: Boolean,
    showLabels: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val buttonColor = if (isSelected) Color(0xFF4CAF50) else Color(0xFFF44336)

        FloatingActionButton(
            onClick = onToggle,
            modifier = Modifier.size(40.dp),
            containerColor = buttonColor
        ) {
            Icon(
                if (isSelected) Icons.Filled.CheckCircle else Icons.Filled.Info,
                contentDescription = day
            )
        }

        if (showLabels) {
            Text(
                day,
                modifier = Modifier
                    .background(buttonColor)
                    .padding(4.dp),
                color = Color.White
            )
        }
    }
}
