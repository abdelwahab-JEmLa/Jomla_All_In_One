package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun But_2(
    showLabels: Boolean,
    onClick: () -> Unit,
    currentFilterMode: MapClientsViewModel.VisibleClientsNow,
    textButton: String = "",
    viewModel: MapClientsViewModel,
) {
    ControlButton(
        onClick = onClick,
        icon = currentFilterMode.icon,
        contentDescription = textButton,
        showLabels = showLabels,
        labelText = currentFilterMode.name,
        containerColor = Color(0xFF2196F3)
    )
}
