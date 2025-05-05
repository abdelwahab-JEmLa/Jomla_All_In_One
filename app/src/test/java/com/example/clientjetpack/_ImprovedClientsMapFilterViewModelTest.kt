package com.example.clientjetpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Repositorys.MapsIDSDatesHistoriqueTransactions
import com.example.clientjetpack.Repositorys.SqlDatasDatesHistoriqueTransactions
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

        logDatesHistoriqueStructure(sqlDatasDatesHistoriqueTransactions)
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
}
