package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun PreviewClientTransactionsListPreview() {
   /* // Initialize test data
    val testTransactions = B_Data_CreateTestTransactions()
    val mapsIDSDatesHistoriqueTransactions = com.example.clientjetpack.D_MapsIDSDatesHistoriqueTransactionsRep_Repository()
        .collectInit(testTransactions)

    // Display the client transactions using LazyColumn
    ClientTransactionsListPreview(mapsIDSDatesHistoriqueTransactions, testTransactions)  */
}
      /*
@Composable
fun ClientTransactionsListPreview(
    d_Rep_MapsIDSDatesHistoriqueTransactions: com.example.clientjetpack.D_MapsIDSDatesHistoriqueTransactionsRep_Repository,
    rawTransactions: List<com.example.clientjetpack.D_TransactionCommercial_Repository> = com.example.clientjetpack.B_Data_CreateTestTransactions()
) {
    // Create SqlData for displaying formatted data
    val sqlData = remember {
        com.example.clientjetpack.D_ParDatesHistoriqueTransactions_Repository(
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
    week: com.example.clientjetpack.D_ParDatesHistoriqueTransactions_Repository.Semaine,
    sqlData: com.example.clientjetpack.D_ParDatesHistoriqueTransactions_Repository,
    mapsData: com.example.clientjetpack.D_MapsIDSDatesHistoriqueTransactionsRep_Repository
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
    day: com.example.clientjetpack.D_ParDatesHistoriqueTransactions_Repository.Jour,
    sqlData: com.example.clientjetpack.D_ParDatesHistoriqueTransactions_Repository,
    mapsData: com.example.clientjetpack.D_MapsIDSDatesHistoriqueTransactionsRep_Repository
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
    transaction: com.example.clientjetpack.D_ParDatesHistoriqueTransactions_Repository.Transaction,
    sqlData: com.example.clientjetpack.D_ParDatesHistoriqueTransactions_Repository
) {
    val client = remember {
        sqlData.clientAchteurs.find { it.vidTimeTemp == transaction.clientId }
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
                // ClientAchteur name
                Text(
                    text = client?.nom ?: "Unknown ClientAchteur",
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
        com.example.clientjetpack.isSameDay(today.timeInMillis, timestamp) -> "Aujourd'hui"
        com.example.clientjetpack.isSameDay(today.apply {
            add(
                Calendar.DAY_OF_MONTH,
                -1
            )
        }.timeInMillis, timestamp) -> "Hier"
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
                                     */
