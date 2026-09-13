package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.A_PressistatntMainActivityButtons_App4

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
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
    on_vent_period_key: String,
    clientsById: Map<String, M2Client> = emptyMap(),
    nom_contains_a_evite_de_delete_leur_oeprations: String = "",
    onDone: () -> Unit = {},
) {
    val protectedTerms = nom_contains_a_evite_de_delete_leur_oeprations
        .split(",")
        .map { it.trim().lowercase() }
        .filter { it.isNotEmpty() }
    val jour_a_ne_pas_depasse = 60
    val maintenant = System.currentTimeMillis()
    val jour_a_ne_pas_depasse_millis = jour_a_ne_pas_depasse * 24L * 60L * 60L * 1000L

    val bonVentsToRemove = bonVents.filter { bonVent ->
        // Le client est marqué comme non-supprimable → on ne touche jamais à ses bons.
        val client = clientsById[bonVent.parent_M2Client_KeyID]
        if (client?.its_non_deletable_client_et_trxs == true) return@filter false

        if (bonVent.etateActuellementEst.nonDeletable) {
            val isCreditType = bonVent.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit
            if (isCreditType) return@filter false
            // Non-crédit + nonDeletable : on autorise quand même la suppression
            // si le bon dépasse jour_a_ne_pas_depasse.
            val age = maintenant - bonVent.creationTimestamps
            if (age <= jour_a_ne_pas_depasse_millis) return@filter false
        }

        if (bonVent.parent_M14VentPeriod_KeyId == on_vent_period_key) return@filter false
        val isSpecialClient =
                protectedTerms.any { term -> bonVent.parent_M2Client_DebugInfos.lowercase().contains(term) }
        !isSpecialClient
    }

    if (bonVentsToRemove.isEmpty()) {
        repo8BonVent.repoScope.launch(Dispatchers.Main) { onDone() }
        return
    }

    repo8BonVent.repoScope.launch {
        val nullUpdates: Map<String, Any?> = bonVentsToRemove.associate { it.keyID to null }
        try {
            M8BonVent.ref.updateChildren(nullUpdates).await()
        } catch (e: Exception) {
        }

        bonVentsToRemove.forEachIndexed { index, bonVent ->
            repo8BonVent.delete(bonVent)
        }

        Log.d(TAG, "all deletes done — calling onDone on Main")
        withContext(Dispatchers.Main) { onDone() }
    }
}
