package Z_CodePartageEntreApps.Modules

import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import android.annotation.SuppressLint
import android.util.Log
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

    fun getDateAndTimString(timestamp: Long = getCurrentTimestamps()): DateAndTimString {
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

    fun getDateAndTimStringAvecSeconds(timestamp: Long = getCurrentTimestamps()): DateAndTimString {
        try {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault()) // Added seconds format

            val date = dateFormat.format(calendar.time)
            val timeString = timeFormat.format(calendar.time)
            val time = timeString.formatTimeToArabicWithSeconds() // Use new extension function with seconds

            return DateAndTimString(date, time)
        } catch (e: Exception) {
            return DateAndTimString()
        }
    }

    fun debugTimestamps(transactions: List<M8BonVent>, tag: String) {
        Log.d(tag, "=== DEBUG TIMESTAMPS ===")
        transactions.forEachIndexed { index, transaction ->
            val date = java.util.Date(transaction.creationTimestamps)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            Log.d(tag, "Transaction $index: VID=${transaction.vid}, Timestamp=${transaction.creationTimestamps}, Date=${dateFormat.format(date)}")
        }
    }

    fun getNomJourArabParDateStr(dataStr: String): String {
        try {
            // Parse the input date string (expected format: "yyyy-MM-dd")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(dataStr) ?: return "غير معروف"

            // RepositorysMainGetter the day of week
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
            // RepositorysMainGetter current date without time
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

            // RepositorysMainSetter given date calendar
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
    fun getDistanceSemainParDateStr(dateString: String): String {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(dateString)

            if (date != null) {
                val calendar = Calendar.getInstance()
                val today = calendar.time
                val diffInMillies = today.time - date.time
                val diffInDays = diffInMillies / (24 * 60 * 60 * 1000)

                when {
                    diffInDays <= 7 -> "هذا الأسبوع"
                    diffInDays <= 14 -> "الأسبوع الماضي"
                    diffInDays <= 21 -> "قبل أسبوعين"
                    diffInDays <= 28 -> "قبل 3 أسابيع"
                    diffInDays <= 35 -> "قبل 4 أسابيع"
                    else -> "قبل أكثر من شهر"
                }
            } else {
                "تاريخ غير صحيح"
            }
        } catch (e: Exception) {
            "خطأ في التاريخ"
        }
    }

    fun getDistanceSemainParDateStrs(dataStr: String): String {
        try {
            // Parse the input date string (expected format: "yyyy-MM-dd")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val givenDate = dateFormat.parse(dataStr) ?: return "هذا الأسبوع"

            // RepositorysMainGetter current date without time
            val currentCalendar = Calendar.getInstance()
            currentCalendar.set(Calendar.HOUR_OF_DAY, 0)
            currentCalendar.set(Calendar.MINUTE, 0)
            currentCalendar.set(Calendar.SECOND, 0)
            currentCalendar.set(Calendar.MILLISECOND, 0)

            // RepositorysMainSetter given date calendar
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
    companion object{
        fun creeStrDate_Et_Time_Depuit_CreationTT(creationTimestamps: Long): String {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = creationTimestamps

            // French day names
            val dayNames = arrayOf(
                "dimanche", "lundi", "mardi", "mercredi",
                "jeudi", "vendredi", "samedi"
            )

            // Get day of week (Calendar.DAY_OF_WEEK returns 1-7, where 1 = Sunday)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val dayName = dayNames[dayOfWeek - 1]

            // Get hour, minute, and second
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val second = calendar.get(Calendar.SECOND)

            // Convert to 12-hour format and determine AM/PM
            val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
            val amPm = if (hour < 12) "am" else "pm"

            // Format minute and second with leading zero if needed
            val minuteStr = if (minute < 10) "0$minute" else "$minute"
            val secondStr = if (second < 10) "0$second" else "$second"

            return "$dayName $hour12:$minuteStr:$secondStr $amPm"
        }
    }
}

// Extension function for String to format time to Arabic (without seconds)
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

// New extension function for String to format time to Arabic WITH seconds
@SuppressLint("DefaultLocale")
fun String.formatTimeToArabicWithSeconds(): String {
    try {
        val timeParts = this.split(":")
        if (timeParts.size < 3) {
            return this.formatTimeToArabic() // Fallback to without seconds if format is wrong
        }

        val hour = timeParts[0].toIntOrNull() ?: return this
        val minute = timeParts[1].toIntOrNull() ?: return this
        val second = timeParts[2].toIntOrNull() ?: return this

        // Check if it's AM or PM
        val isPM = hour >= 12
        val amPmIndicator = if (isPM) "م" else "ص"

        // Convert to 12-hour format
        val hour12 = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }

        // Format to hour:minute:second AM/PM in Arabic
        return String.format("%d:%02d:%02d %s", hour12, minute, second, amPmIndicator)
    } catch (e: Exception) {
        return this
    }
}
