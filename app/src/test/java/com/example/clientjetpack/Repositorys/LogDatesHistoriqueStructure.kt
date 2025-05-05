package com.example.clientjetpack.Repositorys

import java.util.Calendar

fun SqlDatasDatesHistoriqueTransactions
    .log(testTransactions: List<TransactionCommercial>? = null) {

    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")
    println("Created test data structure for SqlDatasDatesHistoriqueTransactions")
    println("Total weeks: ${semaines.size}")

    // Display nested data structure in hierarchical format
    println("\n-- Hierarchical Structure --")

    // Log weeks and their days
    println("\n-- Semaines (Weeks) --")

    // Sort weeks chronologically
    val sortedWeeks = semaines.sortedBy { it.vidTimeTemp }

    sortedWeeks.forEach { semaine ->
        val weekDate = formatTimestampToDate(semaine.vidTimeTemp)

        // Find days in this week
        val daysInWeek = jours.filter { jour ->
            // Calculate the week start for this day
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = jour.vidTimeTemp
            calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            val weekStart = calendar.timeInMillis

            weekStart == semaine.vidTimeTemp
        }.sortedBy { it.vidTimeTemp }

        println("Semaine ($weekDate): ${daysInWeek.size} jour(s)")

        // Process each day in the week
        daysInWeek.forEachIndexed { index, jour ->
            val dayDate = formatTimestampToDate(jour.vidTimeTemp)

            // Find transactions for this day
            val transactionsForDay = transactions.filter { transaction ->
                val transactionDay = java.util.Calendar.getInstance().apply {
                    timeInMillis = transaction.timestamp
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                transactionDay == jour.vidTimeTemp
            }

            println("  ├─ Jour $index ($dayDate): ${transactionsForDay.size} transaction(s)")

            // Group transactions by client
            val clientTransactions = transactionsForDay.groupBy { transaction ->
                val originalTransaction = testTransactions?.find { it.vid == transaction.vidTimeTemp }
                originalTransaction?.clientAcheteurID ?: 0L
            }

            // Log transactions for each day (limited to first 5)
            var transactionCount = 0
            clientTransactions.forEach { (clientId, transactions) ->
                val client = clients.find { it.vidTimeTemp == clientId }
                if (client != null) {
                    // Show each transaction (up to 5 total across all clients)
                    transactions.sortedBy { it.timestamp }.forEach { transaction ->
                        if (transactionCount < 5) {
                            val isLastTransaction = transactionCount == 4 ||
                                    (transactionCount == transactions.size - 1 &&
                                            transactionCount == transactionsForDay.size - 1)
                            val prefix = if (isLastTransaction) "  │  └─" else "  │  ├─"
                            println("$prefix Transaction #$transactionCount (ID: ${transaction.vidTimeTemp}, État: ${transaction.etate})")
                            transactionCount++
                        }
                    }
                }
            }

            // Show ellipsis if there are more transactions
            if (transactionsForDay.size > 5) {
                println("  │  └─ ... ${transactionsForDay.size - 5} more transaction(s)")
            }
        }
    }

    // Log clients and their transactions
    println("\n-- Clients --")
    clients.forEach { client ->
        // Find transactions for this client
        val clientTransactions = transactions.filter { transaction ->
            val originalTransaction = testTransactions?.find { it.vid == transaction.vidTimeTemp }
            originalTransaction?.clientAcheteurID == client.vidTimeTemp
        }

        println("Client (ID: ${client.vidTimeTemp}, Name: ${client.nom}): ${clientTransactions.size} transaction(s)")

        // Log transactions for each client (limited to first 3)
        clientTransactions.take(3).forEachIndexed { index, transaction ->
            val isLastTransaction = index == clientTransactions.take(3).size - 1 &&
                    clientTransactions.size <= 3
            val prefix = if (isLastTransaction) "  └─" else "  ├─"
            println("$prefix Transaction #$index (ID: ${transaction.vidTimeTemp}, État: ${transaction.etate})")
        }

        // Show ellipsis if there are more transactions
        if (clientTransactions.size > 3) {
            println("  └─ ... ${clientTransactions.size - 3} more transaction(s)")
        }
    }

    // Log summary statistics
    println("\n-- Summary Statistics --")
    println("Total weeks: ${semaines.size}")
    println("Total days with transactions: ${jours.size}")
    println("Total clients: ${clients.size}")
    println("Total transactions: ${transactions.size}")

    println("\n======== TEST COMPLETED SUCCESSFULLY ========")
}

// Helper function to format timestamp to readable date
private fun formatTimestampToDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd")
    return formatter.format(date)
}
