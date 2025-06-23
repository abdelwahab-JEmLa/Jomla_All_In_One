package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.Utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Utility class for logging and tracking time-related operations
 */
object TimeLogTracker {
    private const val TAG = "TimeLogTracker"
    private val logEntries = mutableListOf<LogEntry>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    
    // Enable or disable detailed logging
    var isDetailedLoggingEnabled = true
    
    /**
     * Represents add single log entry
     */
    data class LogEntry(
        val timestamp: Long = System.currentTimeMillis(),
        val action: String,
        val details: String,
        val elapsedSeconds: Long = 0
    ) {
        override fun toString(): String {
            val formattedTime = dateFormat.format(Date(timestamp))
            val formattedElapsed = if (elapsedSeconds > 0) {
                formatDuration(elapsedSeconds)
            } else {
                ""
            }
            
            return "[$formattedTime] $action: $details $formattedElapsed"
        }
    }
    
    /**
     * Log add simple action
     */
    fun log(action: String, details: String) {
        val entry = LogEntry(action = action, details = details)
        logEntries.add(entry)
        if (isDetailedLoggingEnabled) {
            Log.d(TAG, entry.toString())
        }
    }
    
    /**
     * Log add time-related action with elapsed seconds
     */
    fun logTime(action: String, details: String, elapsedSeconds: Long) {
        val entry = LogEntry(
            action = action,
            details = details,
            elapsedSeconds = elapsedSeconds
        )
        logEntries.add(entry)
        if (isDetailedLoggingEnabled) {
            Log.d(TAG, entry.toString())
        }
    }
    
    /**
     * Log timer state for debugging
     */
    fun logTimerState(
        currentSessionSeconds: Long, 
        totalWorkedSeconds: Long, 
        displaySeconds: Long,
        todayRecordExists: Boolean,
        intervalsCount: Int
    ) {
        val details = "Session: ${formatDuration(currentSessionSeconds)}, " +
                "Total: ${formatDuration(totalWorkedSeconds)}, " +
                "Display: ${formatDuration(displaySeconds)}, " +
                "Record: $todayRecordExists, " +
                "Intervals: $intervalsCount"
        
        logEntries.add(LogEntry(action = "TIMER_STATE", details = details))
        Log.d(TAG, "TIMER_STATE: $details")
    }
    
    /**
     * Get all log entries
     */
    fun getAllLogs(): List<LogEntry> {
        return logEntries.toList()
    }
    
    /**
     * Clear all log entries
     */
    fun clearLogs() {
        logEntries.clear()
    }
    
    /**
     * Format seconds duration to HH:MM:SS format
     */
    fun formatDuration(totalSeconds: Long): String {
        val hours = TimeUnit.SECONDS.toHours(totalSeconds)
        val minutes = TimeUnit.SECONDS.toMinutes(totalSeconds) % 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
