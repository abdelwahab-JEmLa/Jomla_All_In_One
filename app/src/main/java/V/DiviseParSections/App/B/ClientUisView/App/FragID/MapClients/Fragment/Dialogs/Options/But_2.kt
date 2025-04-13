package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.Options

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_App2FragID1
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun But_2(
    showLabels: Boolean,
    onClick: () -> Unit,
    currentFilterMode: ViewModel_App2FragID1.VisbleClientsNow,
    textButton: String = "",
    viewModelInitApp: ViewModelInitApp,
    viewModel: ViewModel_App2FragID1,
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
