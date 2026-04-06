package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.A_PressistatntMainActivityButtons_App4

import EntreApps.Shared.Models.AbdelwahabJomla_Client_Speciale
import EntreApps.Shared.Models.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import kotlinx.coroutines.launch

fun cleanupOldBonVents_Np(
    repo8BonVent: Repo8BonVent,
    bonVents: List<M8BonVent>,
    on_vent_key: String
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
        // Keep: type is protected
        if (bonVent.etateActuellementEst in typesToKeep) return@filter false

        // Keep: this IS the currently active on_vent BonVent
        if (bonVent.keyID == on_vent_key) return@filter false

        // Keep: special client
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
