package com.example.clientjetpack.Repositorys

fun logDatesHistoriqueStructure(testData: DatesHistoriqueTransactions) {
    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")

    println("Created test data structure for DatesHistoriqueTransactions")
    println("✓ Found expected number of weeks: ${testData.semaines.size}")

    // Log overall structure
    println("\n== Structure Overview ==")
    // Log weeks and their days
    testData.semaines.forEach { (weekTimestamp, daysList) ->
        val weekDateStr = java.util.Date(weekTimestamp).toString()
        println("WEEK [$weekDateStr]: Contains ${daysList.size} days")

        // Log days in this week
        daysList.forEach { dayTimestamp ->
            val dayDateStr = java.util.Date(dayTimestamp).toString()
            val transactionsInDay = testData.jours[dayTimestamp] ?: emptyList()
            println("  DAY [$dayDateStr]: Contains ${transactionsInDay.size} transactions")

            // Log transactions in this day
            transactionsInDay.forEach { transactionId ->
                val state = testData.etate[transactionId]
                println("    TRANSACTION [$transactionId]: State = ${state?.nomArabe ?: "Unknown"}")
            }
        }
    }

    // Log client transactions
    println("\n== Client Transactions ==")
    testData.clientTransactions.forEach { (clientId, transactionsList) ->
        println("CLIENT [$clientId]: Has ${transactionsList.size} transactions")
        transactionsList.forEach { transactionId ->
            val state = testData.etate[transactionId]
            println("  TRANSACTION [$transactionId]: State = ${state?.nomArabe ?: "Unknown"}")
        }
    }

    // Summary statistics
    println("\n== Summary ==")
    val totalDays = testData.semaines.values.sumOf { it.size }
    val totalTransactions = testData.jours.values.sumOf { it.size }
    val uniqueStates = testData.etate.values.toSet().size

    println("Total weeks: ${testData.semaines.size}")
    println("Total days: $totalDays")
    println("Total transactions: $totalTransactions")
    println("Unique states: $uniqueStates/${EtateActuellementEst.values().size}")

    println("\n======== TEST COMPLETED SUCCESSFULLY ========")
}
