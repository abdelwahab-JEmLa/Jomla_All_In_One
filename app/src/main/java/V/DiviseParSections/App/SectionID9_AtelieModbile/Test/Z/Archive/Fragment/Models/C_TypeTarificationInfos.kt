package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Main.B.Models.getKeyFireBase
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class C_TypeTarificationInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entityCorrespond: TypeTarificationEnum = TypeTarificationEnum.ParBenifice,
    val nom: String= entityCorrespond.name,
    val keyFireBase: String = getKeyFireBase(id, nom),

    val needUpdate: Boolean = true
)
