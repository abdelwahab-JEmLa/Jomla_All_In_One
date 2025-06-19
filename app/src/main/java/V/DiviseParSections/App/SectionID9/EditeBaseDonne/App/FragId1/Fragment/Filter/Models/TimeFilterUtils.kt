package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.Models

import java.util.concurrent.TimeUnit

/**
 * Utility functions for time-based filtering
 */
object TimeFilterUtils {

    /**
     * Checks if a timestamp is older than the specified number of days
     * @param timestamp The timestamp to check (in milliseconds)
     * @param days Number of days to compare against
     * @return true if the timestamp is older than the specified days, false otherwise
     */
    fun isOlderThanDays(timestamp: Long, days: Int): Boolean {
        if (timestamp <= 0 || days < 0) return false

        val currentTime = System.currentTimeMillis()
        val diffInMillis = currentTime - timestamp
        val daysDifference = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        return daysDifference >= days
    }

    /**
     * Calculates the number of days between a timestamp and now
     * @param timestamp The timestamp to calculate from (in milliseconds)
     * @return The number of days difference (can be negative if timestamp is in future)
     */
    fun getDaysDifference(timestamp: Long): Long {
        if (timestamp <= 0) return Long.MAX_VALUE // Consider invalid timestamps as very old

        val currentTime = System.currentTimeMillis()
        val diffInMillis = currentTime - timestamp
        return TimeUnit.MILLISECONDS.toDays(diffInMillis)
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
