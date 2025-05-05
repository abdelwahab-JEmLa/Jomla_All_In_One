package com.example.clientjetpack

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.B_Data_CreateTestTransactions
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.D_Rep_MapsIDSDatesHistoriqueTransactions
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.D_Repo_SqlDatasDatesHistoriqueTransactions
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Logs.A_LogMapsIDSDatesHistoriqueTransactions
import com.example.clientjetpack.Logs.FilterByDayeLog
import com.example.clientjetpack.Logs.SqlDatasDatesHistoriqueTransactionslog
import com.example.clientjetpack.Logs.normalizeTimetampFromeStrDate
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
    private val testTransactions = B_Data_CreateTestTransactions()
    private lateinit var mapsIDSDatesHistoriqueTransactions: D_Rep_MapsIDSDatesHistoriqueTransactions
    private lateinit var sqlDatasDatesHistorique: D_Repo_SqlDatasDatesHistoriqueTransactions

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

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLogFunctions() {
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
            FilterByDayeLog(
                sqlDatasDatesHistorique,
                filterDateTimeTamp = normalizeTimetampFromeStrDate("2025-05-05")
            )

            FilterByDayeLog(
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
    fun testLogSqlDatasDatesHistoriqueTransactionslog() {
        try {
            SqlDatasDatesHistoriqueTransactionslog(
                sqlDatasDatesHistorique
            )
            // If we reach here without exceptions, test passes
            assertTrue(true)
        } catch (e: Exception) {
            // If an exception occurs, fail the test
            assertTrue("Exception during logging: ${e.message}", false)
        }
    }
}
