package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But6.Pdf_Generateur

import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Utilisateur
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But6.drawRTLText
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But6.getCurrentMonthArabic
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But6.getCurrentMonthSessions
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
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
private const val NOMB_ETUDION_PAR_PAGE_FIRST = 5  // 5 students on first page
private const val NOMB_ETUDION_PAR_PAGE = 8        // 8 students on subsequent pages
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
                color = "#D32F2F".toColorInt()
            }

            val paintTitleBlack = TextPaint().apply {
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = Color.BLACK
            }

            val paintTableHeader = TextPaint().apply {
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = Color.WHITE
            }

            val paintAbsenceColumnHeader = TextPaint().apply {
                textSize = 7f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = Color.WHITE
            }

            val paintTableCell = TextPaint().apply {
                textSize = SIZE_TEXT_NAME_AGE
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = Color.BLACK
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
                color = Color.WHITE
            }

            val paintBorder = Paint().apply {
                color = Color.BLACK
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

            val paintSubHeaderBg = Paint().apply {
                color = "#1976D2".toColorInt()
                style = Paint.Style.FILL
            }

            val paintSubHeaderText = TextPaint().apply {
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = Color.WHITE
            }

            // Create absence paints using the extracted utility
            val absencePaints = AbsenceDrawer.createAbsencePaints()

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
                        RectF(logoX, yPosition, logoX + logoWidth, yPosition + logoHeight),
                        null
                    )
                    yPosition += logoHeight + 15f
                } catch (e: Exception) {
                    Log.w("PDF", "Logo not found, skipping")
                    yPosition += 10f
                }

                // Title line
                drawRTLText(
                    canvas,
                    "تقرير شهر $currentMonth لمتابعة الحضور",
                    marginLeft, yPosition, contentWidth.toInt(), paintTitleRed,
                    Layout.Alignment.ALIGN_CENTER
                )
                yPosition += 30f

                // Section label
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

            // TODO(1) FIXED: Instead of 4 weeks with 2 sessions each, show 8 individual sessions
            val reportColWidth = 75f
            val sessionColWidth = 80f  // Width for each individual session
            val nameColWidth = 110f
            val numberColWidth = 30f

            // Calculate total number of sessions for the month
            val sessionsInMonth = getCurrentMonthSessions()
            val maxSessionsToShow = 8  // Maximum 8 sessions to fit on page

            val colWidths = buildList {
                add(reportColWidth)
                repeat(minOf(sessionsInMonth, maxSessionsToShow)) {
                    add(sessionColWidth)
                }
                add(nameColWidth)
                add(numberColWidth)
            }.toFloatArray()

            // Get all session dates for the month
            val sessionDates = getSessionDatesForMonth()

            // Headers in RTL order
            val headers = buildList {
                add("عدد\nالغيابات\nمن\n$totalSessions\nحصة")
                // Add session headers from right to left (حصة 8, حصة 7, ..., حصة 1)
                for (sessionIdx in minOf(sessionsInMonth, maxSessionsToShow) - 1 downTo 0) {
                    add("حصة ${sessionIdx + 1}")
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
                val isFirstCol = i == 0
                val isNameCol = i == headers.size - 2
                val isNumberCol = i == headers.lastIndex

                if (isFirstCol || isNameCol || isNumberCol) {
                    // First, name, and number columns span full height
                    canvas.drawRect(xPosition, yPosition, xPosition + colWidths[i],
                        yPosition + totalHeaderHeight, paintBorder)

                    val headerPaint = if (isFirstCol) paintAbsenceColumnHeader else paintTableHeader

                    drawRTLText(
                        canvas, headers[i],
                        xPosition + 3f, yPosition + if (isNumberCol) 15f else 25f,
                        (colWidths[i] - 6f).toInt(),
                        headerPaint, Layout.Alignment.ALIGN_CENTER
                    )
                } else {
                    // Session columns have two-part header
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

            // Draw sub-headers for session dates
            xPosition = marginLeft
            xPosition += reportColWidth

            for (sessionIdx in minOf(sessionsInMonth, maxSessionsToShow) - 1 downTo 0) {
                if (sessionIdx < sessionDates.size) {
                    val sessionDate = sessionDates[sessionIdx]

                    canvas.drawRect(xPosition, yPosition, xPosition + sessionColWidth,
                        yPosition + subHeaderHeight, paintSubHeaderBg)
                    canvas.drawRect(xPosition, yPosition, xPosition + sessionColWidth,
                        yPosition + subHeaderHeight, paintBorder)

                    val dayName = when (sessionDate.dayOfWeek) {
                        Calendar.SUNDAY -> "الأحد"
                        Calendar.THURSDAY -> "الخميس"
                        else -> ""
                    }

                    drawRTLText(
                        canvas, "$dayName\n${sessionDate.dayOfMonth}\n$currentMonth",
                        xPosition + 2f, yPosition + 3f, (sessionColWidth - 4f).toInt(),
                        paintSubHeaderText, Layout.Alignment.ALIGN_CENTER
                    )
                }

                xPosition += sessionColWidth
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

                // Session columns: Get all absences mapped by date
                val absencesByDate = getAbsencesByDate(etudiant, observations)

                for (sessionIdx in minOf(sessionsInMonth, maxSessionsToShow) - 1 downTo 0) {
                    val sessionCellStart = xPosition

                    canvas.drawRect(sessionCellStart, yPosition, sessionCellStart + sessionColWidth,
                        yPosition + rowHeight, paintBorder)

                    if (sessionIdx < sessionDates.size) {
                        val sessionDate = sessionDates[sessionIdx]
                        val absence = absencesByDate[sessionDate]

                        if (absence != null) {
                            AbsenceDrawer.drawAbsenceCell(
                                canvas = canvas,
                                cellX = sessionCellStart,
                                cellY = yPosition,
                                cellWidth = sessionColWidth,
                                cellHeight = rowHeight,
                                isAbsent = true,
                                isJustified = absence.isJustified,
                                justificationText = absence.justification,
                                absenceIcon = absenceIcon,
                                justificationIcon = justificationIcon,
                                paintAbsenceBg = absencePaints.absenceBg,
                                paintJustifiedBg = absencePaints.justifiedBg,
                                paintAbsenceLabel = absencePaints.absenceLabel,
                                paintJustificationLabel = absencePaints.justificationLabel,
                                paintBorder = paintBorder
                            )
                        }
                    }

                    xPosition += sessionColWidth
                }

                // Column: Name with age
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[colWidths.size - 2],
                    yPosition + rowHeight, paintBorder)
                drawRTLText(
                    canvas, student.studentInfo.fullName,
                    xPosition + 5f, yPosition + 20f, (colWidths[colWidths.size - 2] - 10f).toInt(),
                    paintTableCell, Layout.Alignment.ALIGN_NORMAL
                )
                xPosition += colWidths[colWidths.size - 2]

                // Column: Row number
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths.last(),
                    yPosition + rowHeight, paintBorder)

                val rowNumber = (idx + 1).toString()
                drawRTLText(
                    canvas, rowNumber,
                    xPosition + 2f, yPosition + 20f, (colWidths.last() - 4f).toInt(),
                    paintTableCell, Layout.Alignment.ALIGN_CENTER
                )

                yPosition += rowHeight
            }

            // Footer
            yPosition = pageHeight - marginBottom - 20f
            val paintSmall = TextPaint().apply {
                textSize = 10f
                isAntiAlias = true
                color = Color.BLACK
            }
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

/**
 * Get all session dates (Sunday and Thursday) for the current month
 * Returns list of SessionDate objects with day info
 */
data class SessionDate(
    val dayOfMonth: Int,
    val dayOfWeek: Int,
    val date: Date
)

fun getSessionDatesForMonth(): List<SessionDate> {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

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

/**
 * Get absences mapped by session date for easy lookup
 */
fun getAbsencesByDate(
    etudiant: M19Etudiant,
    observations: List<M20ObsarvationEtudion>
): Map<SessionDate, ParentCommunicationCardData_But6.AbsenceDay> {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    val absenceObservations = observations.filter { obs ->
        obs.etudiant_keyID == etudiant.keyID &&
                obs.type == M20ObsarvationEtudion.Type.Raeeb
    }

    val sessionDates = getSessionDatesForMonth()
    val absenceMap = mutableMapOf<SessionDate, ParentCommunicationCardData_But6.AbsenceDay>()

    absenceObservations.forEach { obs ->
        val obsDate = Date(obs.creationTimestamps)
        val obsCal = Calendar.getInstance().apply { time = obsDate }

        if (obsCal.get(Calendar.MONTH) == currentMonth &&
            obsCal.get(Calendar.YEAR) == currentYear) {

            val obsDayOfMonth = obsCal.get(Calendar.DAY_OF_MONTH)
            val obsDayOfWeek = obsCal.get(Calendar.DAY_OF_WEEK)

            // Find matching session date
            val matchingSession = sessionDates.find {
                it.dayOfMonth == obsDayOfMonth && it.dayOfWeek == obsDayOfWeek
            }

            if (matchingSession != null) {
                val isJustified = obs.tabrire_riyab.isNotBlank()
                val justification = if (isJustified) obs.tabrire_riyab else ""

                absenceMap[matchingSession] = ParentCommunicationCardData_But6.AbsenceDay(
                    dayOfWeek = when (obsDayOfWeek) {
                        Calendar.SUNDAY -> 0
                        Calendar.THURSDAY -> 4
                        else -> -1
                    },
                    date = obsDate,
                    isJustified = isJustified,
                    justification = justification
                )
            }
        }
    }

    return absenceMap
}

// Keep the old function for backward compatibility if needed
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
        val dayOfWeek: Int,
        val date: Date,
        val isJustified: Boolean,
        val justification: String = ""
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
            val calendar = Calendar.getInstance()
            val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            var sessionCount = 0
            for (day in 1..maxDay) {
                calendar.set(Calendar.DAY_OF_MONTH, day)
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

                if (dayOfWeek == Calendar.SUNDAY ||
                    dayOfWeek == Calendar.THURSDAY) {
                    sessionCount++
                }
            }
            return sessionCount
        }
    }
}
