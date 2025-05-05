package com.example.clientjetpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Repositorys.MapsIDSDatesHistoriqueTransactions
import com.example.clientjetpack.Repositorys.SqlDatasDatesHistoriqueTransactions
import com.example.clientjetpack.Repositorys.TransactionCommercial
import com.example.clientjetpack.Repositorys.createTestTransactions
import com.example.clientjetpack.Repositorys.log
import com.example.clientjetpack.Tests.A.Filter.FilterType
import com.example.clientjetpack.Tests.A.Filter.getFilteredTransactions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class _ImprovedClientsMapFilterViewModelTest {
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val testTransactions = mutableListOf<TransactionCommercial>()

    private lateinit var datesHistoriqueForTesting: MapsIDSDatesHistoriqueTransactions
    private lateinit var sqlDatasDatesHistoriqueTransactions: SqlDatasDatesHistoriqueTransactions

    private var currentFilter = FilterType.ALL
    private var currentIdJourAuFilter = 1L

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val allTransactionsId = createTestTransactions()

        testTransactions.addAll(allTransactionsId)

        datesHistoriqueForTesting = MapsIDSDatesHistoriqueTransactions()
            .collectInit(testTransactions)

        sqlDatasDatesHistoriqueTransactions =
            SqlDatasDatesHistoriqueTransactions(datesHistoriqueForTesting, testTransactions)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testAllFilterShowsAllTransactions() {
        currentFilter = FilterType.ALL

        val filteredTransactions = getFilteredTransactions(testTransactions, currentFilter)

        assertEquals(testTransactions.size, filteredTransactions.size)
    }
    @Test
    fun testMapsIDsDatesHistoriqueTransactionsLogFunction() {
        // Create a fresh instance of MapsIDSDatesHistoriqueTransactions
        val mapsIDSDatesHistorique = MapsIDSDatesHistoriqueTransactions()
            .collectInit(testTransactions)

        // Since the log function outputs to println, we can't directly assert on its output
        // but we can verify that it completes without exceptions
        try {
            mapsIDSDatesHistorique.log()
            // If we reach here, no exception was thrown
            assert(true)
        } catch (e: Exception) {
            // If an exception is thrown, fail the test
            assert(false) { "Exception thrown during log function execution: ${e.message}" }
        }

        // Additional verification that the data structure is properly populated
        assert(mapsIDSDatesHistorique.semaines.isNotEmpty()) { "Semaines map should not be empty" }
        assert(mapsIDSDatesHistorique.jours.isNotEmpty()) { "Jours map should not be empty" }
        assert(mapsIDSDatesHistorique.clients.isNotEmpty()) { "Clients map should not be empty" }
        assert(mapsIDSDatesHistorique.transactions.isNotEmpty()) { "Transactions map should not be empty" }
    }

    @Test
    fun testLogDatesHistoriqueStructureFunction() {
        // Create a fresh SqlDatasDatesHistoriqueTransactions instance for testing
        val mapsIDSDatesHistorique = MapsIDSDatesHistoriqueTransactions()
            .collectInit(testTransactions)

        // Create SqlDatasDatesHistoriqueTransactions with the MapsIDSDatesHistoriqueTransactions
        val sqlDatasDatesHistorique = SqlDatasDatesHistoriqueTransactions(
            mapsIDSDatesHistorique,
            testTransactions
        )

        // Since the log function outputs to println, we can't directly assert on its output
        // but we can verify that it completes without exceptions
        try {
            sqlDatasDatesHistorique.log(testTransactions)
            // If we reach here, no exception was thrown
            assert(true)
        } catch (e: Exception) {
            // If an exception is thrown, fail the test
            assert(false) { "Exception thrown during log function execution: ${e.message}" }
        }

        // Verify that the data structure is properly populated
        assert(sqlDatasDatesHistorique.semaines.isNotEmpty()) { "Semaines list should not be empty" }
        assert(sqlDatasDatesHistorique.jours.isNotEmpty()) { "Jours list should not be empty" }
        assert(sqlDatasDatesHistorique.clients.isNotEmpty()) { "Clients list should not be empty" }
        assert(sqlDatasDatesHistorique.transactions.isNotEmpty()) { "Transactions list should not be empty" }

        // Verify that the relationships are maintained
        val weekCount = sqlDatasDatesHistorique.semaines.size
        val dayCount = sqlDatasDatesHistorique.jours.size
        val clientCount = sqlDatasDatesHistorique.clients.size
        val transactionCount = sqlDatasDatesHistorique.transactions.size

        // Check that these counts match the expected number of items in the test data
        assertEquals(weekCount, mapsIDSDatesHistorique.semaines.size)
        assertEquals(dayCount, mapsIDSDatesHistorique.jours.size)
        assertEquals(clientCount, mapsIDSDatesHistorique.clients.size)
        assertEquals(transactionCount, mapsIDSDatesHistorique.transactions.size)
    }
}
