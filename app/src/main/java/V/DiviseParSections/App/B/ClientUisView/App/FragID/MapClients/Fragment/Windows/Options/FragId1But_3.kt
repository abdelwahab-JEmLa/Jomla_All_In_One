package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options

import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBaseProtoJuin3
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun FragId1But_3(
    viewModel: ViewModel_MapClients_App2FragID1,
    showLabels: Boolean,
    contentDescription: String,
) {
    ControlButton(
        onClick = {
            // Toggle between the two TypeDeSonMagasine values
            viewModel.auClickeCaUpdateClientPar = if ( viewModel.auClickeCaUpdateClientPar == B_ClientDataBaseProtoJuin3.TypeDeSonMagasine.ATAYAT_MOUKASSARAT) {
                B_ClientDataBaseProtoJuin3.TypeDeSonMagasine.AlIMENTATION_GENERALE
            } else {
                B_ClientDataBaseProtoJuin3.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
            }
        },
        // Choose icon based on current state
        icon = if ( viewModel.auClickeCaUpdateClientPar == B_ClientDataBaseProtoJuin3.TypeDeSonMagasine.ATAYAT_MOUKASSARAT) {
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
