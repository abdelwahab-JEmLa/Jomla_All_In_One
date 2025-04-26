package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.Modules

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GetDateStringName {
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
}
