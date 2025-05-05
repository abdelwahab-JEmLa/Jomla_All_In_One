package com.example.clientjetpack.Tests.A.Filter

import com.example.clientjetpack.Repositorys.EtateActuellementEst
import com.example.clientjetpack.Repositorys.TransactionCommercial

/**
 * Gets filtered transactions based on the current filter type
 */
fun getFilteredTransactions(
    testTransactions: List<TransactionCommercial>,
    filterType: FilterType
): List<TransactionCommercial> {
    return when (filterType) {
        FilterType.ALL -> testTransactions
        FilterType.CIBLE -> {
            testTransactions.filter { it.etateActuellementEst == EtateActuellementEst.Cible }
        }
        FilterType.DatesHistoriqueTransactions -> {
            // In a real implementation, this would filter by date criteria
            testTransactions
        }
    }
}
