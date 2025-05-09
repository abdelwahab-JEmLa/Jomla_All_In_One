package com.example.clientjetpack

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.D_TransactionCommercial_Repository
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.createTimestamp
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.Type
import java.util.Calendar

object TestTransactionDataProvider {

    fun getTransactions(): List<D_TransactionCommercial_Repository> {
        val transactions = mutableListOf<D_TransactionCommercial_Repository>()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        // Create transactions for two days: yesterday and today
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }

        // Client ID 174 - "الاخ لعمري اسماعيل" - Today
        transactions.add(
            createTransaction(
            vid = 1L,
            parentId = 2L,
            clientId = 174L,
            clientName = "الاخ لعمري اسماعيل",
            timestamp = createTimestamp(
                currentYear,
                today.get(Calendar.MONTH) + 1,
                today.get(Calendar.DAY_OF_MONTH),
                12, 30),
            heurDebut = "12:30",
            state = Type.A_COMMANDE_CONFIRME
        )
        )

        transactions.add(
            createTransaction(
            vid = 2L,
            parentId = 2L,
            clientId = 174L,
            clientName = "الاخ لعمري اسماعيل",
            timestamp = createTimestamp(
                currentYear,
                today.get(Calendar.MONTH) + 1,
                today.get(Calendar.DAY_OF_MONTH),
                14, 15),
            heurDebut = "14:15",
            state = Type.ON_MODE_COMMEND_ACTUELLEMENT
        )
        )

        // Client ID 177 - "الاخ زواوي محمد" - Yesterday
        transactions.add(
            createTransaction(
            vid = 8L,
            parentId = 2L,
            clientId = 177L,
            clientName = "الاخ زواوي محمد",
            timestamp = createTimestamp(
                currentYear,
                yesterday.get(Calendar.MONTH) + 1,
                yesterday.get(Calendar.DAY_OF_MONTH),
                21, 45),
            heurDebut = "21:45",
            state = Type.A_COMMANDE_CONFIRME
        )
        )

        transactions.add(
            createTransaction(
            vid = 9L,
            parentId = 2L,
            clientId = 177L,
            clientName = "الاخ زواوي محمد",
            timestamp = createTimestamp(
                currentYear,
                yesterday.get(Calendar.MONTH) + 1,
                yesterday.get(Calendar.DAY_OF_MONTH),
                22, 54),
            heurDebut = "22:54",
            state = Type.ON_MODE_COMMEND_ACTUELLEMENT
        )
        )

        // Client ID 180 - "الاخ سليم بن علي" - Today
        transactions.add(
            createTransaction(
            vid = 10L,
            parentId = 2L,
            clientId = 180L,
            clientName = "الاخ سليم بن علي",
            timestamp = createTimestamp(
                currentYear,
                today.get(Calendar.MONTH) + 1,
                today.get(Calendar.DAY_OF_MONTH),
                9, 10),
            heurDebut = "09:10",
            state = Type.AVEC_MARCHANDISE
        )
        )

        // Client ID 185 - "الاخ محمد بن عمر" - Yesterday
        transactions.add(
            createTransaction(
            vid = 11L,
            parentId = 2L,
            clientId = 185L,
            clientName = "الاخ محمد بن عمر",
            timestamp = createTimestamp(
                currentYear,
                yesterday.get(Calendar.MONTH) + 1,
                yesterday.get(Calendar.DAY_OF_MONTH),
                16, 20),
            heurDebut = "16:20",
            state = Type.ACHETEUR_NON_DISPO
        )
        )

        // Add two more for variety
        transactions.add(
            createTransaction(
            vid = 12L,
            parentId = 2L,
            clientId = 190L,
            clientName = "الاخ أحمد خالد",
            timestamp = createTimestamp(
                currentYear,
                today.get(Calendar.MONTH) + 1,
                today.get(Calendar.DAY_OF_MONTH),
                11, 5),
            heurDebut = "11:05",
            state = Type.COMMANDE_LIVRAI
        )
        )

        transactions.add(
            createTransaction(
            vid = 13L,
            parentId = 2L,
            clientId = 195L,
            clientName = "الاخ حسن محمود",
            timestamp = createTimestamp(
                currentYear,
                yesterday.get(Calendar.MONTH) + 1,
                yesterday.get(Calendar.DAY_OF_MONTH),
                13, 45),
            heurDebut = "13:45",
            state = Type.FERME
        )
        )

        return transactions
    }

    /**
     * Helper function to create a transaction with specific properties
     */
    private fun createTransaction(
        vid: Long,
        parentId: Long,
        clientId: Long,
        clientName: String,
        timestamp: Long,
        heurDebut: String,
        state: Type
    ): D_TransactionCommercial_Repository {
        return D_TransactionCommercial_Repository(
            vid = vid,
            parentVID_1_4_PeriodeVent = parentId,
            clientAcheteurID = clientId,
            nomClientConcerned = clientName,
            timestamps = timestamp,
            heurDebutInString = heurDebut,
            etateActuellementEst = state
        )
    }
}
