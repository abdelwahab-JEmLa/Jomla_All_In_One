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

