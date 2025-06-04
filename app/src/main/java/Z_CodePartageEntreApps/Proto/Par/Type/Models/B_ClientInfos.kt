package Z_CodePartageEntreApps.Proto.Par.Type.Models

import Z_CodePartageEntreApps.Model.getKeyFireBase
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class B_ClientInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String = "Non Difinie",
    val keyFireBase: String = getKeyFireBase(id, nom),

    var cLeDataOuvertDuParentList: Boolean = false,

    val needUpdate: Boolean = true
)
