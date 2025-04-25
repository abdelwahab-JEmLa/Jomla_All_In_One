package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun AffichageHistoriquesTransactionsDeCetteJourParIdClientPRV() {
    AffichageHistoriquesTransactionsDeCetteJourParIdClient()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AffichageHistoriquesTransactionsDeCetteJourParIdClient(
    modifier: Modifier = Modifier,
    viewModel: SecID5FragID2ViewModel = koinViewModel(),
    idClient: Long = 2
) {
    val uiState by viewModel.uiState.collectAsState()

    // Filter transactions by client ID
    val filteredGroupedTransactions = uiState.transactionsDateToList_1_3_TransactionCommercial
        .map { (period, transactions) ->
            Pair(
                period,
                transactions.filter { transaction -> transaction.clientAcheteurID == idClient }
            )
        }
        .filter { (_, transactions) -> transactions.isNotEmpty() }

    LazyColumn(modifier = modifier.fillMaxWidth()) {
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
                        text = "Période: ${period.startDateInString}",
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
                    text = "Pas de transactions pour ce client",
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

@Composable
private fun TransactionItem(transaction: _1_3_TransactionCommercial) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = colorResource(id = transaction.etateActuellementEst.color),
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Transaction #${transaction.vid}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row {
                Text(
                    text = "Heure: ${transaction.heurDebutInString}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "État: ${transaction.etateActuellementEst.nomArabe}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}
