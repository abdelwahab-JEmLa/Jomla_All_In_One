package Z_CodePartageEntreApps.Model.Z.Archive.ApreAlleAuSql

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.IgnoreExtraProperties

class I_CategoriesProduitsAncien(
    var id: Long = 0,
    var infosDeBase: InfosDeBase = InfosDeBase(),
    var statuesMutable: StatuesMutable = StatuesMutable(),
) {
    @IgnoreExtraProperties
    class InfosDeBase(
        var nom: String = "Non Defini",
    )  {
        var groupeParentId by mutableLongStateOf(0L)
    }

    @IgnoreExtraProperties
    class StatuesMutable(
        var indexDonsParentList: Long = 0,

    ) {
        var afficheSonHeader by mutableStateOf(false)
    }
    companion object {

        fun syncData(
            data: I_CategoriesProduitsAncien? = null,
            dataSnapshot: DataSnapshot? = null
        ): Any {
            // Convert from Firebase to model
            if (dataSnapshot != null) {
                return dataSnapshot.getValue(I_CategoriesProduitsAncien::class.java) ?: I_CategoriesProduitsAncien()
            }

            // Convert from model to Firebase map
            val sourceData = data ?: I_CategoriesProduitsAncien()
            return mapOf(
                "keyID" to sourceData.id,
                "infosDeBase" to mapOf(
                    "nom" to sourceData.infosDeBase.nom,
                    "groupeParentId" to sourceData.infosDeBase.groupeParentId
                ),
                "statuesMutable" to mapOf(
                    "indexDonsParentList" to sourceData.statuesMutable.indexDonsParentList,
                    "afficheSonHeader" to sourceData.statuesMutable.afficheSonHeader
                )
            )
        }
    }

}
