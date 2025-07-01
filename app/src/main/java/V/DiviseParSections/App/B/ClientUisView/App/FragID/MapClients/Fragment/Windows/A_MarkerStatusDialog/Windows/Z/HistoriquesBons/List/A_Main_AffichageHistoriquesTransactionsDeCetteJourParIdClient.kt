package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.Filter.MainFilter
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_Main_AffichageHistoriquesTransactionsDeCetteJourParIdClient(
    modifier: Modifier = Modifier,
    viewModel: E0AfficheHistoriqueTransactionsViewModel = koinViewModel(),
    parentTag_ClientKey: String = "",
) {
    Column {
        Text(
            text = "سجل المعاملات",
            style = MaterialTheme.typography.titleMedium,
            modifier =
                Modifier
                    .padding(vertical = 8.dp)
                    .testTag("A_Main_AffichageHistoriquesTransactionsDeCetteJourParIdClient  $parentTag_ClientKey")//<--
                    .semantics {
                        contentDescription = "Liste filtrée des transactions"
                        testTag = "TransactionsList"
                        // Propriété personnalisée pour les tests
                        set(SemanticsPropertyKey("ClientKey"), parentTag_ClientKey)
                    }
        )

        MainFilter(
            Modifier,
            viewModel,
            parentTag_ClientKey
        )
    }
}
