package com.example.clientjetpack

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.A_LogMapsIDSDatesHistoriqueTransactions
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.D_MapsIDSDatesHistoriqueTransactionsRep_Repository
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.D_ParDatesHistoriqueTransactions_Repository
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.A_Logs_FilterByDayeLog
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.SqlDatasDatesHistoriqueTransactionslog
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.normalizeTimetampFromeStrDate
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

@ExperimentalCoroutinesApi
class ImprovedDatesHistoriqueTest {
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    // Lazy initialization of transactions using runBlocking for suspend function
    private val transactions = TestTransactionDataProvider.getTransactions() // This is a suspend function, but runBlocking makes it work

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
    fun testSqlDatasDatesHistoriqueTransactionslog() {
        try {
            SqlDatasDatesHistoriqueTransactionslog(
                sqlDatasDatesHistorique
            )
            // If we reach here without exceptions, test passes
            assertTrue(true)
        } catch (e: Exception) {
            // If an exception occurs, fail the test
            assertTrue("Exception during filtering: ${e.message}", false)
        }
    }
}
