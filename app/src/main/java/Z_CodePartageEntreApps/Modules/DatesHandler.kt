package Z_CodePartageEntreApps.Modules

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class DatesHandler {
    fun getCurrentTimestamps(): Long {
        return System.currentTimeMillis()
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
    fun formatTimeToArabic(timeString: String): String {  // Changed from private to public
        try {
            val timeParts = timeString.split(":")
            if (timeParts.size < 2) {
                return timeString
            }

            val hour = timeParts[0].toIntOrNull() ?: return timeString
            val minute = timeParts[1].toIntOrNull() ?: return timeString

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
            return timeString
        }
    }

}
