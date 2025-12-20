package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter

// Alternative approach: If you want to create a more robust timestamp filtering system
// You can add this helper function to get precise period boundaries:
fun getPeriodTimestampRange(
    ventPeriodKeyID: String,
    repositorysMainGetter: RepositorysMainGetter
): Pair<Long, Long>? {
    val currentPeriod = repositorysMainGetter.repo14VentPeriode.datasValue
        .firstOrNull { it.keyID == ventPeriodKeyID } ?: return null

    // Get all periods for the same parent, sorted by creation timestamp
    val allPeriodsForSameParent = repositorysMainGetter.repo14VentPeriode.datasValue
        .filter { it.parent_M9AppCompt_KeyID == currentPeriod.parent_M9AppCompt_KeyID }
        .sortedBy { it.creationTimestamp }

    val currentPeriodIndex = allPeriodsForSameParent.indexOfFirst { it.keyID == ventPeriodKeyID }
    if (currentPeriodIndex == -1) return null

    val startTimestamp = currentPeriod.creationTimestamp
    val endTimestamp = if (currentPeriodIndex < allPeriodsForSameParent.size - 1) {
        allPeriodsForSameParent[currentPeriodIndex + 1].creationTimestamp
    } else {
        System.currentTimeMillis()
    }

    return Pair(startTimestamp, endTimestamp)
}
