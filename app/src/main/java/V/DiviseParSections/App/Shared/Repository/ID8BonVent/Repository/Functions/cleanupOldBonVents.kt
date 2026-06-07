package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions

import EntreApps.Shared.Models.Relative_Vents.Models.AbdelwahabJomla_Client_Speciale
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import kotlinx.coroutines.launch

fun cleanupOldBonVents(
    repo8BonVent: Repo8BonVent,
    bonVents: List<M8BonVent>
) {
    val typesToKeep = setOf(
        M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit,
        M8BonVent.EtateActuellementEst.Credit,
        M8BonVent.EtateActuellementEst.Versemment,
    )

    val specialClientKeyIDs = AbdelwahabJomla_Client_Speciale.entries
        .map { it.keyID }
        .filter { it.isNotEmpty() }
        .toSet()

    val bonVentsToRemove = bonVents.filter { bonVent ->
        if (bonVent.etateActuellementEst in typesToKeep) {
            return@filter false
        }

        val isSpecialClient = bonVent.parent_M2Client_KeyID in specialClientKeyIDs ||
                bonVent.parent_M2Client_DebugInfos.contains("abdelwahab", ignoreCase = true)

        !isSpecialClient
    }

    if (bonVentsToRemove.isNotEmpty()) {
        repo8BonVent.repoScope.launch {
            bonVentsToRemove.forEach { bonVent ->
                repo8BonVent.delete(bonVent)
            }
        }
    }
}
