package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.B_ClientInfosProtoJuin3
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import Z_MasterOfApps.Resources.LottieJsonGetterR_Raw_Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun FragId1But_3(
    viewModel: MapClientsViewModel,
    showLabels: Boolean,
    contentDescription: String,
) {
    ControlButton(
        onClick = {
            // Toggle between the two TypeDeSonMagasine values
            viewModel.auClickeCaUpdateClientPar = if ( viewModel.auClickeCaUpdateClientPar == B_ClientInfosProtoJuin3.TypeDeSonMagasine.ATAYAT_MOUKASSARAT) {
                B_ClientInfosProtoJuin3.TypeDeSonMagasine.AlIMENTATION_GENERALE
            } else {
                B_ClientInfosProtoJuin3.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
            }
        },
        // Choose icon based on current state
        icon = if ( viewModel.auClickeCaUpdateClientPar == B_ClientInfosProtoJuin3.TypeDeSonMagasine.ATAYAT_MOUKASSARAT) {
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
