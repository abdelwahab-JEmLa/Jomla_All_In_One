package Z_CodePartageEntreApps.Modules

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

data class DateAndTimString(
    val date: String = "yyyy-mm-dd",
    val time: String = "HH:mm"
)

class DatesHandler {

    fun getCurrentTimestamps(): Long {
        return System.currentTimeMillis()
    }

    fun getArabicDayNameFromTimestamp(timestamp: Long): String {
        try {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp

            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            return when (dayOfWeek) {
                Calendar.SUNDAY -> "الأحد"
                Calendar.MONDAY -> "الإثنين"
                Calendar.TUESDAY -> "الثلاثاء"
                Calendar.WEDNESDAY -> "الأربعاء"
                Calendar.THURSDAY -> "الخميس"
                Calendar.FRIDAY -> "الجمعة"
                Calendar.SATURDAY -> "السبت"
                else -> ""
            }
        } catch (e: Exception) {
            return ""
        }
    }

    fun getDateAndTimString(timestamp: Long?): DateAndTimString {
        if (timestamp == null) return DateAndTimString()

        try {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val date = dateFormat.format(calendar.time)
            val timeString = timeFormat.format(calendar.time)
            val time = timeString.formatTimeToArabic()

            return DateAndTimString(date, time)
        } catch (e: Exception) {
            return DateAndTimString()
        }
    }

    fun getNomJourArabParDateStr(dataStr: String): String {
        try {
            // Parse the input date string (expected format: "yyyy-MM-dd")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(dataStr) ?: return "غير معروف"

            // Get the day of week
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            // Map day of week to Arabic name
            return when (dayOfWeek) {
                Calendar.SUNDAY -> "الأحد"
                Calendar.MONDAY -> "الإثنين"
                Calendar.TUESDAY -> "الثلاثاء"
                Calendar.WEDNESDAY -> "الأربعاء"
                Calendar.THURSDAY -> "الخميس"
                Calendar.FRIDAY -> "الجمعة"
                Calendar.SATURDAY -> "السبت"
                else -> "غير معروف"
            }
        } catch (e: Exception) {
            // Return unknown if parsing fails
            return "غير معروف"
        }
    }
    fun getAbrgDistanceSemain(timestamp: Long?): String {
        if (timestamp == null) return ""

        try {
            // Get current date without time
            val currentCalendar = Calendar.getInstance()
            currentCalendar.set(Calendar.HOUR_OF_DAY, 0)
            currentCalendar.set(Calendar.MINUTE, 0)
            currentCalendar.set(Calendar.SECOND, 0)
            currentCalendar.set(Calendar.MILLISECOND, 0)

            // Calculate the start of the current week (Monday)
            val daysToSubtract = when (val dayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> 6
                Calendar.MONDAY -> 0
                else -> dayOfWeek - Calendar.MONDAY
            }
            currentCalendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract)

            // Set given date calendar
            val givenCalendar = Calendar.getInstance()
            givenCalendar.timeInMillis = timestamp
            givenCalendar.set(Calendar.HOUR_OF_DAY, 0)
            givenCalendar.set(Calendar.MINUTE, 0)
            givenCalendar.set(Calendar.SECOND, 0)
            givenCalendar.set(Calendar.MILLISECOND, 0)

            // Calculate difference in days from the start of the current week
            val millsDiff = currentCalendar.timeInMillis - givenCalendar.timeInMillis
            val daysDiff = TimeUnit.MILLISECONDS.toDays(millsDiff)

            // Calculate week difference
            val weeksDiff = daysDiff / 7
            val avant = "الفائت"

            return when {
                weeksDiff == 0L -> "هذا"
                weeksDiff == 1L -> avant
                weeksDiff == 2L -> "ق.$avant"
                weeksDiff == 3L -> "ق.3"
                weeksDiff == 4L -> "ق.4"
                weeksDiff > 4L -> "ق.+"
                else -> "" // For current week or future dates, return empty string
            }
        } catch (e: Exception) {
            return ""
        }
    }

    fun getDistanceSemainParDateStr(dataStr: String): String {
        try {
            // Parse the input date string (expected format: "yyyy-MM-dd")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val givenDate = dateFormat.parse(dataStr) ?: return "هذا الأسبوع"

            // Get current date without time
            val currentCalendar = Calendar.getInstance()
            currentCalendar.set(Calendar.HOUR_OF_DAY, 0)
            currentCalendar.set(Calendar.MINUTE, 0)
            currentCalendar.set(Calendar.SECOND, 0)
            currentCalendar.set(Calendar.MILLISECOND, 0)

            // Set given date calendar
            val givenCalendar = Calendar.getInstance()
            givenCalendar.time = givenDate
            givenCalendar.set(Calendar.HOUR_OF_DAY, 0)
            givenCalendar.set(Calendar.MINUTE, 0)
            givenCalendar.set(Calendar.SECOND, 0)
            givenCalendar.set(Calendar.MILLISECOND, 0)

            // Calculate difference in days
            val millsDiff = currentCalendar.timeInMillis - givenCalendar.timeInMillis
            val daysDiff = TimeUnit.MILLISECONDS.toDays(millsDiff)

            // Calculate week difference
            val weeksDiff = daysDiff / 7

            return when {
                weeksDiff == 0L -> "هذا الأسبوع"
                weeksDiff == 1L -> "الأسبوع الماضي"
                weeksDiff == 2L -> "قبل أسبوعين"
                weeksDiff in 3..10 -> "قبل $weeksDiff أسابيع"
                else -> {
                    val monthsDiff = daysDiff / 30
                    when {
                        monthsDiff == 1L -> "قبل شهر"
                        monthsDiff == 2L -> "قبل شهرين"
                        monthsDiff in 3..10 -> "قبل $monthsDiff أشهر"
                        else -> "قبل أكثر من سنة"
                    }
                }
            }
        } catch (e: Exception) {
            // Return current week if parsing fails
            return "هذا الأسبوع"
        }
    }

    @SuppressLint("DefaultLocale")
    fun formatTimeToArabic(timestamp: Long): String {
        try {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            // Check if it's AM or PM
            val isPM = hour >= 12
            val amPmIndicator = if (isPM) "م" else "ص"

            // Convert to 12-hour format
            val hour12 = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }

            // Format to hour:minute AM/PM in Arabic
            return String.format("%d:%02d %s", hour12, minute, amPmIndicator)
        } catch (e: Exception) {
            return ""
        }
    }
}

// Extension function for String to format time to Arabic
@SuppressLint("DefaultLocale")
fun String.formatTimeToArabic(): String {
    try {
        val timeParts = this.split(":")
        if (timeParts.size < 2) {
            return this
        }

        val hour = timeParts[0].toIntOrNull() ?: return this
        val minute = timeParts[1].toIntOrNull() ?: return this

        // Check if it's AM or PM
        val isPM = hour >= 12
        val amPmIndicator = if (isPM) "م" else "ص"

        // Convert to 12-hour format
        val hour12 = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }

        // Format to hour:minute AM/PM in Arabic
        return String.format("%d:%02d %s", hour12, minute, amPmIndicator)
    } catch (e: Exception) {
        return this
    }
}
