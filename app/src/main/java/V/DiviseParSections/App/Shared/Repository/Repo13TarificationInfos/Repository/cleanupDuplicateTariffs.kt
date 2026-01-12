package V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository

import kotlinx.coroutines.launch

fun cleanupDuplicateTariffs(repo13TarificationInfos: Repo13TarificationInfos, tariffs: List<M13TarificationInfos>) {
   repo13TarificationInfos.repoScope.launch {
       try {
           // Group by TypeChoisi and parent_M1Produit_KeyId
           val grouped = tariffs.groupBy {
               Pair(it.typeChoisi, it.parent_M1Produit_KeyId)
           }

           val toDelete = mutableListOf<M13TarificationInfos>()

           // For each group, keep only the one with the latest timestamp
           grouped.forEach { (_, tariffGroup) ->
               if (tariffGroup.size > 1) {
                   // Sort by timestamp descending to get the most recent first
                   val sortedByTimestamp = tariffGroup.sortedByDescending {
                       it.dernierTimeTampsSynchronisationAvecFireBase
                   }

                   // Add all except the first (most recent) to deletion list
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
