package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Jomla_Clients
import kotlinx.coroutines.launch

fun cleanupOldBonVents(repo8BonVent: Repo8BonVent, bonVents: List<M8BonVent>) {
    val typesToKeep = setOf(
        M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit,
        M8BonVent.EtateActuellementEst.Credit,
        M8BonVent.EtateActuellementEst.Versemment,
        M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
    )

    val bonVentsToRemove = bonVents.filter { bonVent ->
        if (bonVent.etateActuellementEst in typesToKeep) {
            return@filter false
        }

        val parentNameContainsAbdelwahab = bonVent.parent_M2Client_DebugInfos
            .contains("abdelwahab", ignoreCase = true)

        val isJomlaClient = bonVent.parent_M2Client_KeyID == Jomla_Clients.ECHATILLANTS_KEY_ID ||
                bonVent.parent_M2Client_KeyID == Jomla_Clients.Au_Command_KEY_ID

        !parentNameContainsAbdelwahab && !isJomlaClient
    }

    if (bonVentsToRemove.isNotEmpty()) {
        repo8BonVent.repoScope.launch {
            bonVentsToRemove.forEach { bonVent ->
                repo8BonVent.delete(bonVent)
            }
        }
    }
}
