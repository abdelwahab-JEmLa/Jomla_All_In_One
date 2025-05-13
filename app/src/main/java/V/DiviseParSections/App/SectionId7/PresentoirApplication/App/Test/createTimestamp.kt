package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

import java.util.Calendar

fun createTimestamp(day: Int, hour: Int, minute: Int, year: Int = 2025, month: Int = 5): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, hour, minute, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
