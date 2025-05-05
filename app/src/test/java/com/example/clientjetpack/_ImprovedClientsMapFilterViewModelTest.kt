package com.example.clientjetpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
    fun testLogFunctions() {
        try {
            // Log MapsIDSDatesHistoriqueTransactions structure
            A_LogMapsIDSDatesHistoriqueTransactions(mapsIDsDatesHistorique)


            // If we reach here without exceptions, test passes
            assertTrue(true)
        } catch (e: Exception) {
            // If an exception occurs, fail the test
            assertTrue("Exception during logging: ${e.message}", false)
        }
    }
    @Test
    fun testLogSqlDatasDatesHistoriqueTransactionslog() {
        try {

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
