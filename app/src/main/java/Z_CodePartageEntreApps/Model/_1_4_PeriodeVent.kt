package Z_CodePartageEntreApps.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "_1_4_PeriodeVent")
data class _1_4_PeriodeVent(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    // Section InfosDeBase
    var startDateInString: String = "2025-01-01",
    var endDateInString: String = "2025-01-01",

    // Section StatuesMutable

)
