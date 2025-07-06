package V.DiviseParSections.App.Shared.Repository.A.Base

import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.Repo3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.RepoM1ProduitInfos
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

class FocusedVarsHandlerFacade(val getter: GetterFocusedVars, val setter: SetterFocusedVars)

@Stable
class GetterFocusedVars(
    repo2Client: Repo2Client,
    repoM1ProduitInfos: RepoM1ProduitInfos,
    repo3CouleurProduitInfos: Repo3CouleurProduitInfos,
    repo8BonVent: Repo8BonVent,
    private val repo9AppCompt: Repo9AppCompt,  // Make private to ensure we use the observable version
    val repo10OperationVentCouleur: Repo10OperationVentCouleur,
) {
    // Use collectAsState or observe the repository state directly
    val currentM9AppCompt by derivedStateOf {
        repo9AppCompt.datasValue.firstOrNull { it.keyID ==  ParametresAppComptNonSaved().currentAppComptKeyID }
    }

    val onVentM8BonVent by derivedStateOf {
        val targetKey = currentM9AppCompt?.onVentM8BonVentKey // Use currentM9AppCompt instead of repo9AppCompt.currentAppCompt
        repo8BonVent.datasValue.find { it.keyID == targetKey }
    }

    val defaultM8BonVent by derivedStateOf {
        M8BonVent(
            nomClientConcerned = "Default Data",
            parentKeyId9AppComptInfos = ParametresAppComptNonSaved().currentAppComptKeyID,
            parentDebugNameId9AppComptInfos = ParametresAppComptNonSaved().debugNameId9AppComptInfos,
            parentM7VentPeriodKeyId = ParametresAppComptNonSaved().keyIdId7VentPeriod,
            parentM7VentPeriodDebugInfos = ParametresAppComptNonSaved().debugNameId7VentPeriod,
        )
    }

    val onVentM2ClientInfos by derivedStateOf {
        val targetKey = onVentM8BonVent?.parentM2ClientInfosKey
        repo2Client.datasValue.find { it.keyID == targetKey }
    }

    val defaultM3CouleurProduitInfos by derivedStateOf {
        onVentM8BonVent?.let {
            with(it) {
                M10OperationVentCouleur(
                    //---------------------------------Parent VentPeriod----------------------------------------------------------------------------------------------------------------------------------
                    parentHVentPeriodKeyId = parentM7VentPeriodKeyId,
                    parentEVentPeriodDebugName = parentM7VentPeriodDebugInfos,
                    //---------------------------------Parent M8BonVent----------------------------------------------------------------------------------------------------------------------------------
                    parentM8BonVentKeyId = keyID,
                    parentM8BonVentDebugInfos = debugInfos,
                )
            }
        }
    }

    // This is the key fix - make sure this derivedStateOf is reactive to currentM9AppCompt changes
    val focusedM1ProduitInfosAuPrixDifineur by derivedStateOf {
        val keyId = currentM9AppCompt?.focusedAuPrixDifineurM1ProduitInfosKeyId
        Log.d("FocusedVarsDebug", "focusedM1ProduitInfosAuPrixDifineur - keyId: $keyId")

        val result = repoM1ProduitInfos.datasValue.find { it.keyID == keyId }
        Log.d("FocusedVarsDebug", "focusedM1ProduitInfosAuPrixDifineur - result: ${result?.nom}")

        result
    }

    val listFocusedM10OpeVentCouleurParPrixDifineur by derivedStateOf {
        val keyId = focusedM1ProduitInfosAuPrixDifineur?.keyID
        Log.d("FocusedVarsDebug", "listFocusedM10OpeVentCouleurParPrixDifineur - using keyId: $keyId")

        repo10OperationVentCouleur.datasValue.filter {
            it.parentM8BonVentKeyId == (keyId ?: "")
        }
    }

    val ouvertDialogChoixQuantityPourProduitM1ProduitInfos by derivedStateOf {
        repoM1ProduitInfos.datasValue.find {
            it.keyID == (currentM9AppCompt?.dialogChoisireQuantityM1ProduitInfosKeyID ?: "")
        }
    }

    val onVentM3CouleurProduitInfos by derivedStateOf {
        val targetKey = currentM9AppCompt?.onVentM3CouleurProduitInfosKeyID // Use currentM9AppCompt instead of repo9AppCompt.currentAppCompt
        repo10OperationVentCouleur.datasValue.find { it.keyID == targetKey }
    }

    val onVentM8BonVentM10OperationVentFilteredList by derivedStateOf {
        repo10OperationVentCouleur.datasValue.filter {
            it.parentM8BonVentKeyId == (currentM9AppCompt?.onVentM8BonVentKey ?: "")
        }
    }

    companion object {
        @SuppressLint("ModifierFactoryUnreferencedReceiver")
        fun Modifier.getSemanticsTagFocucedVars(getter: GetterFocusedVars): Modifier {
            val map = buildMap {
                put("currentM9AppCompt", getter.currentM9AppCompt ?: "null")
                put("onVentM8BonVent", getter.onVentM8BonVent)
                put("onVentM2ClientInfos", getter.onVentM2ClientInfos ?: "null")
                put("focusedM1ProduitInfosAuPrixDifineur", getter.focusedM1ProduitInfosAuPrixDifineur?.nom ?: "null")
            }

            return map.entries.foldIndexed(this) { index, modifier, (key, value) ->
                modifier.getSemanticsTag(key, value, index + 6)
            }
        }
    }
}

