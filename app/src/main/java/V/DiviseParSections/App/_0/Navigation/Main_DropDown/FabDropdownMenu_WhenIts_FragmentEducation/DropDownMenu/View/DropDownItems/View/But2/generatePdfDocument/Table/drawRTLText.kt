package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import android.util.Log
import androidx.core.graphics.withTranslation
import com.aminography.primecalendar.hijri.HijriCalendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper function to draw RTL text correctly using StaticLayout
 */
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

/**
 * Draw a writable space box for parent input
 */
fun drawWritableSpace(
    canvas: Canvas,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    lines: Int = 3
) {
    val paintLine = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 0.5f
    }

    // Draw horizontal lines for writing
    val lineSpacing = height / (lines + 1)
    for (i in 1..lines) {
        val lineY = y + (lineSpacing * i)
        canvas.drawLine(x, lineY, x + width, lineY, paintLine)
    }
}

/**
 * Get formatted Hijri date using PrimeCalendar library
 */
fun getHijriDate(): String {
    return try {
        val hijriCalendar = HijriCalendar()

        // Get day name in Arabic
        val dayNames = arrayOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
        val dayName = dayNames[hijriCalendar.dayOfWeek - 1]

        // Get month name in Arabic
        val monthNames = arrayOf(
            "محرم", "صفر", "ربيع الأول", "ربيع الآخر", "جمادى الأولى", "جمادى الآخرة",
            "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
        )
        val monthName = monthNames[hijriCalendar.month]

        val day = hijriCalendar.dayOfMonth
        val year = hijriCalendar.year

        "$dayName $day $monthName $year هـ"
    } catch (e: Exception) {
        Log.e("HijriDate", "Error formatting Hijri date", e)
        // Fallback
        val gregorianYear = SimpleDateFormat("yyyy", Locale.FRENCH).format(Date()).toInt()
        val hijriYear = gregorianYear - 579
        val dayNum = SimpleDateFormat("dd", Locale.FRENCH).format(Date())
        "التاريخ الهجري: $dayNum $hijriYear هـ"
    }
}
