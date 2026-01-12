package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But6

import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import android.content.Context
import android.graphics.BitmapFactory
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
    cardsData: List<ParentCommunicationCardData_But6>,
    etudiants: List<M19Etudiant> = emptyList(),
    observations: List<M20ObsarvationEtudion> = emptyList()
): File? {                    //<--
//TODO(1): regle les todos et reccreeec .kt
//fait que le header est jus tpor la premier page les autre c just un tableau

//fait quele tablea est de droit a gautche
    return try {
        val outputDir = context.cacheDir
        val pdfFile = File(outputDir, "temp_attendance_report_${System.currentTimeMillis()}.pdf")

        // A4 Landscape dimensions
        val pageWidth = 842
        val pageHeight = 595

        val pdfDocument = PdfDocument()

        // Filter and sort students by number of absences (descending)
        val filteredData = cardsData.filterIndexed { index, _ ->
            val etudiant = etudiants.getOrNull(index)
            etudiant?.exclue_de_l_affiche_au_classe != true
        }.sortedByDescending { card ->
            card.attendanceInfo.totalAbsences
        }

        val filteredEtudiants = etudiants
            .filter { !it.exclue_de_l_affiche_au_classe }
            .sortedByDescending { it.nmbr_absence_sans_justification }

        val currentMonth = getCurrentMonthArabic()
        val studentsPerPage = 8
        val totalPages = (filteredData.size + studentsPerPage - 1) / studentsPerPage

        for (pageIndex in 0 until totalPages) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val marginLeft = 20f
            val marginRight = 20f
            val marginTop = if (pageIndex == 0) 80f else 30f  // More space for first page
            val marginBottom = 30f
            val contentWidth = (pageWidth - marginLeft - marginRight)

            var yPosition = 20f

            // Paint configurations
            val paintTitle = TextPaint().apply {
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = "#1976D2".toColorInt()
            }

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
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintAbsenceCell = TextPaint().apply {
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.WHITE
            }

            val paintWarning = TextPaint().apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = "#F44336".toColorInt()
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
                color = "#F44336".toColorInt()
                style = Paint.Style.FILL
            }

            val paintWarningBg = Paint().apply {
                color = "#FFF3E0".toColorInt()
                style = Paint.Style.FILL
            }

            // First page header with logo
            if (pageIndex == 0) {
                try {
                    // Load logo from drawable
                    val logoStream = context.resources.openRawResource(
                        context.resources.getIdentifier(
                            "ecole_logo1",
                            "drawable",
                            context.packageName
                        )
                    )
                    val logoBitmap = BitmapFactory.decodeStream(logoStream)

                    val logoSize = 60f
                    val logoX = marginLeft + (contentWidth - logoSize) / 2
                    canvas.drawBitmap(
                        logoBitmap,
                        null,
                        android.graphics.RectF(logoX, yPosition, logoX + logoSize, yPosition + logoSize),
                        null
                    )
                    yPosition += 65f
                } catch (e: Exception) {
                    Log.w("PDF", "Logo not found, skipping")
                }

                // School name
                drawRTLText(
                    canvas,
                    "مدرسة البر",           //<--
                    //TODO(1): //le logo soit width 200.dp enlve "مدرسة البر",
                    marginLeft, yPosition, contentWidth.toInt(), paintTitle,
                    Layout.Alignment.ALIGN_CENTER
                )
                yPosition += 30f
            }

            // Report title
            drawRTLText(
                canvas,
                "تقرير شهر $currentMonth لمتابعة الحضور لقسم عبدالوهاب حنيش",
                marginLeft, yPosition, contentWidth.toInt(), paintHeader,
                Layout.Alignment.ALIGN_CENTER
            )
            yPosition += 28f

            // Date
            val hijriDate = getHijriDate()
            val gregorianDate = SimpleDateFormat("dd/MM/yyyy", Locale("ar")).format(Date())
            drawRTLText(
                canvas, "$hijriDate - $gregorianDate",
                marginLeft, yPosition, contentWidth.toInt(), paintSmall,
                Layout.Alignment.ALIGN_CENTER
            )
            yPosition += 25f

            // Table column widths
            val nameColWidth = 140f
            val weekColWidth = 120f
            val reportColWidth = 100f

            val colWidths = floatArrayOf(               "السن",
                "الأسبوع 1",         //<--
                //TODO(1): ici c "الأسبوع 1" حصة 1 curent date dd جانفي
                //<--
                //TODO(1): ajoute c "الأسبوع 1" حصة 2 curent date dd جانفي
                "الأسبوع 2",                 //<--
                //TODO(1): ect...
                "الأسبوع 3",
                //<--
                //TODO(1): pk ici il n ya pas 8 casse pour لبحصص
                nameColWidth,  // الاسم الكامل مع السن
                weekColWidth,  // الأسبوع 1
                weekColWidth,  // الأسبوع 2
                weekColWidth,  // الأسبوع 3
                weekColWidth,  // الأسبوع 4
                reportColWidth // عدد الغيابات
            )

            val rowHeight = 55f
            val headerHeight = 60f

            // Draw table header
            var xPosition = marginLeft
            canvas.drawRect(marginLeft, yPosition, marginLeft + contentWidth,
                yPosition + headerHeight, paintHeaderBg)

            val weekDatesForHeaders = (0 until 4).map { getWeekDates(it) }
                  //<--
                  //TODO(1): a l interieur normalemnt si present c vide si abcet غياب غير مبرر avec icon warning et on rouge
            val headers = arrayOf(
                "الاسم الكامل",
                formatWeekHeader(0, weekDatesForHeaders[0]),
                formatWeekHeader(1, weekDatesForHeaders[1]),
                formatWeekHeader(2, weekDatesForHeaders[2]),
                formatWeekHeader(3, weekDatesForHeaders[3]),
                "عدد الغيابات\nفي ${getCurrentMonthSessions()} حصص"
            )

            for (i in headers.indices) {
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[i],
                    yPosition + headerHeight, paintBorder)
                drawRTLText(
                    canvas, headers[i],
                    xPosition + 5f, yPosition + 12f, (colWidths[i] - 10f).toInt(),
                    paintTableHeader, Layout.Alignment.ALIGN_CENTER
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
                    canvas.drawRect(marginLeft, yPosition, marginLeft + contentWidth,
                        yPosition + rowHeight, paintAlternateBg)
                }

                xPosition = marginLeft

                // Get absence data for each week
                val weeklyAbsences = getWeeklyAbsences(etudiant, observations)

                // Column 1: Name with age
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[0],
                    yPosition + rowHeight, paintBorder)
                drawRTLText(
                    canvas, student.studentInfo.fullName,
                    xPosition + 5f, yPosition + 20f, (colWidths[0] - 10f).toInt(),
                    paintTableCell, Layout.Alignment.ALIGN_NORMAL
                )
                xPosition += colWidths[0]

                // Columns 2-5: Weekly attendance (4 weeks)
                for (weekIndex in 0 until 4) {
                    val absences = weeklyAbsences[weekIndex]
                    val weekCellStart = xPosition
                    val dayCellWidth = weekColWidth / 5

                    canvas.drawRect(weekCellStart, yPosition, weekCellStart + weekColWidth,
                        yPosition + rowHeight, paintBorder)

                    val weekDates = getWeekDates(weekIndex)

                    for (dayIndex in 0 until 5) {
                        val dayCellX = weekCellStart + (dayIndex * dayCellWidth)

                        if (dayIndex > 0) {
                            canvas.drawLine(dayCellX, yPosition, dayCellX,
                                yPosition + rowHeight, paintBorder)
                        }

                        val (dayName, dayDate) = weekDates[dayIndex]
                        val dateText = if (dayDate > 0) {
                            "$dayName\n$dayDate\n$currentMonth"
                        } else {
                            dayName
                        }

                        // Check for absence
                        val absenceForDay = absences.find { it.dayOfWeek == dayIndex }

                        if (absenceForDay != null) {
                            // Red background for absence
                            canvas.drawRect(
                                dayCellX + 1f, yPosition + 1f,
                                dayCellX + dayCellWidth - 1f, yPosition + rowHeight - 1f,
                                paintAbsenceBg
                            )

                            // Warning icon/text
                            drawRTLText(
                                canvas, "⚠\nغياب\nغير\nمبرر",
                                dayCellX + 2f, yPosition + 5f, (dayCellWidth - 4f).toInt(),
                                paintAbsenceCell, Layout.Alignment.ALIGN_CENTER
                            )
                        } else {
                            // Normal cell with date
                            drawRTLText(
                                canvas, dateText,
                                dayCellX + 2f, yPosition + 12f, (dayCellWidth - 4f).toInt(),
                                paintTableCell, Layout.Alignment.ALIGN_CENTER
                            )
                        }
                    }

                    xPosition += weekColWidth
                }

                // Column 6: Total absences count only
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[5],
                    yPosition + rowHeight, paintBorder)

                val absenceCount = etudiant?.nmbr_absence_sans_justification ?: 0
                val countText = absenceCount.toString()

                val countPaint = if (absenceCount > 0) paintWarning else paintTableCell

                drawRTLText(
                    canvas, countText,
                    xPosition + 5f, yPosition + 20f, (colWidths[5] - 10f).toInt(),
                    countPaint, Layout.Alignment.ALIGN_CENTER
                )

                yPosition += rowHeight
            }

            // Footer
            yPosition = pageHeight - marginBottom - 20f
            drawRTLText(
                canvas, "صفحة ${pageIndex + 1} من $totalPages",
                marginLeft, yPosition, contentWidth.toInt(), paintSmall,
                Layout.Alignment.ALIGN_CENTER
            )

            pdfDocument.finishPage(page)
        }

        // Write to file
        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        Log.i("AttendanceReport", "✅ PDF created: ${pdfFile.absolutePath}")
        pdfFile
    } catch (e: Exception) {
        Log.e("AttendanceReport", "❌ Error creating PDF", e)
        null
    }
}

