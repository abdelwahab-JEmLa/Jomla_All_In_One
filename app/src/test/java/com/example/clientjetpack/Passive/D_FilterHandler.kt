package com.example.clientjetpack.Passive

import com.example.clientjetpack.DB_ParDatesHistoriqueTransactions_Repository

class D_FilterHandler {
    fun filterTransactionsByDay(
        sqlDatasDatesHistorique: DB_ParDatesHistoriqueTransactions_Repository,
        filterDateTimeTamp: Long
    ): List<DB_ParDatesHistoriqueTransactions_Repository.Transaction> {
        // Find transactions for this specific day
        return sqlDatasDatesHistorique.transactions.filter { transaction ->
            isSameDay(transaction.timestamp, filterDateTimeTamp)
        }.sortedBy { it.timestamp }
    }
}

