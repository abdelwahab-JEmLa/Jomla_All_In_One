package com.example.clientjetpack.Repositorys

fun logDatesHistoriqueStructure(testData: SqlDatasDatesHistoriqueTransactions, testTransactions: List<TransactionCommercial>? = null) {
    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")

    println("Created test data structure for MapsIDSDatesHistoriqueTransactions")
    println("✓ Found expected number of weeks: ${testData.semaines.size}")

    // Log overall structure
    println("\n== Structure Overview ==")

    // Display nested data structure in hierarchical format
    println("\n-- Hierarchical Structure --")

    // Group days by their corresponding weeks
    val daysByWeek = testData.jours.groupBy { jour ->
        // Find which week this day belongs to
        testData.semaines.find { semaine ->
            val weekStart = semaine.vidTimeTemp
            val weekEnd = weekStart + (7 * 24 * 60 * 60 * 1000) // 7 days in milliseconds
            jour.vidTimeTemp in weekStart until weekEnd
        }?.vidTimeTemp ?: 0L
    }

    // Display weeks and their days
    testData.semaines.sortedBy { it.vidTimeTemp }.forEachIndexed { weekIndex, semaine ->
        val weekId = semaine.vidTimeTemp
        println("Week ${weekIndex + 1}: ID=${weekId}, Week Number=${semaine.semainCountDonSonAnne}")

        // Get days for this week
        val daysInWeek = daysByWeek[weekId] ?: emptyList()
        daysInWeek.sortedBy { it.vidTimeTemp }.forEachIndexed { dayIndex, jour ->
            println("  -< Day ${dayIndex + 1}: ID=${jour.vidTimeTemp}, Date=${jour.dateStr}")

            // Group transactions by client for this day
            val transactionsForDay = testData.transactions.filter { transaction ->
                val transactionDay = transaction.timestamp - (transaction.timestamp % (24 * 60 * 60 * 1000))
                transactionDay == jour.vidTimeTemp
            }

            // Map transactions to clients
            val clientsWithTransactions = mutableMapOf<Long, MutableList<Pair<SqlDatasDatesHistoriqueTransactions.Transaction, TransactionCommercial?>>>()

            transactionsForDay.forEach { transaction ->
                val originalTransaction = testTransactions?.find { it.vid == transaction.vidTimeTemp }
                val clientId = originalTransaction?.clientAcheteurID ?: 0L

                if (!clientsWithTransactions.containsKey(clientId)) {
                    clientsWithTransactions[clientId] = mutableListOf()
                }

                clientsWithTransactions[clientId]?.add(Pair(transaction, originalTransaction))
            }

            // Display clients and their transactions
            clientsWithTransactions.forEach { (clientId, transactions) ->
                val client = testData.clients.find { it.vidTimeTemp == clientId }
                if (client != null) {
                    println("    -< Client: ID=${client.vidTimeTemp}, Name=${client.nom}")

                    transactions.sortedBy { it.first.vidTimeTemp }.forEachIndexed { transIndex, (transaction, originalTransaction) ->
                        println("      -< Transaction ${transIndex + 1}: ID=${transaction.vidTimeTemp}, Time=${transaction.tempStr}, State=${transaction.etate}")
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
