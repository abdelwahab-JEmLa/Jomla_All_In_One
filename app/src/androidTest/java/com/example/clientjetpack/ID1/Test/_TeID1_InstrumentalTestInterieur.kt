package com.example.clientjetpack.ID1.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.InputEtInfosSqlModels
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.clientjetpack.ID1.Test.A_TarificationTestData.initialTestData
import com.example.clientjetpack.LogFilterRule
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class _TeID1_InstrumentalTestInterieur : KoinTest {
    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val combinedLogFilter = LogFilterRule.filter()
        .filterByTag("InstrumentalTest")
        .filterByTag("onOperationSuccess")
        .filterByTag("OperationTrackerImp")
        .build()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fireBaseHandler: FireBaseHandler

    private val parentDbRef: DatabaseReference =
        _0_0_HeadOfRepositorys_Model.getHeadSqlDataBaseRef()
            .child("C_InputEtInfosSql")

    private val sonDataBaseRef: DatabaseReference = parentDbRef.child("A_Tarification")

    // Add an instance of OperationTrackerImp to handle the interface methods
    private val operationTrackerImp = OperationTrackerImp()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fireBaseHandler = FireBaseHandler(operationTrackerImp)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun testClearDatabase() = runTest {
        // Reset counter before the test
        operationTrackerImp.restartConter()

        // Clear database
        fireBaseHandler.clearDatabaseAsync(sonDataBaseRef)

        assertEquals(
            "Counter should be 1 after database clear",
            1,
            operationTrackerImp.getCounterAlgorithmeCounteAssertSuiveur()
        )
    }

    fun testAdd ()= runTest {
        fireBaseHandler.addAllToFireBaseAsync(initialTestData, sonDataBaseRef)
        // Print current counter value for debugging
        val counterAfterAdd = operationTrackerImp.getCounterAlgorithmeCounteAssertSuiveur()
        println("Counter after add: $counterAfterAdd")

        assertEquals(
            2,
            counterAfterAdd
        )
    }

    @Test
    fun testLoadData() = runTest {
        val result = fireBaseHandler.loadDatasAsync(
            sonDataBaseRef,
            InputEtInfosSqlModels.Tarification::class.java
        )

        assertEquals(
            3,
            result.size)

        assertEquals(
            5,
            operationTrackerImp.getCounterAlgorithmeCounteAssertSuiveur()
        )

        // Check for specific items
        val caramelItem = result.find {
            it.idProduit == 1L &&
                    it.idClient == 1L &&
                    it.idTypeTarification == 1L &&
                    it.prixCurrency == 2.99
        }
        assertTrue("Caramel item not found", caramelItem != null)

        val chocolatItem = result.find {
            it.idProduit == 2L &&
                    it.idClient == 2L &&
                    it.idTypeTarification == 2L &&
                    it.prixCurrency == 4.99
        }
        assertTrue("Chocolat item not found", chocolatItem != null)
    }
}
