package com.example.clientjetpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import java.util.Calendar

@ExperimentalCoroutinesApi
class ImprovedDatesHistoriqueTest {
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val transactions = _B_TestTransactionDataProvider.getTransactions()

    private lateinit var mapsIDSDatesHistoriqueTransactions: D_MapsIDSDatesHistoriqueTransactionsRep_Repository
    private lateinit var sqlDatasDatesHistorique: D_ParDatesHistoriqueTransactions_Repository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Create and initialize data structures
        mapsIDSDatesHistoriqueTransactions = D_MapsIDSDatesHistoriqueTransactionsRep_Repository()
            .collectInit(transactions)

        sqlDatasDatesHistorique = D_ParDatesHistoriqueTransactions_Repository(
            mapsIDSDatesHistoriqueTransactions,
            transactions
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testMapsID() {
        try {
            A_LogMapsIDSDatesHistoriqueTransactions(
                mapsIDSDatesHistoriqueTransactions
            )
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
            A_Logs_FilterByDayeLog(
                sqlDatasDatesHistorique,
                filterDateTimeTamp = normalizeTimetampFromeStrDate("2025-05-05")
            )

            A_Logs_FilterByDayeLog(
                sqlDatasDatesHistorique,
                filterDateTimeTamp = normalizeTimetampFromeStrDate("2025-05-04")
            )

            // If we reach here without exceptions, test passes
            assertTrue(true)
        } catch (e: Exception) {
            // If an exception occurs, fail the test
            assertTrue("Exception during filtering: ${e.message}", false)
        }
    }

    @Test
    fun SqlDatasDatesHistoriqueTransactionsLogDisplayerTest() {
        try {
            val nameDataBase = "SqlDatasDatesHistoriqueTransactions"

            println("======== TESTING $nameDataBase TRANSACTIONS ========")
            println("\n-- Hierarchical Structure --")

            D_ParDatesHistoriqueTransactions_RepositoryHierarchicalStructure(sqlDatasDatesHistorique)

            assertTrue(true)
            println("\n======== TEST COMPLETED SUCCESSFULLY ========\n")
        } catch (e: Exception) {
            // If an exception occurs, fail the test
            assertTrue("Exception during filtering: ${e.message}", false)
        }
    }

    @Test
    fun mapSemainJours_LogDisplayerTest() {
        try {
            val nameDataBase = "mapSemainJours"

            println("======== TESTING $nameDataBase TRANSACTIONS ========")
            println("\n-- Hierarchical Structure --")

            mapSemainJours_HierarchicalStructureLog(mapsIDSDatesHistoriqueTransactions)

            assertTrue(true)
            println("\n======== TEST COMPLETED SUCCESSFULLY ========\n")
        } catch (e: Exception) {
            // If an exception occurs, fail the test
            assertTrue("Exception during filtering: ${e.message}", false)
        }
    }

    private fun mapSemainJours_HierarchicalStructureLog(
        mapsIDSDatesHistoriqueTransactions: D_MapsIDSDatesHistoriqueTransactionsRep_Repository,
    ) {
        println("Semaines (${mapsIDSDatesHistoriqueTransactions.semaines.size}):")

        // Sort weeks by timestamp (from newest to oldest)
        val sortedWeeks = mapsIDSDatesHistoriqueTransactions
            .semaines
            .entries.sortedByDescending { it.key }

        ListLog(sortedWeeks, mapsIDSDatesHistoriqueTransactions)
    }

    private fun ListLog(
        sortedWeeks: List<Map.Entry<Long, MutableList<Long>>>,
        mapsIDSDatesHistoriqueTransactions: D_MapsIDSDatesHistoriqueTransactionsRep_Repository,
    ) {
        sortedWeeks.forEachIndexed { weekIndex, (weekTimestamp, days) ->
            val isLastWeek = weekIndex == sortedWeeks.size - 1
            val weekPrefix = TreePrefix.Type1.get(isLastWeek)

            // Format week timestamp
            val weekDate = formatTimestamp(weekTimestamp)
            println("$weekPrefix Week: $weekDate (${days.size} days)")

            // Sort days within each week (from newest to oldest)
            val sortedDays = days.sortedByDescending { it }

            sortedDays.forEachIndexed { dayIndex, dayTimestamp ->
                val isLastDay = dayIndex == sortedDays.size - 1
                val dayPrefix = TreePrefix.Type2.get(isLastDay)

                // Format day timestamp and count transactions
                val dayDate = formatTimestamp(dayTimestamp)
                val transactions = mapsIDSDatesHistoriqueTransactions.jours[dayTimestamp]?.size ?: 0
                itemLog(
                    dayPrefix,
                    dayDate,
                    transactions,
                    mapsIDSDatesHistoriqueTransactions,
                    dayTimestamp
                )
            }
        }
    }

    private fun itemLog(
        dayPrefix: String,
        dayDate: String,
        transactions: Int,
        mapsIDSDatesHistoriqueTransactions: D_MapsIDSDatesHistoriqueTransactionsRep_Repository,
        dayTimestamp: Long,
    ) {
        println("$dayPrefix Day: $dayDate ($transactions transactions)")

        sousItem(mapsIDSDatesHistoriqueTransactions, dayTimestamp)
    }

    private fun sousItem(
        mapsIDSDatesHistoriqueTransactions: D_MapsIDSDatesHistoriqueTransactionsRep_Repository,
        dayTimestamp: Long,
    ) {
        // If you want to add more detail about transactions on this day:
        val transactionsForDay = mapsIDSDatesHistoriqueTransactions.jours[dayTimestamp]
        if (!transactionsForDay.isNullOrEmpty()) {
            transactionsForDay.forEachIndexed { txIndex, txId ->
                val isLastTx = txIndex == transactionsForDay.size - 1
                val txPrefix = TreePrefix.Type3.get(isLastTx)
                val txState = mapsIDSDatesHistoriqueTransactions.transactions[txId]
                println("$txPrefix Transaction: ID>$txId (State: $txState)")
            }
        }
    }

    // Helper function to format timestamp to readable date
    private fun formatTimestamp(timestamp: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        return "${calendar.get(Calendar.YEAR)}-" +
                "${(calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')}-" +
                calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
    }
}

enum class TreePrefix(private val lastItem: String, private val normalItem: String) {
    Type1("  └─", "  ├─"),
    Type2("     └─", "     ├─"),
    Type3("  │  └─", "  │  ├─"),
    Type4("     ", "  │  ");

    fun get(isLast: Boolean): String = if (isLast) lastItem else normalItem
}

