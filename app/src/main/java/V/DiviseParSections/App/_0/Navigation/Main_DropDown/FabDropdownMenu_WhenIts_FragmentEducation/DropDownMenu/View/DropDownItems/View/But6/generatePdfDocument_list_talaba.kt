package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But6

import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.TextPaint
import android.util.Log
import androidx.core.graphics.toColorInt
import java.io.File
import java.io.FileOutputStream
import java.util.Date

data class ParentCommunicationCardData_But6(
    val studentInfo: StudentInfo,
    val attendanceInfo: AttendanceInfo
) {
    data class StudentInfo(
        val fullName: String,
        val age: Int
    )

    data class AttendanceInfo(
        val totalAbsences: Int,
        val totalSessions: Int,
        val weeklyAbsences: List<List<AbsenceDay>>
    )

    data class AbsenceDay(
        val dayOfWeek: Int,  // 0-4 for Sunday-Thursday
        val date: Date,
        val isJustified: Boolean
    )

    companion object {
        fun fromEtudiant(
            etudiant: M19Etudiant,
            weeklyAbsences: List<List<AbsenceDay>> = emptyList()
        ): ParentCommunicationCardData_But6 {
            return ParentCommunicationCardData_But6(
                studentInfo = StudentInfo(
                    fullName = "${etudiant.nom} ${etudiant.prenom} (${etudiant.age} سنوات)",
                    age = etudiant.age
                ),
                attendanceInfo = AttendanceInfo(
                    totalAbsences = etudiant.nmbr_absence_sans_justification,
                    totalSessions = getCurrentMonthSessions(),
                    weeklyAbsences = weeklyAbsences
                )
            )
        }

        private fun getCurrentMonthSessions(): Int {
            val calendar = java.util.Calendar.getInstance()
            val maxDay = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)

            var sessionCount = 0
            for (day in 1..maxDay) {
                calendar.set(java.util.Calendar.DAY_OF_MONTH, day)
                val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)

                if (dayOfWeek == java.util.Calendar.SUNDAY ||
                    dayOfWeek == java.util.Calendar.THURSDAY) {
                    sessionCount++
                }
            }
            return sessionCount
        }
    }
}
fun generatePdfDocument_6(
    context: Context,
    cardsData: List<ParentCommunicationCardData_But6>,
    etudiants: List<M19Etudiant> = emptyList(),
    observations: List<M20ObsarvationEtudion> = emptyList()
): File? {
    return try {
        val outputDir = context.cacheDir
        val pdfFile = File(outputDir, "temp_attendance_report_${System.currentTimeMillis()}.pdf")

        val pageWidth = 842  // A4 Landscape
        val pageHeight = 595

        val pdfDocument = PdfDocument()

        val currentMonth = getCurrentMonthArabic()
        val studentsPerPage = 8
        val totalPages = (cardsData.size + studentsPerPage - 1) / studentsPerPage

        for (pageIndex in 0 until totalPages) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val marginLeft = 20f
            val marginRight = 20f
            val marginTop = if (pageIndex == 0) 120f else 30f
            val marginBottom = 30f
            val contentWidth = pageWidth - marginLeft - marginRight

            var yPosition = 20f

            // Paint configurations
            val paintTitle = TextPaint().apply {
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = "#1976D2".toColorInt()
            }

            val paintHeader = TextPaint().apply {
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintSmall = TextPaint().apply {
                textSize = 10f
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintTableHeader = TextPaint().apply {
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.WHITE
            }

            val paintTableCell = TextPaint().apply {
                textSize = 8f
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintAbsenceCell = TextPaint().apply {
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.WHITE
            }

            val paintWarning = TextPaint().apply {
                textSize = 14f
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

            // First page header with logo (only on first page)
            if (pageIndex == 0) {
                try {
                    val logoStream = context.resources.openRawResource(
                        context.resources.getIdentifier("ecole_logo1", "drawable", context.packageName)
                    )
                    val logoBitmap = BitmapFactory.decodeStream(logoStream)

                    val logoWidth = 200f
                    val logoHeight = 80f
                    val logoX = marginLeft + (contentWidth - logoWidth) / 2

                    canvas.drawBitmap(
                        logoBitmap,
                        null,
                        android.graphics.RectF(logoX, yPosition, logoX + logoWidth, yPosition + logoHeight),
                        null
                    )
                    yPosition += logoHeight + 15f
                } catch (e: Exception) {
                    Log.w("PDF", "Logo not found, skipping")
                    yPosition += 10f
                }

                // Report title - MONTH ONLY
                drawRTLText(
                    canvas,
                    "تقرير شهر $currentMonth لمتابعة الحضور لقسم عبدالوهاب حنيش",
                    marginLeft, yPosition, contentWidth.toInt(), paintTitle,
                    Layout.Alignment.ALIGN_CENTER
                )
                yPosition += 35f
            }

            // Table starts here (RTL - right to left)
            val reportColWidth = 80f   // عدد الغيابات (rightmost)
            val weekColWidth = 160f    // Each week column (2 cells: Sunday + Thursday)
            val nameColWidth = 110f    // الاسم الكامل (leftmost)

            // RTL order: Report -> Week4 -> Week3 -> Week2 -> Week1 -> Name
            val colWidths = floatArrayOf(
                reportColWidth, // عدد الغيابات (rightmost)
                weekColWidth,   // الأسبوع 4
                weekColWidth,   // الأسبوع 3
                weekColWidth,   // الأسبوع 2
                weekColWidth,   // الأسبوع 1
                nameColWidth    // الاسم الكامل (leftmost)
            )

            val rowHeight = 70f
            val headerHeight = 90f

            // Draw table header (RTL)
            var xPosition = marginLeft
            canvas.drawRect(marginLeft, yPosition, marginLeft + contentWidth,
                yPosition + headerHeight, paintHeaderBg)

            val weekDatesForHeaders = (0 until 4).map { getSundayThursdayDates(it) }

            // Headers in RTL order
            val headers = buildList {
                add("عدد\nالغيابات\nفي ${getCurrentMonthSessions()}\nحصص")

                // Weeks 4, 3, 2, 1 (reverse order for RTL)
                for (weekIdx in 3 downTo 0) {
                    val dates = weekDatesForHeaders[weekIdx]
                    add(formatWeekHeaderWithDates(weekIdx + 1, dates))
                }

                add("الاسم\nالكامل")
            }.toTypedArray()

            for (i in headers.indices) {
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[i],
                    yPosition + headerHeight, paintBorder)
                drawRTLText(
                    canvas, headers[i],
                    xPosition + 3f, yPosition + 8f, (colWidths[i] - 6f).toInt(),
                    paintTableHeader, Layout.Alignment.ALIGN_CENTER
                )
                xPosition += colWidths[i]
            }

            yPosition += headerHeight

            // Draw student rows
            val startIndex = pageIndex * studentsPerPage
            val endIndex = minOf(startIndex + studentsPerPage, cardsData.size)

            for (i in startIndex until endIndex) {
                val student = cardsData[i]
                val etudiant = etudiants.getOrNull(i)

                // Alternate row background
                if ((i - startIndex) % 2 == 1) {
                    canvas.drawRect(marginLeft, yPosition, marginLeft + contentWidth,
                        yPosition + rowHeight, paintAlternateBg)
                }

                xPosition = marginLeft

                // Column 1 (rightmost): Total absences count
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[0],
                    yPosition + rowHeight, paintBorder)

                val absenceCount = etudiant?.nmbr_absence_sans_justification ?: 0
                val countPaint = if (absenceCount > 0) paintWarning else paintTableCell

                drawRTLText(
                    canvas, absenceCount.toString(),
                    xPosition + 5f, yPosition + 25f, (colWidths[0] - 10f).toInt(),
                    countPaint, Layout.Alignment.ALIGN_CENTER
                )
                xPosition += colWidths[0]

                // Columns 2-5: Weekly attendance (4 weeks) - reversed order for RTL
                val weeklyAbsences = getWeeklyAbsences(etudiant, observations)

                for (weekIndex in 3 downTo 0) {
                    val absences = weeklyAbsences[weekIndex]
                    val weekCellStart = xPosition
                    val dayCellWidth = weekColWidth / 2  // 2 cells: Sunday and Thursday

                    canvas.drawRect(weekCellStart, yPosition, weekCellStart + weekColWidth,
                        yPosition + rowHeight, paintBorder)

                    val weekDates = getSundayThursdayDates(weekIndex)

                    // Cell 1: Sunday (الأحد)
                    val sundayCellX = weekCellStart
                    canvas.drawLine(sundayCellX + dayCellWidth, yPosition,
                        sundayCellX + dayCellWidth, yPosition + rowHeight, paintBorder)

                    val sundayAbsence = absences.find { it.dayOfWeek == 0 } // Sunday = 0

                    if (sundayAbsence != null) {
                        // Red cell for Sunday absence
                        canvas.drawRect(
                            sundayCellX + 1f, yPosition + 1f,
                            sundayCellX + dayCellWidth - 1f, yPosition + rowHeight - 1f,
                            paintAbsenceBg
                        )
                        drawRTLText(
                            canvas, "⚠\nغياب\nغير\nمبرر",
                            sundayCellX + 2f, yPosition + 10f, (dayCellWidth - 4f).toInt(),
                            paintAbsenceCell, Layout.Alignment.ALIGN_CENTER
                        )
                    }
                    // else: leave Sunday cell empty (no text)

                    // Cell 2: Thursday (الخميس)
                    val thursdayCellX = weekCellStart + dayCellWidth

                    val thursdayAbsence = absences.find { it.dayOfWeek == 4 } // Thursday = 4

                    if (thursdayAbsence != null) {
                        // Red cell for Thursday absence
                        canvas.drawRect(
                            thursdayCellX + 1f, yPosition + 1f,
                            thursdayCellX + dayCellWidth - 1f, yPosition + rowHeight - 1f,
                            paintAbsenceBg
                        )
                        drawRTLText(
                            canvas, "⚠\nغياب\nغير\nمبرر",
                            thursdayCellX + 2f, yPosition + 10f, (dayCellWidth - 4f).toInt(),
                            paintAbsenceCell, Layout.Alignment.ALIGN_CENTER
                        )
                    }
                    // else: leave Thursday cell empty (no text)

                    xPosition += weekColWidth
                }

                // Column 6 (leftmost): Name with age
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[5],
                    yPosition + rowHeight, paintBorder)
                drawRTLText(
                    canvas, student.studentInfo.fullName,
                    xPosition + 5f, yPosition + 25f, (colWidths[5] - 10f).toInt(),
                    paintTableCell, Layout.Alignment.ALIGN_NORMAL
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

