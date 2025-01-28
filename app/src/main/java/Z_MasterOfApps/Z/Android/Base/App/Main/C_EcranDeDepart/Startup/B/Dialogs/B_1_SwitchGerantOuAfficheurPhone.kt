package Z_MasterOfApps.Z.Android.Base.App.Main.C_EcranDeDepart.Startup.B.Dialogs

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phonelink
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import Z_MasterOfApps.Z.Android.Base.App.Packages._2LocationGpsClients.NH_1.id1_ClientsLocationGps.B.Dialogs.ControlButton

@Composable
fun B_1_SwitchGerantOuAfficheurPhone(
    viewModelInitApp: ViewModelInitApp,
    showLabels: Boolean
) {
    ControlButton(
        onClick = {
            viewModelInitApp
                ._paramatersAppsViewModelModel
                .cLeTelephoneDuGerant = true
        },
        icon = Icons.Default.Phonelink,
        contentDescription = "",
        showLabels = showLabels,
        labelText =  "",
        containerColor =  Color(0xFF2196F3)
    )
}
