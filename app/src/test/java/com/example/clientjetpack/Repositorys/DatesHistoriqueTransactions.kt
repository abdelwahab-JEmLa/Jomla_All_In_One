package com.example.clientjetpack.Repositorys

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class DatesHistoriqueTransactions {
    var semaines by mutableStateOf<Map<Long, MutableList<Long>>>(emptyMap())

    var jours by mutableStateOf<Map<Long, MutableList<Long>>>(emptyMap())

    var clientTransactions by mutableStateOf<Map<Long, MutableList<Long>>>(emptyMap())

    var etate by mutableStateOf<Map<Long, EtateActuellementEst>>(emptyMap())

    fun collectInit(
        testTransactions: List<TransactionCommercial>,
    ): DatesHistoriqueTransactions {
        // Initialize maps to collect data
        val weekMap = mutableMapOf<Long, MutableList<Long>>()
        val dayMap = mutableMapOf<Long, MutableList<Long>>()
        val clientTransMap = mutableMapOf<Long, MutableList<Long>>()
        val stateMap = mutableMapOf<Long, EtateActuellementEst>()

        // Group transactions by day first
        val transactionsByDay = testTransactions.groupBy { transaction ->
            val dayStart = transaction.timestamps - (transaction.timestamps % (24 * 60 * 60 * 1000))
            dayStart
        }

        // Process each day and its transactions
        transactionsByDay.forEach { (dayTimestamp, dayTransactions) ->
            val weekStart = dayTimestamp - (dayTimestamp % (7 * 24 * 60 * 60 * 1000))

            // Add day to the week map
            if (!weekMap.containsKey(weekStart)) {
                weekMap[weekStart] = mutableListOf()
            }
            weekMap[weekStart]?.add(dayTimestamp)

            // Initialize day's transaction list
            if (!dayMap.containsKey(dayTimestamp)) {
                dayMap[dayTimestamp] = mutableListOf()
            }

            // Process each transaction for this day
            dayTransactions.forEach { transaction ->
                // Add transaction ID to day's list
                dayMap[dayTimestamp]?.add(transaction.vid)

                // Group transactions by client
                val clientId = transaction.clientAcheteurID
                if (!clientTransMap.containsKey(clientId)) {
                    clientTransMap[clientId] = mutableListOf()
                }
                clientTransMap[clientId]?.add(transaction.vid)

                // Store transaction state
                stateMap[transaction.vid] = transaction.etateActuellementEst
            }
        }

        // Update state with the collected data
        semaines = weekMap
        jours = dayMap
        clientTransactions = clientTransMap
        etate = stateMap

        return this
    }
}
