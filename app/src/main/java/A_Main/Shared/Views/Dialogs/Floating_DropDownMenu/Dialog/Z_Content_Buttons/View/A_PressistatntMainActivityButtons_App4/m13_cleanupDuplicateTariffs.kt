package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.A_PressistatntMainActivityButtons_App4

import EntreApps.Shared.Models.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

fun cleanupDuplicateTariffs(
    repo13TarificationInfos: Repo13TarificationInfos,
    tariffs: List<M13TarificationInfos>
) {
    repo13TarificationInfos.repoScope.launch {
        try {
            val grouped = tariffs.groupBy {
                Pair(it.typeChoisi, it.parent_M1Produit_KeyId)
            }

            val toMove = mutableListOf<M13TarificationInfos>()

            grouped.forEach { (_, tariffGroup) ->
                if (tariffGroup.size > 1) {
                    // Keep the most recently synchronised entry; move the rest to non-active
                    val sortedByTimestamp = tariffGroup.sortedByDescending {
                        it.dernierTimeTampsSynchronisationAvecFireBase
                    }
                    toMove.addAll(sortedByTimestamp.drop(1))
                }
            }

            if (toMove.isNotEmpty()) {
                toMove.forEach { tariff ->
                    // Move to non-active node first, then remove from active ref
                    M13TarificationInfos.ref_NonActiveDatas
                        .child(tariff.keyID)
                        .setValue(tariff.toFirebaseMap())
                        .await()

                    M13TarificationInfos.ref
                        .child(tariff.keyID)
                        .removeValue()
                        .await()

                    // Keep local DB in sync
                    repo13TarificationInfos.dataBaseCreationFactory.delete(tariff)
                }
            }
        } catch (e: Exception) {
            // Log error if needed
        }
    }
}
