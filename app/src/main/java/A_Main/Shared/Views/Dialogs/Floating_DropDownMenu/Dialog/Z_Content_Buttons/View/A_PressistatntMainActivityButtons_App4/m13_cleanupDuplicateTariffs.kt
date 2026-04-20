package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.A_PressistatntMainActivityButtons_App4

import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
fun cleanupDuplicateTariffs(
    repo13TarificationInfos: Repo13TarificationInfos,
    tariffs: List<M13TarificationInfos>,
    onDone: () -> Unit = {},
) {
    repo13TarificationInfos.repoScope.launch {
        try {
            val toMove = tariffs
                .groupBy { Pair(it.typeChoisi, it.parent_M1Produit_KeyId) }
                .values
                .filter { it.size > 1 }
                .flatMap { group ->
                    group.sortedByDescending {
                        it.dernierTimeTampsSynchronisationAvecFireBase
                    }.drop(1)   // keep the newest; move the rest
                }

            if (toMove.isNotEmpty()) {
                // Batch write duplicates to the non-active node
                val nonActiveUpdates: Map<String, Any> =
                    toMove.associate { it.keyID to it.toFirebaseMap() }
                M13TarificationInfos.ref_NonActiveDatas
                    .updateChildren(nonActiveUpdates).await()

                // Batch delete from the active node
                val nullUpdates: Map<String, Any?> = toMove.associate { it.keyID to null }
                M13TarificationInfos.ref.updateChildren(nullUpdates).await()

                // Local Room deletes (no bulk-delete DAO, so still sequential)
                toMove.forEach { repo13TarificationInfos.dataBaseCreationFactory.delete(it) }
            }
        } catch (_: Exception) { }

        withContext(Dispatchers.Main) { onDone() }
    }
}
