package com.example.clientjetpack

import com.example.clientjetpack.Functions.getFromeDayeStringTime
import com.example.clientjetpack.Repositorys.Type
import java.util.Calendar

fun B_Data_CreateTestTransactions(): List<D_Repo_TransactionCommercial> {
    val testTransactions = ArrayList<D_Repo_TransactionCommercial>()

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

    // Create a timestamp for yesterday at 3:30 PM
    val calendar2 = Calendar.getInstance()
    calendar2.timeInMillis = yesterdayTimestamp
    calendar2.set(Calendar.MINUTE, 30)
    val yesterdayTimestamp330 = calendar2.timeInMillis

    calendar.add(Calendar.DAY_OF_MONTH, -1)
    calendar.set(Calendar.HOUR_OF_DAY, 17) //  PM
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val yesterdayTimestamp17 = calendar.timeInMillis

    // Add a COMMANDE_LIVRAI transaction for client 1
    testTransactions.add(
        D_Repo_TransactionCommercial(
            vid = 1L,
            clientAcheteurID = 1L,  // Set unique client ID
            etateActuellementEst = Type.COMMANDE_LIVRAI,
            nomClientConcerned = "Abderrahmane",
            timestamps = todayTimestamp
        )
    )
    // Add a COMMANDE_LIVRAI transaction for client 1
    testTransactions.add(
        D_Repo_TransactionCommercial(
            vid = 5L,
            clientAcheteurID = 1L,  // Set unique client ID
            etateActuellementEst = Type.ACHETEUR_NON_DISPO,
            nomClientConcerned = "Abderrahmane",
            timestamps = getFromeDayeStringTime("05/05 8.30PM")
        )
    )
    // Add a COMMANDE_LIVRAI transaction for client 1
    testTransactions.add(
        D_Repo_TransactionCommercial(
            vid = 7L,
            clientAcheteurID = 4L,  // Set unique client ID
            etateActuellementEst = Type.ON_MODE_COMMEND_ACTUELLEMENT,
            nomClientConcerned = "Walide",
            timestamps = getFromeDayeStringTime("05/05 10.30PM")
        )
    )

    // Add a CIBLE transaction for client 2
    testTransactions.add(
        D_Repo_TransactionCommercial(
            vid = 2L,
            clientAcheteurID = 2L,  // Set unique client ID
            etateActuellementEst = Type.Cible,
            nomClientConcerned = "Houssine",
            timestamps = yesterdayTimestamp
        )
    )
    // Add a CIBLE transaction for client 2
    testTransactions.add(
        D_Repo_TransactionCommercial(
            vid = 3L,
            clientAcheteurID = 2L,
            etateActuellementEst = Type.COMMANDE_LIVRAI,
            nomClientConcerned = "Houssine",
            timestamps = yesterdayTimestamp330
        )
    )

    // Add a COMMANDE_LIVRAI transaction for client 3
    testTransactions.add(
        D_Repo_TransactionCommercial(
            vid = 4L,
            clientAcheteurID = 3L,  // Set unique client ID
            etateActuellementEst = Type.COMMANDE_LIVRAI,
            nomClientConcerned = "Fares",
            timestamps = yesterdayTimestamp17
        )
    )

    return testTransactions
}
