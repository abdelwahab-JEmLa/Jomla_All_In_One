package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.Z.View.DetailBonVent.View

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object PeriodGenerator {

    fun generateCurrentPeriodId(): String {
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMM", Locale.ENGLISH)
        val year = calendar.get(Calendar.YEAR).toString().takeLast(2)
        val month = monthFormat.format(calendar.time)
        val hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))

        return "F1_${month}${year}_$hour"
    }

    fun getPeriodStartTimestamp(periodId: String? = null): Long {
        return if (periodId != null) {
            parsePeriodIdToTimestamp(periodId)
        } else {
            System.currentTimeMillis()
        }
    }

    private fun parsePeriodIdToTimestamp(periodId: String): Long {
        return try {
            val parts = periodId.split("_")
            if (parts.size >= 3) {
                val monthYear = parts[1]
                val hour = parts[2].toIntOrNull() ?: 0

                val monthStr = monthYear.take(3)
                val yearStr = "20" + monthYear.drop(3)

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, yearStr.toInt())
                calendar.set(Calendar.MONTH, getMonthFromString(monthStr))
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                calendar.timeInMillis
            } else {
                System.currentTimeMillis()
            }
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    private fun getMonthFromString(monthStr: String): Int {
        return when (monthStr.lowercase()) {
            "jan" -> Calendar.JANUARY
            "feb" -> Calendar.FEBRUARY
            "mar" -> Calendar.MARCH
            "apr" -> Calendar.APRIL
            "may" -> Calendar.MAY
            "jun" -> Calendar.JUNE
            "jul" -> Calendar.JULY
            "aug" -> Calendar.AUGUST
            "sep" -> Calendar.SEPTEMBER
            "oct" -> Calendar.OCTOBER
            "nov" -> Calendar.NOVEMBER
            "dec" -> Calendar.DECEMBER
            else -> Calendar.JANUARY
        }
    }

    fun generateBonVentId(clientName: String): String {
        val cleanClientName = clientName.take(10)
            .replace(Regex("[^a-zA-Z0-9]"), "")
            .ifEmpty { "client" }
        val timestamp = System.currentTimeMillis().toString().takeLast(6)

        return "F2_${cleanClientName}_$timestamp"
    }

    fun generateRandomBonVentId(): String {
        val randomId = (1000..9999).random()
        return "F2_bon_$randomId"
    }
}
