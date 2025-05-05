package com.example.clientjetpack.Functions

import java.util.Calendar

/**
 * Helper function to get transaction time (mocking based on transaction ID for this example)
 * In a real implementation, you would get this from the transaction data
 */
 fun getTransactionTime(transactionId: Long, transactionsInDay: List<Long>): Long {
    // This is just a placeholder. In a real implementation, you would get the real timestamp.
    // For demonstration purposes, we'll create a fake time based on transaction ID
    val baseHour = 8 // Start at 8 AM
    val index = transactionsInDay.indexOf(transactionId)

    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, baseHour + (index % 8)) // Spread transactions over 8 hours
    calendar.set(Calendar.MINUTE, ((transactionId * 7) % 60).toInt())    // Pseudo-random minutes

    return calendar.timeInMillis
}
