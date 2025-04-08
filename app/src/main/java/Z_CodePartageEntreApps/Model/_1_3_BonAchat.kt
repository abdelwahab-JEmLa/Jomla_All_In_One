package Z_CodePartageEntreApps.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity
data class _1_3_BonAchat(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    // Section Related Parents Foreign Key IDs
    var parent_1_4_PeriodeVentVid: Long = 0L,
    var clientAcheteurID: Long = 0L,

    // Section InfosDeBase
    var heurDebutInString: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    var heurFinInString: String = "Non Defini",

    // Section StatuesMutable
    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.ENTRE_MAIS_PAS_CONFIRME,

    ) {
    enum class EtateActuellementEst {
        ENTRE_MAIS_PAS_CONFIRME,
        CONFIRME,
        NA_PAS_COMMANDE,
    }


}
