package com.example.clientjetpack.Logs

import com.example.clientjetpack.Repositorys.SqlDatasDatesHistoriqueTransactions
import com.example.clientjetpack.Repositorys.formatTimestampToDate

/**
 * Improved logging function for SqlDatasDatesHistoriqueTransactions structure
 * Removes dependency on testTransactions parameter and simplifies code
 */
fun SqlDatasDatesHistoriqueTransactionslog(
    sqlDatasDatesHistoriqueTransactions: SqlDatasDatesHistoriqueTransactions
) {
    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")
    println("Created test data structure for SqlDatasDatesHistoriqueTransactions")
    println("Total weeks: ${sqlDatasDatesHistoriqueTransactions.semaines.size}")

    // Display nested data structure in hierarchical format
    println("\n-- Hierarchical Structure --")

    // Log weeks and their days
    println("\n-- Semaines (Weeks) --")

    // Sort weeks chronologically
    val sortedWeeks = sqlDatasDatesHistoriqueTransactions.semaines.sortedBy { it.vidTimeTemp }

    sortedWeeks.forEach { semaine ->
        val weekDate = formatTimestampToDate(semaine.vidTimeTemp)

        // Find days in this week directly using the timestamp ranges
        val daysInWeek = sqlDatasDatesHistoriqueTransactions.jours.filter { jour ->
            // A day belongs to a week if it falls within the week's timestamp range
            // (between week start and week start + 6 days)
            jour.vidTimeTemp >= semaine.vidTimeTemp &&
                    jour.vidTimeTemp < (semaine.vidTimeTemp + 7 * 24 * 60 * 60 * 1000L)
        }.sortedBy { it.vidTimeTemp }

        println("Semaine ($weekDate): ${daysInWeek.size} jour(s)")

        // Process each day in the week
        daysInWeek.forEachIndexed { dayIndex, jour ->
            val dayDate = formatTimestampToDate(jour.vidTimeTemp)
            val isLastDay = dayIndex == daysInWeek.size - 1
            val dayPrefix = if (isLastDay) "  └─" else "  ├─"

            // Find transactions for this day using direct timestamp comparison
            val transactionsForDay = sqlDatasDatesHistoriqueTransactions.transactions.filter { transaction ->
                // A transaction belongs to a day if it falls within the day's timestamp range
                // (between day start and day start + 24 hours)
                val transactionDay = transaction.timestamp - (transaction.timestamp % (24 * 60 * 60 * 1000L))
                transactionDay == jour.vidTimeTemp
            }

            println("$dayPrefix Jour $dayIndex ($dayDate): ${transactionsForDay.size} transaction(s)")

            // Group transactions by client directly from SqlDatasDatesHistoriqueTransactions
            val transactionsByClient = transactionsForDay
                .groupBy { transaction ->
                    // Find the client ID by searching for a client that has this transaction in its history
                    sqlDatasDatesHistoriqueTransactions.clients.find { client ->
                        client.ancientIdTransaction == transaction.vidTimeTemp
                    }?.vidTimeTemp ?: 0L
                }

            // Log all transactions for each day without the 5 transaction limit
            var transactionCount = 0
            transactionsByClient.forEach { (_, transactions) ->
                // Show each transaction
                transactions.sortedBy { it.timestamp }.forEachIndexed { tIndex, transaction ->
                    val isLastTransaction = tIndex == transactions.size - 1 &&
                            transactionCount == transactionsForDay.size - 1

                    // Use the correct prefix based on whether this is the last transaction
                    val transPrefix = if (isLastDay) {
                        if (isLastTransaction) "     └─" else "     ├─"
                    } else {
                        if (isLastTransaction) "  │  └─" else "  │  ├─"
                    }

                    println("$transPrefix Transaction #$transactionCount (ID: ${transaction.vidTimeTemp}, État: ${transaction.etate})")
                    transactionCount++
                }
            }
        }
    }

    println("\n======== TEST COMPLETED SUCCESSFULLY ========" +
            "\n")
}
