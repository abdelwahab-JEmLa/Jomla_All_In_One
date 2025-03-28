package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.Dialogs.A

import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.Extension.ViewModelExtension_App2_F1
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun FragId1But_3(
    showLabels: Boolean,
    extensionVM: ViewModelExtension_App2_F1,
) {
    ControlButton(
        onClick = {
            // Toggle between the two TypeDeSonMagasine values
            extensionVM.auClickeCaUpdateClientPar = if ( extensionVM.auClickeCaUpdateClientPar == B_ClientsDataBase.StatueDeBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT) {
                B_ClientsDataBase.StatueDeBase.TypeDeSonMagasine.AlIMENTATION_GENERALE
            } else {
                B_ClientsDataBase.StatueDeBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
            }
        },
        // Choose icon based on current state
        icon = if ( extensionVM.auClickeCaUpdateClientPar == B_ClientsDataBase.StatueDeBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT) {
            LottieJsonGetterR_Raw_Icons.atay
        } else {
            LottieJsonGetterR_Raw_Icons.alimentation
        },
        contentDescription = "auClickeCaUpdateClientPar",
        showLabels = showLabels,
        labelText = "auClickeCaUpdateClientPar",
        containerColor = Color(0xFF2196F3)
    )
}
