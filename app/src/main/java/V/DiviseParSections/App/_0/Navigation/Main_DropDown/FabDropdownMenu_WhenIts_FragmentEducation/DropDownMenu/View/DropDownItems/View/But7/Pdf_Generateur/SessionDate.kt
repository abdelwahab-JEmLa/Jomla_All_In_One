package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But7.Pdf_Generateur

import java.util.Calendar
import java.util.Date

data class SessionDate(
    val dayOfMonth: Int,
    val dayOfWeek: Int,
    val date: Date
)

fun getSessionDatesForMonth(
    //<--
    //TODO(1): cree un nullable val  de month 
): List<SessionDate> {
    val calendar = Calendar.getInstance()

    val sessionDates = mutableListOf<SessionDate>()

    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    for (day in 1..maxDay) {
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.THURSDAY) {
            sessionDates.add(
                SessionDate(
                    dayOfMonth = day,
                    dayOfWeek = dayOfWeek,
                    date = calendar.time
                )
            )
        }
    }

    return sessionDates
}
