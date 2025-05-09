package com.example.clientjetpack

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.D_Repo_TransactionCommercial
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.FirebaseTransactionProvider
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.Type
import kotlinx.coroutines.runBlocking
import java.util.Calendar

/**
 * Provides transaction data for testing and production purposes.
 * Handles fetching data from Firebase in production or generating mock data in tests.
 */
object TestTransactionDataProvider {

    // Flag to determine whether to use Firebase or mock data
    private var useFirebase = true

    // Cache for transactions to avoid unnecessary network calls
    private var cachedTransactions: List<D_Repo_TransactionCommercial>? = null
    private var lastCacheTime: Long = 0
    private const val CACHE_VALIDITY_MS = 5 * 60 * 1000 // 5 minutes

    /**
     * Sets whether to use Firebase or mock data
     */
    fun setUseFirebase(use: Boolean) {
        useFirebase = use
        // Clear cache when switching modes
        cachedTransactions = null
    }

    /**
     * Gets test transactions synchronously - used for tests and UI previews
     * This method will use mock data to avoid network calls in testing scenarios
     */
    fun getTestTransactions(): List<D_Repo_TransactionCommercial> {
        return getMockTransactions()
    }

    /**
     * Gets transactions asynchronously, either from Firebase or mock data based on configuration
     * Implements caching to avoid frequent network calls
     */
    suspend fun getTransactions(): List<D_Repo_TransactionCommercial> {
        // Check if we need to refresh cache
        val currentTime = System.currentTimeMillis()
        val cacheExpired = (currentTime - lastCacheTime) > CACHE_VALIDITY_MS

        if (cachedTransactions == null || cacheExpired) {
            cachedTransactions = if (useFirebase) {
                try {
                    // Fix: Directly return the result from fetchTransactionsFromFirebase
                    val firebaseData = FirebaseTransactionProvider.fetchTransactionsFromFirebase()
                    lastCacheTime = currentTime
                    firebaseData  // This is now properly returned
                } catch (e: Exception) {
                    println("Error fetching Firebase data: ${e.message}. Falling back to mock data.")
                    getMockTransactions()
                }
            } else {
                getMockTransactions()
            }
        }

        return cachedTransactions ?: emptyList()
    }

    /**
     * Clears the transaction cache, forcing the next call to fetch fresh data
     */
    fun clearCache() {
        cachedTransactions = null
        lastCacheTime = 0
    }

    /**
     * Creates mock transactions for testing purposes
     * This simulates data from Firebase with realistic test data
     */
    private fun getMockTransactions(): List<D_Repo_TransactionCommercial> {
        val transactions = mutableListOf<D_Repo_TransactionCommercial>()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        // Create transactions for two days: yesterday and today
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }

        // Client ID 174 - "الاخ لعمري اسماعيل" - Today
        transactions.add(createTransaction(
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
        ))

        transactions.add(createTransaction(
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
        ))

        // Client ID 177 - "الاخ زواوي محمد" - Yesterday
        transactions.add(createTransaction(
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
        ))

        transactions.add(createTransaction(
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
        ))

        // Client ID 180 - "الاخ سليم بن علي" - Today
        transactions.add(createTransaction(
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
        ))

        // Client ID 185 - "الاخ محمد بن عمر" - Yesterday
        transactions.add(createTransaction(
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
        ))

        // Add two more for variety
        transactions.add(createTransaction(
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
        ))

        transactions.add(createTransaction(
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
        ))

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
    ): D_Repo_TransactionCommercial {
        return D_Repo_TransactionCommercial(
            vid = vid,
            parentVID_1_4_PeriodeVent = parentId,
            clientAcheteurID = clientId,
            nomClientConcerned = clientName,
            timestamps = timestamp,
            heurDebutInString = heurDebut,
            etateActuellementEst = state
        )
    }

    /**
     * Helper function to create a timestamp for a specific date and time
     */
    private fun createTimestamp(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, hour, minute, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * Synchronously gets transaction data - useful for UI testing and quick data access
     * Attempts to use cached data or falls back to mock data
     */
    fun getTransactionsSync(): List<D_Repo_TransactionCommercial> {
        return cachedTransactions ?: run {
            if (useFirebase) {
                try {
                    // Note: This blocks the thread and should be avoided in production code
                    // It's provided for testing purposes only
                    runBlocking { getTransactions() }
                } catch (e: Exception) {
                    println("Error in synchronous Firebase fetch: ${e.message}")
                    getMockTransactions()
                }
            } else {
                getMockTransactions()
            }
        }
    }
}
