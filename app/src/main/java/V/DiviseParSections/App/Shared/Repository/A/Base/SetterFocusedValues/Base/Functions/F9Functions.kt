package V.DiviseParSections.App.Shared.Repository.A.Base.SetterFocusedValues.Base.Functions
import V.DiviseParSections.App.Shared.Repository.A.Base.GetterFocusedVars
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import android.util.Log

fun focucePourPrixDeM1Produit(
    produitKey: String,
    getterFocusedVars: GetterFocusedVars,
    repo9AppCompt: Repo9AppCompt
) {
    val currentAppCompt = getterFocusedVars.currentM9AppCompt
    if (currentAppCompt == null) {
        Log.e("FocusedVarsDebug", "Cannot focus product - currentM9AppCompt is null")
        return
    }

    Log.d(
        "FocusedVarsDebug",
        "focucePourPrixDeM1Produit - setting focusedAuPrixDifineurM1ProduitInfosKeyId to: $produitKey"
    )

    val updatedAppCompt = currentAppCompt.copy(
        focusedAuPrixDifineurM1ProduitInfosKeyId = produitKey,
    )

    repo9AppCompt.upsert(updatedAppCompt)

    Log.d(
        "FocusedVarsDebug",
        "focucePourPrixDeM1Produit - upserted app compt with keyId: $produitKey"
    )
}

fun anulleFocucePourPrixDeM1Produit(
    getterFocusedVars: GetterFocusedVars,
    repo9AppCompt: Repo9AppCompt
) {
    val currentAppCompt = getterFocusedVars.currentM9AppCompt
    if (currentAppCompt == null) {
        Log.e("FocusedVarsDebug", "Cannot nullify focus - currentM9AppCompt is null")
        return
    }

    repo9AppCompt.upsert(
        currentAppCompt.copy(
            focusedAuPrixDifineurM1ProduitInfosKeyId = "null",
        )
    )
}

fun updateCurrentAppComptDialogProduit(
    getterFocusedVars: GetterFocusedVars,
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
    getterFocusedVars: GetterFocusedVars,
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

fun ajoutCopyDefaultBonVentEtFocuceLeAuAppCompt(
    id8BonVent: M8BonVent,
    id8BonVentRepository: Repo8BonVent,
) {
    val newData = id8BonVent.copy(creationTimestamps = System.currentTimeMillis())
    id8BonVentRepository.add(newData)
}
