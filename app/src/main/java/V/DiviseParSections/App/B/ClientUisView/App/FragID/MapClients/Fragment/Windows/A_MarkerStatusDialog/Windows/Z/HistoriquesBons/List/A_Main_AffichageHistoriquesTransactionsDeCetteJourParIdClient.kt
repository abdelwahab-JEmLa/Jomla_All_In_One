package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.Filter.MainFilter
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_Main_AffichageHistoriquesTransactionsDeCetteJourParIdClient(
    modifier: Modifier = Modifier,
    viewModel: E0AfficheHistoriqueTransactionsViewModel = koinViewModel(),
    markerStatusDialogM2Client: HClientInfos?=null,
) {
    Column {
        Text(
            text = buildString {
                append((markerStatusDialogM2Client?.nom ?: "null"))
                append(" ")
                append("سجل المعاملات")
            },
            style = MaterialTheme.typography.titleMedium,
            modifier =
                Modifier
                    .padding(vertical = 8.dp)
        )

        MainFilter(
            markerStatusDialogM2Client,
            Modifier,
            viewModel,
        )
    }
}
