package com.example.clientjetpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Logs.A_LogMapsIDSDatesHistoriqueTransactions
import com.example.clientjetpack.Logs.SqlDatasDatesHistoriqueTransactionslog
import com.example.clientjetpack.Repositorys.MapsIDSDatesHistoriqueTransactions
import com.example.clientjetpack.Repositorys.SqlDatasDatesHistoriqueTransactions
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
    fun remplitDayeAvecNoramalized(dayeStr:String): Unit {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 13) // 1 PM
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayTimestamp = calendar.timeInMillis
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
