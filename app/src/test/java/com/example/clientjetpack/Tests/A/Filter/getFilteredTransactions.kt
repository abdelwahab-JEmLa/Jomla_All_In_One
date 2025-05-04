package com.example.clientjetpack.Tests.A.Filter

import com.example.clientjetpack.Repositorys.TransactionCommercial
import com.example.clientjetpack._ImprovedClientsMapFilterViewModelTest
import com.example.clientjetpack._ImprovedClientsMapFilterViewModelTest.FilterType
import kotlinx.coroutines.ExperimentalCoroutinesApi

// Direct filtering function to replace ViewModel functionality
@OptIn(ExperimentalCoroutinesApi::class)
fun _ImprovedClientsMapFilterViewModelTest.getFilteredTransactions(): List<TransactionCommercial> {
    return when (currentFilter) {
        FilterType.ALL -> testTransactions
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
