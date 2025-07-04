package V.DiviseParSections.App.Shared.Repository.A.Base

import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
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
    Repo2Client: Repo2Client,
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

    val onVentId2ClientInfos by derivedStateOf {
        Repo2Client.datasValue.find {
            it.keyID ==
                    onVentId8BonVent.parentM2ClientInfosKey
        }
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
    val repo8BonVent: Repo8BonVent,
    val repo9AppCompt: Repo9AppCompt,
) {
    fun addNewM8BonVent(id8BonVent: GBonVent) = ajoutCopyDefaultBonVentEtFocuceLeAuAppCompt(id8BonVent, repo8BonVent)
    fun updateM9AppCompt(data: Z_AppCompt) = repo9AppCompt.upsert(data)
}

fun ajoutCopyDefaultBonVentEtFocuceLeAuAppCompt(
    id8BonVent: GBonVent,
    id8BonVentRepository: Repo8BonVent,
) {
    val newData = id8BonVent.copy(creationTimestamps = System.currentTimeMillis())
    id8BonVentRepository.add(newData)
}
