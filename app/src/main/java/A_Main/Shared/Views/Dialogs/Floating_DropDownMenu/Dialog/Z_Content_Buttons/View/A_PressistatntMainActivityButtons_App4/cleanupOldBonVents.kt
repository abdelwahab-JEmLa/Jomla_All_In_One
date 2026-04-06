package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.A_PressistatntMainActivityButtons_App4

import EntreApps.Shared.Models.AbdelwahabJomla_Client_Speciale
import EntreApps.Shared.Models.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "CleanupOldBonVents"

fun cleanupOldBonVents_Np(
    repo8BonVent: Repo8BonVent,
    bonVents: List<M8BonVent>,
    on_vent_key: String,
    onDone: () -> Unit = {},           // ← NEW: called on Main when all deletes finish
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
        if (bonVent.etateActuellementEst in typesToKeep) return@filter false
        if (bonVent.keyID == on_vent_key) return@filter false
        val isSpecialClient = bonVent.parent_M2Client_KeyID in specialClientKeyIDs ||
                bonVent.parent_M2Client_DebugInfos.contains("abdelwahab", ignoreCase = true)
        !isSpecialClient
    }

    if (bonVentsToRemove.isEmpty()) {
        Log.d(TAG, "nothing to delete — firing onDone immediately")
        // Nothing to delete – fire onDone immediately on Main so callers don't hang
        repo8BonVent.repoScope.launch(Dispatchers.Main) { onDone() }
        return
    }

    Log.d(TAG, "will delete ${bonVentsToRemove.size} bon-vents — launching coroutine")
    repo8BonVent.repoScope.launch {
        bonVentsToRemove.forEachIndexed { index, bonVent ->
            Log.d(TAG, "  deleting [${index + 1}/${bonVentsToRemove.size}] keyID=${bonVent.keyID}")
            repo8BonVent.delete(bonVent)
        }
        Log.d(TAG, "all deletes done — calling onDone on Main")
        withContext(Dispatchers.Main) { onDone() }
    }
}
