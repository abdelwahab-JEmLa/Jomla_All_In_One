package Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.Extension

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility class for date and time formatting and calculations
 */
object TimeFormatUtils {
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    // RepositorysMainGetter current date in the format "yyyy/MM/dd"
    fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }

    // RepositorysMainGetter current time in the format "HH:mm"
    fun getCurrentTime(): String {
        return timeFormat.format(Date())
    }
    // Format seconds to HH:MM:SS
    fun formatSecondsToTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

}
