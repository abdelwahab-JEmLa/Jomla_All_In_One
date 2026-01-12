package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID6.Pdf_Generateur

import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Ousstad_Tahfid
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.getSessionDatesForMonth
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Constants
private const val SIZE_TEXT_ATTENDANCE_STATUS = 9f
private const val SIZE_TEXT_ABSENCE_COUNT = 9f
private const val SIZE_TEXT_NAME_AGE = 14f
private const val NOMB_ETUDION_PAR_PAGE_FIRST = 5
private const val NOMB_ETUDION_PAR_PAGE = 8
private const val COLUMN_HEIGHT_ETUDION = 60f
private const val FIRST_HEADER_HEIGHT = 20f
// Add selectedMonth parameter to function signature
fun generatePdfDocument_6(
    context: Context,
    cardsData: List<ParentCommunicationCardData_But6>,
    etudiants: List<M19Etudiant> = emptyList(),
    observations: List<M20ObsarvationEtudion> = emptyList(),
    selectedTeacher: Ousstad_Tahfid? = Ousstad_Tahfid.Abdelwahab_Osstad,
    selectedMonth: Calendar? = null  // FIXED: Added missing parameter
): File? {
    return try {
        val outputDir = context.cacheDir
        val pdfFile = File(outputDir, "temp_attendance_report_${System.currentTimeMillis()}.pdf")

        val pageWidth = 842
        val pageHeight = 595

        val pdfDocument = PdfDocument()

        // Use selected month or current month
        val targetMonth = selectedMonth ?: Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMMM", Locale("ar"))
        val currentMonth = monthFormat.format(targetMonth.time)

        // Calculate total sessions for the selected month
        val totalSessions = calculateSessionsForMonth(targetMonth)
        val teacherNameArabic = selectedTeacher?.nom_arab ?:""

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
        } catch (_: Exception) {
            Log.w("PDF", "Justification icon (tabrire.png) not found")
            null
        }

        // Sort students by calculated absences from observations
        val sortedIndices = etudiants.indices.sortedWith(
            compareByDescending<Int> { etudiants[it].calculateUnjustifiedAbsences(observations) }
                .thenBy { "${etudiants[it].nom} ${etudiants[it].prenom}" }
        )

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
            if (pageIndex == 0) 120f else 30f
            val marginBottom = 30f
            val contentWidth = pageWidth - marginLeft - marginRight

            var yPosition = 20f

            // Paint configurations (same as before)
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

            val absencePaints = AbsenceDrawer.createAbsencePaints()

            // First page header
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

                drawRTLText(
                    canvas,
                    "تقرير شهر $currentMonth لمتابعة الحضور",
                    marginLeft, yPosition, contentWidth.toInt(), paintTitleRed,
                    Layout.Alignment.ALIGN_CENTER
                )
                yPosition += 30f

                drawRTLText(
                    canvas,
                    "قسم المنظم",
                    marginLeft, yPosition, contentWidth.toInt(), paintTitleBlack,
                    Layout.Alignment.ALIGN_CENTER
                )
                yPosition += 28f

                drawRTLText(
                    canvas,
                    teacherNameArabic,
                    marginLeft, yPosition, contentWidth.toInt(), paintTitleRed,
                    Layout.Alignment.ALIGN_CENTER
                )
                yPosition += 30f
            }

            val reportColWidth = 75f
            val sessionColWidth = 80f
            val nameColWidth = 110f
            val numberColWidth = 30f

            val maxSessionsToShow = 8

            val colWidths = buildList {
                add(reportColWidth)
                repeat(minOf(totalSessions, maxSessionsToShow)) {
                    add(sessionColWidth)
                }
                add(nameColWidth)
                add(numberColWidth)
            }.toFloatArray()

            // Get session dates for the selected month
            val sessionDates = getSessionDatesForMonth(targetMonth)

            val headers = buildList {
                add("عدد\nالغيابات\nمن\n$totalSessions\nحصة")
                for (sessionIdx in minOf(totalSessions, maxSessionsToShow) - 1 downTo 0) {
                    add("حصة ${sessionIdx + 1}")
                }
                add("الاسم\nالكامل")
                add("رقم")
            }.toTypedArray()

            val rowHeight = COLUMN_HEIGHT_ETUDION
            val headerHeight = FIRST_HEADER_HEIGHT
            val subHeaderHeight = 28f
            val totalHeaderHeight = headerHeight + subHeaderHeight

            // Draw table header
            var xPosition = marginLeft
            canvas.drawRect(marginLeft, yPosition, marginLeft + contentWidth,
                yPosition + totalHeaderHeight, paintHeaderBg)

            for (i in headers.indices) {
                val isFirstCol = i == 0
                val isNameCol = i == headers.size - 2
                val isNumberCol = i == headers.lastIndex

                if (isFirstCol || isNameCol || isNumberCol) {
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

            // Sub-headers for session dates
            xPosition = marginLeft
            xPosition += reportColWidth

            for (sessionIdx in minOf(totalSessions, maxSessionsToShow) - 1 downTo 0) {
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

                // Column 1: Total unjustified absences count
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[0],
                    yPosition + rowHeight, paintBorder)

                val absenceCount = etudiant.calculateUnjustifiedAbsences(observations)

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

                val absencesByDate = getAbsencesByDate(etudiant, observations)

                // Session columns
                for (sessionIdx in minOf(totalSessions, maxSessionsToShow) - 1 downTo 0) {
                    val sessionCellStart = xPosition

                    canvas.drawRect(sessionCellStart, yPosition, sessionCellStart + sessionColWidth,
                        yPosition + rowHeight, paintBorder)

                    if (sessionIdx < sessionDates.size) {
                        val sessionDate = sessionDates[sessionIdx]

                        val absence = absencesByDate.entries.find {
                            it.key.dayOfMonth == sessionDate.dayOfMonth
                        }?.value

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
 * Helper function to calculate total sessions for a given month
 */
private fun calculateSessionsForMonth(month: Calendar): Int {
    val calendar = month.clone() as Calendar
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
        /**
         * FIXED: Now requires observations to calculate absences dynamically
         */
        fun fromEtudiant(
            etudiant: M19Etudiant,
            observations: List<M20ObsarvationEtudion> = emptyList(),
            weeklyAbsences: List<List<AbsenceDay>> = emptyList()
        ): ParentCommunicationCardData_But6 {
            return ParentCommunicationCardData_But6(
                studentInfo = StudentInfo(
                    fullName = "${etudiant.nom} ${etudiant.prenom} (${etudiant.age} سنوات)",
                    age = etudiant.age
                ),
                attendanceInfo = AttendanceInfo(
                    totalAbsences = etudiant.calculateUnjustifiedAbsences(observations),
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
