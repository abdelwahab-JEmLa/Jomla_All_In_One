package com.example.clientjetpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Init.collectAddAuStrNomJourEtSonSemainToStartJourTimeTemp
import com.example.clientjetpack.Repositorys.DatesHistoriqueTransactions
import com.example.clientjetpack.Repositorys.StrNomJourEtSonSemainToStartJourTimeTemp
import com.example.clientjetpack.Repositorys.TransactionCommercial
import com.example.clientjetpack.Repositorys.createTestTransactions
import com.example.clientjetpack.Repositorys.logDatesHistoriqueStructure
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

    private var uniqueDaysForTesting = mutableListOf<StrNomJourEtSonSemainToStartJourTimeTemp>()

    private lateinit var datesHistoriqueForTesting: DatesHistoriqueTransactions

    private var currentFilter = FilterType.ALL
    private var currentIdJourAuFilter = 1L

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val allTransactionsId = createTestTransactions()

        testTransactions.addAll(allTransactionsId)

        uniqueDaysForTesting = collectAddAuStrNomJourEtSonSemainToStartJourTimeTemp(testTransactions)

        datesHistoriqueForTesting = DatesHistoriqueTransactions()
            .collectInit(uniqueDaysForTesting, testTransactions)
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

    @Test
    fun testDatesHistoriqueStructure() {
        // Verify weeks structure
        assert(datesHistoriqueForTesting.semaines.isNotEmpty()) { "Weeks map should not be empty" }

        // Verify days structure
        assert(datesHistoriqueForTesting.jours.isNotEmpty()) { "Days map should not be empty" }

        // Verify total counts
        val totalDays = datesHistoriqueForTesting.semaines.values.sumOf { it.size }
        val totalTransactions = datesHistoriqueForTesting.jours.values.sumOf { it.size }

        assertEquals("Day count should match across structure",
            uniqueDaysForTesting.size, totalDays)

        assertEquals("Transaction count should match across structure",
            testTransactions.size, totalTransactions)
    }
}
