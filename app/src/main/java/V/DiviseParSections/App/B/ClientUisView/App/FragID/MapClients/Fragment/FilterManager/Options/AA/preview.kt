package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.Type
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview
@Composable
private fun preview() {
    // Initialize test data
    val testTransactions = B_Data_CreateTestTransactions()
    val mapsIDSDatesHistoriqueTransactions = D_Rep_MapsIDSDatesHistoriqueTransactions()
        .collectInit(testTransactions)
    val sqlData = D_Repo_SqlDatasDatesHistoriqueTransactions(
        mapsIDSDatesHistoriqueTransactions,
        testTransactions
    )

    // Display the client transactions using LazyColumn
    ClientTransactionsListPreview(sqlData)
}

@Composable
fun ClientTransactionsListPreview(
    sqlData: D_Repo_SqlDatasDatesHistoriqueTransactions
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Group transactions by date (jour)
        val groupedByDay = sqlData.jours.sortedByDescending { it.vidTimeTemp }

        groupedByDay.forEach { jour ->
            val dayTransactions = sqlData.transactions.filter { transaction ->
                val calJour = java.util.Calendar.getInstance().apply {
                    timeInMillis = jour.vidTimeTemp
                }
                val calTrans = java.util.Calendar.getInstance().apply {
                    timeInMillis = transaction.timestamp
                }

                calJour.get(java.util.Calendar.YEAR) == calTrans.get(java.util.Calendar.YEAR) &&
                        calJour.get(java.util.Calendar.DAY_OF_YEAR) == calTrans.get(java.util.Calendar.DAY_OF_YEAR)
            }

            if (dayTransactions.isNotEmpty()) {
                item {
                    DayHeader(jour.dateStr)
                }

                // Group transactions by client for this day
                val transactionsByClient = dayTransactions.groupBy { it.clientId }

                items(transactionsByClient.entries.toList()) { (clientId, clientTransactions) ->
                    val client = sqlData.clients.find { it.vidTimeTemp == clientId }
                    ClientTransactionCard(
                        clientName = client?.nom ?: "Unknown Client",
                        transactions = clientTransactions.sortedBy { it.timestamp }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun DayHeader(dateStr: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(8.dp)
    ) {
        Text(
            text = dateStr,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun ClientTransactionCard(
    clientName: String,
    transactions: List<D_Repo_SqlDatasDatesHistoriqueTransactions.Transaction>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = clientName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            transactions.forEach { transaction ->
                TransactionItem(transaction)

                if (transaction != transactions.last()) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: D_Repo_SqlDatasDatesHistoriqueTransactions.Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(getStatusColor(transaction.etate))
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Transaction details
        Column {
            Text(
                text = "ID: ${transaction.vidTimeTemp}",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = getStatusText(transaction.etate),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = transaction.tempStr,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// Helper function to get color based on transaction status
fun getStatusColor(status: Type): Color {
    return when (status) {
        Type.COMMANDE_LIVRAI -> Color(0xFF4CAF50) // Green
        Type.Cible -> Color(0xFFFF9800) // Orange
        Type.ACHETEUR_NON_DISPO -> Color(0xFFE91E63) // Pink
        Type.ON_MODE_COMMEND_ACTUELLEMENT -> Color(0xFF2196F3) // Blue
        else -> Color(0xFF9E9E9E) // Gray
    }
}

// Helper function to get readable status text
fun getStatusText(status: Type): String {
    return when (status) {
        Type.COMMANDE_LIVRAI -> "Commande Livrée"
        Type.Cible -> "Cible"
        Type.ACHETEUR_NON_DISPO -> "Non Disponible"
        Type.ON_MODE_COMMEND_ACTUELLEMENT -> "En Commande"
        else -> "Non Défini"
    }
}
