package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.Type
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Preview
@Composable
private fun PreviewClientTransactionsListPreview() {
    // Initialize test data
    val testTransactions = B_Data_CreateTestTransactions()
    val mapsIDSDatesHistoriqueTransactions = D_Rep_MapsIDSDatesHistoriqueTransactions()
        .collectInit(testTransactions)

    // Display the client transactions using LazyColumn
    ClientTransactionsListPreview(mapsIDSDatesHistoriqueTransactions, testTransactions)
}

@Composable
fun ClientTransactionsListPreview(
    d_Rep_MapsIDSDatesHistoriqueTransactions: D_Rep_MapsIDSDatesHistoriqueTransactions,
    rawTransactions: List<D_Repo_TransactionCommercial> = B_Data_CreateTestTransactions()
) {
    // Create SqlData for displaying formatted data
    val sqlData = remember {
        D_Repo_SqlDatasDatesHistoriqueTransactions(
            d_Rep_MapsIDSDatesHistoriqueTransactions,
            rawTransactions
        )
    }

    // Sort weeks in descending order (most recent first)
    val sortedWeeks = remember {
        sqlData.semaines.sortedByDescending { it.vidTimeTemp }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // For each week
        items(sortedWeeks) { week ->
            WeekSection(week, sqlData, d_Rep_MapsIDSDatesHistoriqueTransactions)
        }
    }
}

@Composable
fun WeekSection(
    week: D_Repo_SqlDatasDatesHistoriqueTransactions.Semaine,
    sqlData: D_Repo_SqlDatasDatesHistoriqueTransactions,
    mapsData: D_Rep_MapsIDSDatesHistoriqueTransactions
) {
    val weekDays = remember {
        // Get all days in this week
        val dayIds = mapsData.semaines[week.vidTimeTemp] ?: emptyList()
        // Find corresponding day objects and sort by timestamp (descending)
        sqlData.jours.filter { it.vidTimeTemp in dayIds }
            .sortedByDescending { it.vidTimeTemp }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Week header
            Text(
                text = "Week ${week.semainCountDonSonAnne} - ${getWeekDateRange(week.vidTimeTemp)}",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Days in this week
            weekDays.forEach { day ->
                DaySection(day, sqlData, mapsData)
            }
        }
    }
}

@Composable
fun DaySection(
    day: D_Repo_SqlDatasDatesHistoriqueTransactions.Jour,
    sqlData: D_Repo_SqlDatasDatesHistoriqueTransactions,
    mapsData: D_Rep_MapsIDSDatesHistoriqueTransactions
) {
    val transactionIds = remember {
        mapsData.jours[day.vidTimeTemp] ?: emptyList()
    }

    val dayTransactions = remember {
        sqlData.transactions.filter { it.vidTimeTemp in transactionIds }
            .sortedByDescending { it.timestamp }
    }

    Column(modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp)) {
        // Day header with formatted date
        Text(
            text = formatDateLabel(day.vidTimeTemp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Transactions in this day
        dayTransactions.forEach { transaction ->
            TransactionItem(transaction, sqlData)
            if (transaction != dayTransactions.last()) {
                Divider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = Color.LightGray
                )
            }
        }
    }

    if (day != sqlData.jours.last()) {
        Divider(color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun TransactionItem(
    transaction: D_Repo_SqlDatasDatesHistoriqueTransactions.Transaction,
    sqlData: D_Repo_SqlDatasDatesHistoriqueTransactions
) {
    val client = remember {
        sqlData.clients.find { it.vidTimeTemp == transaction.clientId }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Client name
                Text(
                    text = client?.nom ?: "Unknown Client",
                    fontWeight = FontWeight.Medium
                )

                // Transaction time
                Text(
                    text = transaction.tempStr,
                    color = Color.Gray
                )
            }

            // Transaction state/type
            Text(
                text = formatTransactionType(transaction.etate),
                color = getColorForType(transaction.etate)
            )
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Details",
            tint = Color.Gray
        )
    }
}

// Helper function to format transaction type to a more readable form
fun formatTransactionType(type: Type): String {
    return when (type) {
        Type.COMMANDE_LIVRAI -> "Commande livrée"
        Type.Cible -> "Ciblé"
        Type.ON_MODE_COMMEND_ACTUELLEMENT -> "En commande"
        Type.ACHETEUR_NON_DISPO -> "Acheteur non disponible"
        Type.NON_DEFINI -> "Non défini"
        else -> type.toString().replace("_", " ").lowercase().capitalize()
    }
}

// Helper function to get color based on transaction type
@Composable
fun getColorForType(type: Type): Color {
    return when (type) {
        Type.COMMANDE_LIVRAI -> Color.Green
        Type.Cible -> Color.Blue
        Type.ON_MODE_COMMEND_ACTUELLEMENT -> Color(0xFFFFA500) // Orange
        Type.ACHETEUR_NON_DISPO -> Color.Red
        else -> Color.Gray
    }
}

// Helper function to format day timestamps into readable date
fun formatDateLabel(timestamp: Long): String {
    val today = Calendar.getInstance()
    val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }

    // Check if the date is today, yesterday, or another day
    return when {
        isSameDay(today.timeInMillis, timestamp) -> "Aujourd'hui"
        isSameDay(today.apply { add(Calendar.DAY_OF_MONTH, -1) }.timeInMillis, timestamp) -> "Hier"
        else -> {
            val dateFormat = SimpleDateFormat("EEEE d MMMM", Locale.getDefault())
            dateFormat.format(Date(timestamp)).capitalize()
        }
    }
}

// Helper function to get the date range for a week (e.g., "May 1 - May 7")
fun getWeekDateRange(weekStartTimestamp: Long): String {
    val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
    val startDate = Date(weekStartTimestamp)

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = weekStartTimestamp
    calendar.add(Calendar.DAY_OF_WEEK, 6) // End of week (6 days after start)
    val endDate = calendar.time

    return "${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}"
}

// Extension function to capitalize first letter
fun String.capitalize(): String {
    return if (this.isEmpty()) this else this[0].uppercase() + this.substring(1)
}
