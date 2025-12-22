package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.WeekHeader.View.Functions

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravaille
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Utilisateur

fun calculateTotalWeekWorkTimePerVendor(weekRecords: List<K_TempTravaille>): Pair<Int, Int> {
    var totalMinutesAbdelmoumen = 0
    var totalMinutesWalid = 0

    weekRecords.forEach { record ->
        record.intervalesDeTravaille.forEach { interval ->
            val start = interval.tempDepart
            val end = interval.temparrete

            if (start != "HH:mm" && end != "HH:mm") {
                try {
                    val startParts = start.split(":")
                    val endParts = end.split(":")

                    val startMinutes = startParts[0].toInt() * 60 + startParts[1].toInt()
                    val endMinutes = endParts[0].toInt() * 60 + endParts[1].toInt()
                    val duration = endMinutes - startMinutes

                    if (duration > 0) {
                        when (interval.utilisateur) {
                            Utilisateur.Abdelmoumen -> totalMinutesAbdelmoumen += duration
                            Utilisateur.Walid -> totalMinutesWalid += duration
                            else -> totalMinutesAbdelmoumen += duration
                        }
                    }
                } catch (e: Exception) {
                    // Skip invalid entries
                }
            }
        }
    }

    return Pair(totalMinutesAbdelmoumen, totalMinutesWalid)
}
