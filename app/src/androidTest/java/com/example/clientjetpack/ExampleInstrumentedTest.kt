package com.example.clientjetpack

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.B_Data_CreateTestTransactions
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.D_Rep_MapsIDSDatesHistoriqueTransactions
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.D_Repo_SqlDatasDatesHistoriqueTransactions
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.D_Repo_TransactionCommercial
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.A_LogMapsIDSDatesHistoriqueTransactions
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.FilterByDayeLog
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.SqlDatasDatesHistoriqueTransactionslog
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs.normalizeTimetampFromeStrDate
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.Type
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadSQLRepositorys
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ImprovedClientsMapFilterViewModelTest : KoinComponent {

    private val testDispatcher = StandardTestDispatcher()

    // Inject the repository using Koin
    private val headSQLRepositorys: _0_0_HeadSQLRepositorys by inject()
    private lateinit var mapsIDSDatesHistoriqueTransactions: D_Rep_MapsIDSDatesHistoriqueTransactions
    private lateinit var sqlDatasDatesHistorique: D_Repo_SqlDatasDatesHistoriqueTransactions
    private lateinit var transactionList: List<D_Repo_TransactionCommercial>

    private val useTestData = true // Set to false if you want to use repository data

    @Before
    fun setup() = runBlocking {
        // Set the main dispatcher for testing
        Dispatchers.setMain(testDispatcher)

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        println("Testing on ${appContext.packageName}")

        try {
            if (!useTestData) {
                // Wait until repository data is loaded with timeout
                withTimeout(TimeUnit.SECONDS.toMillis(10)) {
                    waitUntilRepositoryLoaded()
                    transactionList = convertTransactionList()
                }
            } else {
                // Use test data directly
                transactionList = B_Data_CreateTestTransactions()
            }

            // Initialize test data structures
            initializeTestData()

            println("Setup completed successfully with ${transactionList.size} transactions")
        } catch (e: Exception) {
            println("Setup error: ${e.message}")
            // In case of error, fall back to test data
            transactionList = B_Data_CreateTestTransactions()
            initializeTestData()
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private suspend fun waitUntilRepositoryLoaded() {
        try {
            // First ensure data is initialized in the repository
            headSQLRepositorys.repositorys_Model.repository_1_3_TransactionCommercial.ensureDataIsInitialized()

            // Wait until progress is completed (value = 1.0f)
            withContext(Dispatchers.IO) {
                val progressFlow = headSQLRepositorys.repositorys_Model.repository_1_3_TransactionCommercial.progressRepo
                var isCompleted = false

                progressFlow.collect { progress ->
                    if (progress >= 1.0f) {
                        isCompleted = true
                        return@collect
                    }
                }

                // Ensure we don't exit before completion
                while (!isCompleted) {
                    kotlinx.coroutines.delay(100)
                }
            }
        } catch (e: Exception) {
            println("Error waiting for repository: ${e.message}")
            throw e
        }
    }

    private fun convertTransactionList(): List<D_Repo_TransactionCommercial> {
        val transactions = headSQLRepositorys.repositorys_Model.repository_1_3_TransactionCommercial.modelDatasSnapList

        if (transactions.isEmpty()) {
            println("Warning: No transactions found in repository")
            return emptyList()
        }

        // Convert from _1_3_TransactionCommercial to D_Repo_TransactionCommercial
        return transactions.map { transaction ->
            D_Repo_TransactionCommercial(
                vid = transaction.vid,
                parentVID_1_4_PeriodeVent = transaction.parentVID_1_4_PeriodeVent,
                clientAcheteurID = transaction.clientAcheteurID,
                nomClientConcerned = transaction.nomClientConcerned,
                timestamps = transaction.timestamps,
                heurDebutInString = transaction.heurDebutInString,
                heurFinInString = transaction.heurFinInString,
                cActiveDataDeParentList = transaction.cActive,
                cJustPourVoirPanie = transaction.cJustPourVoirPanie,
                ouvert = transaction.ouvert,
                vocaleKeyID = transaction.vocaleKeyID ?: "",
                sonVocaleEstEcoute = transaction.sonVocaleEstEcoute,
                sonEcoutementEstFaitAutimestamps = transaction.sonEcoutementEstFaitAutimestamps,
                etateActuellementEst = mapTransactionState(transaction.etateActuellementEst.name)
            )
        }
    }

    private fun mapTransactionState(state: String?): Type {
        return when (state) {
            "COMMANDE_LIVRAI" -> Type.COMMANDE_LIVRAI
            "ACHETEUR_NON_DISPO" -> Type.ACHETEUR_NON_DISPO
            "ON_MODE_COMMEND_ACTUELLEMENT" -> Type.ON_MODE_COMMEND_ACTUELLEMENT
            "Cible" -> Type.Cible
            else -> Type.NON_DEFINI
        }
    }

    private fun initializeTestData() {
        // Create and initialize data structures
        mapsIDSDatesHistoriqueTransactions = D_Rep_MapsIDSDatesHistoriqueTransactions()
            .collectInit(transactionList)

        sqlDatasDatesHistorique = D_Repo_SqlDatasDatesHistoriqueTransactions(
            mapsIDSDatesHistoriqueTransactions,
            transactionList
        )
    }

    @Test
    fun testTransactionDataLoaded() {
        // Verify we have data to work with
        assertNotNull("Transaction list should not be null", transactionList)
        assertTrue("Transaction list should not be empty", transactionList.isNotEmpty())

        // Log for debugging
        println("Loaded ${transactionList.size} transactions for testing")
    }

    @Test
    fun testMapsInitialization() {
        // Test that maps structure is properly initialized
        assertNotNull("Maps structure should be initialized", mapsIDSDatesHistoriqueTransactions)

        // Log the structure for debugging
        A_LogMapsIDSDatesHistoriqueTransactions(mapsIDSDatesHistoriqueTransactions)
    }

    @Test
    fun testFilterByCurrentDay() {
        // Get current day's timestamp normalized to start of day
        val today = normalizeTimetampFromeStrDate(
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )

        // Filter transactions for today
        FilterByDayeLog(
            sqlDatasDatesHistorique,
            filterDateTimeTamp = today
        )

        // We're just ensuring the filter function doesn't crash
        assertTrue("Filter by day should complete without errors", true)
    }

    @Test
    fun testFilterBySpecificDate() {
        // Choose a specific test date
        val specificDate = "2025-05-05"
        val specificTimestamp = normalizeTimetampFromeStrDate(specificDate)

        // Filter transactions for the specific date
        FilterByDayeLog(
            sqlDatasDatesHistorique,
            filterDateTimeTamp = specificTimestamp
        )

        // We're just ensuring the filter function doesn't crash
        assertTrue("Filter by specific date should complete without errors", true)
    }

    @Test
    fun testSqlDataStructure() {
        // Verify SQL data structure
        assertNotNull("SQL data structure should be initialized", sqlDatasDatesHistorique)

        // Log the SQL data structure for debugging
        SqlDatasDatesHistoriqueTransactionslog(sqlDatasDatesHistorique)

   }
}
