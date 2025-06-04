package Z_CodePartageEntreApps.Proto.Par.Type.Models

import Z_CodePartageEntreApps.Model.getKeyFireBase
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
