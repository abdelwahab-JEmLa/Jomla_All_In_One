package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase
// Enhanced version of findLastPurchaseDayForClient with better logging
fun findLastPurchaseDayForClient(
    historicalData: List<_01_VentsHistoriquesDataBase>,
    clientId: Long
): String {
    android.util.Log.d("DayFilter", "Finding last purchase day for client $clientId")
    android.util.Log.d("DayFilter", "Available historical data: ${historicalData.size} periods")

    // Sort periods by creation date (newest first)
    val sortedPeriods = historicalData.sortedByDescending { it.tempCreationStr }
    android.util.Log.d("DayFilter", "Sorted periods: ${sortedPeriods.size}")

    // Find the most recent purchase for this client
    for (period in sortedPeriods) {
        android.util.Log.d("DayFilter", "Checking period: ${period.tempCreationStr}")

        for (vendeur in period.child_012_Compts_Vendeurs) {
            // Check if this client has any purchases in this period
            val clientPurchases = vendeur.child_013_Acheteurs.filter { it.idClient == clientId }
            val hasClientPurchase = clientPurchases.isNotEmpty()

            android.util.Log.d("DayFilter", "Vendor check: Client $clientId has purchases: $hasClientPurchase (found ${clientPurchases.size} purchases)")

            if (hasClientPurchase) {
                // We found a purchase by this client, return the day name
                val dayName = getArabicDayNameFromDateString(period.tempCreationStr)
                android.util.Log.d("DayFilter", "Found purchase! Date: ${period.tempCreationStr}, Day: $dayName")
                return dayName
            }
        }
    }

    android.util.Log.d("DayFilter", "No purchase history found for client $clientId")
    return "" // No purchase history found
}

/**
 * Enhanced version of getArabicDayNameFromDateString with logging
 */
private fun getArabicDayNameFromDateString(dateTimeString: String): String {
    try {
        android.util.Log.d("DayFilter", "Getting day from date string: '$dateTimeString'")

        // Extract date part (yyyy_MM_dd)
        val datePart = dateTimeString.split("(").firstOrNull()?.trim()
        android.util.Log.d("DayFilter", "Extracted date part: '$datePart'")

        if (datePart.isNullOrEmpty()) {
            android.util.Log.e("DayFilter", "Invalid date format - couldn't extract date part")
            return ""
        }

        // Parse the date
        val dateParts = datePart.split("_")
        android.util.Log.d("DayFilter", "Date parts: $dateParts")

        if (dateParts.size < 3) {
            android.util.Log.e("DayFilter", "Invalid date format - not enough parts")
            return ""
        }

        val year = dateParts[0].toIntOrNull()
        val month = dateParts[1].toIntOrNull()?.minus(1) // Month is 0-based in Calendar
        val day = dateParts[2].toIntOrNull()

        android.util.Log.d("DayFilter", "Parsed date components: year=$year, month=$month, day=$day")

        if (year == null || month == null || day == null) {
            android.util.Log.e("DayFilter", "Failed to parse date components")
            return ""
        }

        // Create calendar instance and set the date
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month, day)

        // Get day of week
        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        android.util.Log.d("DayFilter", "Day of week: $dayOfWeek")

        // Convert to Arabic day name
        val arabicDay = when (dayOfWeek) {
            java.util.Calendar.SUNDAY -> "الأحد"
            java.util.Calendar.MONDAY -> "الإثنين"
            java.util.Calendar.TUESDAY -> "الثلاثاء"
            java.util.Calendar.WEDNESDAY -> "الأربعاء"
            java.util.Calendar.THURSDAY -> "الخميس"
            java.util.Calendar.FRIDAY -> "الجمعة"
            java.util.Calendar.SATURDAY -> "السبت"
            else -> ""
        }

        android.util.Log.d("DayFilter", "Arabic day name: $arabicDay")
        return arabicDay
    } catch (e: Exception) {
        android.util.Log.e("DayFilter", "Error parsing date: ${e.message}", e)
        return "" // Return empty string on any parsing error
    }
}
