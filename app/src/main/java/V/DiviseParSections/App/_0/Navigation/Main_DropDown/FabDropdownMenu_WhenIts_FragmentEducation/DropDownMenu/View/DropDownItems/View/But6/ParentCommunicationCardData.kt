package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But6

import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import java.util.Date

/**
 * Data class for Button 6 - Attendance Report
 * Simplified structure focused on attendance tracking
 */
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
