package V.DiviseParSections.App.Shared.Repository.A.Base

import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.Repo3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import android.annotation.SuppressLint
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
        GBonVent(
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

    val onVentM3CouleurProduitInfos by derivedStateOf {
        val targetKey = repo9AppCompt.currentAppCompt?.onVentM3CouleurProduitInfosKeyID
        repo10OperationVentCouleur.datasValue.find { it.keyID == targetKey }
    }

    val defaultM3CouleurProduitInfos by derivedStateOf {
        onVentM8BonVent?.let {
            with(it) {
                FCouleurVentOperationInfos(
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

    fun getDatasM10OperationVentCouleurPourProduit(
        produit: ArticlesBasesStatsTable,
    ): List<FCouleurVentOperationInfos> {
        val currentBonVentKey = onVentM8BonVent?.keyID

        val allOperations = run {
            val repo = repo10OperationVentCouleur
            repo.datasValue
        }


        val operationsForThisProduct = allOperations.filter { operation ->
            val matchesProduct = operation.parentM1ProduitInfosKeyId == produit.keyID
            val matchesBonVent = operation.parentM8BonVentKeyId == currentBonVentKey ||
                    operation.parentM8BonVentKeyId.isEmpty()
            matchesProduct && matchesBonVent
        }
        return operationsForThisProduct
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
                modifier.getSemanticsTag(key, value, index)
            }
        }
    }
}
object DebugsTests {
    const val TAG = "DebugsTests"

    @SuppressLint("ModifierFactoryUnreferencedReceiver")
    fun Modifier.getSemanticsTag(nomVal: String, data: Any?, index: Int = 0): Modifier {
        val logTag = "Debug_${nomVal}_${index + 1}"
        val dataString = when (data) {
            null -> "null"
            is String -> data
            is Number -> data.toString()
            is Boolean -> data.toString()
            else -> data.toString().take(100) // Limit length for logging
        }

        // Log the data for debugging purposes
        android.util.Log.d(TAG, "[$logTag] $nomVal = $dataString")

        return this.semantics(mergeDescendants = true) {
            // Fix: Use dataString instead of data to ensure "null" is displayed when data is null
            set(SemanticsPropertyKey("${index + 1} TagDebug == [$nomVal]"), data)
        }
    }

    var shouldPerformInitialSearch = true

    @Composable
    fun DebugTestsPerformInitialSearch(
        enabled: Boolean,
        onSearchQueryChange: (String) -> Unit,
        focusRequester: FocusRequester
    ) {
        LaunchedEffect(enabled) {
            if (enabled) {
                delay(2000)
                onSearchQueryChange("sor")
                focusRequester.requestFocus()
                shouldPerformInitialSearch = false
            }
        }
    }
}

@Stable
class SetterFocusedVars(
    val Repo2Client: Repo2Client,
    val repo8BonVent: Repo8BonVent,
    val repo9AppCompt: Repo9AppCompt,
) {
    fun addNewM8BonVent(id8BonVent: GBonVent) =
        ajoutCopyDefaultBonVentEtFocuceLeAuAppCompt(id8BonVent, repo8BonVent)

    fun updateM9AppCompt(data: Z_AppCompt) = repo9AppCompt.upsert(data)
    fun addNewM2ClientInfos(newClient: HClientInfos) = Repo2Client.addClient(newClient)
}

fun ajoutCopyDefaultBonVentEtFocuceLeAuAppCompt(
    id8BonVent: GBonVent,
    id8BonVentRepository: Repo8BonVent,
) {
    val newData = id8BonVent.copy(creationTimestamps = System.currentTimeMillis())
    id8BonVentRepository.add(newData)
}
