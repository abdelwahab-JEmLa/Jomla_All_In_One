package com.example.clientjetpack.Logs

import com.example.clientjetpack.Tests.B.Data.SqlDatasDatesHistoriqueTransactions
import com.example.clientjetpack.Repositorys.formatTimestampToDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Improved logging function for SqlDatasDatesHistoriqueTransactions structure
 * Properly associates transactions with days based on timestamp comparison
 */
fun SqlDatasDatesHistoriqueTransactionslog(
    sqlDatasDatesHistoriqueTransactions: SqlDatasDatesHistoriqueTransactions
) {
    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")
    println("Created test data structure for SqlDatasDatesHistoriqueTransactions")
    println("Total weeks: ${sqlDatasDatesHistoriqueTransactions.semaines.size}")
    println("Total days: ${sqlDatasDatesHistoriqueTransactions.jours.size}")
    println("Total clients: ${sqlDatasDatesHistoriqueTransactions.clients.size}")
    println("Total transactions: ${sqlDatasDatesHistoriqueTransactions.transactions.size}")

    // Display nested data structure in hierarchical format
    println("\n-- Hierarchical Structure --")

    // Log weeks and their days
    println("\n-- Semaines (Weeks) --")

    // Sort weeks chronologically
    val sortedWeeks = sqlDatasDatesHistoriqueTransactions.semaines.sortedBy { it.vidTimeTemp }

    sortedWeeks.forEach { semaine ->
        val weekDate = formatTimestampToDate(semaine.vidTimeTemp)

        // Find days in this week using timestamp comparison for week belonging
        val daysInWeek = sqlDatasDatesHistoriqueTransactions.jours.filter { jour ->
            belongsToSameWeek(jour.vidTimeTemp, semaine.vidTimeTemp)
        }.sortedBy { it.vidTimeTemp }

        println("Semaine ($weekDate): ${daysInWeek.size} jour(s)")

        // Process each day in the week
        daysInWeek.forEachIndexed { dayIndex, jour ->
            val dayDate = formatTimestampToDate(jour.vidTimeTemp)
            val isLastDay = dayIndex == daysInWeek.size - 1
            val dayPrefix = if (isLastDay) "  └─" else "  ├─"

            // Find transactions for this day using direct timestamp comparison
            val transactionsForDay = sqlDatasDatesHistoriqueTransactions.transactions.filter { transaction ->
                isSameDay(transaction.timestamp, jour.vidTimeTemp)
            }.sortedBy { it.timestamp }

            println("$dayPrefix Jour $dayIndex ($dayDate): ${transactionsForDay.size} transaction(s)")

            // Group transactions by client
            val transactionsByClient = transactionsForDay
                .groupBy { transaction -> transaction.clientId }
                .toSortedMap()

            // Log transactions grouped by client
            var transactionCount = 0
            transactionsByClient.forEach { (clientId, transactions) ->
                // Find client name
                val clientName = sqlDatasDatesHistoriqueTransactions.clients
                    .find { it.vidTimeTemp == clientId }?.nom ?: "Unknown Client"

                val clientPrefix = if (isLastDay) "     " else "  │  "
                println("$clientPrefix Client ID: $clientId ($clientName) - ${transactions.size} transaction(s)")

                // Log individual transactions
                transactions.forEachIndexed { tIndex, transaction ->
                    val isLastTransaction = tIndex == transactions.size - 1 &&
                            transactionCount == transactionsForDay.size - 1

                    val transactionPrefix = if (isLastDay) {
                        if (isLastTransaction) "     └─" else "     ├─"
                    } else {
                        if (isLastTransaction) "  │  └─" else "  │  ├─"
                    }

                    val timeStr = formatTime(transaction.timestamp)
                    println("$transactionPrefix Transaction #$transactionCount (ID: ${transaction.vidTimeTemp}, État: ${transaction.etate}, Time: $timeStr)")
                    transactionCount++
                }
            }
        }
    }

    println("\n======== TEST COMPLETED SUCCESSFULLY ========\n")
}

/**
 * Helper function to format timestamp to time (HH:mm)
 */
private fun formatTime(timestamp: Long): String {
    if (timestamp <= 0) return "N/A"
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

/**
 * Helper function to check if two timestamps belong to the same week
 */
private fun belongsToSameWeek(timestamp1: Long, timestamp2: Long): Boolean {
    val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp2 }

    return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
            cal1.get(java.util.Calendar.WEEK_OF_YEAR) == cal2.get(java.util.Calendar.WEEK_OF_YEAR)
}

/**
 * Helper function to check if two timestamps belong to the same day
 */
private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
    val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp2 }

    return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
            cal1.get(java.util.Calendar.MONTH) == cal2.get(java.util.Calendar.MONTH) &&
            cal1.get(java.util.Calendar.DAY_OF_MONTH) == cal2.get(java.util.Calendar.DAY_OF_MONTH)
}
