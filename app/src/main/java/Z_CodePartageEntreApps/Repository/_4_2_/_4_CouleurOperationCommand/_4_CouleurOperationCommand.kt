package Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class _4_CouleurOperationCommand(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    // Section Related Parents Foreign Key IDs
    var couleurIndex_ParentVID: Long = 0L,
    var produitVID_ParentKey: Long? =null,

    // Section InfosDeBase
    var totaleQuantity: Int = 0,

)
