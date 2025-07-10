package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options

import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
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
            viewModel.auClickeCaUpdateClientPar = if ( viewModel.auClickeCaUpdateClientPar == M2Client.TypeDeSonMagasine.ATAYAT_MOUKASSARAT) {
                M2Client.TypeDeSonMagasine.AlIMENTATION_GENERALE
            } else {
                M2Client.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
            }
        },
        // Choose icon based on current state
        icon = if ( viewModel.auClickeCaUpdateClientPar == M2Client.TypeDeSonMagasine.ATAYAT_MOUKASSARAT) {
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
