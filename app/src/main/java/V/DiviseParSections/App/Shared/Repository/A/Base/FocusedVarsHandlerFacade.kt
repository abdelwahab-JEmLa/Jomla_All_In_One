package V.DiviseParSections.App.Shared.Repository.A.Base

import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.semantics.SemanticsPropertyKey

class FocusedVarsHandlerFacade(val getter: GetterFocusedVars, val setter: SetterFocusedVars)

@Stable
class GetterFocusedVars(
    repo8BonVent: Repo8BonVent,
    repo9AppCompt: Repo9AppCompt,
) {
    val currentM9AppCompt by derivedStateOf { repo9AppCompt.datasValue.firstOrNull { it.bsonObjectId == "b1" } }

    val onVentId8BonVent by derivedStateOf {
        repo8BonVent.datasValue.find {
            it.keyID == repo9AppCompt.currentAppCompt?.onVentM8BonVentKey
        } ?: defaultId8BonVent
    }

    val defaultId8BonVent by derivedStateOf {
        GBonVent(
            nomClientConcerned = "Default Data",
            parentKeyId9AppComptInfos = ParametresAppComptNonSaved().keyIdId9AppComptInfos,
            parentDebugNameId9AppComptInfos = ParametresAppComptNonSaved().debugNameId9AppComptInfos,

            parentKeyId7VentPeriod = ParametresAppComptNonSaved().keyIdId7VentPeriod,
            parentDebugNameId7VentPeriod = ParametresAppComptNonSaved().debugNameId7VentPeriod,
        )
    }

    fun getSemantics_defaultId8BonVent(): Pair<SemanticsKeys, SemanticsValues> {
        val semanticsKeys = SemanticsKeys(
            m8Key = SemanticsPropertyKey("DebugID1=defaultId8BonVent"),
            m9Key = SemanticsPropertyKey("DebugID1=currentM9AppCompt")
        )

        val semanticsValues = SemanticsValues(
            m8Value = defaultId8BonVent,
            m9Value = currentM9AppCompt
        )

        return Pair(semanticsKeys, semanticsValues)
    }

    data class SemanticsKeys(
        val m8Key: SemanticsPropertyKey<GBonVent>,
        val m9Key: SemanticsPropertyKey<Z_AppCompt?>
    )

    data class SemanticsValues(
        val m8Value: GBonVent,
        val m9Value: Z_AppCompt?
    )
}


@Stable
class SetterFocusedVars(
    val id8BonVentRepository: Repo8BonVent,
    val id9AppComptRepository: Repo9AppCompt,
) {
    fun focuceAddNewM8BonVent(id8BonVent: GBonVent) = ajoutCopyDefaultBonVentEtFocuceLeAuAppCompt(id8BonVent, id8BonVentRepository, id9AppComptRepository)
}

fun ajoutCopyDefaultBonVentEtFocuceLeAuAppCompt(
    id8BonVent: GBonVent,
    id8BonVentRepository: Repo8BonVent,
    id9AppComptRepository: Repo9AppCompt
) {
    val currentAppCompt = id9AppComptRepository.currentAppCompt

    val newData = id8BonVent.copy(creationTimestamps = System.currentTimeMillis())
    id8BonVentRepository.add(newData)
    focuceBonVentAuAppCompt(currentAppCompt, newData, id9AppComptRepository)
}

private fun focuceBonVentAuAppCompt(
    currentAppCompt: Z_AppCompt?,
    newData: GBonVent,
    id9AppComptRepository: Repo9AppCompt
) {
    currentAppCompt?.copy(
        onVentM8BonVentKey = newData.keyID,
        onVentM8BonVentDebugInfos = newData.dataDebugInfos,
    )?.let {
        id9AppComptRepository.upsert(
            it
        )
    }
}
