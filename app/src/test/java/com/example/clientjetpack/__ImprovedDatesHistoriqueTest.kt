package com.example.clientjetpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Z_Passive.A_LogMapsIDSDatesHistoriqueTransactions
import com.example.clientjetpack.Z_Passive.A_Logs_FilterByDayeLog
import com.example.clientjetpack.Z_Passive.D_ParDatesHistoriqueTransactions_RepositoryHierarchicalStructure
import com.example.clientjetpack.Z_Passive._B_TestTransactionDataProvider
import com.example.clientjetpack.Z_Passive.normalizeTimetampFromeStrDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
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

    private lateinit var mapsIDSDatesHistoriqueTransactions: DA_MapsIDSDatesHistoriqueTransactionsRep_Repository
    private lateinit var sqlDatasDatesHistorique: DB_ParDatesHistoriqueTransactions_Repository
    private lateinit var joursRepository: DB_ParDatesHistoriqueTransactions_Repository
        .JoursRepositoryImp

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Create and initialize data structures
        mapsIDSDatesHistoriqueTransactions = DA_MapsIDSDatesHistoriqueTransactionsRep_Repository()
            .collectInit(transactions)

        sqlDatasDatesHistorique = DB_ParDatesHistoriqueTransactions_Repository(
            mapsIDSDatesHistoriqueTransactions,
            transactions
        )

        // Initialize JoursRepositoryImp
        joursRepository = DB_ParDatesHistoriqueTransactions_Repository
            .JoursRepositoryImp(sqlDatasDatesHistorique)
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
    fun logAvantEtApreAppliqueFilter() {
        try {
            val name = "logAvantEtApreAppliqueFilter"

            println("======== TESTING $name TRANSACTIONS ========")
            mapSemainJours_LogDisplayerTest(
                mapsIDSDatesHistoriqueTransactions
            )

            // Get the first day and set it active
            val firstDay = sqlDatasDatesHistorique.jours[1]

            firstDay.itsActiveDaye = true  // Set the value in the model
            joursRepository.update(firstDay)  // Update via repository

            assertEquals(true, sqlDatasDatesHistorique.jours[1].itsActiveDaye)

            mapSemainJours_LogDisplayerTest(
                mapsIDSDatesHistoriqueTransactions
            )

            assertTrue(true)
            println("\n========TEST $name  COMPLETED SUCCESSFULLY ========\n")

        } catch (e: Exception) {
            assertTrue("Exception during filtering: ${e.message}", false)
        }
    }

    fun mapSemainJours_LogDisplayerTest(
        mapsIDSDatesHistoriqueTransactionsPassed: DA_MapsIDSDatesHistoriqueTransactionsRep_Repository) {
        try {
            val nameDataBase = "mapSemainJours"

            println("======== TESTING $nameDataBase TRANSACTIONS ========")
            println("\n-- Hierarchical Structure --")

            mapSemainJours_HierarchicalStructureLog(mapsIDSDatesHistoriqueTransactionsPassed)

            assertTrue(true)
            println("\n======== TEST COMPLETED SUCCESSFULLY ========\n")
        } catch (e: Exception) {
            assertTrue("Exception during filtering: ${e.message}", false)
        }
    }

    private fun mapSemainJours_HierarchicalStructureLog(
        mapsIDSDatesHistoriqueTransactions: DA_MapsIDSDatesHistoriqueTransactionsRep_Repository,
    ) {
        println("Semaines (${mapsIDSDatesHistoriqueTransactions.semaines.size}):")

        val sortedWeeks = mapsIDSDatesHistoriqueTransactions
            .semaines
            .entries.sortedByDescending { it.key }

        ListLog(sortedWeeks)
    }

    private fun ListLog(
        sortedWeeks: List<Map.Entry<Long, MutableList<Long>>>,
    ) {
        sortedWeeks.forEachIndexed { weekIndex, (weekTimestamp, days) ->
            val isLastWeek = weekIndex == sortedWeeks.size - 1
            val weekPrefix = TreePrefix.Type1.get(isLastWeek)

            val weekDate = formatTimestamp(weekTimestamp)
            println("$weekPrefix Week: $weekDate (${days.size} days)")

            val sortedDays = days.sortedByDescending { it }

            sortedDays.forEachIndexed { dayIndex, dayTimestamp ->
                val isLastDay = dayIndex == sortedDays.size - 1
                val dayPrefix = TreePrefix.Type2.get(isLastDay)

                val dayDate = formatTimestamp(dayTimestamp)

                val jourObject = sqlDatasDatesHistorique.jours.find { it.vidTimeTemp == dayTimestamp }
                val isActive = jourObject?.itsActiveDaye ?: false

                println("$dayPrefix Day: $dayDate itsActiveDaye=$isActive")
            }
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        return "${calendar.get(Calendar.YEAR)}-" +
                "${(calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')}-" +
                calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
    }
}
