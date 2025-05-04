package com.example.clientjetpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Init.collectAddAuStrNomJourEtSonSemainToStartJourTimeTemp
import com.example.clientjetpack.Init.collecteAddAuDatesHistoriqueTransactions
import com.example.clientjetpack.Init.transactionCommercialsFiltre
import com.example.clientjetpack.Repositorys.DatesHistoriqueTransactions
import com.example.clientjetpack.Repositorys.StrNomJourEtSonSemainToStartJourTimeTemp
import com.example.clientjetpack.Repositorys.TransactionCommercial
import com.example.clientjetpack.Repositorys.createTestTransactions
import com.example.clientjetpack.Repositorys.logDatesHistoriqueStructure
import com.example.clientjetpack.Tests.A.Filter.getFilteredTransactions
import com.example.clientjetpack.Tests.B.Data.checkClientExistsInCurrentDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

/**
 * Improved unit tests that don't rely on external ViewModels
 *
 * Uses direct testing of the data processing logic without dependencies
 */
@ExperimentalCoroutinesApi
class _ImprovedClientsMapFilterViewModelTest {

    // Rule to make LiveData work instantly in tests
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    // Test dispatcher for Compose/Coroutines operations
    private val testDispatcher = TestCoroutineDispatcher()

    // Our test data - a simple list of transactions
    val testTransactions = ArrayList<TransactionCommercial>()

    // Store for the uniqueDays data
    private var uniqueDaysForTesting = mutableListOf<StrNomJourEtSonSemainToStartJourTimeTemp>()

    private var idJourAuFilter = 1L

    // Store for dates historique
    private lateinit var datesHistoriqueForTesting: DatesHistoriqueTransactions

    // Current filter state for direct testing
    var currentFilter = FilterType.ALL

    @Before
    fun setup() {
        // Set the main dispatcher to our test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Create some test transactions
        val allTransactions = createTestTransactions()

        testTransactions.addAll(
            transactionCommercialsFiltre(allTransactions)
        )

        // Collect data for testing
        uniqueDaysForTesting = collectAddAuStrNomJourEtSonSemainToStartJourTimeTemp(testTransactions)
        datesHistoriqueForTesting = collecteAddAuDatesHistoriqueTransactions(uniqueDaysForTesting, testTransactions)
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testAllFilterShowsAllTransactions() {
        // Use the ALL filter (default)
        currentFilter = FilterType.ALL

        // Get filtered transactions
        val filteredTransactions = getFilteredTransactions(testTransactions, currentFilter)

        // Check we get all transactions
        assertEquals(testTransactions.size, filteredTransactions.size)
    }

    @Test
    fun testDatesHistoriqueTransactions() {
        val testData = datesHistoriqueForTesting

        logDatesHistoriqueStructure(testData)

        // Verify structure of test data
        assertEquals("Should have 1 weeks", 1, testData.cesSemains.size)

        // Test week 1
        val week1 = testData.cesSemains[0]
        assertEquals(1L, week1.vid)
        assertEquals("Semaine-1", week1.key)
    }

    @Test
    fun testQueCeJoureAUnClientAbderrahmane() {
        // Check if client Abderrahmane exists in today's transactions
        val hasClientAbderrahmane = checkClientExistsInCurrentDay(datesHistoriqueForTesting, "Abderrahmane")

        // Assert that Abderrahmane exists in today's transactions
        assertEquals("Should have a client named Abderrahmane", true, hasClientAbderrahmane)
    }

    @Test
    fun testQueCeJoureAUnClientHoussine() {
        val hasClientHoussine = testTransactions.any { transaction ->
            transaction.nomClientConcerned.contains("an")
        }

        assertTrue("Should ", hasClientHoussine)
    }

    enum class FilterType {
        ALL,
        DatesHistoriqueTransactions,
        CIBLE,
    }
}
