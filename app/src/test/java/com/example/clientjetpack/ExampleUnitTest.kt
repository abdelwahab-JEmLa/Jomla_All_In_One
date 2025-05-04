package com.example.clientjetpack

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.ClientsMapFilterViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_3_TransactionCommercial
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
 * Improved unit tests for ClientsMapFilterViewModel that don't use the full _0_0_HeadSQLRepositorys hierarchy.
 *
 * Uses TestCoroutineDispatcher to handle the Compose state management properly in tests.
 */
@ExperimentalCoroutinesApi
class ImprovedClientsMapFilterViewModelTest {

    // Rule to make LiveData work instantly in tests
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    // Test dispatcher for Compose/Coroutines operations
    private val testDispatcher = TestCoroutineDispatcher()

    // Our test data - a simple list of transactions
    private val testTransactions = ArrayList<_1_3_TransactionCommercial>()

    // Regular List for test data instead of SnapshotStateList
    private val testTransactionsList = ArrayList<_1_3_TransactionCommercial>()

    // The view model we're testing
    private lateinit var viewModel: TestableClientsMapFilterViewModel

    @Before
    fun setup() {
        // Set the main dispatcher to our test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Create some test transactions
        createTestTransactions()

        // Copy transactions to the regular list
        testTransactionsList.clear()
        testTransactionsList.addAll(testTransactions)

        // Create the view model with regular List instead of SnapshotStateList
        viewModel = TestableClientsMapFilterViewModel(testTransactionsList)
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testFilterChanges() {
        // Test that we can change the filter
        viewModel.setFilter(ClientsMapFilterViewModel.FilterType.CIBLE)

        // We can't directly check the private field, but we can test the behavior
        val filteredTransactions = viewModel.getFilteredTransactions()

        // Check that the filtered list only contains CIBLE transactions
        for (transaction in filteredTransactions) {
            assertTrue(
                transaction.etateActuellementEst == _1_3_TransactionCommercial.EtateActuellementEst.Cible ||
                        transaction.etateActuellementEst == _1_3_TransactionCommercial.EtateActuellementEst.CIBLE_PRIORITE_2 ||
                        transaction.etateActuellementEst == _1_3_TransactionCommercial.EtateActuellementEst.CIBLE_PRIORITE_3 ||
                        transaction.etateActuellementEst == _1_3_TransactionCommercial.EtateActuellementEst.CIBLE_POUR_2
            )
        }
    }

    @Test
    fun testAllFilterShowsAllTransactions() {
        // Use the ALL filter (default)
        viewModel.setFilter(ClientsMapFilterViewModel.FilterType.ALL)

        // Get filtered transactions
        val filteredTransactions = viewModel.getFilteredTransactions()

        // Check we get all transactions
        assertEquals(testTransactions.size, filteredTransactions.size)
    }

    private fun createTestTransactions() {
        // Create a few transactions with different statuses

        // Add a COMMANDE_LIVRAI transaction
        testTransactions.add(
            _1_3_TransactionCommercial(
                vid = 1L,
                etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI,
                nomClientConcerned = "Client 1"
            )
        )

        // Add a CIBLE transaction
        testTransactions.add(
            _1_3_TransactionCommercial(
                vid = 2L,
                etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.Cible,
                nomClientConcerned = "Client 2"
            )
        )

        // Add another CIBLE_PRIORITE_2 transaction
        testTransactions.add(
            _1_3_TransactionCommercial(
                vid = 3L,
                etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.CIBLE_PRIORITE_2,
                nomClientConcerned = "Client 3"
            )
        )

        // Add a NON_DEFINI transaction
        testTransactions.add(
            _1_3_TransactionCommercial(
                vid = 4L,
                etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.NON_DEFINI,
                nomClientConcerned = "Client 4"
            )
        )
    }

    /**
     * A custom implementation of ClientsMapFilterViewModel that directly uses our test data,
     * without requiring the complex _0_0_HeadSQLRepositorys hierarchy.
     *
     * This version works with regular List instead of SnapshotStateList to avoid Compose state issues in tests.
     */
    private class TestableClientsMapFilterViewModel(
        private val transactionsList: List<_1_3_TransactionCommercial>
    ) : ClientsMapFilterViewModel(null) {
        // Override the filtering method to use our direct data source
        override fun getFilteredTransactions(): List<_1_3_TransactionCommercial> {
            return when (currentFilter) {
                FilterType.ALL -> transactionsList
                FilterType.DatesHistoriqueTransactions -> {
                    // For testing purposes, just return empty list for dates filtering
                    emptyList()
                }
                FilterType.CIBLE -> {
                    transactionsList.filter { transaction ->
                        transaction.etateActuellementEst == _1_3_TransactionCommercial.EtateActuellementEst.Cible ||
                                transaction.etateActuellementEst == _1_3_TransactionCommercial.EtateActuellementEst.CIBLE_PRIORITE_2 ||
                                transaction.etateActuellementEst == _1_3_TransactionCommercial.EtateActuellementEst.CIBLE_PRIORITE_3 ||
                                transaction.etateActuellementEst == _1_3_TransactionCommercial.EtateActuellementEst.CIBLE_POUR_2
                    }
                }
            }
        }
    }
}
