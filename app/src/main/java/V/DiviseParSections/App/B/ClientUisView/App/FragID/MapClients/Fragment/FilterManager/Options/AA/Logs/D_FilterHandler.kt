package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs

class D_FilterHandler {
    fun filterTransactionsByDay(
        sqlDatasDatesHistorique: Any,
        filterDateTimeTamp: Long
    ): List<V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.D_ParDatesHistoriqueTransactions_Repository.Any.Transaction> {
        // Find transactions for this specific day
        return sqlDatasDatesHistorique.transactions.filter { transaction ->
            isSameDay(transaction.timestamp, filterDateTimeTamp)
        }.sortedBy { it.timestamp }
    }
}

