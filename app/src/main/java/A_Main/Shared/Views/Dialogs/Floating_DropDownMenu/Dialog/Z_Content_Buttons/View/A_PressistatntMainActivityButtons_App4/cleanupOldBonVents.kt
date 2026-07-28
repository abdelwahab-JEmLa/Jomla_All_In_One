package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.A_PressistatntMainActivityButtons_App4

import EntreApps.Shared.Models.Relative_Vents.Models.AbdelwahabJomla_Client_Speciale
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "CleanupOldBonVents"

fun cleanupOldBonVents_Np(
    repo8BonVent: Repo8BonVent,
    bonVents: List<M8BonVent>,
    on_vent_key: String,
    nom_contains_a_evite_de_delete_leur_oeprations: String = "",
    onDone: () -> Unit = {},
) {
    val specialClientKeyIDs = AbdelwahabJomla_Client_Speciale.entries
        .map { it.keyID }
        .filter { it.isNotEmpty() }
        .toSet()

    // For each client, find the key of their most recent bon vent (by creationTimestamps).
    val lastBonVentKeyPerClient: Set<String> = bonVents
        .groupBy { it.parent_M2Client_KeyID }
        .values
        .mapNotNull { clientBons -> clientBons.maxByOrNull { it.creationTimestamps }?.keyID }
        .toSet()
    Log.d(TAG, "lastBonVentKeyPerClient count=${lastBonVentKeyPerClient.size}")

    val protectedTerms = nom_contains_a_evite_de_delete_leur_oeprations
        .split(",")
        .map { it.trim().lowercase() }
        .filter { it.isNotEmpty() }

    val bonVentsToRemove = bonVents.filter { bonVent ->
        if (bonVent.etateActuellementEst.nonDeletable) return@filter false
        if (bonVent.keyID == on_vent_key) return@filter false
        if (bonVent.keyID in lastBonVentKeyPerClient) return@filter false
        val isSpecialClient = bonVent.parent_M2Client_KeyID in specialClientKeyIDs ||
                bonVent.parent_M2Client_DebugInfos.contains("abdelwahab", ignoreCase = true) ||
                protectedTerms.any { term -> bonVent.parent_M2Client_DebugInfos.lowercase().contains(term) }
        !isSpecialClient
    }

    if (bonVentsToRemove.isEmpty()) {
        Log.d(TAG, "nothing to delete — firing onDone immediately")
        repo8BonVent.repoScope.launch(Dispatchers.Main) { onDone() }
        return
    }

    Log.d(TAG, "will delete ${bonVentsToRemove.size} bon-vents — launching coroutine")
    repo8BonVent.repoScope.launch {
        // Batch all Firebase removes in one multi-path updateChildren call (null = delete).
        // This replaces N individual removeValue() round-trips with a single atomic write.
        val nullUpdates: Map<String, Any?> = bonVentsToRemove.associate { it.keyID to null }
        try {
            M8BonVent.ref.updateChildren(nullUpdates).await()
            Log.d(TAG, "batch Firebase delete confirmed for ${bonVentsToRemove.size} items")
        } catch (e: Exception) {
            Log.e(TAG, "batch Firebase delete failed: ${e.message}", e)
        }

        // Room deletes are sequential (no multi-key delete in Room without a custom DAO query).
        // The repo.delete() Firebase leg is a harmless no-op since the node is already removed.
        bonVentsToRemove.forEachIndexed { index, bonVent ->
            Log.d(TAG, "  local delete [${index + 1}/${bonVentsToRemove.size}] keyID=${bonVent.keyID}")
            repo8BonVent.delete(bonVent)
        }

        Log.d(TAG, "all deletes done — calling onDone on Main")
        withContext(Dispatchers.Main) { onDone() }
    }
}
