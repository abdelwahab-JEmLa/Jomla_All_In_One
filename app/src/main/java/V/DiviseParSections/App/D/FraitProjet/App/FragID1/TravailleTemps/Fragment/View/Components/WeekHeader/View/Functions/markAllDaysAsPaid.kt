package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.WeekHeader.View.Functions

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.WeekInfo
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import androidx.compose.runtime.MutableState
import java.util.Calendar
import java.util.Locale

fun markAllDaysAsPaid(
    weekInfo: WeekInfo,
    viewModel: RecordingViewModel,
    paidStatus: MutableState<Boolean>
) {
    val weekRecords = viewModel.repository.modelDatas.filter { record ->
        val dateString = record.infosDeBase.dateInString
        val parts = dateString.split("/")
        if (parts.size == 3) {
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1
            val day = parts[2].toInt()

            val calendar = Calendar.getInstance().apply {
                firstDayOfWeek = Calendar.SATURDAY
                minimalDaysInFirstWeek = 1
            }
            calendar.set(year, month, day)

            calendar.get(Calendar.YEAR) == weekInfo.year &&
                    calendar.get(Calendar.WEEK_OF_YEAR) == weekInfo.weekNumber
        } else {
            false
        }
    }

    weekRecords.forEach { record ->
        record.infosDeBase.paye = paidStatus.value
        viewModel.repository.updateOnPasseData(record)
    }
}

fun translateWeekTextToArabic(weekInfo: WeekInfo): String {
    return when {
        weekInfo.isCurrentWeek -> "هذا الأسبوع"
        isLastWeek(weekInfo) -> "الأسبوع الماضي"
        else -> {
            val weekDifference = getWeekDifference(weekInfo)
            if (weekDifference > 0) {
                "منذ $weekDifference أسابيع"
            } else {
                "في غضون ${-weekDifference} أسابيع"
            }
        }
    }
}

fun translateWorkDurationToArabic(daysWorked: Double, totalMinutes: Int): String {
    return when {
        totalMinutes == 0 -> "0 يوم"
        daysWorked < 1.0 -> {
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            if (minutes > 0) {
                "$hours ساعات و $minutes دقيقة"
            } else {
                "$hours ساعات"
            }
        }
        daysWorked == 1.0 -> "1 يوم"
        else -> {
            val fullDays = (totalMinutes / (8 * 60))
            val remainingMinutes = totalMinutes % (8 * 60)
            val remainingHours = remainingMinutes / 60
            val remainingMins = remainingMinutes % 60

            when {
                remainingHours > 0 && remainingMins > 0 -> {
                    "$fullDays أيام و $remainingHours ساعات و $remainingMins دقيقة"
                }
                remainingHours > 0 -> {
                    "$fullDays أيام و $remainingHours ساعات"
                }
                remainingMins > 0 -> {
                    "$fullDays أيام و $remainingMins دقيقة"
                }
                else -> {
                    "$fullDays أيام"
                }
            }
        }
    }
}

fun isLastWeek(weekInfo: WeekInfo): Boolean {
    val calendar = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.SATURDAY
        minimalDaysInFirstWeek = 1
    }
    val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
    val currentYear = calendar.get(Calendar.YEAR)

    if (currentWeek == 1 && weekInfo.weekNumber > 50 && weekInfo.year == currentYear - 1) {
        return true
    }

    return weekInfo.year == currentYear && weekInfo.weekNumber == currentWeek - 1
}

fun getWeekDifference(weekInfo: WeekInfo): Int {
    val calendar = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.SATURDAY
        minimalDaysInFirstWeek = 1
    }
    val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
    val currentYear = calendar.get(Calendar.YEAR)

    if (weekInfo.year == currentYear) {
        return currentWeek - weekInfo.weekNumber
    }

    val yearDiff = currentYear - weekInfo.year
    if (yearDiff == 1) {
        val weeksInLastYear = Calendar.getInstance(Locale.getDefault()).apply {
            firstDayOfWeek = Calendar.SATURDAY
            minimalDaysInFirstWeek = 1
            set(Calendar.YEAR, currentYear - 1)
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 31)
        }.get(Calendar.WEEK_OF_YEAR)

        return currentWeek + (weeksInLastYear - weekInfo.weekNumber)
    } else if (yearDiff == -1) {
        val weeksInCurrentYear = Calendar.getInstance(Locale.getDefault()).apply {
            firstDayOfWeek = Calendar.SATURDAY
            minimalDaysInFirstWeek = 1
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 31)
        }.get(Calendar.WEEK_OF_YEAR)

        return -(weekInfo.weekNumber + (weeksInCurrentYear - currentWeek))
    }

    return currentWeek - weekInfo.weekNumber + (yearDiff * 52)
}
