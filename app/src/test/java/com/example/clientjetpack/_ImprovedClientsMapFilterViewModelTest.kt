package com.example.clientjetpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Functions.belongsToSameWeek
import com.example.clientjetpack.Functions.isSameDay
import com.example.clientjetpack.Logs.A_LogMapsIDSDatesHistoriqueTransactions
import com.example.clientjetpack.Logs.SqlDatasDatesHistoriqueTransactionslog
import com.example.clientjetpack.Repositorys.MapsIDSDatesHistoriqueTransactions
import com.example.clientjetpack.Repositorys.SqlDatasDatesHistoriqueTransactions
import com.example.clientjetpack.Tests.B.Data.createTestTransactions
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
class ImprovedDatesHistoriqueTest {
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val testTransactions = createTestTransactions()
    private lateinit var mapsIDsDatesHistorique: MapsIDSDatesHistoriqueTransactions
    private lateinit var sqlDatasDatesHistorique: SqlDatasDatesHistoriqueTransactions

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Create and initialize data structures
        mapsIDsDatesHistorique = MapsIDSDatesHistoriqueTransactions()
            .collectInit(testTransactions)

        sqlDatasDatesHistorique = SqlDatasDatesHistoriqueTransactions(
            mapsIDsDatesHistorique,
            testTransactions
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testMapsDataStructure() {
        // Verify week-day relationships
        mapsIDsDatesHistorique.semaines.forEach { (weekTimestamp, daysList) ->
            daysList.forEach { dayTimestamp ->
                // Verify each day belongs to its week
                assertTrue(belongsToSameWeek(dayTimestamp, weekTimestamp))
            }
        }

        // Verify day-transaction relationships
        mapsIDsDatesHistorique.jours.forEach { (dayTimestamp, transactionsList) ->
            transactionsList.forEach { transactionId ->
                // Get the original transaction
                val transaction = testTransactions.find { it.vid == transactionId }

                if (transaction != null) {
                    // Verify transaction belongs to this day
                    assertTrue(isSameDay(transaction.timestamps, dayTimestamp))
                }
            }
        }

        // Verify client-transaction relationships
        mapsIDsDatesHistorique.clients.forEach { (clientId, transactionsList) ->
            transactionsList.forEach { transactionId ->
                // Get the original transaction
                val transaction = testTransactions.find { it.vid == transactionId }

                if (transaction != null) {
                    // Verify transaction belongs to this client
                    assertEquals(clientId, transaction.clientAcheteurID)
                }
            }
        }

        // Verify transaction states
        mapsIDsDatesHistorique.transactions.forEach { (transactionId, state) ->
            // Get the original transaction
            val transaction = testTransactions.find { it.vid == transactionId }

            if (transaction != null) {
                // Verify state matches
                assertEquals(transaction.etateActuellementEst, state)
            }
        }
    }

    @Test
    fun testLogFunctions() {
        try {
            // Log MapsIDSDatesHistoriqueTransactions structure
            A_LogMapsIDSDatesHistoriqueTransactions(mapsIDsDatesHistorique)

            // Log SqlDatasDatesHistoriqueTransactions structure
            SqlDatasDatesHistoriqueTransactionslog(sqlDatasDatesHistorique)

            // If we reach here without exceptions, test passes
            assertTrue(true)
        } catch (e: Exception) {
            // If an exception occurs, fail the test
            assertTrue("Exception during logging: ${e.message}", false)
        }
    }


}
