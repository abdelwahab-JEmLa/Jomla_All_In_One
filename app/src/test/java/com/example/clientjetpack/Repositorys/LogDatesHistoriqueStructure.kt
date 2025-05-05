package com.example.clientjetpack.Repositorys

fun logDatesHistoriqueStructure(testData: SqlDatasDatesHistoriqueTransactions) {
    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")

    println("Created test data structure for MapsIDSDatesHistoriqueTransactions")
    println("✓ Found expected number of weeks: ${testData.semaines.size}")

    // Log overall structure
    println("\n== Structure Overview ==")

    // Print weeks structure
    println("\n=== Weeks ===")
    testData.semaines.forEachIndexed { index, semaine ->
        println("Week $index: ID=${semaine.vidTimeTemp}, Week number in year: ${semaine.semainCountDonSonAnne}")
    }

    // Print days structure
    println("\n=== Days ===")
    testData.jours.forEachIndexed { index, jour ->
        println("Day $index: ID=${jour.vidTimeTemp}, Date: ${jour.dateStr}")
    }

    // Print clients structure
    println("\n=== Clients ===")
    testData.clients.forEachIndexed { index, client ->
        println("Client $index: ID=${client.vidTimeTemp}, Name: ${client.nom}")
    }

    // Print transactions structure
    println("\n=== Transactions ===")
    testData.transactions.forEachIndexed { index, transaction ->
        println("Transaction $index: ID=${transaction.vidTimeTemp}, Time: ${transaction.tempStr}, State: ${transaction.etate}")
    }

    println("\n======== TEST COMPLETED SUCCESSFULLY ========")
}
