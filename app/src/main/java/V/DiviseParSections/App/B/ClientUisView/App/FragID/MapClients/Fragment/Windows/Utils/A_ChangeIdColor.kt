package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options.ControlButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Composable
fun A_ChangeIdColor(
    showLabels: Boolean,
    contentDescription: String,
    viewModel: ViewModel_MapClients_App2FragID1,
) {
    var clearDataClickCount by remember { mutableIntStateOf(0) }

    ControlButton(
        onClick = {
            if (clearDataClickCount == 0) {
                clearDataClickCount++
            } else {
                clearDataClickCount = 0
            }
        },
        icon = Icons.Default.Repeat,
        contentDescription = contentDescription,
        showLabels = showLabels,
        labelText = if (clearDataClickCount == 0) contentDescription else "Click again to confirm",
        containerColor = if (clearDataClickCount == 0) Color(0xFFE91E63) else Color(0xFFFFEB3B)
    )
}
