package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But6

import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Utilisateur
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
import java.util.Calendar
import java.util.Date

// Constants
private const val SIZE_TEXT_ATTENDANCE_STATUS = 9f
private const val SIZE_TEXT_ABSENCE_COUNT = 9f
private const val SIZE_TEXT_NAME_AGE = 14f
private const val SIZE_TEXT_ABSENCE_LABEL = 7f
private const val SIZE_TEXT_JUSTIFICATION = 6f
private const val SIZE_PNG = 20f
private const val NOMB_ETUDION_PAR_PAGE_FIRST = 5  // 8 students on first page (reduced by 1)
private const val NOMB_ETUDION_PAR_PAGE = 8        // 9 students on subsequent pages
private const val COLUMN_HEIGHT_ETUDION = 60f
private const val FIRST_HEADER_HEIGHT = 20f

fun generatePdfDocument_6(
    context: Context,
    cardsData: List<ParentCommunicationCardData_But6>,
    etudiants: List<M19Etudiant> = emptyList(),
    observations: List<M20ObsarvationEtudion> = emptyList(),
    currentUtilisateur: Utilisateur = Utilisateur.Admin
): File? {
    return try {
        val outputDir = context.cacheDir
        val pdfFile = File(outputDir, "temp_attendance_report_${System.currentTimeMillis()}.pdf")

        val pageWidth = 842  // A4 Landscape
        val pageHeight = 595

        val pdfDocument = PdfDocument()

        val currentMonth = getCurrentMonthArabic()
        val totalSessions = getCurrentMonthSessions()
        val teacherNameArabic = currentUtilisateur.nom_arab

        // Load icons
        val absenceIcon = try {
            val iconStream = context.resources.openRawResource(
                context.resources.getIdentifier("absent", "drawable", context.packageName)
            )
            BitmapFactory.decodeStream(iconStream)
        } catch (e: Exception) {
            Log.w("PDF", "Absence icon (absent.png) not found")
            null
        }

        val justificationIcon = try {
            val iconStream = context.resources.openRawResource(
                context.resources.getIdentifier("tabrire", "drawable", context.packageName)
            )
            BitmapFactory.decodeStream(iconStream)
        } catch (e: Exception) {
            Log.w("PDF", "Justification icon (tabrire.png) not found")
            null
        }

        // Sort students: first by absences (descending), then by name (ascending)
        val sortedIndices = etudiants.indices.sortedWith(
            compareByDescending<Int> { etudiants[it].nmbr_absence_sans_justification }
                .thenBy { "${etudiants[it].nom} ${etudiants[it].prenom}" }
        )

        // Calculate pagination with different size for first page
        val studentsFirstPage = NOMB_ETUDION_PAR_PAGE_FIRST
        val studentsPerPage = NOMB_ETUDION_PAR_PAGE

        val totalPages = if (cardsData.size <= studentsFirstPage) {
            1
        } else {
            1 + ((cardsData.size - studentsFirstPage + studentsPerPage - 1) / studentsPerPage)
        }

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
            val paintTitleRed = TextPaint().apply {
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = "#D32F2F".toColorInt()  // Red color
            }

            val paintTitleBlack = TextPaint().apply {
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
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

            val paintAbsenceColumnHeader = TextPaint().apply {
                textSize = 7f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.WHITE
            }

            val paintTableCell = TextPaint().apply {
                textSize = SIZE_TEXT_NAME_AGE
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintAbsenceLabel = TextPaint().apply {
                textSize = SIZE_TEXT_ABSENCE_LABEL
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.WHITE
            }

            val paintJustificationLabel = TextPaint().apply {
                textSize = SIZE_TEXT_JUSTIFICATION
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.WHITE
            }

            val paintWarning = TextPaint().apply {
                textSize = SIZE_TEXT_ABSENCE_COUNT
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = "#F44336".toColorInt()
            }

            val paintSuccess = TextPaint().apply {
                textSize = SIZE_TEXT_ATTENDANCE_STATUS
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = "#4CAF50".toColorInt()
            }

            val paintSessionLabel = TextPaint().apply {
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
                color = "#F44336".toColorInt()
                style = Paint.Style.FILL
            }

            val paintJustifiedBg = Paint().apply {
                color = "#FF9800".toColorInt()
                style = Paint.Style.FILL
            }

            val paintSubHeaderBg = Paint().apply {
                color = "#1976D2".toColorInt()
                style = Paint.Style.FILL
            }

            val paintSubHeaderText = TextPaint().apply {
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.WHITE
            }

            // First page header with logo and teacher name
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

                // First line: "تقرير شهر {month} لمتابعة الحضور" in RED and BOLD
                drawRTLText(
                    canvas,
                    "تقرير شهر $currentMonth لمتابعة الحضور",
                    marginLeft, yPosition, contentWidth.toInt(), paintTitleRed,
                    Layout.Alignment.ALIGN_CENTER
                )
                yPosition += 30f

                // Second line: "قسم المنظم {teacher}" with teacher name in RED and BOLD, rest in black
                // Split into two parts
                drawRTLText(
                    canvas,
                    "قسم المنظم",
                    marginLeft, yPosition, contentWidth.toInt(), paintTitleBlack,
                    Layout.Alignment.ALIGN_CENTER
                )
                yPosition += 28f

                // Teacher name in RED
                drawRTLText(
                    canvas,
                    teacherNameArabic,
                    marginLeft, yPosition, contentWidth.toInt(), paintTitleRed,
                    Layout.Alignment.ALIGN_CENTER
                )
                yPosition += 30f
            }

            val reportColWidth = 75f
            val weekColWidth = 160f
            val nameColWidth = 110f
            val numberColWidth = 30f

            val colWidths = floatArrayOf(
                reportColWidth,
                weekColWidth,
                weekColWidth,
                weekColWidth,
                weekColWidth,
                nameColWidth,
                numberColWidth
            )

            // Get week dates for headers
            val weekDatesForHeaders = (0 until 4).map { getSundayThursdayDates(it) }

            // Headers in RTL order
            val headers = buildList {
                add("عدد\nالغيابات\nمن\n$totalSessions\nحصة")
                for (weekIdx in 3 downTo 0) {
                    add("الأسبوع ${weekIdx + 1}")
                }
                add("الاسم\nالكامل")
                add("رقم")
            }.toTypedArray()

            val rowHeight = COLUMN_HEIGHT_ETUDION
            val headerHeight = FIRST_HEADER_HEIGHT
            val subHeaderHeight = 28f
            val totalHeaderHeight = headerHeight + subHeaderHeight

            // Draw table header background
            var xPosition = marginLeft
            canvas.drawRect(marginLeft, yPosition, marginLeft + contentWidth,
                yPosition + totalHeaderHeight, paintHeaderBg)

            // Draw main header cells
            for (i in headers.indices) {
                if (i == 0 || i == headers.size - 2 || i == headers.lastIndex) {
                    canvas.drawRect(xPosition, yPosition, xPosition + colWidths[i],
                        yPosition + totalHeaderHeight, paintBorder)

                    val headerPaint = if (i == 0) paintAbsenceColumnHeader else paintTableHeader

                    drawRTLText(
                        canvas, headers[i],
                        xPosition + 3f, yPosition +
                                if (i == headers.lastIndex) 15f else 25f,
                        (colWidths[i] - 6f).toInt(),
                        headerPaint, Layout.Alignment.ALIGN_CENTER
                    )
                } else {
                    canvas.drawRect(xPosition, yPosition, xPosition + colWidths[i],
                        yPosition + headerHeight, paintBorder)
                    drawRTLText(
                        canvas, headers[i],
                        xPosition + 3f, yPosition + 6f, (colWidths[i] - 6f).toInt(),
                        paintTableHeader, Layout.Alignment.ALIGN_CENTER
                    )
                }
                xPosition += colWidths[i]
            }

            yPosition += headerHeight

            // Draw sub-headers for session labels
            xPosition = marginLeft
            xPosition += reportColWidth

            for (weekIdx in 3 downTo 0) {
                val dates = weekDatesForHeaders[weekIdx]
                val dayCellWidth = weekColWidth / 2

                canvas.drawRect(xPosition, yPosition, xPosition + weekColWidth,
                    yPosition + subHeaderHeight, paintSubHeaderBg)
                canvas.drawRect(xPosition, yPosition, xPosition + weekColWidth,
                    yPosition + subHeaderHeight, paintBorder)

                val thursdayCellX = xPosition
                canvas.drawLine(thursdayCellX + dayCellWidth, yPosition,
                    thursdayCellX + dayCellWidth, yPosition + subHeaderHeight, paintBorder)

                if (dates.second > 0) {
                    drawRTLText(
                        canvas, "حصة 2\nالخميس ${dates.second}\n${getCurrentMonthArabic()}",
                        thursdayCellX + 2f, yPosition + 3f, (dayCellWidth - 4f).toInt(),
                        paintSubHeaderText, Layout.Alignment.ALIGN_CENTER
                    )
                }

                val sundayCellX = xPosition + dayCellWidth
                if (dates.first > 0) {
                    drawRTLText(
                        canvas, "حصة 1\nالأحد ${dates.first}\n${getCurrentMonthArabic()}",
                        sundayCellX + 2f, yPosition + 3f, (dayCellWidth - 4f).toInt(),
                        paintSubHeaderText, Layout.Alignment.ALIGN_CENTER
                    )
                }

                xPosition += weekColWidth
            }

            yPosition += subHeaderHeight

            // Calculate student indices for this page
            val (startIndex, endIndex) = if (pageIndex == 0) {
                Pair(0, minOf(studentsFirstPage, cardsData.size))
            } else {
                val previousStudents = studentsFirstPage + (pageIndex - 1) * studentsPerPage
                Pair(previousStudents, minOf(previousStudents + studentsPerPage, cardsData.size))
            }

            // Draw student rows
            for (idx in startIndex until endIndex) {
                val i = sortedIndices[idx]
                val student = cardsData[i]
                val etudiant = etudiants[i]

                if ((idx - startIndex) % 2 == 1) {
                    canvas.drawRect(marginLeft, yPosition, marginLeft + contentWidth,
                        yPosition + rowHeight, paintAlternateBg)
                }

                xPosition = marginLeft

                // Column 1: Total absences count
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[0],
                    yPosition + rowHeight, paintBorder)

                val absenceCount = etudiant.nmbr_absence_sans_justification

                if (absenceCount == 0) {
                    drawRTLText(
                        canvas, "تم\nحضور\nال$totalSessions\nحصص",
                        xPosition + 3f, yPosition + 12f, (colWidths[0] - 6f).toInt(),
                        paintSuccess, Layout.Alignment.ALIGN_CENTER
                    )
                } else {
                    drawRTLText(
                        canvas, "$absenceCount",
                        xPosition + 5f, yPosition + 18f, (colWidths[0] - 10f).toInt(),
                        paintWarning, Layout.Alignment.ALIGN_CENTER
                    )
                }
                xPosition += colWidths[0]

                // Columns 2-5: Weekly attendance
                val weeklyAbsences = getWeeklyAbsencesWithJustification(etudiant, observations)

                for (weekIndex in 3 downTo 0) {
                    val absences = weeklyAbsences[weekIndex]
                    val weekCellStart = xPosition
                    val dayCellWidth = weekColWidth / 2

                    canvas.drawRect(weekCellStart, yPosition, weekCellStart + weekColWidth,
                        yPosition + rowHeight, paintBorder)

                    // Thursday (RIGHT cell)
                    val thursdayCellX = weekCellStart
                    canvas.drawLine(thursdayCellX + dayCellWidth, yPosition,
                        thursdayCellX + dayCellWidth, yPosition + rowHeight, paintBorder)

                    val thursdayAbsence = absences.find { it.dayOfWeek == 4 }

                    if (thursdayAbsence != null) {
                        val bgPaint = if (thursdayAbsence.isJustified) paintJustifiedBg else paintAbsenceBg

                        canvas.drawRect(
                            thursdayCellX + 1f, yPosition + 1f,
                            thursdayCellX + dayCellWidth - 1f, yPosition + rowHeight - 1f,
                            bgPaint
                        )

                        val iconToUse = if (thursdayAbsence.isJustified && justificationIcon != null) {
                            justificationIcon
                        } else {
                            absenceIcon
                        }

                        if (iconToUse != null) {
                            val iconSize = SIZE_PNG
                            val iconX = thursdayCellX + (dayCellWidth - iconSize) / 2
                            val iconY = yPosition + 5f

                            canvas.drawBitmap(
                                iconToUse,
                                null,
                                android.graphics.RectF(iconX, iconY, iconX + iconSize, iconY + iconSize),
                                null
                            )

                            val labelText = if (thursdayAbsence.isJustified) {
                                "غياب\nمبرر"
                            } else {
                                "غياب\nغير مبرر"
                            }

                            drawRTLText(
                                canvas, labelText,
                                thursdayCellX + 2f, yPosition + 28f, (dayCellWidth - 4f).toInt(),
                                paintAbsenceLabel, Layout.Alignment.ALIGN_CENTER
                            )

                            if (thursdayAbsence.isJustified && thursdayAbsence.justification.isNotBlank()) {
                                drawRTLText(
                                    canvas, thursdayAbsence.justification,
                                    thursdayCellX + 2f, yPosition + 40f, (dayCellWidth - 4f).toInt(),
                                    paintJustificationLabel, Layout.Alignment.ALIGN_CENTER
                                )
                            }
                        }
                    }

                    // Sunday (LEFT cell)
                    val sundayCellX = weekCellStart + dayCellWidth
                    val sundayAbsence = absences.find { it.dayOfWeek == 0 }

                    if (sundayAbsence != null) {
                        val bgPaint = if (sundayAbsence.isJustified) paintJustifiedBg else paintAbsenceBg

                        canvas.drawRect(
                            sundayCellX + 1f, yPosition + 1f,
                            sundayCellX + dayCellWidth - 1f, yPosition + rowHeight - 1f,
                            bgPaint
                        )

                        val iconToUse = if (sundayAbsence.isJustified && justificationIcon != null) {
                            justificationIcon
                        } else {
                            absenceIcon
                        }

                        if (iconToUse != null) {
                            val iconSize = SIZE_PNG
                            val iconX = sundayCellX + (dayCellWidth - iconSize) / 2
                            val iconY = yPosition + 5f

                            canvas.drawBitmap(
                                iconToUse,
                                null,
                                android.graphics.RectF(iconX, iconY, iconX + iconSize, iconY + iconSize),
                                null
                            )

                            val labelText = if (sundayAbsence.isJustified) {
                                "غياب\nمبرر"
                            } else {
                                "غياب\nغير مبرر"
                            }

                            drawRTLText(
                                canvas, labelText,
                                sundayCellX + 2f, yPosition + 28f, (dayCellWidth - 4f).toInt(),
                                paintAbsenceLabel, Layout.Alignment.ALIGN_CENTER
                            )

                            if (sundayAbsence.isJustified && sundayAbsence.justification.isNotBlank()) {
                                drawRTLText(
                                    canvas, sundayAbsence.justification,
                                    sundayCellX + 2f, yPosition + 40f, (dayCellWidth - 4f).toInt(),
                                    paintJustificationLabel, Layout.Alignment.ALIGN_CENTER
                                )
                            }
                        }
                    }

                    xPosition += weekColWidth
                }

                // Column 6: Name with age
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[5],
                    yPosition + rowHeight, paintBorder)
                drawRTLText(
                    canvas, student.studentInfo.fullName,
                    xPosition + 5f, yPosition + 20f, (colWidths[5] - 10f).toInt(),
                    paintTableCell, Layout.Alignment.ALIGN_NORMAL
                )
                xPosition += colWidths[5]

                // Column 7: Row number
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[6],
                    yPosition + rowHeight, paintBorder)

                val rowNumber = (idx + 1).toString()
                drawRTLText(
                    canvas, rowNumber,
                    xPosition + 2f, yPosition + 20f, (colWidths[6] - 4f).toInt(),
                    paintTableCell, Layout.Alignment.ALIGN_CENTER
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

fun getWeeklyAbsencesWithJustification(
    etudiant: M19Etudiant?,
    observations: List<M20ObsarvationEtudion>
): List<List<ParentCommunicationCardData_But6.AbsenceDay>> {
    if (etudiant == null) return List(4) { emptyList() }

    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    val absenceObservations = observations.filter { obs ->
        obs.etudiant_keyID == etudiant.keyID &&
                obs.type == M20ObsarvationEtudion.Type.Raeeb
    }

    val weeklyAbsences = MutableList(4) { mutableListOf<ParentCommunicationCardData_But6.AbsenceDay>() }

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
                val isJustified = obs.tabrire_riyab.isNotBlank()
                val justification = if (isJustified) obs.tabrire_riyab else ""

                weeklyAbsences[weekOfMonth].add(
                    ParentCommunicationCardData_But6.AbsenceDay(
                        dayOfWeek = dayOfWeek,
                        date = obsDate,
                        isJustified = isJustified,
                        justification = justification
                    )
                )
            }
        }
    }

    return weeklyAbsences
}

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
        val isJustified: Boolean,
        val justification: String = ""  // Added justification text
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
