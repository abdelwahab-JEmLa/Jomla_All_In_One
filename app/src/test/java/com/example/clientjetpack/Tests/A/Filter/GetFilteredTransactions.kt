package com.example.clientjetpack.Tests.A.Filter

import com.example.clientjetpack.Repositorys.TransactionCommercial
import com.example.clientjetpack._ImprovedClientsMapFilterViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Gets filtered transactions based on the current filter type
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun getFilteredTransactions(
    testTransactions: List<TransactionCommercial>,
    filterType: _ImprovedClientsMapFilterViewModelTest.FilterType
): List<TransactionCommercial> {
    return when (filterType) {
        _ImprovedClientsMapFilterViewModelTest.FilterType.ALL -> testTransactions
        _ImprovedClientsMapFilterViewModelTest.FilterType.CIBLE -> {
            testTransactions.filter { it.etateActuellementEst == TransactionCommercial.EtateActuellementEst.Cible }
        }
        _ImprovedClientsMapFilterViewModelTest.FilterType.DatesHistoriqueTransactions -> {
            // In a real implementation, this would filter by date criteria
            testTransactions
        }
    }
}
