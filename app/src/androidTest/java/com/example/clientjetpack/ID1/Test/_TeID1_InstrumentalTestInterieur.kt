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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class _TeID1_InstrumentalTestInterieur : KoinTest, FireBaseHandler.OperationTracker {
    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val combinedLogFilter = LogFilterRule.filter()
        .filterByTag("InstrumentalTest")
        .filterByTag("onOperationSuccess")
        .build()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fireBaseHandler: FireBaseHandler

    private val parentDbRef: DatabaseReference =
        _0_0_HeadOfRepositorys_Model.getHeadSqlDataBaseRef()
            .child("C_InputEtInfosSql")

    private val sonDataBaseRef: DatabaseReference = parentDbRef.child("A_Tarification")

    private var result: List<InputEtInfosSqlModels.Tarification> = emptyList()

    private var algorithmeCounteAssertSuiveur = 0

    override fun incrementCounter() {
        algorithmeCounteAssertSuiveur += 1
    }

    override fun getCounterAlgorithmeCounteAssertSuiveur(): Int {
        return algorithmeCounteAssertSuiveur
    }

    override fun restartConter() {
        algorithmeCounteAssertSuiveur = 0
    }

    @Before
    fun setup() = runTest {
        Dispatchers.setMain(testDispatcher)

        fireBaseHandler = FireBaseHandler(this@_TeID1_InstrumentalTestInterieur)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun testTarificationDataLoad() = runTest {
        // Reset counter for the test assertions
        restartConter()

        result = fireBaseHandler.loadDatasAsync(
            sonDataBaseRef,
            InputEtInfosSqlModels.Tarification::class.java
        )
            .sortedBy { tarification -> tarification.vidTimestamp }

        // Verify data was loaded correctly
        assertEquals("Should load 3 tarification items", 3, result.size)
        assertEquals(
            "Counter should be 1 after loading data",
            1,
            getCounterAlgorithmeCounteAssertSuiveur()
        )

        // Verify data content is correct
        val sortedTestData = initialTestData.sortedBy { it.vidTimestamp }
        val expectedData = sortedTestData.first()

        val firstResult = result.first()
        assertEquals(expectedData, firstResult)
    }

    @Test
    fun test1() = runTest {
        // Reset counter before each test
        restartConter()

        fireBaseHandler.clearDatabaseAsync(sonDataBaseRef)
        // Expected counter value after clearDatabaseAsync: 1
        assertEquals(
            "Counter should be 1 after database clear",
            1,
            getCounterAlgorithmeCounteAssertSuiveur()
        )

        fireBaseHandler.addAllToFireBaseAsync(initialTestData, sonDataBaseRef)
        // Expected counter after adding 3 test items: 4 (1 + 3)
        assertEquals(
            "Counter should be 4 after adding test data (1+3)",
            4,
            getCounterAlgorithmeCounteAssertSuiveur()
        )
    }
}
