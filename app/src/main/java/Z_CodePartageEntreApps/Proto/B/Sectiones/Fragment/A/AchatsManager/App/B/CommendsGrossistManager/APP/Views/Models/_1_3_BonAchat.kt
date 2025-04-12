package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views.Models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity
data class _1_3_BonAchat(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    // Section Related Parents Foreign Key IDs
    var parentVID_1_4_PeriodeVent: Long = 0L,
    var clientAcheteurID: Long = 0L,

    // Section InfosDeBase
    var heurDebutInString: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    var heurFinInString: String = "Non Defini",

    // Section StatuesMutable
    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,

    ) {
    @IgnoreExtraProperties
    enum class EtateActuellementEst(val color: Int, val nomArabe: String) {
        ON_MODE_COMMEND_ACTUELLEMENT(android.R.color.holo_green_light, ""),
        NON_DEFINI(android.R.color.white, "غير محدد"),
        A_COMMANDE(android.R.color.holo_red_light, ""),
    }
}
