package com.example.clientjetpack.Logs

import com.example.clientjetpack.Repositorys.MapsIDSDatesHistoriqueTransactions
import com.example.clientjetpack.Repositorys.formatTimestampToDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Improved logging function for MapsIDSDatesHistoriqueTransactions
 * Ensures correct week-day associations and provides detailed transaction information
 */
fun A_LogMapsIDSDatesHistoriqueTransactions(mapsIDSDatesHistoriqueTransactions: MapsIDSDatesHistoriqueTransactions) {
    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")
    println("Created test data structure for MapsIDSDatesHistoriqueTransactions")
    println("Total weeks: ${mapsIDSDatesHistoriqueTransactions.semaines.size}")
    println("Total days: ${mapsIDSDatesHistoriqueTransactions.jours.size}")
    println("Total clients: ${mapsIDSDatesHistoriqueTransactions.clients.size}")
    println("Total transactions: ${mapsIDSDatesHistoriqueTransactions.transactions.size}")

    // Display nested data structure in hierarchical format
    println("\n-- Hierarchical Structure --")

    // Log weeks and their days
    println("\n-- Semaines (Weeks) --")

    // Sort weeks chronologically
    val sortedWeeks = mapsIDSDatesHistoriqueTransactions.semaines.entries.sortedBy { it.key }

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
                println("$clientPrefix Client ID: $clientId - ${transactionIds.size} transaction(s)")

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
                    val timeStr = formatTimestampToTime(getTransactionTime(transactionId, transactionsInDay))

                    println("$transactionPrefix Transaction #$transactionCount (ID: $transactionId, État: $transactionType, Time: $timeStr)")
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
private fun formatTimestampToTime(timestamp: Long): String {
    if (timestamp <= 0) return "N/A"
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

/**
 * Helper function to get transaction time (mocking based on transaction ID for this example)
 * In a real implementation, you would get this from the transaction data
 */
private fun getTransactionTime(transactionId: Long, transactionsInDay: List<Long>): Long {
    // This is just a placeholder. In a real implementation, you would get the real timestamp.
    // For demonstration purposes, we'll create a fake time based on transaction ID
    val baseHour = 8 // Start at 8 AM
    val index = transactionsInDay.indexOf(transactionId)

    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, baseHour + (index % 8)) // Spread transactions over 8 hours
    calendar.set(Calendar.MINUTE, ((transactionId * 7) % 60).toInt())    // Pseudo-random minutes

    return calendar.timeInMillis
}
