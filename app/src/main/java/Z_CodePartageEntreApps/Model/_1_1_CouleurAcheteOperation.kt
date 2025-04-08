package Z_CodePartageEntreApps.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class _1_1_CouleurAcheteOperation(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    // Section Related Parents Foreign Key IDs
    var couleurId_ParentVID: Long = 0L,
    var parentProduitAchateOperationVID: Long? =null,

    // Section InfosDeBase
    var totaleQuantity: Int = 1,


    ) {

}
