package V.DiviseParSections.App.SectionID9_AtelieModbile.FragID2.Test

import V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.Function.getStrDateTime
import V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.getKeyFireBase
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.PrimaryKey
import com.example.clientjetpack.R
import com.google.firebase.database.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class D_TarificationInfosT2(
    @PrimaryKey
    val id: Long = 0L,
    //Forging IDs
    val idParentProduit: Long = 0L,
    val typeTarificationEnumT2Correspond: TypeTarificationEnumT2 = TypeTarificationEnumT2.PRIX_BASE,
    val idParentBonAchat: Long = 0L,

    //Base Infos
    val prixCurrency: Double = 0.0,
    val timestamps: Long = 0,
    val nom: String = getStrDateTime(id),

    //keyFireBase
    val keyFireBase: String = getKeyFireBase(dataNom = nom),

    //Etates Mutable
    val needUpdate: Boolean = true
)

fun getKeyFireBase(
    dataId: Long? = null,
    dataNom: String? = null
): String {
    return if (dataId != null) {
        "-<$dataId($dataNom)"
    } else {
        "-<$dataNom"
    }
}

enum class TypeTarificationEnumT2(
    val iconVector: ImageVector? = null,
    val couleur: Color = Color.White,
    val nomArabe: String ="",
) {
    LeMaxPrixArrive(Icons.Filled.ArrowUpward, Color(0xFFFF9800),"فائدة محققة مع لاضا كثير من الزيناء"),
    AU_GERANT(Icons.Filled.Done, Color(0xFF4CAF50),"التقدير للمدير "),
    DEFINI(Icons.Filled.Edit, Color(0xFFFFEB3B),"المحدد من المدير بنصرف "),
    Historique(Icons.Filled.History, Color(0xFF2196F3),"السعر الذي وصلنا له"),
    PRIX_BASE(Icons.Filled.EditOff, Color(0xFFF44336),"الفايدة ابتداءا تكاد تكون معدومة ")
}

data class BonAchatT2(
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

    var cActive: Boolean = false,

    var cJustPourVoirPanie: Boolean = false,
    var ouvert: Boolean = false,

    var vocaleKeyID: String = "",
    var sonVocaleEstEcoute: Boolean = false,
    var sonEcoutementEstFaitAutimestamps: Long = 0,

    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.NON_DEFINI,
) {
    val fireBaseKeyID_1_3_TransactionCommercial: String
        get() {
            val parent = "(${parentVID_1_4_PeriodeVent})"
            val thisVal = "->(${clientAcheteurID}_($nomClientConcerned))"

            val name = if (cJustPourVoirPanie)
                "PourVoirPanie"
            else
                etateActuellementEst.nomArabe

            val autre = "->($name)"

            return "$parent$thisVal$autre"
        }

    @IgnoreExtraProperties
    enum class EtateActuellementEst(val color: Int, val nomArabe: String) {
        NON_DEFINI(android.R.color.white, "غير محدد"),

        ON_MODE_COMMEND_ACTUELLEMENT(android.R.color.holo_green_light, "تم تنفيذ المطلوب في "),
        A_COMMANDE_CONFIRME(android.R.color.holo_purple, "تم تاكيد الطلبية"),
        PourVoirPanie(android.R.color.holo_red_light, "للنظر"),
        COMMANDE_LIVRAI(android.R.color.holo_blue_dark, "تم أيصال منتجاته"),

        AVEC_MARCHANDISE(R.color.couleur1, "عندو سلعة"),
        ACHETEUR_NON_DISPO(R.color.c2, "الشاري غائب"),
        FERME(android.R.color.darker_gray, "مغلق"),

        A_EVITE(android.R.color.black, "اقترح ان يتجنب لمدة اسبوعين"),

        RAPPORT_AU_ENREGESTREMENT_VOCALE(android.R.color.black, "التقرير قي التسجيل الصوتي "),

        ON_MODE_VOIRE_PANIE_ARTICLES(android.R.color.holo_blue_dark, "في معاينة السلة"),

        Cible(android.R.color.holo_red_light, "Cible"),
        CIBLE_PRIORITE_2(android.R.color.holo_orange_dark, "CIBLE_PRIORITE_2"),
        CIBLE_PRIORITE_3(android.R.color.holo_green_light, "CIBLE_PRIORITE_3"),
        CIBLE_POUR_2(android.R.color.holo_blue_dark, "CIBLE_POUR_2"),
    }
}
