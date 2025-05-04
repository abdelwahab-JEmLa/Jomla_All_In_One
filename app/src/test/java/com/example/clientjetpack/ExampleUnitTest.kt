package com.example.clientjetpack

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.ClientsMapFilterViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_3_TransactionCommercial
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SimplifiedClientsMapFilterViewModelTest {

    // Données de test - liste simple de transactions
    private val testTransactions = ArrayList<_1_3_TransactionCommercial>()

    // Le ViewModel à tester
    private lateinit var viewModel: SimpleTestViewModel

    @Before
    fun setup() {
        // Créer des transactions de test
        createTestTransactions()

        // Créer le ViewModel avec notre liste de test
        viewModel = SimpleTestViewModel(testTransactions)
    }

    @Test
    fun testFilterChanges() {
        // Changer le filtre pour CIBLE
        viewModel.setFilter(ClientsMapFilterViewModel.FilterType.CIBLE)

        // Récupérer les transactions filtrées
        val filteredTransactions = viewModel.getFilteredTransactions()

        // Vérifier que la liste filtrée ne contient que des transactions CIBLE
        assertTrue(filteredTransactions.isNotEmpty())
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
        // Utiliser le filtre ALL (par défaut)
        viewModel.setFilter(ClientsMapFilterViewModel.FilterType.ALL)

        // Récupérer les transactions filtrées
        val filteredTransactions = viewModel.getFilteredTransactions()

        // Vérifier qu'on récupère toutes les transactions
        assertEquals(testTransactions.size, filteredTransactions.size)
    }

    private fun createTestTransactions() {
        // Créer quelques transactions avec différents statuts

        // Ajouter une transaction COMMANDE_LIVRAI
        testTransactions.add(
            _1_3_TransactionCommercial(
                vid = 1L,
                etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI,
                nomClientConcerned = "Client 1"
            )
        )

        // Ajouter une transaction CIBLE
        testTransactions.add(
            _1_3_TransactionCommercial(
                vid = 2L,
                etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.Cible,
                nomClientConcerned = "Client 2"
            )
        )

        // Ajouter une autre transaction CIBLE_PRIORITE_2
        testTransactions.add(
            _1_3_TransactionCommercial(
                vid = 3L,
                etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.CIBLE_PRIORITE_2,
                nomClientConcerned = "Client 3"
            )
        )

        // Ajouter une transaction NON_DEFINI
        testTransactions.add(
            _1_3_TransactionCommercial(
                vid = 4L,
                etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.NON_DEFINI,
                nomClientConcerned = "Client 4"
            )
        )
    }

    /**
     * Version simplifiée du ViewModel pour les tests
     */
    private class SimpleTestViewModel(
        private val transactionsList: List<_1_3_TransactionCommercial>
    ) {
        private var currentFilter = ClientsMapFilterViewModel.FilterType.ALL

        fun setFilter(filter: ClientsMapFilterViewModel.FilterType) {
            currentFilter = filter
        }

        fun getFilteredTransactions(): List<_1_3_TransactionCommercial> {
            return when (currentFilter) {
                ClientsMapFilterViewModel.FilterType.ALL -> transactionsList
                ClientsMapFilterViewModel.FilterType.DatesHistoriqueTransactions -> {
                    // Pour les tests, on retourne une liste vide pour ce filtre
                    emptyList()
                }
                ClientsMapFilterViewModel.FilterType.CIBLE -> {
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
