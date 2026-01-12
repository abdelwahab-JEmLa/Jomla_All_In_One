package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But6.Pdf_Generateur

import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.drawRTLText
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import androidx.core.graphics.toColorInt
import java.util.Calendar
import java.util.Date

/**
 * Utility object for drawing absence-related cells in PDF documents
 * Handles the visual representation of absences, justifications, and icons
 */
object AbsenceDrawer {
    
    private const val SIZE_PNG = 20f
    private const val SIZE_TEXT_ABSENCE_LABEL = 7f
    private const val SIZE_TEXT_JUSTIFICATION = 6f

    /**
     * Draw a single absence cell with icon, label, and optional justification
     */
    fun drawAbsenceCell(
        canvas: Canvas,
        cellX: Float,
        cellY: Float,
        cellWidth: Float,
        cellHeight: Float,
        isAbsent: Boolean,
        isJustified: Boolean,
        justificationText: String = "",
        absenceIcon: Bitmap?,
        justificationIcon: Bitmap?,
        paintAbsenceBg: Paint,
        paintJustifiedBg: Paint,
        paintAbsenceLabel: TextPaint,
        paintJustificationLabel: TextPaint,
        paintBorder: Paint
    ) {
        if (!isAbsent) return

        // Draw background
        val bgPaint = if (isJustified) paintJustifiedBg else paintAbsenceBg
        canvas.drawRect(
            cellX + 1f, 
            cellY + 1f,
            cellX + cellWidth - 1f, 
            cellY + cellHeight - 1f,
            bgPaint
        )

        // Draw icon
        val iconToUse = if (isJustified && justificationIcon != null) {
            justificationIcon
        } else {
            absenceIcon
        }

        if (iconToUse != null) {
            drawIconInCell(canvas, cellX, cellY, cellWidth, iconToUse)
        }

        // Draw label
        val labelText = if (isJustified) {
            "غياب\nمبرر"
        } else {
            "غياب\nغير مبرر"
        }

        drawRTLText(
            canvas, 
            labelText,
            cellX + 2f, 
            cellY + 28f, 
            (cellWidth - 4f).toInt(),
            paintAbsenceLabel, 
            Layout.Alignment.ALIGN_CENTER
        )

        // Draw justification text if present
        if (isJustified && justificationText.isNotBlank()) {
            drawRTLText(
                canvas, 
                justificationText,
                cellX + 2f, 
                cellY + 40f, 
                (cellWidth - 4f).toInt(),
                paintJustificationLabel, 
                Layout.Alignment.ALIGN_CENTER
            )
        }
    }

    /**
     * Draw an icon centered in a cell
     */
    private fun drawIconInCell(
        canvas: Canvas,
        cellX: Float,
        cellY: Float,
        cellWidth: Float,
        icon: Bitmap
    ) {
        val iconSize = SIZE_PNG
        val iconX = cellX + (cellWidth - iconSize) / 2
        val iconY = cellY + 5f

        canvas.drawBitmap(
            icon,
            null,
            android.graphics.RectF(iconX, iconY, iconX + iconSize, iconY + iconSize),
            null
        )
    }

    /**
     * Create Paint objects for absence drawing
     */
    fun createAbsencePaints(): AbsencePaints {
        return AbsencePaints(
            absenceBg = Paint().apply {
                color = "#F44336".toColorInt()
                style = Paint.Style.FILL
            },
            justifiedBg = Paint().apply {
                color = "#FF9800".toColorInt()
                style = Paint.Style.FILL
            },
            absenceLabel = TextPaint().apply {
                textSize = SIZE_TEXT_ABSENCE_LABEL
                typeface = android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT, 
                    android.graphics.Typeface.BOLD
                )
                isAntiAlias = true
                color = android.graphics.Color.WHITE
            },
            justificationLabel = TextPaint().apply {
                textSize = SIZE_TEXT_JUSTIFICATION
                typeface = android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT, 
                    android.graphics.Typeface.BOLD
                )
                isAntiAlias = true
                color = android.graphics.Color.WHITE
            }
        )
    }

    /**
     * Data class holding Paint objects for absence rendering
     */
    data class AbsencePaints(
        val absenceBg: Paint,
        val justifiedBg: Paint,
        val absenceLabel: TextPaint,
        val justificationLabel: TextPaint
    )
}

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
 * FIXED: Get absences from observations (Type.Raeeb)
 * Maps session dates to absence information including justification
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
