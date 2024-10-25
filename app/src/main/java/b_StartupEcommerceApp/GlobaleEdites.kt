package b_StartupEcommerceApp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun FloatingActionButtons(
    showFloatingButtons: Boolean,
    onToggleNavBar: () -> Unit,
    onToggleFloatingButtons: () -> Unit,
    onToggleOutlineFilter: () -> Unit,
    onChangeGridColumns: (Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimatedVisibility(
            visible = showFloatingButtons,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            FloatingActionButtonGroup(
                onToggleNavBar = onToggleNavBar,
                onToggleOutlineFilter = onToggleOutlineFilter,
                onChangeGridColumns = onChangeGridColumns
            )
        }

        FloatingActionButton(onClick = onToggleFloatingButtons) {
            Icon(
                imageVector = if (showFloatingButtons) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (showFloatingButtons) "Hide Options" else "Show Options"
            )
        }
    }
}

@Composable
fun FloatingActionButtonGroup(
    onToggleNavBar: () -> Unit,
    onToggleOutlineFilter: () -> Unit,
    onChangeGridColumns: (Int) -> Unit,
) {
    var currentGridColumns by remember { mutableIntStateOf(2) }
    var showLabels by remember { mutableStateOf(false) }

    data class FabButton(
        val icon: ImageVector,
        val label: String,
        val onClick: () -> Unit
    )

    val buttons = listOf(
        FabButton(Icons.Default.EditCalendar, "Filter") { onToggleOutlineFilter() },
        FabButton(Icons.Default.GridView, "Grid") {
            currentGridColumns = (currentGridColumns % 4) + 1
            onChangeGridColumns(currentGridColumns)
        },
        FabButton(
            if (showLabels) Icons.Default.Close else Icons.Default.Details,
            if (showLabels) "Hide Labels" else "Show Labels"
        ) { showLabels = !showLabels },
        FabButton(Icons.Default.Home, "Home") { onToggleNavBar() }
    )

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttons.forEach { button ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                AnimatedVisibility(
                    visible = showLabels,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    Surface(
                        modifier = Modifier.padding(end = 8.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = button.label,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                FloatingActionButton(
                    onClick = button.onClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(button.icon, contentDescription = button.label)
                }
            }
        }
    }
}