package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_4_PeriodeVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.ViewModel.SecID5FragID2UiState
import Z_CodePartageEntreApps.Modules.DatesHandler
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MainList(
    filteredGroupedTransactions: List<Pair<_1_4_PeriodeVent, List<C3_TransactionCommercial>>>,
    dateStringName: DatesHandler,
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    uiState: SecID5FragID2UiState,
    idClient: Long,
    onClickToOpenTransaction: (C3_TransactionCommercial) -> Unit,
) {
    val groupedByWeek = remember(filteredGroupedTransactions) {
        filteredGroupedTransactions.groupBy { (period, _) ->
            try {
                val distanceWeek = dateStringName.getDistanceSemainParDateStr(period.startDateInString)
                if (distanceWeek == "قبل 3 أسابيع" || distanceWeek.isEmpty()) {
                    "الأسبوع من ${period.startDateInString}"
                } else {
                    distanceWeek
                }
            } catch (e: Exception) {
                "الأسبوع من ${period.startDateInString}"
            }
        }
    }

    val sortedWeeks = remember(groupedByWeek) {
        groupedByWeek.toList().sortedByDescending { (_, periodsInWeek) ->
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                periodsInWeek.maxOfOrNull { (period, _) ->
                    dateFormat.parse(period.startDateInString)?.time ?: 0L
                } ?: 0L
            } catch (e: Exception) {
                0L
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (filteredGroupedTransactions.isNotEmpty()) {
            sortedWeeks.forEach { (weekString, periodsInWeek) ->
                C_1_Header_WeekHeaderItem(weekString)

                val sortedPeriodsInWeek = periodsInWeek.sortedByDescending { (period, _) ->
                    try {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .parse(period.startDateInString)?.time ?: 0L
                    } catch (e: Exception) {
                        0L
                    }
                }

                sortedPeriodsInWeek.forEach { (period, transactions) ->
                    val dayName = dateStringName.getNomJourArabParDateStr(period.startDateInString)
                    val startTime = period.heurDebutInString
                    val endTime = period.endDateInString.ifEmpty { "الآن" }

                    C_2_Header_PeriodHeaderItem(
                        dayName = dayName,
                        startTime = startTime,
                        endTime = endTime
                    )

                    transactions.forEach { transaction ->
                        MainItem(
                            viewModel = viewModel,
                            transaction = transaction,
                            onClickToOpenTransaction = onClickToOpenTransaction
                        )
                    }
                }
            }
        } else {
            Text(
                text = if (uiState.transactionsDateToList_C_3_BonAchate.isNotEmpty())
                    "لا توجد معاملات للعميل $idClient"
                else "جاري تحميل البيانات...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
