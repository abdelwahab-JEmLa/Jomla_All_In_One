package com.example.clientjetpack

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.D_ParDatesHistoriqueTransactions_Repository
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.belongsToSameWeek
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.formatTime
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.formatTimestampToDate
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.isSameDay


fun SqlDatasDatesHistoriqueTransactionsHierarchicalStructure(
    sqlDatasDatesHistoriqueTransactions: D_ParDatesHistoriqueTransactions_Repository
) {
    val sortedWeeks = sqlDatasDatesHistoriqueTransactions.semaines.sortedBy { it.vidTimeTemp }

    sortedWeeks.forEach { semaine ->
        val weekDate = formatTimestampToDate(semaine.vidTimeTemp)

        // Find days in this week using timestamp comparison for week belonging
        val daysInWeek = sqlDatasDatesHistoriqueTransactions.jours.filter { jour ->
            belongsToSameWeek(jour.vidTimeTemp, semaine.vidTimeTemp)
        }.sortedBy { it.vidTimeTemp }

        println("Semaine ($weekDate): ${daysInWeek.size} jour(s)")

        // Process each day in the week
        daysInWeek.forEachIndexed { dayIndex, jour ->
            val dayDate = formatTimestampToDate(jour.vidTimeTemp)
            val isLastDay = dayIndex == daysInWeek.size - 1

            // Using the enum instance method correctly
            val dayPrefix = TreePrefix.DAY.get(isLastDay)

            // Find transactions for this day using direct timestamp comparison
            val transactionsForDay =
                sqlDatasDatesHistoriqueTransactions.transactions.filter { transaction ->
                    isSameDay(transaction.timestamp, jour.vidTimeTemp)
                }.sortedBy { it.timestamp }

            println("$dayPrefix Jour $dayIndex ($dayDate): ${transactionsForDay.size} transaction(s)")

            // Group transactions by client
            val transactionsByClient = transactionsForDay
                .groupBy { transaction -> transaction.clientId }
                .toSortedMap()

            // Log transactions grouped by client
            var transactionCount = 0
            transactionsByClient.forEach { (clientId, transactions) ->
                // Find client name
                val clientName = sqlDatasDatesHistoriqueTransactions.clients
                    .find { it.vidTimeTemp == clientId }?.nom ?: "Unknown Client"

                // Using the enum instance method correctly
                val clientPrefix = if (isLastDay)
                    TreePrefix.CLIENT_SPACING.get(true)
                else
                    TreePrefix.CLIENT_SPACING.get(false)

                println("$clientPrefix Client ID: $clientId ($clientName) - ${transactions.size} transaction(s)")

                // Log individual transactions
                transactions.forEachIndexed { tIndex, transaction ->
                    val isLastTransaction = tIndex == transactions.size - 1 &&
                            transactionCount == transactionsForDay.size - 1

                    // Using the enum instance method correctly
                    val transactionPrefix = if (isLastDay) {
                        TreePrefix.TRANSACTION_LAST_DAY.get(isLastTransaction)
                    } else {
                        TreePrefix.TRANSACTION_NORMAL_DAY.get(isLastTransaction)
                    }

                    val timeStr = formatTime(transaction.timestamp)
                    println("$transactionPrefix Transaction #$transactionCount (ID: ${transaction.vidTimeTemp}, État: ${transaction.etate}, Time: $timeStr)")
                    transactionCount++
                }
            }
        }
    }
}
