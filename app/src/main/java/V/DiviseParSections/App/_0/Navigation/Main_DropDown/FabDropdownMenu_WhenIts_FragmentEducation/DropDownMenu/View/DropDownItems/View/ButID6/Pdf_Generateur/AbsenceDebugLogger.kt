package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID6.Pdf_Generateur

import Application5.App.Repository.M19Etudiant
import Application5.App.Repository.getSessionDatesForMonth
import Application5.App.Repository.M20ObsarvationEtudion
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object AbsenceDebugLogger {
    private const val TAG = "AbsenceDebug"

    fun logFullDebugInfo(
        etudiant: M19Etudiant,
        observations: List<M20ObsarvationEtudion>,
        selectedMonth: Calendar?,
        selectedTeacher: String?
    ) {
        val targetCalendar = selectedMonth?.clone() as? Calendar ?: Calendar.getInstance()
        val targetMonth = targetCalendar.get(Calendar.MONTH)
        val targetYear = targetCalendar.get(Calendar.YEAR)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("ar"))

        Log.e(TAG, "")
        Log.e(TAG, "═══════════════════════════════════════════════")
        Log.e(TAG, "ABSENCE DEBUG REPORT")
        Log.e(TAG, "═══════════════════════════════════════════════")
        Log.e(TAG, "Student: ${etudiant.nom} ${etudiant.prenom}")
        Log.e(TAG, "Student KeyID: ${etudiant.keyID}")
        Log.e(TAG, "Student Teacher Key: ${etudiant.parent_ousstad_key}")
        Log.e(TAG, "Selected Teacher: $selectedTeacher")
        Log.e(TAG, "Target Month: ${monthFormat.format(targetCalendar.time)}")
        Log.e(TAG, "Target Month Number: ${targetMonth + 1}/$targetYear")
        Log.e(TAG, "───────────────────────────────────────────────")

        // Log all observations for this student
        val studentObservations = observations.filter { it.etudiant_keyID == etudiant.keyID }
        Log.e(TAG, "Total Observations for Student: ${studentObservations.size}")
        
        val raeebObservations = studentObservations.filter { it.type == M20ObsarvationEtudion.Type.Raeeb }
        Log.e(TAG, "Total Raeeb Observations: ${raeebObservations.size}")

        // Log observations in target month
        val targetMonthObservations = raeebObservations.filter { obs ->
            val obsDate = Calendar.getInstance().apply { timeInMillis = obs.creationTimestamps }
            obsDate.get(Calendar.MONTH) == targetMonth && obsDate.get(Calendar.YEAR) == targetYear
        }
        Log.e(TAG, "Raeeb Observations in Target Month: ${targetMonthObservations.size}")
        Log.e(TAG, "───────────────────────────────────────────────")

        // Log each observation in detail
        targetMonthObservations.forEachIndexed { idx, obs ->
            val obsDate = Calendar.getInstance().apply { timeInMillis = obs.creationTimestamps }
            Log.e(TAG, "Observation #${idx + 1}:")
            Log.e(TAG, "  KeyID: ${obs.keyID}")
            Log.e(TAG, "  Date: ${dateFormat.format(obsDate.time)}")
            Log.e(TAG, "  Day of Month: ${obsDate.get(Calendar.DAY_OF_MONTH)}")
            Log.e(TAG, "  Day of Week: ${getDayNameArabic(obsDate.get(Calendar.DAY_OF_WEEK))}")
            Log.e(TAG, "  Is Justified: ${obs.tabrire_riyab.isNotBlank()}")
            Log.e(TAG, "  Justification: '${obs.tabrire_riyab}'")
            Log.e(TAG, "  Is Ijaza: ${obs.tabrire_riyab.contains("مجاز من المدرسة", ignoreCase = true) || obs.tabrire_riyab.contains("اجازة من المدرسة", ignoreCase = true)}")
        }

        // Log session dates
        val sessionDates = getSessionDatesForMonth(targetCalendar)
        Log.e(TAG, "───────────────────────────────────────────────")
        Log.e(TAG, "Session Dates in Target Month: ${sessionDates.size}")
        sessionDates.forEachIndexed { idx, session ->
            Log.e(TAG, "  Session ${idx + 1}: Day ${session.dayOfMonth} (${getDayNameArabic(session.dayOfWeek)})")
        }

        // Log matching results
        Log.e(TAG, "───────────────────────────────────────────────")
        Log.e(TAG, "MATCHING ANALYSIS:")
        targetMonthObservations.forEach { obs ->
            val obsDate = Calendar.getInstance().apply { timeInMillis = obs.creationTimestamps }
            val obsDayOfMonth = obsDate.get(Calendar.DAY_OF_MONTH)
            val matchingSession = sessionDates.find { it.dayOfMonth == obsDayOfMonth }
            
            if (matchingSession != null) {
                Log.e(TAG, "  ✅ Day $obsDayOfMonth → MATCHED to session")
            } else {
                Log.e(TAG, "  ❌ Day $obsDayOfMonth → NOT MATCHED (not a session day)")
            }
        }

        Log.e(TAG, "═══════════════════════════════════════════════")
        Log.e(TAG, "")
    }

    private fun getDayNameArabic(dayOfWeek: Int): String {
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

    /**
     * Quick check function to call when generating PDF
     */
    fun quickCheck(
        studentName: String,
        observations: List<M20ObsarvationEtudion>,
        selectedMonth: Calendar?
    ): String {
        val targetCalendar = selectedMonth?.clone() as? Calendar ?: Calendar.getInstance()
        val targetMonth = targetCalendar.get(Calendar.MONTH)
        val targetYear = targetCalendar.get(Calendar.YEAR)
        
        val raeebCount = observations.count { obs ->
            val obsDate = Calendar.getInstance().apply { timeInMillis = obs.creationTimestamps }
            obs.type == M20ObsarvationEtudion.Type.Raeeb &&
            obsDate.get(Calendar.MONTH) == targetMonth &&
            obsDate.get(Calendar.YEAR) == targetYear
        }
        
        return "Student: $studentName, Raeeb in month: $raeebCount"
    }
}
