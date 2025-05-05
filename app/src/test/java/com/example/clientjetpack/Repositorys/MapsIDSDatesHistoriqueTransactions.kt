package com.example.clientjetpack.Repositorys

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.clientjetpack.Logs.Functions.normalizeToDay
import com.example.clientjetpack.Logs.Functions.normalizeToWeekStart

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




}
