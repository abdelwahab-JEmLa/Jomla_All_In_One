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

    // Find client data by ID
    val clientData = remember(uiState.sl_3_ClientsDataBase, idClient) {
        uiState.sl_3_ClientsDataBase.find { it.vid == idClient }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        ClientHeaderSection(clientData)

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            filteredGroupedTransactions.forEach { (period, transactions) ->
                stickyHeader {
                    // Period header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Période: ${dateStringName.getNomJourArabParDateStr(period.startDateInString)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "De ${period.heurDebutInString} à ${if (period.endDateInString.isNotEmpty()) period.endDateInString else "maintenant"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Divider(modifier = Modifier.padding(top = 4.dp))
                    }
                }

                items(transactions) { transaction ->
                    TransactionItem(transaction)
                }
            }

            // Show a message if no transactions are found
            if (filteredGroupedTransactions.isEmpty()) {
                item {
                    Text(
                        text = if (uiState.transactionsDateToList_1_3_TransactionCommercial.isNotEmpty())
                            "Pas de transactions pour le client $idClient"
                        else "Chargement des données...",
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

