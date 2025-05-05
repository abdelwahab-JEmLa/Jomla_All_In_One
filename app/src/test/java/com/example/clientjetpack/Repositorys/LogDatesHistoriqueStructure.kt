package com.example.clientjetpack.Repositorys

fun logDatesHistoriqueStructure(testData: DatesHistoriqueTransactions) {
    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")

    println("Created test data structure for DatesHistoriqueTransactions")
    println("✓ Found expected number of weeks: ${testData.semaines.size}")

    // Log overall structure
    println("\n== Structure Overview ==")


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
