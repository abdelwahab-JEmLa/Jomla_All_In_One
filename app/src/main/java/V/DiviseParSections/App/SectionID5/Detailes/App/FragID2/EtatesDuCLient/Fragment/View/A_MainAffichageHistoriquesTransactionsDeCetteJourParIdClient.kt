package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.View

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel.SecID5FragID2ViewModel
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.Modules.GetDateStringName
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

    // Find client data by ID
    val clientData = remember(uiState.sl_3_ClientsDataBase, idClient) {
        uiState.sl_3_ClientsDataBase.find { it.vid == idClient }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        ClientHeaderSection(clientData)

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            if (filteredGroupedTransactions.isNotEmpty()) {
                // Iterate through each week group
                groupedByWeek.forEach { (weekString, periodsInWeek) ->
                    // Week header
                    stickyHeader {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondary)
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = weekString,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Divider(
                                modifier = Modifier.padding(top = 4.dp),
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }

                    // Display transactions for each period in this week
                    periodsInWeek.forEach { (period, transactions) ->
                        // Period header
                        stickyHeader {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "يوم: ${dateStringName.getNomJourArabParDateStr(period.startDateInString)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "من ${period.heurDebutInString} إلى ${if (period.endDateInString.isNotEmpty()) period.endDateInString else "الآن"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Divider(modifier = Modifier.padding(top = 4.dp))
                            }
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
