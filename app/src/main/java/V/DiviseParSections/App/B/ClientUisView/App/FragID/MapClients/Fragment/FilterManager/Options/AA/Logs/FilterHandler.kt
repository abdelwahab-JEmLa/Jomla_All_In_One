package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs

class FilterHandler {
    fun filterTransactionsByDay(
        sqlDatasDatesHistorique: D_Repo_SqlDatasDatesHistoriqueTransactions,
        filterDateTimeTamp: Long
    ): List<D_Repo_SqlDatasDatesHistoriqueTransactions.Transaction> {
        // Find transactions for this specific day
        return sqlDatasDatesHistorique.transactions.filter { transaction ->
            isSameDay(transaction.timestamp, filterDateTimeTamp)
        }.sortedBy { it.timestamp }
    }

}

