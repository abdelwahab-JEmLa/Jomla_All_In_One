package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But6

import java.util.Calendar
import java.util.Date

/**
 * Get only Sunday and Thursday dates for a specific week
 * Returns: Pair(Sunday date, Thursday date)
 */
fun getSundayThursdayDates(weekIndex: Int): Pair<Int, Int> {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    // Set to first day of month
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    // Find first Sunday
    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    // Move to target week
    calendar.add(Calendar.WEEK_OF_YEAR, weekIndex)

    // Get Sunday date
    val sundayDate = if (calendar.get(Calendar.MONTH) == currentMonth &&
        calendar.get(Calendar.YEAR) == currentYear) {
        calendar.get(Calendar.DAY_OF_MONTH)
    } else 0

    // Move to Thursday (4 days after Sunday)
    calendar.add(Calendar.DAY_OF_MONTH, 4)

    // Get Thursday date
    val thursdayDate = if (calendar.get(Calendar.MONTH) == currentMonth &&
        calendar.get(Calendar.YEAR) == currentYear) {
        calendar.get(Calendar.DAY_OF_MONTH)
    } else 0

    return Pair(sundayDate, thursdayDate)
}

/**
 * Format week header with Sunday and Thursday dates
 */
fun formatWeekHeaderWithDates(weekNumber: Int, dates: Pair<Int, Int>): String {
    val currentMonth = getCurrentMonthArabic()
    val (sundayDate, thursdayDate) = dates

    return if (sundayDate > 0 && thursdayDate > 0) {
        "الأسبوع $weekNumber\nحصص الأحد $sundayDate\nالخميس $thursdayDate\n$currentMonth"
    } else {
        "الأسبوع $weekNumber"
    }
}

data class AbsenceInfo(
    val weekOfMonth: Int,
    val dayOfWeek: Int,
    val date: Date
)
