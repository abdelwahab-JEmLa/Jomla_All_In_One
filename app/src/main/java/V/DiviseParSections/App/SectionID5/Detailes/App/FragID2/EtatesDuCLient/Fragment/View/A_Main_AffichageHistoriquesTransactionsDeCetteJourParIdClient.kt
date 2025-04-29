package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.View

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel.ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Composable
fun A_Main_AffichageHistoriquesTransactionsDeCetteJourParIdClient(
    modifier: Modifier = Modifier,
    viewModel: ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient = koinViewModel(),
    idClient: Long = 0
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateStringName = DatesHandler()

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

    // Main Column container - removed heightIn constraint
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Title or header for the transactions section


        // Regular Column without scrolling behavior
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (filteredGroupedTransactions.isNotEmpty()) {
                // Iterate through each week group
                groupedByWeek.forEach { (weekString, periodsInWeek) ->
                    // Week header
                    C_1_Header_WeekHeaderItem(weekString)

                    // Display transactions for each period in this week
                    periodsInWeek.forEach { (period, transactions) ->
                        // Period header
                        C_2_Header_PeriodHeaderItem(
                            dayName = dateStringName.getNomJourArabParDateStr(period.startDateInString),
                            startTime = period.heurDebutInString,
                            endTime = period.endDateInString.ifEmpty { "الآن" }
                        )

                        // Transactions for this period
                        transactions.forEach { transaction ->
                            B_Item_TransactionItem(
                                viewModel=viewModel,
                                transaction=transaction)
                        }
                    }
                }
            } else {
                // Show a message if no transactions are found
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
