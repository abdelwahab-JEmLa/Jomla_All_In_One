package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But6

import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import android.graphics.Canvas
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import android.util.Log
import androidx.core.graphics.withTranslation
import com.aminography.primecalendar.hijri.HijriCalendar
import java.util.Calendar
import java.util.Date

fun getWeeklyAbsences(
    etudiant: M19Etudiant?,
    observations: List<M20ObsarvationEtudion>
): List<List<AbsenceInfo>> {
    if (etudiant == null) return List(4) { emptyList() }

    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    val absenceObservations = observations.filter { obs ->
        obs.etudiant_keyID == etudiant.keyID &&
                obs.type == M20ObsarvationEtudion.Type.Raeeb
    }

    val weeklyAbsences = MutableList(4) { mutableListOf<AbsenceInfo>() }

    absenceObservations.forEach { obs ->
        val obsDate = Date(obs.creationTimestamps)
        val obsCal = Calendar.getInstance().apply { time = obsDate }

        if (obsCal.get(Calendar.MONTH) == currentMonth &&
            obsCal.get(Calendar.YEAR) == currentYear) {

            val weekOfMonth = obsCal.get(Calendar.WEEK_OF_MONTH) - 1
            val dayOfWeek = when (obsCal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> 0
                Calendar.MONDAY -> 1
                Calendar.TUESDAY -> 2
                Calendar.WEDNESDAY -> 3
                Calendar.THURSDAY -> 4
                else -> -1
            }

            if (weekOfMonth in 0..3 && dayOfWeek in 0..4) {
                weeklyAbsences[weekOfMonth].add(
                    AbsenceInfo(weekOfMonth, dayOfWeek, obsDate)
                )
            }
        }
    }

    return weeklyAbsences
}

fun getCurrentMonthArabic(): String {
    val monthNames = arrayOf(
        "جانفي", "فيفري", "مارس", "أفريل", "ماي", "جوان",
        "جويلية", "أوت", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )
    return monthNames[Calendar.getInstance().get(Calendar.MONTH)]
}

fun getCurrentMonthSessions(): Int {
    val calendar = Calendar.getInstance()
    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    var sessionCount = 0
    for (day in 1..maxDay) {
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Count Sunday and Thursday
        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.THURSDAY) {
            sessionCount++
        }
    }

    return sessionCount
}

fun drawRTLText(
    canvas: Canvas,
    text: String,
    x: Float,
    y: Float,
    width: Int,
    paint: TextPaint,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_CENTER
) {
    canvas.withTranslation(x, y) {
        val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(alignment)
            .setTextDirection(TextDirectionHeuristics.RTL)
            .setIncludePad(false)
            .build()

        layout.draw(this)
    }
}

fun getHijriDate(): String {
    return try {
        val hijriCalendar = HijriCalendar()

        val dayNames = arrayOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء",
            "الخميس", "الجمعة", "السبت")
        val dayName = dayNames[hijriCalendar.dayOfWeek - 1]

        val monthNames = arrayOf(
            "محرم", "صفر", "ربيع الأول", "ربيع الآخر", "جمادى الأولى", "جمادى الآخرة",
            "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
        )
        val monthName = monthNames[hijriCalendar.month]

        "$dayName ${hijriCalendar.dayOfMonth} $monthName ${hijriCalendar.year} هـ"
    } catch (e: Exception) {
        Log.e("HijriDate", "Error formatting Hijri date", e)
        "التاريخ الهجري"
    }
}
