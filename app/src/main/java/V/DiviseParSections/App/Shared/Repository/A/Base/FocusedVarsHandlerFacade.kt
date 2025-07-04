package V.DiviseParSections.App.Shared.Repository.A.Base

import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

class FocusedVarsHandlerFacade(val getter: GetterFocusedVars, val setter: SetterFocusedVars, )

@Stable
class GetterFocusedVars(
    repo8BonVent: Repo8BonVent,
    repo9AppCompt: Repo9AppCompt,
) {
    val currentAppCompt by derivedStateOf { repo9AppCompt.datasValue.firstOrNull { it.bsonObjectId == "b1" } }

    val onVentId8BonVent by derivedStateOf {
        repo8BonVent.datasValue.find {
            it.keyID == repo9AppCompt.currentAppCompt?.id8BonVentonVentKey
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
}

@Stable
class SetterFocusedVars(
    val id8BonVentRepository: Repo8BonVent,
    val id9AppComptRepository: Repo9AppCompt,
) {
    fun focuceM8BonVent(id8BonVent: GBonVent) = ajoutCopyDefaultBonVentEtFocuceLeAuAppCompt(id8BonVent, id8BonVentRepository, id9AppComptRepository)
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
        id8BonVentonVentKey = newData.keyID,
        id8BonVentDebugNameKey = newData.keyID,
    )?.let {
        id9AppComptRepository.upsert(
            it
        )
    }
}
