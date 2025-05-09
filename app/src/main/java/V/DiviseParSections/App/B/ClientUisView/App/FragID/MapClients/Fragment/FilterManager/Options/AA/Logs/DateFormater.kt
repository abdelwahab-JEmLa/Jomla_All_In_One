package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Enleve.B_Data_CreateTestTransactions
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun createTimestamp(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day, hour, minute, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

fun belongsToSameWeek(timestamp1: Long, timestamp2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)
}

// Helper function to format timestamp to readable date
fun formatTimestampToDate(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd")
    return formatter.format(date)
}

fun getTransactionTime(transactionId: Long, transactionsInDay: List<Long>): Long {
    val testTransactions = B_Data_CreateTestTransactions()
    val transaction = testTransactions.find { it.vid == transactionId }

    // If found, return its actual timestamp
    if (transaction != null) {
        return transaction.timestamps
    }

    // If not found (should not happen in our test scenario), use the fallback approach
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
    val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}

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
