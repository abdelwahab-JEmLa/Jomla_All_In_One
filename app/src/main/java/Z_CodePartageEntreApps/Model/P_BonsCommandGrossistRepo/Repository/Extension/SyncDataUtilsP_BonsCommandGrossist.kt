package Z_CodePartageEntreApps.Model.P_BonsCommandGrossistRepo.Repository.Extension

import Z_CodePartageEntreApps.Model.P_BonsCommandGrossist
import com.google.firebase.database.DataSnapshot

/**
 * Utility class for data synchronization between app models and Firebase
 */
object SyncDataUtilsP_BonsCommandGrossist {
    fun syncData(
        data: P_BonsCommandGrossist? = null,
        tempTravaille: P_BonsCommandGrossist? = null,
        dataSnapshot: DataSnapshot? = null
    ): Any {
        // Convert from Firebase to model
        if (dataSnapshot != null) {
            return dataSnapshot.getValue(P_BonsCommandGrossist::class.java) ?: P_BonsCommandGrossist()
        }

        // Convert from model to Firebase map
        val sourceData = data ?: tempTravaille ?: P_BonsCommandGrossist()
        return mapOf(
            "vid" to sourceData.vid,
            "infosDeBase" to mapOf(
                "GrossistChoisiID" to sourceData.infosDeBase.GrossistChoisiID,
                "dateInString" to sourceData.infosDeBase.dateInString
            ),
            "etatesMutable" to mapOf<String, Any>(),
            "produitCommendeIDs" to sourceData.produitCommendeIDs
        )
    }
}
