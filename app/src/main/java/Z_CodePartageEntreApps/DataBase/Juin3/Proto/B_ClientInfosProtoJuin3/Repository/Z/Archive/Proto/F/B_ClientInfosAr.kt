package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.F

import Z_CodePartageEntreApps.Model.getKeyFireBase
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class B_ClientInfosAr(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String = "Non Difinie",
    val keyFireBase: String = getKeyFireBase(id, nom),

    var cLeDataOuvertDuParentList: Boolean = false,

    val needUpdate: Boolean = true
)
