package com.example.clientjetpack.Functions

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


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

/**
 * Converts a date/time string in format "dd/MM h.mmA" or "dd/MM h.mma"
 * (like "05/05 8.30PM" or "05/05 8.30pm") into a timestamp
 */
fun getFromeDayeStringTime(dateTimeStr: String): Long {
    try {
        // Handle variations in input format (PM/pm, with or without spaces)
        val cleanInput = dateTimeStr.trim().replace("\\s+".toRegex(), " ")
            .uppercase() // Standardize AM/PM to uppercase

        // Parse different potential formats
        val dateTimeParts = cleanInput.split(" ")
        if (dateTimeParts.size != 2) {
            throw ParseException("Invalid date/time format: $dateTimeStr", 0)
        }

        val datePart = dateTimeParts[0] // e.g., "05/05"
        var timePart = dateTimeParts[1] // e.g., "8.30PM"

        // Add current year to the date part since it's not specified in the input
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val fullDateStr = "$datePart/$currentYear"

        // Handle time format with period separator (8.30PM)
        timePart = timePart.replace(".", ":")

        // Extract AM/PM indicator
        val hasAmPm = timePart.endsWith("AM") || timePart.endsWith("PM")
        if (!hasAmPm) {
            throw ParseException("Time must include AM or PM: $timePart", 0)
        }

        // Create a complete date-time string
        val fullDateTime = "$fullDateStr $timePart"

        // Parse the date-time string
        val formatter = SimpleDateFormat("dd/MM/yyyy h:mma", Locale.US)
        val date = formatter.parse(fullDateTime) ?: throw ParseException(
            "Failed to parse: $fullDateTime",
            0
        )

        return date.time
    } catch (e: Exception) {
        // In case of any parsing errors, log and return current time as fallback
        println("Error parsing date/time string '$dateTimeStr': ${e.message}")
        return System.currentTimeMillis()
    }
}

fun formatTime(timestamp: Long): String {
    if (timestamp <= 0) return "N/A"
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