@Stable
class SetterFocusedVars(
    val getterFocusedVars: GetterFocusedVars,
    val Repo2Client: Repo2Client,
    val repo8BonVent: Repo8BonVent,
    val repo9AppCompt: Repo9AppCompt,
    val repo10OperationVentCouleur: Repo10OperationVentCouleur,
) {
    fun addNewM8BonVent(id8BonVent: M8BonVent) =
        ajoutCopyDefaultBonVentEtFocuceLeAuAppCompt(id8BonVent, repo8BonVent)

    fun addNewM2ClientInfos(newClient: HClientInfos) = Repo2Client.addClient(newClient)

    fun ajouteNewM10OperationVentCouleur(it: M10OperationVentCouleur) {
        repo10OperationVentCouleur.addOrUpdateData(it)
    }

    fun updateFocuceM9AppCompt(data: Z_AppCompt) = repo9AppCompt.upsert(data)

    fun ouvrireDialogChoisireQuantity(
        m10OperationVentCouleur: M10OperationVentCouleur
    ) = focuceOnVentM3CouleurProduitInfos(
        m10OperationVentCouleur = m10OperationVentCouleur,
        getterFocusedVars = getterFocusedVars,
        repo9AppCompt = repo9AppCompt,
    )

    fun fermeDialogChoisireQuantityDeVentCouleur(produitKey: String) {
        focuceOnVentM3CouleurProduitInfos(
            getterFocusedVars = getterFocusedVars,
            repo9AppCompt = repo9AppCompt,
        )
        focucePourPrixDeM1Produit(
            produitKey,
            getterFocusedVars,
            repo9AppCompt
        )
    }

    fun ouvrireM1ProduitDialogChoisireQuantityFacade(produit: ArticlesBasesStatsTable) =
        updateCurrentAppComptDialogProduit(
            getterFocusedVars,
            repo9AppCompt,
            produit,
        )

    fun focucePourPrixDeM1ProduitFacade(produit: ArticlesBasesStatsTable) {
        Log.d("FocusedVarsDebug", "focucePourPrixDeM1ProduitFacade called with produit: ${produit.nom}, keyID: ${produit.keyID}")

        // Make sure we have a valid current app compt before trying to update
        val currentAppCompt = getterFocusedVars.currentM9AppCompt
        if (currentAppCompt == null) {
            Log.e("FocusedVarsDebug", "currentM9AppCompt is null, cannot focus product")
            return
        }

        focucePourPrixDeM1Produit(
            produit.keyID,
            getterFocusedVars,
            repo9AppCompt
        )

        // Add a small delay to ensure the state update has propagated
        // This is a workaround - ideally your repository should handle this properly
        Log.d("FocusedVarsDebug", "Updated focusedAuPrixDifineurM1ProduitInfosKeyId to: ${produit.keyID}")
    }

    fun fermeFocucePourPrixDeM1ProduitDialogChoisireQuantityFacade(produit: ArticlesBasesStatsTable) {
        updateCurrentAppComptDialogProduit(getterFocusedVars, repo9AppCompt)
        focucePourPrixDeM1Produit(
            produit.keyID,
            getterFocusedVars,
            repo9AppCompt
        )
    }

    fun anulleFocucePourPrixDeM1ProduitFacade() {
        anulleFocucePourPrixDeM1Produit(getterFocusedVars, repo9AppCompt)
    }
}

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

    Log.d("FocusedVarsDebug", "focucePourPrixDeM1Produit - setting focusedAuPrixDifineurM1ProduitInfosKeyId to: $produitKey")

    val updatedAppCompt = currentAppCompt.copy(
        focusedAuPrixDifineurM1ProduitInfosKeyId = produitKey,
    )

    repo9AppCompt.upsert(updatedAppCompt)

    Log.d("FocusedVarsDebug", "focucePourPrixDeM1Produit - upserted app compt with keyId: $produitKey")
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
            onVentM3CouleurProduitDebugInfos = m10OperationVentCouleur?.debugInfos ?: "null",
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
