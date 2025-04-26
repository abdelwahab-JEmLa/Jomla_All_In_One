package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models

import Z_CodePartageEntreApps.Modules.DatesHandler
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
    var nomClientConcerned: String = "Non Defini",

    // Section InfosDeBase
    var timestamps: Long = DatesHandler().getCurrentTimestamps(),
    var heurDebutInString: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),

    var heurFinInString: String = "Non Defini",

    // Section StatuesMutable
    var cJustPourVoirPanie: Boolean = false,

    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.NON_DEFINI,
) {
    val fireBaseKeyID_1_3_TransactionCommercial: String
        get() {
            val parent = "(${parentVID_1_4_PeriodeVent})"
            val thisVal = "->(${clientAcheteurID}_($nomClientConcerned))"

            val name = if (cJustPourVoirPanie)
                "cJustPourVoirPanie"
            else
                etateActuellementEst.nomArabe

            val autre = "->($name)"

            return "$parent$thisVal$autre"
        }

    @IgnoreExtraProperties
    enum class EtateActuellementEst(val color: Int, val nomArabe: String) {
        NON_DEFINI(android.R.color.white, "غير محدد"),

        ON_MODE_COMMEND_ACTUELLEMENT(android.R.color.holo_green_light, "في طلب"),
        A_COMMANDE_CONFIRME(android.R.color.holo_purple, "تم تاكيد الطلبية"),
        COMMANDE_LIVRAI(android.R.color.holo_blue_dark, "تم أيصال منتجاته"),

        AVEC_MARCHANDISE(android.R.color.holo_blue_bright, "عندو سلعة"),
        A_EVITE(android.R.color.black, "يتجنب"),
        ACHETEUR_NON_DISPO(android.R.color.darker_gray, "الشاري غائب"),
        FERME(android.R.color.darker_gray, "مغلق"),

        ON_MODE_VOIRE_PANIE_ARTICLES(android.R.color.holo_blue_dark, "في معاينة السلة"),

        Cible(android.R.color.holo_red_light, "Cible"),
        CIBLE_PRIORITE_2(android.R.color.holo_orange_dark, "CIBLE_PRIORITE_2"),
        CIBLE_PRIORITE_3(android.R.color.holo_green_light, "CIBLE_PRIORITE_3"),
        CIBLE_POUR_2(android.R.color.holo_blue_dark, "CIBLE_POUR_2"),
    }
}
