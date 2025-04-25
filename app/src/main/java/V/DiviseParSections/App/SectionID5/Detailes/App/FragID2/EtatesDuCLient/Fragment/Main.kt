package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.DataBase._01_VentsHistoriques.Models._013_ClientTransaction
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.DataBase._01_VentsHistoriques.Models._01_PeriodVentHistorique
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.DataBase._01_VentsHistoriques.Models._14_TransactionStatue
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.DataBase._01_VentsHistoriques.Repository._01_VentsHistoriquesDataBase_Repository
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Preview
@Composable
private fun AffichageHistoriquesTransactionsDeCetteJourParIdClientPRV() {
    AffichageHistoriquesTransactionsDeCetteJourParIdClient(idClient = 185L)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AffichageHistoriquesTransactionsDeCetteJourParIdClient(
    modifier: Modifier = Modifier,
    _01_VentsHistoriquesDataBase_Repository: _01_VentsHistoriquesDataBase_Repository = koinInject(),
    idClient: Long,
) {

    val clientTransactionsHistoriques =
        _01_VentsHistoriquesDataBase_Repository.getClientTransactionsHistoriques(idClient)

    LazyColumn(modifier = modifier) {
        clientTransactionsHistoriques.forEach { (period, transactions) ->
            // Sticky header for the period
            stickyHeader {
                PeriodHeader(period)
            }

            // Display each transaction as an item
            items(transactions.size) { index ->
                val transaction = transactions[index]
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
private fun PeriodHeader(period: _01_PeriodVentHistorique) {
    // Implement your header design here
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp
    ) {
        Text(
            text = "Period: ${period.tempCreationStr}",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun TransactionItem(transaction: _013_ClientTransaction) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Client info
        Text(
            text = transaction.nomClient,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Show transaction details
        Text(
            text = "Transaction ID: ${transaction.idClient}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Date: ${transaction.tempDateCreationStr}",
            style = MaterialTheme.typography.bodySmall
        )

        // Transaction states/history
        if (transaction.child_14A_HistoriquesDeCetteJour.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Status History:",
                style = MaterialTheme.typography.labelMedium
            )

            transaction.child_14A_HistoriquesDeCetteJour.forEach { state ->
                Row(
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status indicator
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = getColorForState(state),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = state.etateTransactionName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (state.description.isNotEmpty()) {
                            Text(
                                text = state.description,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            text = "${state.dateCreationStr} ${state.tempCreationStr}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Products
        if (transaction.child_14Produits.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Products:",
                style = MaterialTheme.typography.labelMedium
            )

            transaction.child_14Produits.forEach { product ->
                Row(
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${product.startDesignation} (Qty: ${product.quantity})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun getColorForState(state: _14_TransactionStatue): Color {
    return when (state.etateTransaction) {
        _14_TransactionStatue.EtateTransaction.NON_DEFINI -> Color(0xFFFFA500) // Orange
        _14_TransactionStatue.EtateTransaction.AVEC_MARCHANDISE -> Color(0xFF33B5E5) // Light Blue
        _14_TransactionStatue.EtateTransaction.A_EVITE -> Color.Black
        _14_TransactionStatue.EtateTransaction.COMMANDE_LENCE -> Color(0xFF99CC00) // Light Green
        _14_TransactionStatue.EtateTransaction.ACHAT_TERMINE -> Color(0xFFAA66CC) // Purple
        _14_TransactionStatue.EtateTransaction.ACHETEUR_NON_DISPO -> Color.Gray
        _14_TransactionStatue.EtateTransaction.FERME -> Color.DarkGray
    }
}
