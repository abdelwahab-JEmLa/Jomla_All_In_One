package Z_CodePartageEntreApps.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "_1_3_BonAchat")
data class _1_3_BonAchat(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var clientAchteurID: Long = 0L,
    // Section Related Parents Foreign Key IDs
    var parent_1_4_PeriodeVent: Long = 0L,

    // Section InfosDeBase
    var heurDebutInString: String = "00:00",
    var heurFinInString: String = "00:00",

    // Section StatuesMutable

)
