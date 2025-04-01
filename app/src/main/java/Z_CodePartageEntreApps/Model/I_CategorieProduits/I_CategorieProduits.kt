package Z_CodePartageEntreApps.Model.I_CategorieProduits

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class I_CategorieProduits(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    // Section InfosDeBase
    var nom: String = "Non Defini",
    var groupeParentId: Long = 0L,

    // Section StatuesMutable
    var indexDonsParentList: Long = 0,
    var afficheSonHeader: Boolean = false,
) {
    fun hasRelevantChanges(oldData: I_CategorieProduits, newData: I_CategorieProduits): Boolean {
        return  oldData.nom != newData.nom ||
                oldData.groupeParentId != newData.groupeParentId ||
                oldData.indexDonsParentList != newData.indexDonsParentList ||
                oldData.afficheSonHeader != newData.afficheSonHeader
    }
}
