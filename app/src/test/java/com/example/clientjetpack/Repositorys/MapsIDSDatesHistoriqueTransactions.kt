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

        // Log the state after initialization
        println("Collected data:")
        println("- Weeks: ${weekMap.size}")
        println("- Days: ${dayMap.size}")
        println("- Clients: ${clientTransMap.size}")
        println("- Transactions: ${stateMap.size}")

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
    semaines.forEach { (weekTimestamp, days) ->
        val weekDate = formatTimestampToDate(weekTimestamp)
        println("Semaine ($weekDate): ${days.size} jour(s)")

        // Log days for each week
        days.forEachIndexed { index, dayTimestamp ->
            val dayDate = formatTimestampToDate(dayTimestamp)
            val transactionsInDay = jours[dayTimestamp]?.size ?: 0
            println("  ├─ Jour $index ($dayDate): $transactionsInDay transaction(s)")

            // Log transactions for each day (limited to first 5)
            jours[dayTimestamp]?.take(5)?.forEachIndexed { tIndex, transactionId ->
                val state = transactions[transactionId]
                val isLastTransaction = tIndex == (jours[dayTimestamp]?.take(5)?.size ?: 1) - 1 &&
                        ((jours[dayTimestamp]?.size ?: 0) <= 5)
                val prefix = if (isLastTransaction) "  │  └─" else "  │  ├─"
                println("$prefix Transaction #$tIndex (ID: $transactionId, État: $state)")
            }

            // Show ellipsis if there are more transactions
            if ((jours[dayTimestamp]?.size ?: 0) > 5) {
                println("  │  └─ ... ${(jours[dayTimestamp]?.size ?: 0) - 5} more transaction(s)")
            }
        }
    }

    // Log clients and their transactions
    println("\n-- Clients --")
    clients.forEach { (clientId, clientTransactions) ->
        println("Client (ID: $clientId): ${clientTransactions.size} transaction(s)")

        // Log transactions for each client (limited to first 3)
        clientTransactions.take(3).forEachIndexed { index, transactionId ->
            val state = transactions[transactionId]
            val isLastTransaction = index == clientTransactions.take(3).size - 1 &&
                    clientTransactions.size <= 3
            val prefix = if (isLastTransaction) "  └─" else "  ├─"
            println("$prefix Transaction #$index (ID: $transactionId, État: $state)")
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
