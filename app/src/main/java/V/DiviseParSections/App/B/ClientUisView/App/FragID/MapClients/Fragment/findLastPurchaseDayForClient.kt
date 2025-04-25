package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.DataBase._01_VentsHistoriques.Models._01_PeriodVentHistorique
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.DataBase._01_VentsHistoriques.Models._14_TransactionStatue
import android.annotation.SuppressLint

data class LastPurchaseInfo(
    val dayName: String = "",
    val timeStr: String = ""
)

fun findLastPurchaseInfoForClient(
    historicalData: List<_01_PeriodVentHistorique>,
    clientId: Long
): LastPurchaseInfo {
    val sortedPeriods = historicalData.sortedByDescending { it.tempCreationStr }

    for (period in sortedPeriods) {
        for (vendeur in period.child_012_Compts_Vendeurs) {
            val clientPurchases = vendeur.child_013_Acheteurs.filter { it.idClient == clientId }

            if (clientPurchases.isNotEmpty()) {
                val dayName = getArabicDayNameFromDateString(period.tempCreationStr)
                val timeStr = extractTimeFromDateString(period.tempCreationStr)
                return LastPurchaseInfo(dayName, timeStr)
            }
        }
    }
    return LastPurchaseInfo()
}

private fun extractTimeFromDateString(dateTimeString: String): String {
    try {
        val timeMatch = Regex("\\((.*?)\\)").find(dateTimeString)
        val extractedTime = timeMatch?.groupValues?.getOrNull(1) ?: ""

        if (extractedTime.isEmpty()) {
            return ""
        }

        // Convert to Arabic time format with ص (AM) or م (PM)
        return formatTimeToArabic(extractedTime)
    } catch (e: Exception) {
        return ""
    }
}

@SuppressLint("DefaultLocale")
private fun formatTimeToArabic(timeString: String): String {
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

fun getClientStateInArabic(
    clientId: Long,
    historicalData: List<_01_PeriodVentHistorique>
): String {
    val sortedPeriods = historicalData.sortedByDescending { it.tempCreationStr }

    for (period in sortedPeriods) {
        for (vendeur in period.child_012_Compts_Vendeurs) {
            val clientEntry = vendeur.child_013_Acheteurs.find { it.idClient == clientId }

            if (clientEntry != null && clientEntry.child_14A_HistoriquesDeCetteJour.isNotEmpty()) {
                // Sort historical entries by date and time in descending order
                val sortedHistoricalEntries = clientEntry.child_14A_HistoriquesDeCetteJour.sortedWith(
                    compareByDescending<_14_TransactionStatue> { it.dateCreationStr }
                        .thenByDescending { it.tempCreationStr }
                )

                // Return the most recent state's Arabic name
                if (sortedHistoricalEntries.isNotEmpty()) {
                    return sortedHistoricalEntries.first().etateTransaction.nomArabe
                }
            }
        }
    }
    return _14_TransactionStatue.EtateTransaction.NON_DEFINI.nomArabe
}

fun findLastPurchaseDayForClient(
    historicalData: List<_01_PeriodVentHistorique>,
    clientId: Long
): String {
    return findLastPurchaseInfoForClient(historicalData, clientId).dayName
}

private fun getArabicDayNameFromDateString(dateTimeString: String): String {
    try {
        val datePart = dateTimeString.split("(").firstOrNull()?.trim()

        if (datePart.isNullOrEmpty()) {
            return ""
        }

        val dateParts = datePart.split("_")

        if (dateParts.size < 3) {
            return ""
        }

        val year = dateParts[0].toIntOrNull()
        val month = dateParts[1].toIntOrNull()?.minus(1)
        val day = dateParts[2].toIntOrNull()

        if (year == null || month == null || day == null) {
            return ""
        }

        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month, day)

        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)

        return when (dayOfWeek) {
            java.util.Calendar.SUNDAY -> "الأحد"
            java.util.Calendar.MONDAY -> "الإثنين"
            java.util.Calendar.TUESDAY -> "الثلاثاء"
            java.util.Calendar.WEDNESDAY -> "الأربعاء"
            java.util.Calendar.THURSDAY -> "الخميس"
            java.util.Calendar.FRIDAY -> "الجمعة"
            java.util.Calendar.SATURDAY -> "السبت"
            else -> ""
        }
    } catch (e: Exception) {
        return ""
    }
}
