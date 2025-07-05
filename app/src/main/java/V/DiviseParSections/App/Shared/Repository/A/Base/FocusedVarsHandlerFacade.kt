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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.delay

class FocusedVarsHandlerFacade(val getter: GetterFocusedVars, val setter: SetterFocusedVars)

@Stable
class GetterFocusedVars(
    repo2Client: Repo2Client,
    repoM1ProduitInfos: RepoM1ProduitInfos,
    repo3CouleurProduitInfos: Repo3CouleurProduitInfos,
    repo8BonVent: Repo8BonVent,
    repo9AppCompt: Repo9AppCompt,
    val repo10OperationVentCouleur: Repo10OperationVentCouleur,
) {
    val currentM9AppCompt by derivedStateOf {
        repo9AppCompt.datasValue.firstOrNull { it.bsonObjectId == "b1" }
    }

    val onVentM8BonVent by derivedStateOf {
        val targetKey = repo9AppCompt.currentAppCompt?.onVentM8BonVentKey
        repo8BonVent.datasValue.find { it.keyID == targetKey }
    }

    val defaultM8BonVent by derivedStateOf {
        M8BonVent(
            nomClientConcerned = "Default Data",
            parentKeyId9AppComptInfos = ParametresAppComptNonSaved().keyIdId9AppComptInfos,
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

    val ouvertDialogChoixQuantityPourProduitM1ProduitInfos by derivedStateOf {
        repoM1ProduitInfos.datasValue.find {
            it.keyID == (currentM9AppCompt?.dialogChoisireQuantityM1ProduitInfosKeyID ?: "")
        }
    }

    val onVentM3CouleurProduitInfos by derivedStateOf {
        val targetKey = repo9AppCompt.currentAppCompt?.onVentM3CouleurProduitInfosKeyID
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
            }

            return map.entries.foldIndexed(this) { index, modifier, (key, value) ->
                modifier.getSemanticsTag(key, value, index+6)
            }
        }
    }
}

object DebugsTests {
    const val TAG = "DebugsTests"

    @SuppressLint("ModifierFactoryUnreferencedReceiver")
    fun Modifier.getSemanticsTag(nomVal: String, data: Any?, index: Int = 0): Modifier {
        log(nomVal, index, data)

        return this.semantics(mergeDescendants = true) {
            set(SemanticsPropertyKey("${index + 1} TagDebug == [$nomVal]"), data)
        }
    }

    private fun log(nomVal: String, index: Int, data: Any?) {
        val logTag = "Debug_${nomVal}_${index + 1}"
        val dataString = when (data) {
            null -> "null"
            is String -> data
            is Number -> data.toString()
            is Boolean -> data.toString()
            else -> data.toString()
        }

        Log.d(TAG, "[$logTag] $nomVal = $dataString")
    }


    @Composable
    fun DebugTestsPerformInitialSearch(
        enabled: Boolean,
        focusRequester: FocusRequester,
        onSearchQueryChange: (String) -> Unit
    ) {
        LaunchedEffect(enabled) {
            if (enabled) {
                delay(2000)
                onSearchQueryChange("liya")
                focusRequester.requestFocus()
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

    fun closeDialogChoisireQuantity(
    ) = focuceOnVentM3CouleurProduitInfos(
        getterFocusedVars = getterFocusedVars,
        repo9AppCompt = repo9AppCompt,
    )

    
    fun ouvrireDialogChoisireQuantity(
        m10OperationVentCouleur: M10OperationVentCouleur
    ) = focuceOnVentM3CouleurProduitInfos(
        m10OperationVentCouleur =m10OperationVentCouleur,
        getterFocusedVars = getterFocusedVars,
        repo9AppCompt = repo9AppCompt,
    )

    fun updateFocuseM9AppCompt(data: Z_AppCompt) = repo9AppCompt.upsert(data)
    
    fun ouvrireM1ProduitDialogChoisireQuantityFacade(produit: ArticlesBasesStatsTable) = updateCurrentAppComptDialogProduit(
        getterFocusedVars,
        repo9AppCompt,
        produit,)
    
    fun fermeM1ProduitDialogChoisireQuantityFacade() = updateCurrentAppComptDialogProduit( getterFocusedVars, repo9AppCompt,)
}

fun updateCurrentAppComptDialogProduit(
    getterFocusedVars: GetterFocusedVars,
    repo9AppCompt: Repo9AppCompt,
    produit: ArticlesBasesStatsTable?=null
) {
    repo9AppCompt.upsert(
        getterFocusedVars.currentM9AppCompt!!.copy(
            dialogChoisireQuantityM1ProduitInfosKeyID = produit?.keyID ?: "null",
            dialogChoisireQuantityM1ProduitInfosDebugName = produit?.nom ?: "null"
        )
    )
}

fun focuceOnVentM3CouleurProduitInfos(
    m10OperationVentCouleur: M10OperationVentCouleur?=null,
    getterFocusedVars: GetterFocusedVars,
    repo9AppCompt: Repo9AppCompt
) {
    repo9AppCompt.upsert(
        getterFocusedVars.currentM9AppCompt!!.copy(
            onVentM3CouleurProduitDebugInfos = m10OperationVentCouleur?.debugInfos ?: "null",
            onVentM3CouleurProduitInfosKeyID = m10OperationVentCouleur?.keyID ?: "null"
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
