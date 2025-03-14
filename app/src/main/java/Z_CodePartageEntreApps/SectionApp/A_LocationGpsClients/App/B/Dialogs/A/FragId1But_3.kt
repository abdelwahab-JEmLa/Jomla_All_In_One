package Z_CodePartageEntreApps.SectionApp.A_LocationGpsClients.App.B.Dialogs.A

import Z_CodePartageEntreApps.Model.B_ClientsDataBase.StatueDeBase.TypeDeSonMagasine
import Z_CodePartageEntreApps.Resources.LottieJsonGetterR_Raw_Icons
import Z_CodePartageEntreApps.SectionApp.A_LocationGpsClients.App.ViewModel.Extension.ViewModelExtension_App2_F1
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun FragId1But_3(
    showLabels: Boolean,
    extensionVM: ViewModelExtension_App2_F1,
    contentDescription: String,
) {
    ControlButton(
        onClick = {
            // Toggle between the two TypeDeSonMagasine values
            extensionVM.auClickeCaUpdateClientPar = if ( extensionVM.auClickeCaUpdateClientPar == TypeDeSonMagasine.ATAYAT_MOUKASSARAT) {
                TypeDeSonMagasine.AlIMENTATION_GENERALE
            } else {
                TypeDeSonMagasine.ATAYAT_MOUKASSARAT
            }
        },
        // Choose icon based on current state
        icon = if ( extensionVM.auClickeCaUpdateClientPar == TypeDeSonMagasine.ATAYAT_MOUKASSARAT) {
            LottieJsonGetterR_Raw_Icons.atay
        } else {
            LottieJsonGetterR_Raw_Icons.alimentation
        },
        contentDescription = contentDescription,
        showLabels = showLabels,
        labelText = contentDescription,
        containerColor = Color(0xFF2196F3)
    )
}
