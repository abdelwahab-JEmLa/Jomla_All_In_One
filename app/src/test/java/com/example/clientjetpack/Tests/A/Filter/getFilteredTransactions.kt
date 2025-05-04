package com.example.clientjetpack.Tests.A.Filter

import com.example.clientjetpack.ImprovedClientsMapFilterViewModelTest
import com.example.clientjetpack.ImprovedClientsMapFilterViewModelTest.FilterType
import com.example.clientjetpack.Repositorys.TransactionCommercial
import kotlinx.coroutines.ExperimentalCoroutinesApi

// Direct filtering function to replace ViewModel functionality
@OptIn(ExperimentalCoroutinesApi::class)
fun ImprovedClientsMapFilterViewModelTest.getFilteredTransactions(): List<TransactionCommercial> {
    return when (currentFilter) {
        ImprovedClientsMapFilterViewModelTest.FilterType.ALL -> testTransactions
        FilterType.DatesHistoriqueTransactions -> {
            testTransactions.filter { transaction ->
                transaction.etateActuellementEst == TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI
            }
        }

        FilterType.CIBLE -> {
            testTransactions.filter { transaction ->
                transaction.etateActuellementEst == TransactionCommercial.EtateActuellementEst.Cible ||
                        transaction.etateActuellementEst == TransactionCommercial.EtateActuellementEst.CIBLE_PRIORITE_2 ||
                        transaction.etateActuellementEst == TransactionCommercial.EtateActuellementEst.CIBLE_PRIORITE_3 ||
                        transaction.etateActuellementEst == TransactionCommercial.EtateActuellementEst.CIBLE_POUR_2
            }
        }
    }
}
