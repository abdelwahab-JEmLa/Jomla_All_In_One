package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.WeekHeader.View.Functions

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.WeekHeader.View.WeekSalesData
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.WeekInfo
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import java.util.Calendar

fun calculateWeekSalesData(
    weekInfo: WeekInfo,
    focusedValuesGetter: FocusedValuesGetter
): WeekSalesData {
    val repo14 = focusedValuesGetter.repo14VentPeriode

    if (repo14 == null) {
        return WeekSalesData(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    }

    val weekPeriods = repo14.datasValue.filter { period ->
        val calendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.SATURDAY
            minimalDaysInFirstWeek = 1
            timeInMillis = period.creationTimestamp
        }

        calendar.get(Calendar.YEAR) == weekInfo.year &&
                calendar.get(Calendar.WEEK_OF_YEAR) == weekInfo.weekNumber
    }

    var totalCash = 0.0
    var totalCredit = 0.0
    var totalSavedBalance = 0.0
    var pre_fraits_voiture_essance_marche_et_paprasse = 0.0


    weekPeriods.forEach { period ->
        totalCash += period.cash_Vents_Totale
        totalCredit += period.credit_Vents_Totale
        totalSavedBalance += period.saved_balance
        pre_fraits_voiture_essance_marche_et_paprasse += period.pre_fraits_voiture_essance_marche_et_paprasse
    }

    val totalSales = totalCash + totalCredit
    val profitPercentage = if (totalSales > 0) {
        (totalSavedBalance / totalSales) * 100
    } else {
        0.0
    }

    return WeekSalesData(
        totalCashSales = totalCash,
        totalCreditSales = totalCredit,
        totalSales = totalSales,
        totalSavedBalance = totalSavedBalance,
        pre_fraits_voiture_essance_marche_et_paprasse = pre_fraits_voiture_essance_marche_et_paprasse,
        profitPercentage = profitPercentage
    )
}
