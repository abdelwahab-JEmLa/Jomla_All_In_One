package com.example.clientjetpack.Repositorys

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Calendar

/**
 * Simplified storage for transaction relationships between weeks, days, clients, and transactions.
 * Reduced complexity and code size by using more efficient data structures and concise functions.
 */
class MapsIDSDatesHistoriqueTransactions {
    // Maps to store hierarchical relationship data
    var semaines by mutableStateOf<Map<Long, MutableList<Long>>>(emptyMap())
    var jours by mutableStateOf<Map<Long, MutableList<Long>>>(emptyMap())
    var clients by mutableStateOf<Map<Long, MutableList<Long>>>(emptyMap())
    var transactions by mutableStateOf<Map<Long, Type>>(emptyMap())

    /**
     * Initializes data collections from a list of transactions.
     * Uses more concise code with Kotlin collection operations.
     */
    fun collectInit(
        testTransactions: List<TransactionCommercial>,
    ): MapsIDSDatesHistoriqueTransactions {
        // Temporary mutable collections for building the data structure
        val weekMap = mutableMapOf<Long, MutableList<Long>>()
        val dayMap = mutableMapOf<Long, MutableList<Long>>()
        val clientMap = mutableMapOf<Long, MutableList<Long>>()
        val txStateMap = mutableMapOf<Long, Type>()

        // Process each transaction once
        testTransactions.forEach { transaction ->
            // Get timestamps for day and week
            val (dayTimestamp, weekTimestamp) = getDayAndWeekTimestamps(transaction.timestamps)

            // Add day to week map
            weekMap.getOrPut(weekTimestamp) { mutableListOf() }.apply {
                if (!contains(dayTimestamp)) add(dayTimestamp)
            }

            // Add transaction to day map
            dayMap.getOrPut(dayTimestamp) { mutableListOf() }.add(transaction.vid)

            // Add transaction to client map
            clientMap.getOrPut(transaction.clientAcheteurID) { mutableListOf() }.add(transaction.vid)

            // Store transaction state
            txStateMap[transaction.vid] = transaction.etateActuellementEst
        }

        // Update state with collected data - ensure we maintain mutable type compatibility
        semaines = weekMap
        jours = dayMap
        clients = clientMap
        transactions = txStateMap

        return this
    }

    /**
     * Helper function to calculate day and week timestamps from a transaction timestamp.
     * Returns a Pair of (dayTimestamp, weekTimestamp).
     */
    private fun getDayAndWeekTimestamps(timestamp: Long): Pair<Long, Long> {
        // Calculate day start timestamp (midnight of the day)
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dayTimestamp = calendar.timeInMillis

        // Calculate week start timestamp (midnight of the first day of the week)
        val weekCalendar = Calendar.getInstance().apply {
            timeInMillis = dayTimestamp
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }
        val weekTimestamp = weekCalendar.timeInMillis

        return Pair(dayTimestamp, weekTimestamp)
    }
}
