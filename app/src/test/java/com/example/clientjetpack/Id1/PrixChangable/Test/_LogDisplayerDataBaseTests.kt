package com.example.clientjetpack.Id1.PrixChangable.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.DA_MapsIDSDatesHistoriqueTransactionsRep_Repository
import com.example.clientjetpack.DB_ParDatesHistoriqueTransactions_Repository
import com.example.clientjetpack.TreePrefix
import com.example.clientjetpack.Z_Passive._B_TestTransactionDataProvider
import com.example.clientjetpack.Z_Passive.strDateFromVidTimestamp
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

@ExperimentalCoroutinesApi
class _TestsDisplayerLogDataBase {
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

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun SepareReferentialDataBases() {
        try {
            val name = "A_DataBasesSepareReferential"

            println("======== TESTING $name TRANSACTIONS ========")
            mapSemainJours_LogDisplayerTest(
                mapsIDSDatesHistoriqueTransactions
            )

            val firstDay = sqlDatasDatesHistorique.jours[1]

            firstDay.itsActiveDaye = true  // Set the value in the model
            joursRepository.update(firstDay)  // Update via repository
            println("joursRepository.update(firstDay)\n")

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

    private fun mapSemainJours_LogDisplayerTest(
        mapsIDSDatesHistoriqueTransactionsPassed: DA_MapsIDSDatesHistoriqueTransactionsRep_Repository
    ) {
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

            val weekDate = strDateFromVidTimestamp(weekTimestamp)

            //Header
            println("$weekPrefix Week: $weekDate (${days.size} days)")

            ItemLog(days)
        }
    }

    private fun ItemLog(
        sortedDays: List<Long>
    ) {
        sortedDays.forEachIndexed { dayIndex, dayTimestamp ->
            val isLastDay = dayIndex == sortedDays.size - 1
            val dayPrefix = TreePrefix.Type2.get(isLastDay)

            val dayDate = strDateFromVidTimestamp(dayTimestamp)

            val jourObject = sqlDatasDatesHistorique.jours.find { it.vidTimeTemp == dayTimestamp }
            val isActive = jourObject?.itsActiveDaye ?: false

            println("$dayPrefix Day: $dayDate itsActiveDaye = $isActive")
        }
    }


}
