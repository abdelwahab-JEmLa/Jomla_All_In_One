package com.example.clientjetpack.Repositorys

import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.room.Entity
import androidx.room.PrimaryKey
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


    calendar.add(Calendar.DAY_OF_MONTH, -1)
    calendar.set(Calendar.HOUR_OF_DAY, 17) //  PM
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val yesterdayTimestamp17 = calendar.timeInMillis

    // Add a COMMANDE_LIVRAI transaction for client 1
    testTransactions.add(
        TransactionCommercial(
            vid = 1L,
            clientAcheteurID = 1L,  // Set unique client ID
            etateActuellementEst = Type.COMMANDE_LIVRAI,
            nomClientConcerned = "Abderrahmane",
            timestamps = todayTimestamp
        )
    )

    // Add a CIBLE transaction for client 2
    testTransactions.add(
        TransactionCommercial(
            vid = 2L,
            clientAcheteurID = 2L,  // Set unique client ID
            etateActuellementEst = Type.Cible,
            nomClientConcerned = "Houssine",
            timestamps = yesterdayTimestamp
        )
    )
    // Add a CIBLE transaction for client 2
    testTransactions.add(
        TransactionCommercial(
            vid = 3L,
            clientAcheteurID = 2L,
            etateActuellementEst = Type.COMMANDE_LIVRAI,
            nomClientConcerned = "Houssine",
            timestamps = yesterdayTimestamp
        )
    )

    // Add a COMMANDE_LIVRAI transaction for client 3
    testTransactions.add(
        TransactionCommercial(
            vid = 4L,
            clientAcheteurID = 3L,  // Set unique client ID
            etateActuellementEst = Type.COMMANDE_LIVRAI,
            nomClientConcerned = "Fares",
            timestamps = yesterdayTimestamp17
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
    var cActiveDataDeParentList: Boolean = false,

    var cJustPourVoirPanie: Boolean = false,
    var ouvert: Boolean = false,

    var vocaleKeyID: String = "",
    var sonVocaleEstEcoute: Boolean = false,
    var sonEcoutementEstFaitAutimestamps: Long = 0,

    var etateActuellementEst: Type =
        Type.NON_DEFINI,
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
}
