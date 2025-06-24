package Z_CodePartageEntreApps.Model.C_GrossistsDataBaseRepository.Extension

import Z_CodePartageEntreApps.Model.C_GrossistsDataBase
import com.google.firebase.database.DataSnapshot

/**
 * Utility class for data synchronization between app models and Firebase
 */
object SyncDataUtilsC_GrossistsDataBase {
    fun syncData(
        data: C_GrossistsDataBase? = null,
        tempTravaille: C_GrossistsDataBase? = null,
        dataSnapshot: DataSnapshot? = null
    ): Any {
        // Convert from Firebase to model
        if (dataSnapshot != null) {
            return dataSnapshot.getValue(C_GrossistsDataBase::class.java) ?: C_GrossistsDataBase()
        }

        // Convert from model to Firebase map
        val sourceData = data ?: tempTravaille ?: C_GrossistsDataBase()
        return mapOf(
            "keyID" to sourceData.id,
            "nom" to sourceData.nom,
            "statueDeBase" to mapOf(
                "couleur" to sourceData.statueDeBase.couleur,
                "itIndexInParentList" to sourceData.statueDeBase.itIndexInParentList,
                "caRefDonAncienDataBase" to sourceData.statueDeBase.caRefDonAncienDataBase,
                "cUnClientTemporaire" to sourceData.statueDeBase.cUnClientTemporaire,
                "auFilterFAB" to sourceData.statueDeBase.auFilterFAB,
                "actuelleEtat" to sourceData.statueDeBase.actuelleEtat
            )
        )
    }
}
