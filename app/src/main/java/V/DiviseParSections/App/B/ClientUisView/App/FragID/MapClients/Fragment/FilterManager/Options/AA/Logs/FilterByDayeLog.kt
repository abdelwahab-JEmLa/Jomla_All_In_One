package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs

/**
 * Logs filtered transactions for a specific day
 */
fun FilterByDayeLog(
    sqlDatasDatesHistorique: D_DatesHistoriqueTransactions_Repository,
    filterDateTimeTamp: Long
) {
    println("======== TESTING FILTERED DATES HISTORIQUE BY DAY ========")

    // Display filtered data structure for specific day
    println("\n-- Filtered Transactions for Day: ${formatTimestampToDate(filterDateTimeTamp)} --")

    // Use the new filter function to get filtered transactions
    val filteredTransactions = FilterHandler()
        .filterTransactionsByDay(sqlDatasDatesHistorique, filterDateTimeTamp)

    println("Found ${filteredTransactions.size} transaction(s) for this day")

    // Group transactions by client
    val transactionsByClient = filteredTransactions
        .groupBy { transaction -> transaction.clientId }
        .toSortedMap()

    // Log transactions grouped by client
    var transactionCount = 0
    transactionsByClient.forEach { (clientId, transactions) ->
        // Find client name
        val clientName = sqlDatasDatesHistorique.clients
            .find { it.vidTimeTemp == clientId }?.nom ?: "Unknown Client"

        println("Client ID: $clientId ($clientName) - ${transactions.size} transaction(s)")

        // Log individual transactions
        transactions.forEachIndexed { tIndex, transaction ->
            val isLastTransaction = tIndex == transactions.size - 1
            val transactionPrefix = if (isLastTransaction) "  └─" else "  ├─"

            val timeStr = formatTime(transaction.timestamp)
            println("$transactionPrefix Transaction #$transactionCount (ID: ${transaction.vidTimeTemp}, État: ${transaction.etate}, Time: $timeStr)")
            transactionCount++
        }
    }

    if (filteredTransactions.isEmpty()) {
        println("No transactions found for date: ${formatTimestampToDate(filterDateTimeTamp)}")
    }

    println("\n======== FILTER TEST COMPLETED SUCCESSFULLY ========\n")
}
