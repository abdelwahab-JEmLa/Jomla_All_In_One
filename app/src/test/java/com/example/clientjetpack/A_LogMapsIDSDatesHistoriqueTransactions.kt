package com.example.clientjetpack

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun normalizeTimetampFromeStrDate(stringDate: String): Long {
    val calendar = Calendar.getInstance()
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

fun A_LogMapsIDSDatesHistoriqueTransactions(
    mapsIDSDatesHistoriqueTransactions: D_MapsIDSDatesHistoriqueTransactionsRep_Repository
) {
    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")

    println("\n-- Hierarchical Structure --")

    println("\n-- Semaines (Weeks) --")

    val sortedWeeks = mapsIDSDatesHistoriqueTransactions.semaines.entries.sortedBy { it.key }

    val testTransactions = _B_TestTransactionDataProvider.getTransactions()

    sortedWeeks.forEach { (weekTimestamp, dayTimestamps) ->
        val weekDate = formatTimestampToDate(weekTimestamp)
        println("Semaine ($weekDate): ${dayTimestamps.size} jour(s)")

        val sortedDays = dayTimestamps.sortedBy { it }

        sortedDays.forEachIndexed { dayIndex, dayTimestamp ->
            val dayDate = formatTimestampToDate(dayTimestamp)
            val isLastDay = dayIndex == sortedDays.size - 1
            val dayPrefix = if (isLastDay) "  └─" else "  ├─"

            val transactionsInDay = mapsIDSDatesHistoriqueTransactions.jours[dayTimestamp] ?: emptyList()
            println("$dayPrefix Jour $dayIndex ($dayDate): ${transactionsInDay.size} transaction(s)")

            val transactionsByClient = mutableMapOf<Long, MutableList<Long>>()

            transactionsInDay.forEach { transactionId ->
                mapsIDSDatesHistoriqueTransactions.clients.forEach { (clientId, clientTransactions) ->
                    if (clientTransactions.contains(transactionId)) {
                        transactionsByClient.getOrPut(clientId) { mutableListOf() }.add(transactionId)
                    }
                }
            }

            var transactionCount = 0
            transactionsByClient.forEach { (clientId, transactionIds) ->
                val clientPrefix = if (isLastDay) "     " else "  │  "

                val clientName = testTransactions
                    .find { it.clientAcheteurID == clientId }?.nomClientConcerned ?: "Client $clientId"

                println("$clientPrefix Client ID: $clientId ($clientName) - ${transactionIds.size} transaction(s)")

                transactionIds.forEachIndexed { tIndex, transactionId ->
                    val isLastTransaction = tIndex == transactionIds.size - 1 &&
                            transactionCount == transactionsInDay.size - 1

                    val transactionPrefix = if (isLastDay) {
                        if (isLastTransaction) "     └─" else "     ├─"
                    } else {
                        if (isLastTransaction) "  │  └─" else "  │  ├─"
                    }

                    val transactionType = mapsIDSDatesHistoriqueTransactions.transactions[transactionId]

                    val transaction = testTransactions.find { it.vid == transactionId }
                    val timeStr = if (transaction != null) {
                        formatTimestampToTime(transaction.timestamps)
                    } else {
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
