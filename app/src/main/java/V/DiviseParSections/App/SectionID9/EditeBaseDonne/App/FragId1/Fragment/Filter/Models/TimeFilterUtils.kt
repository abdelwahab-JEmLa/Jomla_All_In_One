package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.Models

import java.util.Calendar

/**
 * Utility functions for time-based filtering
 */
object TimeFilterUtils {

    /**
     * Checks if a timestamp is older than the specified number of days
     * Uses calendar-based comparison to match getTimeDifferenceInArabic behavior
     * @param timestamp The timestamp to check (in milliseconds)
     * @param days Number of days to compare against
     * @return true if the timestamp is older than the specified days, false otherwise
     */
    fun isOlderThanDays(timestamp: Long, days: Int): Boolean {
        if (timestamp <= 0 || days < 0) return false

        // Get calendar dates to compare actual days, not just 24-hour periods
        val currentCalendar = Calendar.getInstance()
        val timestampCalendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        val currentYear = currentCalendar.get(Calendar.YEAR)
        val currentDayOfYear = currentCalendar.get(Calendar.DAY_OF_YEAR)
        val timestampYear = timestampCalendar.get(Calendar.YEAR)
        val timestampDayOfYear = timestampCalendar.get(Calendar.DAY_OF_YEAR)

        val dayDifference = if (currentYear == timestampYear) {
            currentDayOfYear - timestampDayOfYear
        } else {
            // For different years, calculate the actual day difference
            val diffInMillis = System.currentTimeMillis() - timestamp
            val diffInDays = (diffInMillis / (24 * 60 * 60 * 1000)).toInt()
            diffInDays
        }

        return dayDifference >= days
    }


    /**
     * Safely parses a string to integer for days filter
     * @param daysString The string to parse
     * @return The parsed integer or 0 if parsing fails
     */
    fun parseDaysString(daysString: String): Int {
        return try {
            if (daysString.isBlank()) 0 else daysString.toInt().coerceAtLeast(0)
        } catch (e: NumberFormatException) {
            0
        }
    }
}
