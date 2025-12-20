package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter

// Helper function to check if a grossist has operations in a specific period
fun isGrossistActiveInPeriod(
    grossistKeyID: String,
    ventPeriodKeyID: String,
    repositorysMainGetter: RepositorysMainGetter
): Boolean {
    return repositorysMainGetter.repo11AchatOperation.datasValue.any { achatOperation ->
        achatOperation.parent_M15Grossist_KeyID == grossistKeyID &&
                achatOperation.parent_M14VentPeriod_KeyID == ventPeriodKeyID
    }
}
