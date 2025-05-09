package com.example.clientjetpack

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.Type
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Calendar

class D_MapsIDSDatesHistoriqueTransactionsRep_Repository {
    var semaines by mutableStateOf<Map<Long, MutableList<Long>>>(emptyMap())
    var jours by mutableStateOf<Map<Long, MutableList<Long>>>(emptyMap())
    var clients by mutableStateOf<Map<Long, MutableList<Long>>>(emptyMap())
    var transactions by mutableStateOf<Map<Long, Type>>(emptyMap())

    fun collectInit(
        testTransactions: List<D_TransactionCommercial_Repository>,
    ): D_MapsIDSDatesHistoriqueTransactionsRep_Repository {
        val weekMap = mutableMapOf<Long, MutableList<Long>>()
        val dayMap = mutableMapOf<Long, MutableList<Long>>()
        val clientMap = mutableMapOf<Long, MutableList<Long>>()
        val txStateMap = mutableMapOf<Long, Type>()

        testTransactions.forEach { transaction ->
            val dayTimestamp = normalizeToDay(transaction.timestamps)
            val weekTimestamp = normalizeToWeekStart(dayTimestamp)

            weekMap.getOrPut(weekTimestamp) { mutableListOf() }.apply {
                if (!contains(dayTimestamp)) add(dayTimestamp)
            }

            dayMap.getOrPut(dayTimestamp) { mutableListOf() }.add(transaction.vid)

            clientMap.getOrPut(transaction.clientAcheteurID) { mutableListOf() }
                .add(transaction.vid)

            txStateMap[transaction.vid] = transaction.etateActuellementEst
        }

        semaines = weekMap
        jours = dayMap
        clients = clientMap
        transactions = txStateMap

        return this
    }

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

    private fun normalizeToWeekStart(dayTimestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dayTimestamp
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }
        return calendar.timeInMillis
    }
}
