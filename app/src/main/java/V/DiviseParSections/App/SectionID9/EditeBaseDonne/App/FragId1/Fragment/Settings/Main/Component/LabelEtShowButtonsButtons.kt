package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main.Component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MiscellaneousServices
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LabelEtShowButtonsButtons(
    showLabels: Boolean,
    showButtons: Boolean,
    onShowLabelsToggle: () -> Unit,
    onShowButtonsToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonBackgroundColor = Color.Yellow

    if (showButtons) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (showLabels) {
                Text("Labels") // Texte avant le bouton
            }
            // Labels toggle button
            FloatingActionButton(
                onClick = onShowLabelsToggle,
                modifier = Modifier.size(40.dp),
                containerColor = buttonBackgroundColor
            ) {
                val iconColor = Color.Black

                Icon(
                    imageVector = Icons.Default.Label,
                    contentDescription = "Toggle Labels",
                    tint = iconColor
                )
            }
        }
    }

    Row(
        modifier = modifier, // Add the modifier here as well
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showLabels) {
            Text(if (showButtons) "Hide" else "OptionsFragmentButtons") // Texte avant le bouton
        }
        // Show buttons toggle button - This should always be visible to toggle showButtons state
        FloatingActionButton(
            onClick = onShowButtonsToggle,
            modifier = Modifier.size(40.dp),
            containerColor = buttonBackgroundColor
        ) {
            val iconColor = Color.Black

            Icon(
                imageVector = if (showButtons) Icons.Default.Visibility else Icons.Default.MiscellaneousServices,
                contentDescription = "Toggle Buttons Visibility",
                tint = iconColor
            )
        }
    }
}
