package Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.Repository.Extension

import Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.SoldArticlesTabelle
import com.google.firebase.database.DataSnapshot

/**
 * Utility class for data synchronization between app models and Firebase
 */
object SyncDataUtils {
    fun syncData(
        data: SoldArticlesTabelle? = null,
        tempTravaille: SoldArticlesTabelle? = null,
        dataSnapshot: DataSnapshot? = null
    ): Any {
        // Convert from Firebase to model
        if (dataSnapshot != null) {
            return dataSnapshot.getValue(SoldArticlesTabelle::class.java) ?: SoldArticlesTabelle()
        }

        // Convert from model to Firebase map
        val sourceData = data ?: tempTravaille ?: SoldArticlesTabelle()
        return mapOf(
            "vid" to sourceData.vid,
            "idArticle" to sourceData.idArticle,
            "nameArticle" to sourceData.nameArticle,
            "clientSoldToItId" to sourceData.clientSoldToItId,
            "nomClient" to sourceData.nomClient,
            "date" to sourceData.date,
            "color1IdPicked" to sourceData.color1IdPicked,
            "color1SoldQuantity" to sourceData.color1SoldQuantity,
            "color2IdPicked" to sourceData.color2IdPicked,
            "color2SoldQuantity" to sourceData.color2SoldQuantity,
            "color3IdPicked" to sourceData.color3IdPicked,
            "color3SoldQuantity" to sourceData.color3SoldQuantity,
            "color4IdPicked" to sourceData.color4IdPicked,
            "color4SoldQuantity" to sourceData.color4SoldQuantity,
            "confimed" to sourceData.confimed,
            "colorsAcheterIdsToQuantity" to sourceData.colorsAcheterIdsToQuantity
        )
    }
}
