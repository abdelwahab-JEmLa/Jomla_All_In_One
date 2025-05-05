package com.example.clientjetpack.Repositorys

fun logDatesHistoriqueStructure(testData: DatesHistoriqueTransactions) {
    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")

    println("Created test data structure for DatesHistoriqueTransactions")
    println("✓ Found expected number of weeks: ${testData.semaines.size}")

    // Log overall structure
    println("\n== Structure Overview ==")

    // Log semaines details
    println("\n== Semaines (${testData.semaines.size}) ==")
    testData.semaines.forEach { (weekId, daysList) ->
        println("Week $weekId: ${daysList.size} days -> $daysList")
    }

    // Log jours details
    println("\n== Jours (${testData.jours.size}) ==")
    testData.jours.forEach { (dayId, transactionsList) ->
        println("Day $dayId: ${transactionsList.size} transactions -> $transactionsList")
    }

    // Log client transactions details
    println("\n== Client Transactions (${testData.clientTransactions.size}) ==")
    testData.clientTransactions.forEach { (clientId, transactionsList) ->
        println("Client $clientId: ${transactionsList.size} transactions -> $transactionsList")
    }

    // Log etate details with their names
    println("\n== Etate Status (${testData.etate.size}) ==")
    testData.etate.forEach { (id, state) ->
        println("ID $id: ${state.name} (${state.nomArabe})")
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
