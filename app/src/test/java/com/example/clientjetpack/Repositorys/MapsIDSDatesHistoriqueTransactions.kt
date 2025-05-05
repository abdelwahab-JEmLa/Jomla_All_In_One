package com.example.clientjetpack.Repositorys

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Calendar

class MapsIDSDatesHistoriqueTransactions {
    var semaines by mutableStateOf<Map<Long, MutableList<Long>>>(emptyMap())

    var jours by mutableStateOf<Map<Long, MutableList<Long>>>(emptyMap())

    var clients by mutableStateOf<Map<Long, MutableList<Long>>>(emptyMap())

    var transactions by mutableStateOf<Map<Long, Type>>(emptyMap())



    fun collectInit(
        testTransactions: List<TransactionCommercial>,
    ): MapsIDSDatesHistoriqueTransactions {
        // Initialize maps to collect data
        val weekMap = mutableMapOf<Long, MutableList<Long>>()
        val dayMap = mutableMapOf<Long, MutableList<Long>>()
        val clientTransMap = mutableMapOf<Long, MutableList<Long>>()
        val stateMap = mutableMapOf<Long, Type>()

        // Process each transaction
        testTransactions.forEach { transaction ->
            // Calculate day start timestamp (midnight of the day)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = transaction.timestamps
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val dayTimestamp = calendar.timeInMillis

            // Calculate week start timestamp (midnight of the first day of the week)
            val weekCalendar = Calendar.getInstance()
            weekCalendar.timeInMillis = dayTimestamp
            // Set to first day of week (Sunday in most locales)
            weekCalendar.set(Calendar.DAY_OF_WEEK, weekCalendar.firstDayOfWeek)
            val weekStart = weekCalendar.timeInMillis

            // Add day to the week map
            if (!weekMap.containsKey(weekStart)) {
                weekMap[weekStart] = mutableListOf()
            }
            if (!weekMap[weekStart]!!.contains(dayTimestamp)) {
                weekMap[weekStart]!!.add(dayTimestamp)
            }

            // Initialize day's transaction list
            if (!dayMap.containsKey(dayTimestamp)) {
                dayMap[dayTimestamp] = mutableListOf()
            }

            // Add transaction ID to day's list
            dayMap[dayTimestamp]!!.add(transaction.vid)

            // Group transactions by client
            val clientId = transaction.clientAcheteurID
            if (!clientTransMap.containsKey(clientId)) {
                clientTransMap[clientId] = mutableListOf()
            }
            clientTransMap[clientId]!!.add(transaction.vid)

            // Store transaction state
            stateMap[transaction.vid] = transaction.etateActuellementEst
        }

        // Update state with the collected data
        semaines = weekMap
        jours = dayMap
        clients = clientTransMap
        transactions = stateMap

        return this
    }
}

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

// Helper function to format timestamp to readable date
private fun formatTimestampToDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd")
    return formatter.format(date)
}
