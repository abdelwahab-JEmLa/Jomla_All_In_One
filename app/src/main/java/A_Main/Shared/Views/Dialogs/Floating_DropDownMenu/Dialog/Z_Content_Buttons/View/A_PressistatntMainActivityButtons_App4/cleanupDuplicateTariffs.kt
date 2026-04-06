package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.A_PressistatntMainActivityButtons_App4

import EntreApps.Shared.Models.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import kotlinx.coroutines.launch

fun cleanupDuplicateTariffs(repo13TarificationInfos: Repo13TarificationInfos, tariffs: List<M13TarificationInfos>) {
   repo13TarificationInfos.repoScope.launch {
       try {
           val grouped = tariffs.groupBy {
               Pair(it.typeChoisi, it.parent_M1Produit_KeyId)
           }

           val toDelete = mutableListOf<M13TarificationInfos>()

           grouped.forEach { (_, tariffGroup) ->
               if (tariffGroup.size > 1) {
                   val sortedByTimestamp = tariffGroup.sortedByDescending {
                       it.dernierTimeTampsSynchronisationAvecFireBase
                   }

                   toDelete.addAll(sortedByTimestamp.drop(1))
               }
           }

           // Delete duplicates from local database and Firebase
           if (toDelete.isNotEmpty()) {
               // Delete from Firebase only
               toDelete.forEach { tariff ->
                   repo13TarificationInfos.dataBaseCreationFactory.delete(tariff)
               }
           }
       } catch (e: Exception) {
           // Log error if needed
       }
   }
}
