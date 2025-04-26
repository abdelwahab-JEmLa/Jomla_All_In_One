package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.View

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel.SecID5FragID2ViewModel
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.Modules.GetDateStringName
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun A_MainAffichageHistoriquesTransactionsDeCetteJourParIdClient(
    modifier: Modifier = Modifier,
    viewModel: SecID5FragID2ViewModel = koinViewModel(),
    idClient: Long = 2
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateStringName = GetDateStringName()

    // Filter transactions by client ID
    val filteredGroupedTransactions = remember(uiState.transactionsDateToList_1_3_TransactionCommercial, idClient) {
        uiState.transactionsDateToList_1_3_TransactionCommercial
            .map { (period, transactions) ->
                val filteredTransactions = transactions.filter { it.clientAcheteurID == idClient }
                Pair(period, filteredTransactions)
            }
            .filter { (_, transactions) -> transactions.isNotEmpty() }
    }

    // Group periods by week
    val groupedByWeek = remember(filteredGroupedTransactions) {
        filteredGroupedTransactions.groupBy { (period, _) ->
            dateStringName.getDistanceSemainParDateStr(period.startDateInString)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            if (filteredGroupedTransactions.isNotEmpty()) {
                // Iterate through each week group
                groupedByWeek.forEach { (weekString, periodsInWeek) ->
                    // Week header (extracted to a separate composable)
                    stickyHeader {
                        WeekHeaderItem(weekString)
                    }

                    // Display transactions for each period in this week
                    periodsInWeek.forEach { (period, transactions) ->
                        // Period header (now as a regular item instead of stickyHeader)
                        item {
                            PeriodHeaderItem(
                                dayName = dateStringName.getNomJourArabParDateStr(period.startDateInString),
                                startTime = period.heurDebutInString,
                                endTime = if (period.endDateInString.isNotEmpty()) period.endDateInString else "الآن"
                            )
                        }

                        // Transactions for this period
                        items(transactions) { transaction ->
                            TransactionItem(transaction)
                        }
                    }
                }
            } else {
                // Show a message if no transactions are found
                item {
                    Text(
                        text = if (uiState.transactionsDateToList_1_3_TransactionCommercial.isNotEmpty())
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
    }
}

