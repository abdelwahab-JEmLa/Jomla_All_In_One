package com.example.clientjetpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Functions.belongsToSameWeek
import com.example.clientjetpack.Functions.formatTime
import com.example.clientjetpack.Functions.formatTimestampToDate
import com.example.clientjetpack.Functions.formatTimestampToTime
import com.example.clientjetpack.Functions.getTransactionTime
import com.example.clientjetpack.Functions.isSameDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@ExperimentalCoroutinesApi
class ImprovedDatesHistoriqueTest {
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val testTransactions = B_Data_CreateTestTransactions()
    private lateinit var mapsIDSDatesHistoriqueTransactions: D_Rep_MapsIDSDatesHistoriqueTransactions
    private lateinit var sqlDatasDatesHistorique: D_Repo_SqlDatasDatesHistoriqueTransactions

    private val filterDayTimeTamp = normalizeTimetampFromeStrDate("2025-05-05")


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Create and initialize data structures
        mapsIDSDatesHistoriqueTransactions = D_Rep_MapsIDSDatesHistoriqueTransactions()
            .collectInit(testTransactions)

        sqlDatasDatesHistorique = D_Repo_SqlDatasDatesHistoriqueTransactions(
            mapsIDSDatesHistoriqueTransactions,
            testTransactions
        )
    }

    fun normalizeTimetampFromeStrDate(stringDate: String): Long {
        val calendar = Calendar.getInstance()
        // Parse the date string to get a timestamp
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(stringDate)

        calendar.apply {
            time = date ?: Date() // Use the parsed date or current date as fallback
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLogFunctions() {
        try {

            println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")

            // Display nested data structure in hierarchical format
            println("\n-- Hierarchical Structure --")

            // Log weeks and their days
            println("\n-- Semaines (Weeks) --")

            // Sort weeks chronologically
            val sortedWeeks = mapsIDSDatesHistoriqueTransactions.semaines.entries.sortedBy { it.key }

            sortedWeeks.forEach { (weekTimestamp, dayTimestamps) ->
                val weekDate = formatTimestampToDate(weekTimestamp)
                println("Semaine ($weekDate): ${dayTimestamps.size} jour(s)")

                // Sort days chronologically
                val sortedDays = dayTimestamps.sortedBy { it }

                // Process each day in the week
                sortedDays.forEachIndexed { dayIndex, dayTimestamp ->
                    val dayDate = formatTimestampToDate(dayTimestamp)
                    val isLastDay = dayIndex == sortedDays.size - 1
                    val dayPrefix = if (isLastDay) "  └─" else "  ├─"

                    // Find transactions for this day
                    val transactionsInDay = mapsIDSDatesHistoriqueTransactions.jours[dayTimestamp] ?: emptyList()
                    println("$dayPrefix Jour $dayIndex ($dayDate): ${transactionsInDay.size} transaction(s)")

                    // Group transactions by client
                    val transactionsByClient = mutableMapOf<Long, MutableList<Long>>()

                    // Find which client each transaction belongs to
                    transactionsInDay.forEach { transactionId ->
                        mapsIDSDatesHistoriqueTransactions.clients.forEach { (clientId, clientTransactions) ->
                            if (clientTransactions.contains(transactionId)) {
                                transactionsByClient.getOrPut(clientId) { mutableListOf() }.add(transactionId)
                            }
                        }
                    }

                    // Log transactions grouped by client
                    var transactionCount = 0
                    transactionsByClient.forEach { (clientId, transactionIds) ->
                        val clientPrefix = if (isLastDay) "     " else "  │  "
                        println("$clientPrefix Client ID: $clientId - ${transactionIds.size} transaction(s)")

                        // Log individual transactions
                        transactionIds.forEachIndexed { tIndex, transactionId ->
                            val isLastTransaction = tIndex == transactionIds.size - 1 &&
                                    transactionCount == transactionsInDay.size - 1

                            val transactionPrefix = if (isLastDay) {
                                if (isLastTransaction) "     └─" else "     ├─"
                            } else {
                                if (isLastTransaction) "  │  └─" else "  │  ├─"
                            }

                            val transactionType = mapsIDSDatesHistoriqueTransactions.transactions[transactionId]
                            val timeStr = formatTimestampToTime(getTransactionTime(transactionId, transactionsInDay))

                            println("$transactionPrefix Transaction #$transactionCount (ID: $transactionId, État: $transactionType, Time: $timeStr)")
                            transactionCount++
                        }
                    }
                }
            }

            println("\n======== TEST COMPLETED SUCCESSFULLY ========\n")

            // If we reach here without exceptions, test passes
            assertTrue(true)
        } catch (e: Exception) {
            // If an exception occurs, fail the test
            assertTrue("Exception during logging: ${e.message}", false)
        }
    }

    @Test
    fun testFilterByDay() {
        try {
            println("======== TESTING FILTERED DATES HISTORIQUE BY DAY ========")

            // Display filtered data structure for specific day
            println("\n-- Filtered Transactions for Day: ${formatTimestampToDate(filterDayTimeTamp)} --")

            // Find transactions for this specific day
            val filteredTransactions = sqlDatasDatesHistorique.transactions.filter { transaction ->
                isSameDay(transaction.timestamp, filterDayTimeTamp)
            }.sortedBy { it.timestamp }

            println("Found ${filteredTransactions.size} transaction(s) for this day")

            // Group transactions by client
            val transactionsByClient = filteredTransactions
                .groupBy { transaction -> transaction.clientId }
                .toSortedMap()

            // Log transactions grouped by client
            var transactionCount = 0
            transactionsByClient.forEach { (clientId, transactions) ->
                // Find client name
                val clientName = sqlDatasDatesHistorique.clients
                    .find { it.vidTimeTemp == clientId }?.nom ?: "Unknown Client"

                println("Client ID: $clientId ($clientName) - ${transactions.size} transaction(s)")

                // Log individual transactions
                transactions.forEachIndexed { tIndex, transaction ->
                    val isLastTransaction = tIndex == transactions.size - 1
                    val transactionPrefix = if (isLastTransaction) "  └─" else "  ├─"

                    val timeStr = formatTime(transaction.timestamp)
                    println("$transactionPrefix Transaction #$transactionCount (ID: ${transaction.vidTimeTemp}, État: ${transaction.etate}, Time: $timeStr)")
                    transactionCount++
                }
            }

            if (filteredTransactions.isEmpty()) {
                println("No transactions found for date: ${formatTimestampToDate(filterDayTimeTamp)}")
            }

            println("\n======== FILTER TEST COMPLETED SUCCESSFULLY ========\n")

            // If we reach here without exceptions, test passes
            assertTrue(true)
        } catch (e: Exception) {
            // If an exception occurs, fail the test
            assertTrue("Exception during filtering: ${e.message}", false)
        }
    }

    @Test
    fun testLogSqlDatasDatesHistoriqueTransactionslog() {
        try {
            println("======== TESTING DATES HISTORIQUE TRANSACTIONS ========")

            // Display nested data structure in hierarchical format
            println("\n-- Hierarchical Structure --")

            // Log weeks and their days
            println("\n-- Semaines (Weeks) --")

            // Sort weeks chronologically
            val sortedWeeks = sqlDatasDatesHistorique.semaines.sortedBy { it.vidTimeTemp }

            sortedWeeks.forEach { semaine ->
                val weekDate = formatTimestampToDate(semaine.vidTimeTemp)

                // Find days in this week using timestamp comparison for week belonging
                val daysInWeek = sqlDatasDatesHistorique.jours.filter { jour ->
                    belongsToSameWeek(jour.vidTimeTemp, semaine.vidTimeTemp)
                }.sortedBy { it.vidTimeTemp }

                println("Semaine ($weekDate): ${daysInWeek.size} jour(s)")

                // Process each day in the week
                daysInWeek.forEachIndexed { dayIndex, jour ->
                    val dayDate = formatTimestampToDate(jour.vidTimeTemp)
                    val isLastDay = dayIndex == daysInWeek.size - 1
                    val dayPrefix = if (isLastDay) "  └─" else "  ├─"

                    // Find transactions for this day using direct timestamp comparison
                    val transactionsForDay = sqlDatasDatesHistorique.transactions.filter { transaction ->
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
                        val clientName = sqlDatasDatesHistorique.clients
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

            // If we reach here without exceptions, test passes
            assertTrue(true)
        } catch (e: Exception) {
            // If an exception occurs, fail the test
            assertTrue("Exception during logging: ${e.message}", false)
        }
    }
}
