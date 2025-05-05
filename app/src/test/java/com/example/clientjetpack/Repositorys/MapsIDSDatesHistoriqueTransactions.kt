package com.example.clientjetpack.Repositorys

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Calendar

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
            // Calculate normalized timestamps for day and week
            val dayTimestamp = normalizeToDay(transaction.timestamps)
            val weekTimestamp = normalizeToWeekStart(dayTimestamp)

            // Add day to week map if it belongs to this week
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

        // Update state with collected data
        semaines = weekMap
        jours = dayMap
        clients = clientMap
        transactions = txStateMap

        return this
    }

    /**
     * Normalizes a timestamp to midnight of the day (00:00:00.000)
     */
    private fun normalizeToDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    /**
     * Normalizes a timestamp to the first day of the week containing the timestamp
     */
    private fun normalizeToWeekStart(dayTimestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dayTimestamp
            // Set to first day of week (usually Sunday or Monday depending on locale)
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }
        return calendar.timeInMillis
    }

    /**
     * Gets all transaction IDs for a specific week
     */
    fun getTransactionsForWeek(weekTimestamp: Long): List<Long> {
        val result = mutableListOf<Long>()

        // Get all days in this week
        semaines[weekTimestamp]?.forEach { dayTimestamp ->
            // Get all transactions for each day
            jours[dayTimestamp]?.let { result.addAll(it) }
        }

        return result
    }

    /**
     * Gets all transaction IDs for a specific client
     */
    fun getTransactionsForClient(clientId: Long): List<Long> {
        return clients[clientId] ?: emptyList()
    }

    /**
     * Gets all client IDs that have transactions on a specific day
     */
    fun getClientsForDay(dayTimestamp: Long): Set<Long> {
        val result = mutableSetOf<Long>()

        // Get all transactions for this day
        jours[dayTimestamp]?.forEach { transactionId ->
            // Find which client this transaction belongs to
            clients.forEach { (clientId, transactionIds) ->
                if (transactionIds.contains(transactionId)) {
                    result.add(clientId)
                }
            }
        }

        return result
    }

    /**
     * Checks if a transaction belongs to a specific day
     */
    fun isTransactionInDay(transactionId: Long, dayTimestamp: Long): Boolean {
        return jours[dayTimestamp]?.contains(transactionId) == true
    }

    /**
     * Checks if a transaction belongs to a specific client
     */
    fun isTransactionForClient(transactionId: Long, clientId: Long): Boolean {
        return clients[clientId]?.contains(transactionId) == true
    }
}
