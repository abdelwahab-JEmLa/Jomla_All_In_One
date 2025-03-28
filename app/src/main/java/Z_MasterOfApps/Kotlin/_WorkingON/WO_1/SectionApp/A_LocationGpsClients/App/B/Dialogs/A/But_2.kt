package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.B.Dialogs.A

import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Extension.ViewModelExtension_App2_F1
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Extension.VisbleClientsNow
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun But_2(
    viewModel: ViewModelInitApp,
    showLabels: Boolean,
    onClick: () -> Unit,
    extensionVM: ViewModelExtension_App2_F1,
    currentFilterMode: VisbleClientsNow,
    textButton: String="",
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
