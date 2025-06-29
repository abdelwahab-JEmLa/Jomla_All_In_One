package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.ViewModel.SecID5FragID2UiState
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.MVentPeriode
import Z_CodePartageEntreApps.Modules.DatesHandler
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
    onClickToOpenTransaction: (GBonVent) -> Unit
) {
    val datasGBonVentRepository = viewModel.getter.gBonVentRepository.datasValue
    val transactionsDateToListGBonVent: List<Pair<MVentPeriode, List<GBonVent>>> =
        remember(datasGBonVentRepository) {
            // Group transactions by date periods
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            datasGBonVentRepository.groupBy { transaction ->
                // Extract date from transaction and create period
                val transactionDate = transaction.parentPeriodeVentKeyID ?: ""
                val period = try {
                    val date = dateFormat.parse(transactionDate)
                    val calendar = java.util.Calendar.getInstance().apply {
                        time = date ?: java.util.Date()
                    }
                    val year = calendar.get(java.util.Calendar.YEAR)
                    val month = calendar.get(java.util.Calendar.MONTH) + 1
                    val startDate = String.format("%04d-%02d-01", year, month)
                    val endDate = String.format("%04d-%02d-%02d", year, month,
                        calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH))

                    MVentPeriode(
                        startDateInString = startDate,
                        endDateInString = endDate,
                    )
                } catch (e: Exception) {
                    // Default period for invalid dates
                    MVentPeriode(
                        startDateInString = "1970-01-01",
                        endDateInString = "1970-01-31",
                    )
                }
                period
            }.map { (period, transactions) ->
                Pair(period, transactions)
            }
        }

    val filteredGroupedTransactions = remember(datasGBonVentRepository, idClient) {
        transactionsDateToListGBonVent
            .map { (period, transactions) ->
                val filteredTransactions = transactions.filter { transaction ->
                    transaction.parentHClientOldID == idClient
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
