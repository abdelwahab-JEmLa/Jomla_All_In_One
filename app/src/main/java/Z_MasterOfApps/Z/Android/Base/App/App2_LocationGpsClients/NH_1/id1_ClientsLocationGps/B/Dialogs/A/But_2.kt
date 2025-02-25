package Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps.B.Dialogs.A

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App2_LocationGpsClients.NH_1.id1_ClientsLocationGps.B.Dialogs.ControlButton
import Z_MasterOfApps.Z.Android.Main.Utils.LottieJsonGetterR_Raw_Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun But_2(
    viewModel: ViewModelInitApp,
    showLabels: Boolean,
    onClick: () -> Unit,
) {
    ControlButton(
        onClick = onClick,
        icon = LottieJsonGetterR_Raw_Icons.reacticonanimatedjsonurl,
        contentDescription = "DialogeOptions",
        showLabels = showLabels,
        labelText = "DialogeOptions",
        containerColor = Color(0xFF2196F3)
    )
}
