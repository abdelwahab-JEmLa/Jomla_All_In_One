package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But6

import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But4.ParentCommunicationCardData
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import android.util.Log
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withTranslation
import com.aminography.primecalendar.hijri.HijriCalendar
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun generatePdfDocument_6(
    context: Context,
    cardsData: List<ParentCommunicationCardData>,
    etudiants: List<M19Etudiant> = emptyList(),
    observations: List<M20ObsarvationEtudion> = emptyList()
): File? {
    return try {
        val outputDir = context.cacheDir
        val pdfFile = File(outputDir, "temp_parent_comm_${System.currentTimeMillis()}.pdf")

        // A4 Landscape dimensions for better table layout
        val pageWidth = 842
        val pageHeight = 595

        val pdfDocument = PdfDocument()

        // Filter out excluded students
        val filteredData = cardsData.filterIndexed { index, _ ->
            val etudiant = etudiants.getOrNull(index)
            etudiant?.exclue_de_l_affiche_au_classe != true
        }

        val filteredEtudiants = etudiants.filter { !it.exclue_de_l_affiche_au_classe }

        // Get current month in Arabic
        val currentMonth = getCurrentMonthArabic()

        // Calculate students per page (approximately 8-10 students per page)
        val studentsPerPage = 8
        val totalPages = (filteredData.size + studentsPerPage - 1) / studentsPerPage

        for (pageIndex in 0 until totalPages) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val marginLeft = 20f
            val marginRight = 20f
            val marginTop = 30f
            val marginBottom = 30f
            val contentWidth = (pageWidth - marginLeft - marginRight)

            var yPosition = marginTop

            // Paint configurations
            val paintHeader = TextPaint().apply {
                textSize = 18f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintSmall = TextPaint().apply {
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintTableHeader = TextPaint().apply {
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.WHITE
            }

            val paintTableCell = TextPaint().apply {
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintAbsenceCell = TextPaint().apply {
                textSize = 7f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.WHITE
            }

            val paintBorder = Paint().apply {
                color = android.graphics.Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 1.5f
            }

            val paintHeaderBg = Paint().apply {
                color = "#2196F3".toColorInt()
                style = Paint.Style.FILL
            }

            val paintAlternateBg = Paint().apply {
                color = "#F5F5F5".toColorInt()
                style = Paint.Style.FILL
            }

            val paintAbsenceBg = Paint().apply {
                color = "#F44336".toColorInt() // Red for absence
                style = Paint.Style.FILL
            }

            // Title - Updated with current month
            drawRTLText(
                canvas,
                "تقرير شهر $currentMonth لمتابعة الحضور لقسم عبدالوهاب حنيش",
                marginLeft, yPosition, contentWidth.toInt(), paintHeader, Layout.Alignment.ALIGN_CENTER
            )
            yPosition += 28f

            // Date
            val hijriDate = getHijriDate()
            val gregorianDate = SimpleDateFormat("dd/MM/yyyy", Locale("ar")).format(Date())
            drawRTLText(
                canvas, "$hijriDate - $gregorianDate",
                marginLeft, yPosition, contentWidth.toInt(), paintSmall, Layout.Alignment.ALIGN_CENTER
            )
            yPosition += 25f

            // Table column widths - Each student on one line with weekly columns
            val nameColWidth = 120f
            val ageColWidth = 30f
            val weekColWidth = 120f // Width for each week column
            val reportColWidth = 80f

            val colWidths = floatArrayOf(
                nameColWidth,  // الاسم الكامل
                ageColWidth,   // السن
                weekColWidth,  // الأسبوع 1
                weekColWidth,  // الأسبوع 2
                weekColWidth,  // الأسبوع 3
                weekColWidth,  // الأسبوع 4
                reportColWidth // تقرير شهري
            )

            val rowHeight = 50f
            val headerHeight = 35f

            // Draw table header
            var xPosition = marginLeft
            canvas.drawRect(marginLeft, yPosition, marginLeft + contentWidth, yPosition + headerHeight, paintHeaderBg)

            val headers = arrayOf(
                "الاسم الكامل",
                "السن",
                "الأسبوع 1",
                "الأسبوع 2",
                "الأسبوع 3",
                "الأسبوع 4",
                "تقرير شهري"
            )

            for (i in headers.indices) {
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[i], yPosition + headerHeight, paintBorder)
                drawRTLText(
                    canvas, headers[i],
                    xPosition + 5f, yPosition + 10f, (colWidths[i] - 10f).toInt(), paintTableHeader,
                    Layout.Alignment.ALIGN_CENTER
                )
                xPosition += colWidths[i]
            }

            yPosition += headerHeight

            // Draw student rows
            val startIndex = pageIndex * studentsPerPage
            val endIndex = minOf(startIndex + studentsPerPage, filteredData.size)

            for (i in startIndex until endIndex) {
                val student = filteredData[i]
                val etudiant = filteredEtudiants.getOrNull(i)

                // Alternate row background
                if ((i - startIndex) % 2 == 1) {
                    canvas.drawRect(marginLeft, yPosition, marginLeft + contentWidth, yPosition + rowHeight, paintAlternateBg)
                }

                xPosition = marginLeft

                // Get absence data for each week
                val weeklyAbsences = getWeeklyAbsences(etudiant, observations)

                // Column 1: Name
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[0], yPosition + rowHeight, paintBorder)
                drawRTLText(
                    canvas, student.studentInfo.fullName,
                    xPosition + 5f, yPosition + 15f, (colWidths[0] - 10f).toInt(), paintTableCell,
                    Layout.Alignment.ALIGN_NORMAL
                )
                xPosition += colWidths[0]

                // Column 2: Age
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[1], yPosition + rowHeight, paintBorder)
                drawRTLText(
                    canvas, "${student.studentInfo.age}",
                    xPosition + 5f, yPosition + 15f, (colWidths[1] - 10f).toInt(), paintTableCell,
                    Layout.Alignment.ALIGN_CENTER
                )
                xPosition += colWidths[1]

                // Columns 3-6: Weekly attendance (4 weeks)
                for (weekIndex in 0 until 4) {
                    val absences = weeklyAbsences[weekIndex]

                    // Draw week cell with sub-cells for each day
                    val weekCellStart = xPosition
                    val dayCellWidth = weekColWidth / 5 // 5 days per week

                    // Draw border for the entire week cell
                    canvas.drawRect(weekCellStart, yPosition, weekCellStart + weekColWidth, yPosition + rowHeight, paintBorder)

                    // Get the actual dates for this week
                    val weekDates = getWeekDates(weekIndex)

                    for (dayIndex in 0 until 5) {
                        val dayCellX = weekCellStart + (dayIndex * dayCellWidth)

                        // Draw vertical separator between days
                        if (dayIndex > 0) {
                            canvas.drawLine(dayCellX, yPosition, dayCellX, yPosition + rowHeight, paintBorder)
                        }

                        // Get day name and date
                        val (dayName, dayDate) = weekDates[dayIndex]
                        val currentMonthName = getCurrentMonthArabic()
                        val dateText = if (dayDate > 0) {
                            "$dayName\n$dayDate\n$currentMonthName"
                        } else {
                            dayName
                        }

                        // Check if there's an absence for this day
                        val hasAbsence = absences.any { it.dayOfWeek == dayIndex }

                        if (hasAbsence) {
                            // Fill with red background
                            canvas.drawRect(
                                dayCellX + 1f, yPosition + 1f,
                                dayCellX + dayCellWidth - 1f, yPosition + rowHeight - 1f,
                                paintAbsenceBg
                            )

                            // Draw "غياب غير مبرر" text
                            drawRTLText(
                                canvas, "غياب\nغير\nمبرر",
                                dayCellX + 2f, yPosition + 8f, (dayCellWidth - 4f).toInt(), paintAbsenceCell,
                                Layout.Alignment.ALIGN_CENTER
                            )
                        } else {
                            // Draw day name and date in normal cell
                            drawRTLText(
                                canvas, dateText,
                                dayCellX + 2f, yPosition + 12f, (dayCellWidth - 4f).toInt(), paintTableCell,
                                Layout.Alignment.ALIGN_CENTER
                            )
                        }
                    }

                    xPosition += weekColWidth
                }

                // Column 7: Monthly Report (15 غياب غير مبرر / 8 حصص)
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[6], yPosition + rowHeight, paintBorder)
                val absenceCount = etudiant?.nmbr_absence_sans_justification ?: 0
                val totalSessions = getCurrentMonthSessions()
                val reportText = "$absenceCount غياب غير مبرر / $totalSessions حصص"
                drawRTLText(
                    canvas, reportText,
                    xPosition + 5f, yPosition + 18f, (colWidths[6] - 10f).toInt(), paintTableCell,
                    Layout.Alignment.ALIGN_CENTER
                )
                xPosition += colWidths[6]

                yPosition += rowHeight
            }

            // Footer
            yPosition = pageHeight - marginBottom - 20f
            drawRTLText(
                canvas, "صفحة ${pageIndex + 1} من $totalPages",
                marginLeft, yPosition, contentWidth.toInt(), paintSmall, Layout.Alignment.ALIGN_CENTER
            )

            pdfDocument.finishPage(page)
        }

        // Write to file
        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        Log.i("ParentCommPdf", "✅ PDF créé: ${pdfFile.absolutePath}")
        pdfFile
    } catch (e: Exception) {
        Log.e("ParentCommPdf", "❌ Erreur lors de la création du PDF", e)
        null
    }
}

