package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity
data class _1_3_TransactionCommercial(
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

    var fireBaseKeyID: String = "$parentVID_1_4_PeriodeVent->(${clientAcheteurID}->($etateActuellementEst))",

    ) {

    @IgnoreExtraProperties
    enum class EtateActuellementEst(val color: Int, val nomArabe: String) {
        NON_DEFINI(android.R.color.white, "غير محدد"),
        ON_MODE_COMMEND_ACTUELLEMENT(android.R.color.holo_green_light, ""),
        A_COMMANDE_CONFIRME(android.R.color.holo_purple, "تم البيع له"),
        AVEC_MARCHANDISE(android.R.color.holo_blue_light, "عندو سلعة"),
        A_EVITE(android.R.color.black, "يتجنب"),
        COMMANDE_LENCE(android.R.color.holo_green_light, "في طلب"),
        ACHETEUR_NON_DISPO(android.R.color.darker_gray, "الشاري غائب"),
        FERME(android.R.color.darker_gray, "مغلق"),
    }
}
