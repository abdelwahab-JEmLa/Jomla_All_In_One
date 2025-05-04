package com.example.clientjetpack.Repositorys

import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.clientjetpack.R
import com.google.firebase.database.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun createTestTransactions(): List<TransactionCommercial> {
    val testTransactions = ArrayList<TransactionCommercial>()

    // Set timestamps for different days
    val calendar = Calendar.getInstance()

    // First transaction: today at 1 PM
    calendar.set(Calendar.HOUR_OF_DAY, 13) // 1 PM
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val todayTimestamp = calendar.timeInMillis

    // Second transaction: yesterday at 3 PM
    calendar.add(Calendar.DAY_OF_MONTH, -1)
    calendar.set(Calendar.HOUR_OF_DAY, 15) // 3 PM
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val yesterdayTimestamp = calendar.timeInMillis


    // Add a COMMANDE_LIVRAI transaction
    testTransactions.add(
        TransactionCommercial(
            vid = 1L,
            etateActuellementEst = TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI,
            nomClientConcerned = "Abderrahmane",
            timestamps = todayTimestamp
        )
    )

    // Add a CIBLE transaction
    testTransactions.add(
        TransactionCommercial(
            vid = 2L,
            etateActuellementEst = TransactionCommercial.EtateActuellementEst.Cible,
            nomClientConcerned = "Houssine",
            timestamps = yesterdayTimestamp
        )
    )



    return testTransactions
}

@Entity
data class TransactionCommercial(
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
