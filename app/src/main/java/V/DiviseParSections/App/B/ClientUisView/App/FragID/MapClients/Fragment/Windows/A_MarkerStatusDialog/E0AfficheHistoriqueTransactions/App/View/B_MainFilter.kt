package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.ViewModel.SecID5FragID2UiState
import Z_CodePartageEntreApps.Modules.DatesHandler
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.TransactionVent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MainFilter(
    uiState: SecID5FragID2UiState,
    dateStringName: DatesHandler,
    idClient: Long,
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    onClickToOpenTransaction: (TransactionVent) -> Unit
) {
    val filteredGroupedTransactions = remember(uiState.transactionsDateToList_C_3_BonAchate, idClient) {
        uiState.transactionsDateToList_C_3_BonAchate
            .map { (period, transactions) ->
                val filteredTransactions = transactions.filter { transaction ->
                    transaction.clientAcheteurID == idClient
                }
                Pair(period, filteredTransactions)
            }
            .filter { (_, transactions) -> transactions.isNotEmpty() }
            .sortedByDescending { (period, _) ->
                try {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .parse(period.startDateInString)?.time ?: 0L
                } catch (e: Exception) {
                    0L
                }
            }
    }

    MainList(filteredGroupedTransactions, dateStringName, viewModel, uiState, idClient, onClickToOpenTransaction )
}
