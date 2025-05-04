package com.example.clientjetpack.Tests.B.Data

import com.example.clientjetpack.Repositorys.DatesHistoriqueTransactions
import com.example.clientjetpack.Init.getEndOfDay
import com.example.clientjetpack.Init.getStartOfDay
import java.util.Calendar

/**
 * Tests if a specific client exists in today's transactions
 */
fun checkClientExistsInCurrentDay(
    datesHistorique: DatesHistoriqueTransactions,
    clientName: String
): Boolean {
    // Check if we have any weeks
    if (datesHistorique.cesSemains.isEmpty()) {
        return false
    }

    // Find the current day's transactions (today)
    val currentDay = datesHistorique.cesSemains.flatMap { semain ->
        semain.cesJours
    }.find { jour ->
        // Find the most recent day (should be today based on our test data)
        val calendar = Calendar.getInstance()
        val todayStartTime = getStartOfDay(calendar.timeInMillis)
        val todayEndTime = getEndOfDay(calendar.timeInMillis)

        jour.cesCommercialTransactions.any { transaction ->
            transaction.timestamps in todayStartTime..todayEndTime
        }
    }

    // Check if we found the current day
    if (currentDay == null) {
        return false
    }

    // Check if the specified client exists in today's transactions
    return currentDay.cesCommercialTransactions.any { transaction ->
        transaction.nomClientConcerned == clientName
    }
}
