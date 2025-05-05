package com.example.clientjetpack.Logs

import com.example.clientjetpack.Repositorys.MapsIDSDatesHistoriqueTransactions
import com.example.clientjetpack.Repositorys.formatTimestampToDate

fun MapsIDSDatesHistoriqueTransactions.log() {
    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")
    println("Created test data structure for MapsIDSDatesHistoriqueTransactions")

    // Display nested data structure in hierarchical format
    println("\n-- Hierarchical Structure --")

    // Log weeks and their days
    println("\n-- Semaines (Weeks) --")

    // Sort weeks chronologically
    val sortedWeeks = semaines.entries.sortedBy { it.key }

    sortedWeeks.forEach { (weekTimestamp, days) ->
        val weekDate = formatTimestampToDate(weekTimestamp)
        println("Semaine ($weekDate): ${days.size} jour(s)")

        // Sort days chronologically for consistent output
        val sortedDays = days.sortedBy { it }

        // Log days for each week
        sortedDays.forEachIndexed { index, dayTimestamp ->
            val dayDate = formatTimestampToDate(dayTimestamp)
            val transactionsInDay = jours[dayTimestamp]?.size ?: 0
            val isLastDay = index == sortedDays.size - 1
            val dayPrefix = if (isLastDay) "  └─" else "  ├─"

            println("$dayPrefix Jour $index ($dayDate): $transactionsInDay transaction(s)")

            // Log all transactions for each day without the 5 transaction limit
            jours[dayTimestamp]?.let { transactionIds ->
                transactionIds.forEachIndexed { tIndex, transactionId ->
                    val state = transactions[transactionId]
                    val isLastTransaction = tIndex == transactionIds.size - 1

                    // Use correct prefix based on whether this is the last day and last transaction
                    val transPrefix = if (isLastDay) {
                        if (isLastTransaction) "     └─" else "     ├─"
                    } else {
                        if (isLastTransaction) "  │  └─" else "  │  ├─"
                    }

                    println("$transPrefix Transaction #$tIndex (ID: $transactionId, État: $state)")
                }
            }
        }
    }

    println("\n======== TEST COMPLETED SUCCESSFULLY ========")
}
