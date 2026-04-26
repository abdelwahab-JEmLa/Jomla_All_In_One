package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.ButID6.Pdf_Generateur

import Application5.App.Repository.M19Etudiant
import Application5.App.Repository.M20ObsarvationEtudion
import java.util.Calendar

/**
 * Data class to hold absence statistics for a student
 */
data class AbsenceStatistics(
    val totalAbsences: Int,
    val unjustifiedAbsences: Int,
    val justifiedAbsences: Int,
    val ijazaAbsences: Int
) {
    companion object {
        /**
         * Calculate absence statistics for a student in a specific month
         */
        fun calculate(
            etudiant: M19Etudiant,
            observations: List<M20ObsarvationEtudion>,
            selectedMonth: Calendar? = null
        ): AbsenceStatistics {
            val targetCalendar = selectedMonth?.clone() as? Calendar ?: Calendar.getInstance()
            val targetMonth = targetCalendar.get(Calendar.MONTH)
            val targetYear = targetCalendar.get(Calendar.YEAR)

            // Filter Raeeb observations for this student in the target month
            val absenceObservations = observations.filter { obs ->
                val obsDate = Calendar.getInstance().apply { timeInMillis = obs.creationTimestamps }
                obs.etudiant_keyID == etudiant.keyID &&
                        obs.type == M20ObsarvationEtudion.Type.Raeeb &&
                        obsDate.get(Calendar.MONTH) == targetMonth &&
                        obsDate.get(Calendar.YEAR) == targetYear
            }

            val totalAbsences = absenceObservations.size
            
            val unjustifiedAbsences = absenceObservations.count { 
                it.tabrire_riyab.isBlank() 
            }
            
            val ijazaAbsences = absenceObservations.count { 
                it.tabrire_riyab.contains("مجاز من المدرسة", ignoreCase = true) ||
                it.tabrire_riyab.contains("اجازة من المدرسة", ignoreCase = true)
            }
            
            val justifiedAbsences = absenceObservations.count { 
                it.tabrire_riyab.isNotBlank() &&
                !it.tabrire_riyab.contains("مجاز من المدرسة", ignoreCase = true) &&
                !it.tabrire_riyab.contains("اجازة من المدرسة", ignoreCase = true)
            }

            return AbsenceStatistics(
                totalAbsences = totalAbsences,
                unjustifiedAbsences = unjustifiedAbsences,
                justifiedAbsences = justifiedAbsences,
                ijazaAbsences = ijazaAbsences
            )
        }
    }
}
