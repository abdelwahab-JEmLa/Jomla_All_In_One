package com.example.clientjetpack.Repositorys

import java.util.Calendar

fun SqlDatasDatesHistoriqueTransactions
        .log(testTransactions: List<TransactionCommercial>? = null) {

    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")
    println("Created test data structure for SqlDatasDatesHistoriqueTransactions")
    println("Total weeks: ${semaines.size}")

    // Display nested data structure in hierarchical format
    println("\n-- Hierarchical Structure --")

    // Log weeks and their days
    println("\n-- Semaines (Weeks) --")

    // Sort weeks chronologically
    val sortedWeeks = semaines.sortedBy { it.vidTimeTemp }

    sortedWeeks.forEach { semaine ->
        val weekDate = formatTimestampToDate(semaine.vidTimeTemp)

        // Find days in this week - using the semaine.vidTimeTemp directly
        val daysInWeek = jours.filter { jour ->
            // Get the week start for this day by calculating back to the first day of the week
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = jour.vidTimeTemp

            // Set to first day of week and reset time to midnight
            calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)

            // Compare the calculated week start with the semaine timestamp
            calendar.timeInMillis == semaine.vidTimeTemp
        }.sortedBy { it.vidTimeTemp }

        println("Semaine ($weekDate): ${daysInWeek.size} jour(s)")

        // Process each day in the week
        daysInWeek.forEachIndexed { dayIndex, jour ->
            val dayDate = formatTimestampToDate(jour.vidTimeTemp)
            val isLastDay = dayIndex == daysInWeek.size - 1
            val dayPrefix = if (isLastDay) "  └─" else "  ├─"

            // Find transactions for this day
            val transactionsForDay = transactions.filter { transaction ->
                val transactionDay = java.util.Calendar.getInstance().apply {
                    timeInMillis = transaction.timestamp
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                transactionDay == jour.vidTimeTemp
            }

            println("$dayPrefix Jour $dayIndex ($dayDate): ${transactionsForDay.size} transaction(s)")

            // Group transactions by client
            val clientTransactions = transactionsForDay.groupBy { transaction ->
                val originalTransaction = testTransactions?.find { it.vid == transaction.vidTimeTemp }
                originalTransaction?.clientAcheteurID ?: 0L
            }

            // Log all transactions for each day without the 5 transaction limit
            var transactionCount = 0
            clientTransactions.forEach { (clientId, transactions) ->
                val client = clients.find { it.vidTimeTemp == clientId }
                if (client != null) {
                    // Show each transaction
                    transactions.sortedBy { it.timestamp }.forEachIndexed { tIndex, transaction ->
                        val isLastTransaction = tIndex == transactions.size - 1 &&
                                transactionCount == transactionsForDay.size - 1

                        // Use the correct prefix based on whether this is the last transaction
                        val transPrefix = if (isLastDay) {
                            if (isLastTransaction) "     └─" else "     ├─"
                        } else {
                            if (isLastTransaction) "  │  └─" else "  │  ├─"
                        }

                        println("$transPrefix Transaction #$transactionCount (ID: ${transaction.vidTimeTemp}, État: ${transaction.etate})")
                        transactionCount++
                    }
                }
            }
        }
    }

    println("\n======== TEST COMPLETED SUCCESSFULLY ========")
}

// Helper function to format timestamp to readable date
private fun formatTimestampToDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd")
    return formatter.format(date)
}