/**
 * Format week header with current date
 */
fun formatWeekHeader(weekIndex: Int, weekDates: List<Pair<String, Int>>): String {
    val currentMonth = getCurrentMonthArabic()
    val firstDate = weekDates.firstOrNull()?.second ?: 0
    val lastDate = weekDates.lastOrNull()?.second ?: 0

    return if (firstDate > 0 && lastDate > 0) {
        "الأسبوع ${weekIndex + 1}\nحصة $firstDate-$lastDate $currentMonth"
    } else {
        "الأسبوع ${weekIndex + 1}"
    }
}

/**
 * Get the dates for a specific week of the month
 */
fun getWeekDates(weekIndex: Int): List<Pair<String, Int>> {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    calendar.set(Calendar.DAY_OF_MONTH, 1)

    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    calendar.add(Calendar.WEEK_OF_YEAR, weekIndex)

    val dayNames = arrayOf("الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس")
    val dates = mutableListOf<Pair<String, Int>>()

    for (i in 0 until 5) {
        if (calendar.get(Calendar.MONTH) == currentMonth &&
            calendar.get(Calendar.YEAR) == currentYear) {
            dates.add(Pair(dayNames[i], calendar.get(Calendar.DAY_OF_MONTH)))
        } else {
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
    val weekOfMonth: Int,
    val dayOfWeek: Int,
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
 */
fun getCurrentMonthSessions(): Int {
    val calendar = Calendar.getInstance()
    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    var sessionCount = 0
    for (day in 1..maxDay) {
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.THURSDAY) {
            sessionCount++
        }
    }

    return sessionCount
}

/**
 * Helper function to draw RTL text
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
 * Get formatted Hijri date
 */
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
