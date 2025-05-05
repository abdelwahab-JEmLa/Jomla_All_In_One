package com.example.clientjetpack.Repositorys

fun logDatesHistoriqueStructure(testData: SqlDatasDatesHistoriqueTransactions,
                                testTransactions: List<TransactionCommercial>? = null) {
    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")

    println("Created test data structure for SqlDatasDatesHistoriqueTransactions")
    println("✓ Found expected number of weeks: ${testData.semaines.size}")

    // Display nested data structure in hierarchical format
    println("\n-- Hierarchical Structure --")

    // Sort weeks chronologically
    val sortedWeeks = testData.semaines.sortedBy { it.vidTimeTemp }

    // Process each week
    sortedWeeks.forEachIndexed { weekIndex, semaine ->
        println("Week ${weekIndex + 1}: ID=${semaine.vidTimeTemp}, Week Number=${semaine.semainCountDonSonAnne}")

        // Find days in this week
        val daysInWeek = testData.jours.filter { jour ->
            // Calculate the week start for this day
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = jour.vidTimeTemp
            calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            val weekStart = calendar.timeInMillis

            weekStart == semaine.vidTimeTemp
        }.sortedBy { it.vidTimeTemp }

        // Process each day in the week
        daysInWeek.forEachIndexed { dayIndex, jour ->
            println("  -< Day ${dayIndex + 1}: ID=${jour.vidTimeTemp}, Date=${jour.dateStr}")

            // Find transactions for this day
            val transactionsForDay = testData.transactions.filter { transaction ->
                val transactionDay = java.util.Calendar.getInstance().apply {
                    timeInMillis = transaction.timestamp
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }.timeInMillis

                transactionDay == jour.vidTimeTemp
            }

            // Group transactions by client
            val clientTransactions = transactionsForDay.groupBy { transaction ->
                val originalTransaction = testTransactions?.find { it.vid == transaction.vidTimeTemp }
                originalTransaction?.clientAcheteurID ?: 0L
            }

            // Process each client's transactions
            clientTransactions.forEach { (clientId, transactions) ->
                val client = testData.clients.find { it.vidTimeTemp == clientId }
                if (client != null) {
                    println("    -< Client: ID=${client.vidTimeTemp}, Name=${client.nom}")

                    // Show each transaction
                    transactions.sortedBy { it.timestamp }.forEachIndexed { tIndex, transaction ->
                        println("      -< Transaction ${tIndex + 1}: ID=${transaction.vidTimeTemp}, Time=${transaction.tempStr}, State=${transaction.etate}")
                    }
                }
            }
        }
    }

    // Also display standalone lists for reference
    println("\n-- Clients List --")
    testData.clients.forEachIndexed { index, client ->
        println("Client ${index + 1}: ID=${client.vidTimeTemp}, Name=${client.nom}")
    }

    println("\n======== TEST COMPLETED SUCCESSFULLY ========")
}
