package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import kotlinx.coroutines.launch

fun cleanupOldBonVents(repo8BonVent: Repo8BonVent, bonVents: List<M8BonVent>) {
   val typesToKeep = setOf(
       M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit,
       M8BonVent.EtateActuellementEst.Credit,
       M8BonVent.EtateActuellementEst.Versemment,
       M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
   )

   val bonVentsToRemove = bonVents.filter { bonVent ->
       bonVent.etateActuellementEst !in typesToKeep
   }

   if (bonVentsToRemove.isNotEmpty()) {
       repo8BonVent.repoScope.launch {
           bonVentsToRemove.forEach { bonVent ->
               repo8BonVent.delete(bonVent)
           }
       }
   }
}
