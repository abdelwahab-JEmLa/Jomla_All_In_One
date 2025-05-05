package com.example.clientjetpack.Init

import com.example.clientjetpack.Repositorys.DatesHistoriqueTransactions
import com.example.clientjetpack.Repositorys.EtateActuellementEst
import com.example.clientjetpack.Repositorys.StrNomJourEtSonSemainToStartJourTimeTemp
import com.example.clientjetpack.Repositorys.TransactionCommercial
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Filters transactions by their current state
 */
fun transactionCommercialsFiltre(transactions: List<TransactionCommercial>): List<TransactionCommercial> {
    return transactions
        .filter {
            it.etateActuellementEst ==
                    EtateActuellementEst.COMMANDE_LIVRAI
        }
}

/**
 * Collects and organizes transactions by day and week
 */
fun collectAddAuStrNomJourEtSonSemainToStartJourTimeTemp(
    testTransactions: List<TransactionCommercial>
): MutableList<StrNomJourEtSonSemainToStartJourTimeTemp> {
    // Track changes in transactions with COMMANDE_LIVRAI status
    val calendar = Calendar.getInstance()
    val today = calendar.timeInMillis

    // Create a list to store unique days with transactions
    val uniqueDays = mutableListOf<StrNomJourEtSonSemainToStartJourTimeTemp>()

    testTransactions.forEach { transaction ->
        // Get transaction date
        val transactionDate = Date(transaction.timestamps)

        // Get start and end of day
        val startDay = getStartOfDay(transaction.timestamps)
        val endDay = getEndOfDay(transaction.timestamps)

        // Calculate how many weeks ago this was
        val weeksDifference = getWeeksDifference(today, transaction.timestamps)

        // Get day name in Arabic
        val dayFormat = SimpleDateFormat("EEEE", Locale("ar"))
        val dayName = dayFormat.format(transactionDate)

        // Create key for uniqueness
        val key = "${startDay}_${dayName}_${weeksDifference}"

        // Check if this day is already in our list
        val existingDay = uniqueDays.find { it.key == key }

        if (existingDay == null) {
            // Add new day if not exists
            uniqueDays.add(
                StrNomJourEtSonSemainToStartJourTimeTemp(
                    vid = transaction.vid,
                    nomJourArabe = dayName,
                    estDonLaSemainDistantDe = weeksDifference,
                    jourEstEntreTimeTemp = Pair(startDay, endDay),
                    key = key
                )
            )
        }
    }

    return uniqueDays
}

/**
 * Creates DatesHistoriqueTransactions structure from uniqueDays data
 */
fun collecteAddAuDatesHistoriqueTransactions(
    uniqueDaysForTesting: List<StrNomJourEtSonSemainToStartJourTimeTemp>,
    testTransactions: List<TransactionCommercial>
): DatesHistoriqueTransactions {
    // Create instances for weeks and days based on the collected dates
    val semainsList = mutableListOf<DatesHistoriqueTransactions.SemainKey>()

    // Group by week distance using our uniqueDaysForTesting
    val groupedByWeek = uniqueDaysForTesting.groupBy {
        it.estDonLaSemainDistantDe
    }

    groupedByWeek.forEach { (weekDistance, days) ->
        val semainKey = DatesHistoriqueTransactions.SemainKey().apply {
            vid = (weekDistance + 1).toLong()
        }

        val joursList = mutableListOf<DatesHistoriqueTransactions.SemainKey.JourKey>()

        days.forEach { dayInfo ->
            val jourKey = DatesHistoriqueTransactions.SemainKey.JourKey().apply {
                vid = dayInfo.vid
            }

            // Find all transactions for this day
            val dayTransactions = testTransactions.filter { transaction ->
                transaction >= dayInfo.jourEstEntreTimeTemp.first &&
                        transaction.timestamps <= dayInfo.jourEstEntreTimeTemp.second
            }

            jourKey.cesCommercialTransactionsKeys = dayTransactions
            joursList.add(jourKey)
        }

        semainKey.cesJour = joursList
        semainsList.add(semainKey)
    }

    // Return the created structure
    return DatesHistoriqueTransactions().apply {
        this.cesSemain = semainsList
    }
}

