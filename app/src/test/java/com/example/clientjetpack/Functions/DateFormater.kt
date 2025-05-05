package com.example.clientjetpack.Functions

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Helper function to format timestamp to time (HH:mm)
 */
 fun formatTime(timestamp: Long): String {
    if (timestamp <= 0) return "N/A"
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

/**
 * Helper function to check if two timestamps belong to the same week
 */
 fun belongsToSameWeek(timestamp1: Long, timestamp2: Long): Boolean {
    val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp2 }

    return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
            cal1.get(java.util.Calendar.WEEK_OF_YEAR) == cal2.get(java.util.Calendar.WEEK_OF_YEAR)
}

// Helper function to format timestamp to readable date
fun formatTimestampToDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd")
    return formatter.format(date)
}

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

/**
 * Helper function to format timestamp to time (HH:mm)
 */
 fun formatTimestampToTime(timestamp: Long): String {
    if (timestamp <= 0) return "N/A"
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

/**
 * Helper function to check if two timestamps belong to the same day
 */
 fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
    val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp2 }

    return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
            cal1.get(java.util.Calendar.MONTH) == cal2.get(java.util.Calendar.MONTH) &&
            cal1.get(java.util.Calendar.DAY_OF_MONTH) == cal2.get(java.util.Calendar.DAY_OF_MONTH)
}
