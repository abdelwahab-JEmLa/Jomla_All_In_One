package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.getKeyFireBase
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class A_ProduitInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String = "",
    val keyFireBase: String = getKeyFireBase(id, nom),

    val needUpdate: Boolean = true
)
