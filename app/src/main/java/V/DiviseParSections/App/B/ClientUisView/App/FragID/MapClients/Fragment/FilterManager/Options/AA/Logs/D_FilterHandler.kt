package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs

class D_FilterHandler {
    fun filterTransactionsByDay(
        sqlDatasDatesHistorique: D_ParDatesHistoriqueTransactions_Repository,
        filterDateTimeTamp: Long
    ): List<D_ParDatesHistoriqueTransactions_Repository.Transaction> {
        // Find transactions for this specific day
        return sqlDatasDatesHistorique.transactions.filter { transaction ->
            isSameDay(transaction.timestamp, filterDateTimeTamp)
        }.sortedBy { it.timestamp }
    }
}

