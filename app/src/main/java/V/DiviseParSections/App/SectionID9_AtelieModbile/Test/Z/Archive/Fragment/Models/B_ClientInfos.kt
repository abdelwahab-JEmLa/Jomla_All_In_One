package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.getKeyFireBase
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