/**
 * Get the dates for a specific week of the month
 * Returns list of (dayName, dayNumber) pairs for Sunday-Thursday
 */
fun getWeekDates(weekIndex: Int): List<Pair<String, Int>> {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    // Set to first day of month
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    // Find first Sunday of the month
    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    // Move to the target week
    calendar.add(Calendar.WEEK_OF_YEAR, weekIndex)

    val dayNames = arrayOf("الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس")
    val dates = mutableListOf<Pair<String, Int>>()

    // Get dates for Sunday through Thursday
    for (i in 0 until 5) {
        // Make sure we're still in the same month
        if (calendar.get(Calendar.MONTH) == currentMonth &&
            calendar.get(Calendar.YEAR) == currentYear) {
            dates.add(Pair(dayNames[i], calendar.get(Calendar.DAY_OF_MONTH)))
        } else {
            // If we've gone into next month, use placeholder
            dates.add(Pair(dayNames[i], 0))
        }
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    return dates
}

/**
 * Data class to represent absence information
 */
data class AbsenceInfo(
    val weekOfMonth: Int, // 0-3 for weeks 1-4
    val dayOfWeek: Int,   // 0-4 for Sunday-Thursday
    val date: Date
)

/**
 * Get weekly absences organized by week of the month
 */
fun getWeeklyAbsences(
    etudiant: M19Etudiant?,
    observations: List<M20ObsarvationEtudion>
): List<List<AbsenceInfo>> {
    if (etudiant == null) return List(4) { emptyList() }

    // Filter absence observations for this student in current month
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    val absenceObservations = observations.filter { obs ->
        obs.etudiant_keyID == etudiant.keyID &&
                obs.type == M20ObsarvationEtudion.Type.Raeeb
    }

    // Group absences by week (4 weeks per month)
    val weeklyAbsences = MutableList(4) { mutableListOf<AbsenceInfo>() }

    absenceObservations.forEach { obs ->
        val obsDate = Date(obs.creationTimestamps)
        val obsCal = Calendar.getInstance().apply { time = obsDate }

        // Check if observation is in current month
        if (obsCal.get(Calendar.MONTH) == currentMonth &&
            obsCal.get(Calendar.YEAR) == currentYear) {

            val weekOfMonth = obsCal.get(Calendar.WEEK_OF_MONTH) - 1 // 0-based
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

/**
 * Get current month name in Arabic
 */
fun getCurrentMonthArabic(): String {
    val monthNames = arrayOf(
        "جانفي", "فيفري", "مارس", "أفريل", "ماي", "جوان",
        "جويلية", "أوت", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )
    val calendar = Calendar.getInstance()
    return monthNames[calendar.get(Calendar.MONTH)]
}

/**
 * Calculate total sessions for current month
 * Assumes 2 sessions per week (Sunday and Thursday)
 */
fun getCurrentMonthSessions(): Int {
    val calendar = Calendar.getInstance()
    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    var sessionCount = 0
    for (day in 1..maxDay) {
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Count Sundays and Thursdays as session days
        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.THURSDAY) {
            sessionCount++
        }
    }

    return sessionCount
}

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
 * Get formatted Hijri date using PrimeCalendar library
 */
fun getHijriDate(): String {
    return try {
        val hijriCalendar = HijriCalendar()

        val dayNames = arrayOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
        val dayName = dayNames[hijriCalendar.dayOfWeek - 1]

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
        val gregorianYear = SimpleDateFormat("yyyy", Locale.FRENCH).format(Date()).toInt()
        val hijriYear = gregorianYear - 579
        val dayNum = SimpleDateFormat("dd", Locale.FRENCH).format(Date())
        "التاريخ الهجري: $dayNum $hijriYear هـ"
    }
}

// Add new absence type to M20ObsarvationEtudion.Type enum
// You'll need to update your enum to include:
// Absence_Sans_Justification
