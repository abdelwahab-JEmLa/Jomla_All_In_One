package com.example.clientjetpack.Id2.ClientGpsFilter.Test.Z_Passive

import com.example.clientjetpack.Id2.ClientGpsFilter.Test.DB_ParDatesHistoriqueTransactions_Repository

/**
 * Logs filtered transactions for a specific day
 */
fun A_Logs_FilterByDayeLog(
    sqlDatasDatesHistorique: DB_ParDatesHistoriqueTransactions_Repository,
    filterDateTimeTamp: Long
) {
    println("======== TESTING FILTERED DATES HISTORIQUE BY DAY ========")

    // Display filtered data structure for specific day
    println("\n-- Filtered Transactions for Day: ${formatTimestampToDate(filterDateTimeTamp)} --")

    // Use the new filter function to get filtered transactions
    val filteredTransactions = D_FilterHandler()
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
        val clientAchteurName = sqlDatasDatesHistorique.clients
            .find { it.vidTimeTemp == clientId }?.nom ?: "Unknown ClientAchteur"

        println("ClientAchteur ID: $clientId ($clientAchteurName) - ${transactions.size} transaction(s)")

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
