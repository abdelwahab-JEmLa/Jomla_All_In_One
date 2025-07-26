package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt

class BonVentOperations(
    private val getter: RepositorysMainGetter,
    private val gBonVentRepository: Repo8BonVent,
    private val zAppComptRepositoryComposable: Repo9AppCompt
) {

}
