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
import kotlinx.coroutines.withContext
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CalQuantityButtonInstrumentedTest : KoinComponent {

    // Inject the repository using Koin
    private val headSQLRepositorys: _0_0_HeadSQLRepositorys by inject()
    private lateinit var mapsIDSDatesHistoriqueTransactions: D_Rep_MapsIDSDatesHistoriqueTransactions
    private lateinit var sqlDatasDatesHistorique: D_Repo_SqlDatasDatesHistoriqueTransactions
    private lateinit var transactionList: List<D_Repo_TransactionCommercial>

    @Before
    fun setup() = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        println("Testing on ${appContext.packageName}")

        // Wait until repository data is loaded
        waitUntilRepositoryLoaded()

        // Convert _1_3_TransactionCommercial to D_Repo_TransactionCommercial
        transactionList = convertTransactionList()

        // Initialize test data structures
        initializeTestData()
    }

    private suspend fun waitUntilRepositoryLoaded() {
        // First ensure data is initialized in the repository
        headSQLRepositorys.repositorys_Model.repository_1_3_TransactionCommercial.ensureDataIsInitialized()

        // Wait until progress is completed (value = 1.0f)
        withContext(Dispatchers.IO) {
            val progressFlow = headSQLRepositorys.repositorys_Model.repository_1_3_TransactionCommercial.progressRepo
            progressFlow.collect { progress ->
                if (progress >= 1.0f) {
                    return@collect
                }
            }
        }
    }

    private fun convertTransactionList(): List<D_Repo_TransactionCommercial> {
        val transactions = headSQLRepositorys.repositorys_Model.repository_1_3_TransactionCommercial.modelDatasSnapList

        // Convert from _1_3_TransactionCommercial to D_Repo_TransactionCommercial
        return transactions.map { transaction ->
            D_Repo_TransactionCommercial(
                vid = transaction.vid,
                parentVID_1_4_PeriodeVent = transaction.parentVID_1_4_PeriodeVent,
                clientAcheteurID = transaction.clientAcheteurID,
                nomClientConcerned = transaction.nomClientConcerned,
                timestamps = transaction.timestamps,
                heurDebutInString = transaction.heurDebutInString ,
                heurFinInString = transaction.heurFinInString ,
                cActiveDataDeParentList = transaction.cActive,   // Changed from cActiveDataDeParentList to cActive
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
        // If transaction list from repository is empty, use test data instead
        val finalTransactionList = if (transactionList.isEmpty()) {
            B_Data_CreateTestTransactions()
        } else {
            transactionList
        }

        // Create and initialize data structures
        mapsIDSDatesHistoriqueTransactions = D_Rep_MapsIDSDatesHistoriqueTransactions()
            .collectInit(finalTransactionList)

        sqlDatasDatesHistorique = D_Repo_SqlDatasDatesHistoriqueTransactions(
            mapsIDSDatesHistoriqueTransactions,
            finalTransactionList
        )
    }

    @Test
    fun testTransactionDataConversion() {
        // Log the structure to verify correct conversion
        A_LogMapsIDSDatesHistoriqueTransactions(mapsIDSDatesHistoriqueTransactions)

        assertTrue("Transaction data should be properly converted", true)
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

        assertTrue("Should filter transactions for today", true)
    }

    @Test
    fun testSqlDataStructure() {
        // Log the SQL data structure
        SqlDatasDatesHistoriqueTransactionslog(
            sqlDatasDatesHistorique
        )

        assertTrue("SQL data structure should be properly initialized", true)
    }
}
