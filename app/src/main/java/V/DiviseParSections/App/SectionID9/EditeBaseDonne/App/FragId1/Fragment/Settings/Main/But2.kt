package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
 fun But2(showLabels: Boolean, selectedCount: Int, onBulkMove: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showLabels) Text("Move ($selectedCount)")
        FloatingActionButton(
            onClick = onBulkMove,
            modifier = Modifier.size(40.dp),
            containerColor = if (selectedCount > 0) Color.Green else Color.Gray
        ) {
            Icon(Icons.Default.SwapHoriz, "Bulk Move Products", tint = Color.Black)
        }
    }
}
