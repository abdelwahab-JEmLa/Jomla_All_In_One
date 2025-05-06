package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.D_Repo_SqlDatasDatesHistoriqueTransactions
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.belongsToSameWeek
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.formatTime
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.formatTimestampToDate
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.isSameDay

/**
 * Improved logging function for D_Repo_SqlDatasDatesHistoriqueTransactions structure
 * Properly associates transactions with days based on timestamp comparison
 */
fun SqlDatasDatesHistoriqueTransactionslog(
    sqlDatasDatesHistoriqueTransactions: D_Repo_SqlDatasDatesHistoriqueTransactions
) {
    println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")

    // Display nested data structure in hierarchical format
    println("\n-- Hierarchical Structure --")

    // Log weeks and their days
    println("\n-- Semaines (Weeks) --")

    // Sort weeks chronologically
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
            val dayPrefix = if (isLastDay) "  └─" else "  ├─"

            // Find transactions for this day using direct timestamp comparison
            val transactionsForDay = sqlDatasDatesHistoriqueTransactions.transactions.filter { transaction ->
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

                val clientPrefix = if (isLastDay) "     " else "  │  "
                println("$clientPrefix Client ID: $clientId ($clientName) - ${transactions.size} transaction(s)")

                // Log individual transactions
                transactions.forEachIndexed { tIndex, transaction ->
                    val isLastTransaction = tIndex == transactions.size - 1 &&
                            transactionCount == transactionsForDay.size - 1

                    val transactionPrefix = if (isLastDay) {
                        if (isLastTransaction) "     └─" else "     ├─"
                    } else {
                        if (isLastTransaction) "  │  └─" else "  │  ├─"
                    }

                    val timeStr = formatTime(transaction.timestamp)
                    println("$transactionPrefix Transaction #$transactionCount (ID: ${transaction.vidTimeTemp}, État: ${transaction.etate}, Time: $timeStr)")
                    transactionCount++
                }
            }
        }
    }

    println("\n======== TEST COMPLETED SUCCESSFULLY ========\n")
}


