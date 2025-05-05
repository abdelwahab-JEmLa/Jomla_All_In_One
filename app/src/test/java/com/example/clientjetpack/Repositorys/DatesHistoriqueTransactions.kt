package com.example.clientjetpack.Repositorys

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Class to manage historical transaction dates and their states
 */
class DatesHistoriqueTransactions {
    var semaines by mutableStateOf<Map<Long, List<Long>>>(emptyMap())

    var jours by mutableStateOf<Map<Long, List<Long>>>(emptyMap())

    var clientTransactions by mutableStateOf<Map<Long, List<Long>>>(emptyMap())

    var etate by mutableStateOf<Map<Long, EtateActuellementEst>>(emptyMap())

    fun collectInit(
        uniqueDaysForTesting: List<StrNomJourEtSonSemainToStartJourTimeTemp>,
        testTransactions: List<TransactionCommercial>,
    ): DatesHistoriqueTransactions {
        // Create mutable maps to populate
        val weekMap = mutableMapOf<Long, MutableList<Long>>()
        val dayMap = mutableMapOf<Long, MutableList<Long>>()
        val clientTransMap = mutableMapOf<Long, MutableList<Long>>()
        val stateMap = mutableMapOf<Long, EtateActuellementEst>()

        // Process each unique day
        uniqueDaysForTesting.forEach { dayInfo ->
            val startDayTimestamp = dayInfo.jourEstEntreTimeTemp.first

            // Group days by week
            // Use start of the week as the key for weeks
            val weekStart = startDayTimestamp - (startDayTimestamp % (7 * 24 * 60 * 60 * 1000))

            // Add day to the week
            if (!weekMap.containsKey(weekStart)) {
                weekMap[weekStart] = mutableListOf()
            }
            weekMap[weekStart]?.add(startDayTimestamp)

            // Collect transactions for this day
            val dayTransactions = testTransactions.filter {
                it.timestamps >= dayInfo.jourEstEntreTimeTemp.first &&
                        it.timestamps <= dayInfo.jourEstEntreTimeTemp.second
            }

            // Add transactions to the day
            if (!dayMap.containsKey(startDayTimestamp)) {
                dayMap[startDayTimestamp] = mutableListOf()
            }

            dayTransactions.forEach { transaction ->
                dayMap[startDayTimestamp]?.add(transaction.vid)

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
