package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs

class FilterHandler {
    fun filterTransactionsByDay(
        sqlDatasDatesHistorique: D_SqlDatasDatesHistoriqueTransactions_Repository,
        filterDateTimeTamp: Long
    ): List<D_SqlDatasDatesHistoriqueTransactions_Repository.Transaction> {
        // Find transactions for this specific day
        return sqlDatasDatesHistorique.transactions.filter { transaction ->
            isSameDay(transaction.timestamp, filterDateTimeTamp)
        }.sortedBy { it.timestamp }
    }
}

