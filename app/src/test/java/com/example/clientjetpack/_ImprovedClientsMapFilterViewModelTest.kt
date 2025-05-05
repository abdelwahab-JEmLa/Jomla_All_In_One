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

    // Using StandardTestDispatcher instead of the deprecated TestCoroutineDispatcher
    private val testDispatcher = StandardTestDispatcher()

    // Our test data - a simple list of transactions
    private val testTransactions = ArrayList<TransactionCommercial>()

    // Store for the uniqueDays data
    private var uniqueDaysForTesting = mutableListOf<StrNomJourEtSonSemainToStartJourTimeTemp>()

    // Store for dates historique
    private lateinit var datesHistoriqueForTesting: DatesHistoriqueTransactions

    private var currentIdJourAuFilter = 1L
    private var currentFilter = FilterType.ALL

    @Before
    fun setup() {
        // Set the main dispatcher to our test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Create some test transactions
        val allTransactions = createTestTransactions()

        testTransactions.addAll(
            transactionCommercialsFiltre(allTransactions)
        )

        uniqueDaysForTesting = collectAddAuStrNomJourEtSonSemainToStartJourTimeTemp(testTransactions)

        datesHistoriqueForTesting = collecteAddAuDatesHistoriqueTransactions(uniqueDaysForTesting, testTransactions)
        logDatesHistoriqueStructure(datesHistoriqueForTesting)
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

    enum class FilterType {
        ALL,
        DatesHistoriqueTransactions,
        CIBLE,
    }
}
