package Z_CodePartageEntreApps.Modules

import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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

    // Distance relative en arabe, du type "قبل 5 أيام" (il y a 5 jours) ou
    // "قبل أسبوع ويومين" (il y a 1 semaine et 2 jours). Contrairement à
    // getAbrgDistanceSemain (qui donne le jour de la semaine, ex: "الخميس ق.2")
    // ou à getTimeDifferenceInArabicWithMintes (mois/années arrondis), cette
    // fonction combine semaines + jours restants pour une durée précise et
    // lisible, utilisée par exemple pour afficher depuis quand un crédit est
    // ouvert (voir creditLabel dans ClientRow / But1_Floating_ClientsListDialog).
    fun getRelativeTimeArabic(timestamp: Long): String {
        try {
            // Comparaison sur des journées entières (minuit à minuit), pas sur
            // des millisecondes brutes, pour que "hier soir tard" et "hier
            // matin" comptent tous les deux comme 1 jour d'écart.
            val startOfDay: (Long) -> Calendar = { ts ->
                Calendar.getInstance().apply {
                    timeInMillis = ts
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
            }

            val today = startOfDay(System.currentTimeMillis())
            val given = startOfDay(timestamp)

            val diffMillis = today.timeInMillis - given.timeInMillis
            val totalDays = TimeUnit.MILLISECONDS.toDays(diffMillis)

            if (totalDays < 0L) return "" // date future : cas non géré ici
            if (totalDays == 0L) return isolateRtl("اليوم")
            if (totalDays == 1L) return isolateRtl("أمس")

            val weeks = totalDays / 7
            val days = totalDays % 7

            val daysPart = when (days) {
                0L -> ""
                1L -> "يوم"
                2L -> "يومين"
                in 3..10 -> "$days أيام"
                else -> "$days يوم"
            }

            val weeksPart = when (weeks) {
                0L -> ""
                1L -> "أسبوع"
                2L -> "أسبوعين"
                in 3..10 -> "$weeks أسابيع"
                else -> "$weeks أسبوع"
            }

            val body = when {
                weeks == 0L -> daysPart
                days == 0L -> weeksPart
                else -> "$weeksPart و $daysPart"
            }

            return isolateRtl("قبل $body")
        } catch (e: Exception) {
            return ""
        }
    }

    // Isole le texte arabe entre marqueurs bidi (RLI ... PDI, U+2067/U+2069)
    // avant de l'insérer dans un libellé à majorité latine comme
    // "Crédit : +4500,00 DA · قبل أسبوع و يومين". Sans ça, quand ce genre de
    // texte mixte latin+arabe passe à la ligne, l'algorithme bidi peut
    // mélanger l'ordre des mots/chiffres arabes sur la 2e ligne (bug constaté
    // à l'écran : "أيام3أسبوعين و" au lieu de "أسبوعين و 3 أيام"). L'isolate
    // dit au moteur de texte "ce bloc est un îlot RTL autonome, ne le
    // réordonne pas en fonction du contexte latin autour" — recommandation
    // Unicode standard pour du texte bidirectionnel dynamique/concaténé.
    private fun isolateRtl(text: String): String = "\u2067$text\u2069"

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

            if (weeksDiff < 0L) return "" // future dates: return empty string

            // Arabic name of the day the given date falls on (e.g. الخميس, الاحد)
            val dayName = getArabicDayNameFromTimestamp(timestamp)
            if (dayName.isEmpty()) return ""


            return when {
                weeksDiff == 0L -> "هذا.$dayName"                 // ex: هذا.الاحد
                weeksDiff == 1L -> "$dayName.الفائت"               // ex: الخميس.الفائت
                weeksDiff in 2..5 -> "$dayName ق.${weeksDiff - 1}" // ex: الخميس ق.1, ق.2, ق.3, ق.4
                else -> "$dayName ق.+"
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

    companion object {
        fun getTimeDifferenceInArabicWithMintes(timestamp: Long): String {
            val currentTime = System.currentTimeMillis()
            val difference = currentTime - timestamp

            val seconds = difference / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val months = days / 30
            val years = days / 365

            return when {
                years > 0 -> "${years} سنة"
                months > 0 -> "${months} شهر"
                days > 0 -> "${days} يوم"
                hours > 0 -> "${hours} ساعة"
                minutes > 0 -> {
                    val remainingSeconds = seconds % 60
                    if (remainingSeconds > 0) {
                        "$minutes دقيقة و $remainingSeconds ثانية"
                    } else {
                        "${minutes} دقيقة"
                    }
                }
                seconds > 0 -> "${seconds} ثانية"
                else -> "الآن"
            }
        }

        fun get_PersonaleDateFormatArab(timestamp: Long?): String {
            if (timestamp == null) return ""

            try {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = timestamp

                // Get Arabic day name
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val arabicDayName = when (dayOfWeek) {
                    Calendar.SUNDAY -> "الأحد"
                    Calendar.MONDAY -> "الإثنين"
                    Calendar.TUESDAY -> "الثلاثاء"
                    Calendar.WEDNESDAY -> "الأربعاء"
                    Calendar.THURSDAY -> "الخميس"
                    Calendar.FRIDAY -> "الجمعة"
                    Calendar.SATURDAY -> "السبت"
                    else -> ""
                }

                // Get day of month
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

                // Get Arabic month name
                val month = calendar.get(Calendar.MONTH)
                val arabicMonthName = when (month) {
                    Calendar.JANUARY -> "جانفي"
                    Calendar.FEBRUARY -> "فيفري"
                    Calendar.MARCH -> "مارس"
                    Calendar.APRIL -> "أفريل"
                    Calendar.MAY -> "ماي"
                    Calendar.JUNE -> "جوان"
                    Calendar.JULY -> "جويلية"
                    Calendar.AUGUST -> "أوت"
                    Calendar.SEPTEMBER -> "سبتمبر"
                    Calendar.OCTOBER -> "أكتوبر"
                    Calendar.NOVEMBER -> "نوفمبر"
                    Calendar.DECEMBER -> "ديسمبر"
                    else -> ""
                }

                // Get month number for display in parentheses
                val monthNumber = month + 1

                // Get week distance using existing function
                val datesHandler = DatesHandler()
                val weekDistance = datesHandler.getAbrgDistanceSemain(timestamp)

                // Format the final string: "السبت 18 أوت(8) قبل أسبوع"
                return buildString {
                    append(arabicDayName)
                    append(" ")
                    append(dayOfMonth)
                    append(" ")
                    append(arabicMonthName)
                    append("(")
                    append(monthNumber)
                    append(")")
                    if (weekDistance.isNotEmpty()) {
                        append(" ")
                        append(weekDistance)
                    }
                }
            } catch (e: Exception) {
                return ""
            }
        }

        fun creeStrDate_Et_Time_Depuit_CreationTT(creationTimestamps: Long): Pair<String, String> {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = creationTimestamps

            val dayNames = arrayOf(
                "dimanche", "lundi", "mardi", "mercredi",
                "jeudi", "vendredi", "samedi"
            )

            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val dayName = dayNames[dayOfWeek - 1]

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val second = calendar.get(Calendar.SECOND)

            val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
            val amPm = if (hour < 12) "am" else "pm"

            val minuteStr = if (minute < 10) "0$minute" else "$minute"
            val secondStr = if (second < 10) "0$second" else "$second"

            return Pair(dayName,"$hour12:$minuteStr:$secondStr $amPm")
        }
        fun formatDateWithAmPm(date: Date): String {
            val calendar = Calendar.getInstance().apply { time = date }
            val frenchDays = arrayOf("Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam")
            val frenchMonths = arrayOf("Jan", "Fév", "Mar", "Avr", "Mai", "Jun", "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc")
            val dayOfWeek = frenchDays[calendar.get(Calendar.DAY_OF_WEEK) - 1]
            val month = frenchMonths[calendar.get(Calendar.MONTH)]
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val year = calendar.get(Calendar.YEAR)
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            return "$dayOfWeek $dayOfMonth/$month/$year ${String.format("%02d:%02d", hour, minute)}"
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
