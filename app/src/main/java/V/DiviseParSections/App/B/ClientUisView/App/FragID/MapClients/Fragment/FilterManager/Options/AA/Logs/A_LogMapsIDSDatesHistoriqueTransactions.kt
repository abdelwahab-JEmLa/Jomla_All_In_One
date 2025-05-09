package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Enleve.B_Data_CreateTestTransactions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun normalizeTimetampFromeStrDate(stringDate: String): Long {
    val calendar = Calendar.getInstance()
    // Parse the date string to get a timestamp
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = dateFormat.parse(stringDate)

    calendar.apply {
        time = date ?: Date() // Use the parsed date or current date as fallback
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}

/**
 * Improved logging function for D_MapsIDSDatesHistoriqueTransactionsRep_Repository
 * Ensures correct week-day associations and provides detailed transaction information
 */
fun A_LogMapsIDSDatesHistoriqueTransactions(
    mapsIDSDatesHistoriqueTransactions: D_MapsIDSDatesHistoriqueTransactionsRep_Repository
) {
    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")

    // Display nested data structure in hierarchical format
    println("\n-- Hierarchical Structure --")

    // Log weeks and their days
    println("\n-- Semaines (Weeks) --")

    // Sort weeks chronologically
    val sortedWeeks = mapsIDSDatesHistoriqueTransactions.semaines.entries.sortedBy { it.key }

    // Load test transactions to access real timestamps
    val testTransactions = B_Data_CreateTestTransactions()

    sortedWeeks.forEach { (weekTimestamp, dayTimestamps) ->
        val weekDate = formatTimestampToDate(weekTimestamp)
        println("Semaine ($weekDate): ${dayTimestamps.size} jour(s)")

        // Sort days chronologically
        val sortedDays = dayTimestamps.sortedBy { it }

        // Process each day in the week
        sortedDays.forEachIndexed { dayIndex, dayTimestamp ->
            val dayDate = formatTimestampToDate(dayTimestamp)
            val isLastDay = dayIndex == sortedDays.size - 1
            val dayPrefix = if (isLastDay) "  └─" else "  ├─"

            // Find transactions for this day
            val transactionsInDay = mapsIDSDatesHistoriqueTransactions.jours[dayTimestamp] ?: emptyList()
            println("$dayPrefix Jour $dayIndex ($dayDate): ${transactionsInDay.size} transaction(s)")

            // Group transactions by client
            val transactionsByClient = mutableMapOf<Long, MutableList<Long>>()

            // Find which client each transaction belongs to
            transactionsInDay.forEach { transactionId ->
                mapsIDSDatesHistoriqueTransactions.clients.forEach { (clientId, clientTransactions) ->
                    if (clientTransactions.contains(transactionId)) {
                        transactionsByClient.getOrPut(clientId) { mutableListOf() }.add(transactionId)
                    }
                }
            }

            // Log transactions grouped by client
            var transactionCount = 0
            transactionsByClient.forEach { (clientId, transactionIds) ->
                val clientPrefix = if (isLastDay) "     " else "  │  "

                // Find client name from test transactions
                val clientName = testTransactions
                    .find { it.clientAcheteurID == clientId }?.nomClientConcerned ?: "Client $clientId"

                println("$clientPrefix Client ID: $clientId ($clientName) - ${transactionIds.size} transaction(s)")

                // Log individual transactions
                transactionIds.forEachIndexed { tIndex, transactionId ->
                    val isLastTransaction = tIndex == transactionIds.size - 1 &&
                            transactionCount == transactionsInDay.size - 1

                    val transactionPrefix = if (isLastDay) {
                        if (isLastTransaction) "     └─" else "     ├─"
                    } else {
                        if (isLastTransaction) "  │  └─" else "  │  ├─"
                    }

                    val transactionType = mapsIDSDatesHistoriqueTransactions.transactions[transactionId]

                    // Find the actual transaction from test data to get proper timestamp
                    val transaction = testTransactions.find { it.vid == transactionId }
                    val timeStr = if (transaction != null) {
                        formatTimestampToTime(transaction.timestamps)
                    } else {
                        // Fallback to old method if not found
                        formatTimestampToTime(getTransactionTime(transactionId, transactionsInDay))
                    }

                    println("$transactionPrefix Transaction #$transactionCount (ID: $transactionId, État: $transactionType, Time: $timeStr)")
                    transactionCount++
                }
            }
        }
    }

    println("\n======== TEST COMPLETED SUCCESSFULLY ========\n")
}
