package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID6.Pdf_Generateur

import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.SessionDate
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.getSessionDatesForMonth
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument.Table.drawRTLText
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import android.util.Log
import androidx.core.graphics.toColorInt
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
        Log.d("AbsenceDrawer", "drawAbsenceCell called:")
        Log.d("AbsenceDrawer", "  isAbsent: $isAbsent")
        Log.d("AbsenceDrawer", "  cellX: $cellX, cellY: $cellY")
        Log.d("AbsenceDrawer", "  cellWidth: $cellWidth, cellHeight: $cellHeight")

        if (!isAbsent) {
            Log.d("AbsenceDrawer", "  ⚠️ Exiting early - isAbsent = false")
            return
        }

        Log.d("AbsenceDrawer", "  ✅ Drawing background...")
        // Draw background
        val bgPaint = if (isJustified) paintJustifiedBg else paintAbsenceBg
        canvas.drawRect(
            cellX + 1f,
            cellY + 1f,
            cellX + cellWidth - 1f,
            cellY + cellHeight - 1f,
            bgPaint
        )
        Log.d("AbsenceDrawer", "  ✅ Background drawn with color: ${if (isJustified) "orange" else "red"}")

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

/**
 * FIXED: Get absences from observations (Type.Raeeb)
 * Maps session dates to absence information including justification
 *
 * Fix: Now properly matches observations to session dates by day-of-month only.
 * Includes comprehensive logging to debug missing absences.
 */
