package V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.Functions
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.GetFocusedVars
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import android.util.Log

fun setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
    produitKey: String,
    getterFocusedVars: GetFocusedVars,
    repo9AppCompt: Repo9AppCompt
) {
    val currentAppCompt = getterFocusedVars.currentM9AppCompt

    val updatedAppCompt = currentAppCompt?.copy(
        activeFocuce_TariffPrixDifineur_M1ProduitKeyID = produitKey,
    )

    if (updatedAppCompt != null) {
        repo9AppCompt.upsert(updatedAppCompt)
    }
}

fun clear_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
    getterFocusedVars: GetFocusedVars,
    repo9AppCompt: Repo9AppCompt
) {
    val currentAppCompt = getterFocusedVars.currentM9AppCompt ?: return

    repo9AppCompt.upsert(
        currentAppCompt.copy(
            activeFocuce_TariffPrixDifineur_M1ProduitKeyID = "null",
            activeFocuseTariffPrixDifineurM1ProduitDebugInfos = "null",
        )
    )
}

fun updateCurrentAppComptDialogProduit(
    getterFocusedVars: GetFocusedVars,
    repo9AppCompt: Repo9AppCompt,
    produit: ArticlesBasesStatsTable? = null
) {
    val currentAppCompt = getterFocusedVars.currentM9AppCompt
    if (currentAppCompt == null) {
        Log.e("FocusedVarsDebug", "Cannot update dialog product - currentM9AppCompt is null")
        return
    }

    repo9AppCompt.upsert(
        currentAppCompt.copy(
            dialogChoisireQuantityM1ProduitInfosKeyID = produit?.keyID ?: "null",
            dialogChoisireQuantityM1ProduitInfosDebugName = produit?.nom ?: "null",
        )
    )
}

fun focuceOnVentM3CouleurProduitInfos(
    m10OperationVentCouleur: M10OperationVentCouleur? = null,
    getterFocusedVars: GetFocusedVars,
    repo9AppCompt: Repo9AppCompt
) {
    val currentAppCompt = getterFocusedVars.currentM9AppCompt
    if (currentAppCompt == null) {
        Log.e("FocusedVarsDebug", "Cannot focus couleur product - currentM9AppCompt is null")
        return
    }

    repo9AppCompt.upsert(
        currentAppCompt.copy(
            onVentM3CouleurProduitDebugInfos = m10OperationVentCouleur?.getDebugInfos() ?: "null",
            onVentM3CouleurProduitInfosKeyID = m10OperationVentCouleur?.keyID ?: "null",
        )
    )
}

fun add_New_M8BonVent(
    m8BonVent: M8BonVent,
    repo8BonVent: Repo8BonVent,
) {
    val newData = m8BonVent.copy(creationTimestamps = System.currentTimeMillis())
    repo8BonVent.add(newData)
}
