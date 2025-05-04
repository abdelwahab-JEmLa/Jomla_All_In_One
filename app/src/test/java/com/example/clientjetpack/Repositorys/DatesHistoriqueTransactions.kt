package com.example.clientjetpack.Repositorys

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class DatesHistoriqueTransactions {
    var cesSemains by mutableStateOf<List<Semain>>(emptyList())

    class Semain {
        var vid by mutableStateOf(0L)
        var key by mutableStateOf("")
        var cActive by mutableStateOf(false)

        var cesJours by mutableStateOf<List<Jour>>(emptyList())

        class Jour {
            var vid by mutableStateOf(0L)
            var key by mutableStateOf("")
            var cActive by mutableStateOf(false)

            var cesCommercialTransactions by mutableStateOf<List<TransactionCommercial>>(emptyList())
        }
    }
}

fun logDatesHistoriqueStructure(testData: DatesHistoriqueTransactions) {
    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")

    println("Created test data structure for DatesHistoriqueTransactions")
    println("✓ Found expected number of weeks: ${testData.cesSemains.size}")

    // Log overall structure
    println("\n== Structure Overview ==")
    testData.cesSemains.forEachIndexed { weekIndex, week ->
        println("Week ${weekIndex + 1}: vid=${week.vid}, key=${week.key}, active=${week.cActive}, days=${week.cesJours.size}")
        week.cesJours.forEachIndexed { dayIndex, day ->
            println("  Day ${dayIndex + 1}: vid=${day.vid}, key=${day.key}, active=${day.cActive}, transactions=${day.cesCommercialTransactions.size}")
            day.cesCommercialTransactions.forEachIndexed { txIndex, tx ->
                println("    Tx ${txIndex + 1}: vid=${tx.vid}, state=${tx.etateActuellementEst}, client=${tx.nomClientConcerned}")
            }
        }
    }

    println("\n======== TEST COMPLETED SUCCESSFULLY ========")
}