fun getAbsencesByDate(
    etudiant: M19Etudiant,
    observations: List<M20ObsarvationEtudion>
): Map<SessionDate, ParentCommunicationCardData_But6.AbsenceDay> {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    // SPECIAL LOGGING: Check if this is the student "فدا" with date 04-01-2026
    val isTargetStudent = etudiant.prenom.contains("فدا", ignoreCase = true)
    val targetDate = Calendar.getInstance().apply {
        set(2026, Calendar.JANUARY, 4, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }

    if (isTargetStudent) {
        Log.e("AbsenceDrawer", "")
        Log.e("AbsenceDrawer", "🔍🔍🔍 SPECIAL DEBUG FOR STUDENT: ${etudiant.prenom} 🔍🔍🔍")
        Log.e("AbsenceDrawer", "Student full name: ${etudiant.nom} ${etudiant.prenom}")
        Log.e("AbsenceDrawer", "Student KeyID: ${etudiant.keyID}")
        Log.e("AbsenceDrawer", "Looking for absence on: 04/01/2026 (الأحد)")
    }

    // Filter observations for this student of type Raeeb in current month
    val absenceObservations = observations.filter { obs ->
        val obsDate = Calendar.getInstance().apply { timeInMillis = obs.creationTimestamps }
        obs.etudiant_keyID == etudiant.keyID &&
                obs.type == M20ObsarvationEtudion.Type.Raeeb &&
                obsDate.get(Calendar.MONTH) == currentMonth &&
                obsDate.get(Calendar.YEAR) == currentYear
    }

    if (isTargetStudent) {
        Log.e("AbsenceDrawer", "")
        Log.e("AbsenceDrawer", "📋 ALL OBSERVATIONS FOR THIS STUDENT:")
        observations.filter { it.etudiant_keyID == etudiant.keyID }.forEachIndexed { idx, obs ->
            val obsDate = Calendar.getInstance().apply { timeInMillis = obs.creationTimestamps }
            Log.e("AbsenceDrawer", "  Obs #${idx + 1}:")
            Log.e("AbsenceDrawer", "    Type: ${obs.type}")
            Log.e("AbsenceDrawer", "    Date: ${dateFormat.format(Date(obs.creationTimestamps))}")
            Log.e("AbsenceDrawer", "    Month: ${obsDate.get(Calendar.MONTH) + 1}, Year: ${obsDate.get(Calendar.YEAR)}")
            Log.e("AbsenceDrawer", "    Is Raeeb: ${obs.type == M20ObsarvationEtudion.Type.Raeeb}")
            Log.e("AbsenceDrawer", "    In current month: ${obsDate.get(Calendar.MONTH) == currentMonth && obsDate.get(Calendar.YEAR) == currentYear}")
            if (obs.type == M20ObsarvationEtudion.Type.Raeeb) {
                Log.e("AbsenceDrawer", "    Justification: '${obs.tabrire_riyab}'")
            }
        }
        Log.e("AbsenceDrawer", "")
        Log.e("AbsenceDrawer", "✅ FILTERED Raeeb observations: ${absenceObservations.size}")
    }

    // Get all session dates for the month
    val sessionDates = getSessionDatesForMonth()
    val absenceMap = mutableMapOf<SessionDate, ParentCommunicationCardData_But6.AbsenceDay>()

    if (isTargetStudent) {
        Log.e("AbsenceDrawer", "")
        Log.e("AbsenceDrawer", "📅 SESSION DATES IN JANUARY 2026:")
        sessionDates.forEach { session ->
            val isTargetDay = session.dayOfMonth == 4
            val marker = if (isTargetDay) "👉 TARGET DAY 👈" else ""
            Log.e("AbsenceDrawer", "  Day ${session.dayOfMonth} (${getDayName(session.dayOfWeek)}) $marker")
        }
    }


    // Debug logging
    Log.d("AbsenceDrawer", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    Log.d("AbsenceDrawer", "Student: ${etudiant.nom} ${etudiant.prenom}")
    Log.d("AbsenceDrawer", "Student KeyID: ${etudiant.keyID}")
    Log.d("AbsenceDrawer", "Current Month: ${currentMonth + 1}/$currentYear")
    Log.d("AbsenceDrawer", "Total Raeeb observations found: ${absenceObservations.size}")
    Log.d("AbsenceDrawer", "Total session dates (الأحد/الخميس): ${sessionDates.size}")
    Log.d("AbsenceDrawer", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

    // Log all session dates
    Log.d("AbsenceDrawer", "Session dates in month:")
    sessionDates.forEach { session ->
        Log.d("AbsenceDrawer", "  - Day ${session.dayOfMonth} (${getDayName(session.dayOfWeek)})")
    }
    Log.d("AbsenceDrawer", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

    // Process each observation
    absenceObservations.forEachIndexed { index, obs ->
        val obsDate = Date(obs.creationTimestamps)
        val obsCal = Calendar.getInstance().apply { time = obsDate }
        val obsDayOfMonth = obsCal.get(Calendar.DAY_OF_MONTH)
        val obsDayOfWeek = obsCal.get(Calendar.DAY_OF_WEEK)

        Log.d("AbsenceDrawer", "")
        Log.d("AbsenceDrawer", "Observation #${index + 1}:")
        Log.d("AbsenceDrawer", "  Date: ${dateFormat.format(obsDate)}")
        Log.d("AbsenceDrawer", "  Day of month: $obsDayOfMonth")
        Log.d("AbsenceDrawer", "  Day of week: ${getDayName(obsDayOfWeek)}")
        Log.d("AbsenceDrawer", "  Is justified: ${obs.tabrire_riyab.isNotBlank()}")
        if (obs.tabrire_riyab.isNotBlank()) {
            Log.d("AbsenceDrawer", "  Justification: '${obs.tabrire_riyab}'")
        }

        // FIXED: Match by day-of-month to the nearest session date
        val matchingSession = sessionDates.find { it.dayOfMonth == obsDayOfMonth }

        if (isTargetStudent && obsDayOfMonth == 4) {
            Log.e("AbsenceDrawer", "")
            Log.e("AbsenceDrawer", "🎯🎯🎯 PROCESSING TARGET OBSERVATION (04/01/2026) 🎯🎯🎯")
            Log.e("AbsenceDrawer", "  Observation KeyID: ${obs.keyID}")
            Log.e("AbsenceDrawer", "  Full timestamp: ${obs.creationTimestamps}")
            Log.e("AbsenceDrawer", "  Date formatted: ${dateFormat.format(obsDate)}")
            Log.e("AbsenceDrawer", "  Day of month: $obsDayOfMonth")
            Log.e("AbsenceDrawer", "  Day of week: ${getDayName(obsDayOfWeek)}")
            Log.e("AbsenceDrawer", "  Student KeyID matches: ${obs.etudiant_keyID == etudiant.keyID}")
            Log.e("AbsenceDrawer", "  Type is Raeeb: ${obs.type == M20ObsarvationEtudion.Type.Raeeb}")
            Log.e("AbsenceDrawer", "  Justification: '${obs.tabrire_riyab}'")
            Log.e("AbsenceDrawer", "  Is justified: ${obs.tabrire_riyab.isNotBlank()}")
            Log.e("AbsenceDrawer", "  Matching session found: ${matchingSession != null}")
            if (matchingSession != null) {
                Log.e("AbsenceDrawer", "  Session day: ${matchingSession.dayOfMonth}")
                Log.e("AbsenceDrawer", "  Session day of week: ${getDayName(matchingSession.dayOfWeek)}")
            } else {
                Log.e("AbsenceDrawer", "  ❌ NO SESSION FOUND - This is the problem!")
                Log.e("AbsenceDrawer", "  Available session days: ${sessionDates.map { it.dayOfMonth }}")
            }
        }

        if (matchingSession != null) {
            val isJustified = obs.tabrire_riyab.isNotBlank()
            val justification = if (isJustified) obs.tabrire_riyab else ""

            absenceMap[matchingSession] = ParentCommunicationCardData_But6.AbsenceDay(
                dayOfWeek = when (matchingSession.dayOfWeek) {
                    Calendar.SUNDAY -> 0
                    Calendar.THURSDAY -> 4
                    else -> -1
                },
                date = obsDate,
                isJustified = isJustified,
                justification = justification
            )

            if (isTargetStudent && obsDayOfMonth == 4) {
                Log.e("AbsenceDrawer", "  ✅✅✅ SUCCESSFULLY ADDED TO ABSENCE MAP ✅✅✅")
                Log.e("AbsenceDrawer", "  Will display as: ${if (isJustified) "غياب مبرر" else "غياب غير مبرر"}")
            }

            Log.d("AbsenceDrawer", "  ✅ MATCHED to session day $obsDayOfMonth")
            Log.d("AbsenceDrawer", "  Status: ${if (isJustified) "مبرر (Justified)" else "غير مبرر (Unjustified)"}")
        } else {
            if (isTargetStudent && obsDayOfMonth == 4) {
                Log.e("AbsenceDrawer", "  ❌❌❌ FAILED TO MATCH - ABSENCE WILL NOT DISPLAY ❌❌❌")
            }
            Log.w("AbsenceDrawer", "  ⚠️ NO SESSION FOUND for day $obsDayOfMonth")
            Log.w("AbsenceDrawer", "  This day (${getDayName(obsDayOfWeek)}) is not a session day (الأحد or الخميس)")
            Log.w("AbsenceDrawer", "  This absence will COUNT in totals but NOT DISPLAY in calendar")
        }
    }

    if (isTargetStudent) {
        Log.e("AbsenceDrawer", "")
        Log.e("AbsenceDrawer", "📊 FINAL RESULT FOR فدا:")
        Log.e("AbsenceDrawer", "  Total Raeeb observations: ${absenceObservations.size}")
        Log.e("AbsenceDrawer", "  Matched to sessions: ${absenceMap.size}")
        Log.e("AbsenceDrawer", "  Has entry for day 4: ${absenceMap.keys.any { it.dayOfMonth == 4 }}")
        if (absenceMap.keys.any { it.dayOfMonth == 4 }) {
            val day4Entry = absenceMap.entries.find { it.key.dayOfMonth == 4 }
            Log.e("AbsenceDrawer", "  Day 4 status: ${if (day4Entry?.value?.isJustified == true) "مبرر" else "غير مبرر"}")
        } else {
            Log.e("AbsenceDrawer", "  ❌ NO ENTRY FOR DAY 4 - PROBLEM CONFIRMED!")
        }
        Log.e("AbsenceDrawer", "🔍🔍🔍 END SPECIAL DEBUG 🔍🔍🔍")
        Log.e("AbsenceDrawer", "")
    }

    Log.d("AbsenceDrawer", "")
    Log.d("AbsenceDrawer", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    Log.d("AbsenceDrawer", "SUMMARY:")
    Log.d("AbsenceDrawer", "  Total observations: ${absenceObservations.size}")
    Log.d("AbsenceDrawer", "  Matched to sessions: ${absenceMap.size}")
    Log.d("AbsenceDrawer", "  Unmatched: ${absenceObservations.size - absenceMap.size}")
    Log.d("AbsenceDrawer", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

    return absenceMap
}

/**
 * Helper function to get day name in Arabic for logging
 */
private fun getDayName(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        Calendar.SUNDAY -> "الأحد"
        Calendar.MONDAY -> "الإثنين"
        Calendar.TUESDAY -> "الثلاثاء"
        Calendar.WEDNESDAY -> "الأربعاء"
        Calendar.THURSDAY -> "الخميس"
        Calendar.FRIDAY -> "الجمعة"
        Calendar.SATURDAY -> "السبت"
        else -> "Unknown"
    }
}
