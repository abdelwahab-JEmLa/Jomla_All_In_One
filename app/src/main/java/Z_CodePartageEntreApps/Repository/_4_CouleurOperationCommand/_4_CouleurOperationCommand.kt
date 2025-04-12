package Z_CodePartageEntreApps.Repository._4_CouleurOperationCommand

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class _4_CouleurOperationCommand(
    @PrimaryKey(autoGenerate = true)

    // Section Related Parents Foreign Key IDs
    var couleurIndex_ParentVID: Long = 0L,
    var produitVID_ParentKey: Long? =null,

    // Section InfosDeBase
    var totaleQuantity: Int = 0,

    ) {

}
