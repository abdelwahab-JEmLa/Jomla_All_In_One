package V.DiviseParSections.App.Shared.Repository

import Z_CodePartageEntreApps.Model.getKeyFireBase
import Z_CodePartageEntreApps.Proto.Par.Type.Models.TypeTarificationEnum
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
