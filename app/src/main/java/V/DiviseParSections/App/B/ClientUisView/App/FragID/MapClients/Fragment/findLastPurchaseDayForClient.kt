package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase

/**
 * Finds the Arabic day name of the last purchase for a specific client
 *
 * @param historicalData List of historical sales periods
 * @param clientId ID of the client to search for
 * @return Arabic name of the day (e.g., "السبت" for Saturday) or empty string if not found
 */
fun findLastPurchaseDayForClient(
    historicalData: List<_01_VentsHistoriquesDataBase>,
    clientId: Long
): String {
    // Sort periods by creation date (newest first)
    val sortedPeriods = historicalData.sortedByDescending { it.tempCreationStr }

    // Find the most recent purchase for this client
    for (period in sortedPeriods) {
        for (vendeur in period.child_012_Compts_Vendeurs) {
            // Check if this client has any purchases in this period
            val hasClientPurchase = vendeur.child_013_Acheteurs.any { it.idClient == clientId }

            if (hasClientPurchase) {
                // We found a purchase by this client, return the day name
                return getArabicDayNameFromDateString(period.tempCreationStr)
            }
        }
    }

    return "" // No purchase history found
}

/**
 * Converts a date string to Arabic day name
 * 
 * @param dateTimeString Format: "yyyy_MM_dd(HH:mm)" 
 * @return Arabic day name
 */
private fun getArabicDayNameFromDateString(dateTimeString: String): String {
    try {
        // Extract date part (yyyy_MM_dd)
        val datePart = dateTimeString.split("(").firstOrNull()?.trim() ?: return ""
        
        // Parse the date
        val dateParts = datePart.split("_")
        if (dateParts.size < 3) return ""
        
        val year = dateParts[0].toIntOrNull() ?: return ""
        val month = dateParts[1].toIntOrNull()?.minus(1) ?: return "" // Month is 0-based in Calendar
        val day = dateParts[2].toIntOrNull() ?: return ""
        
        // Create calendar instance and set the date
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month, day)
        
        // Get day of week (1 = Sunday, 2 = Monday, ..., 7 = Saturday)
        return when (calendar.get(java.util.Calendar.DAY_OF_WEEK)) {
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
        return "" // Return empty string on any parsing error
    }
}
