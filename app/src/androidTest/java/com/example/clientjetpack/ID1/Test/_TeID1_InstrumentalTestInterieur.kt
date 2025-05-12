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
import kotlin.math.abs

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class _TeID1_InstrumentalTestInterieur : KoinTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

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

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fireBaseHandler = FireBaseHandler()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun testFullWorkflow() = runTest {
        fireBaseHandler.clearDatabaseAsync(sonDataBaseRef)
        fireBaseHandler.addAllToFireBaseAsync(initialTestData, sonDataBaseRef)

        val result = fireBaseHandler.loadDatasAsync(
            sonDataBaseRef,
            InputEtInfosSqlModels.Tarification::class.java
        )

        assertEquals("Expected 3 items in the database", 3, result.size)

        initialTestData.forEach { expectedItem ->
            val matchingItem = result.find { loadedItem ->
                loadedItem.idProduit == expectedItem.idProduit &&
                        loadedItem.idClient == expectedItem.idClient &&
                        loadedItem.idTypeTarification == expectedItem.idTypeTarification &&
                        loadedItem.prixCurrency == expectedItem.prixCurrency
            }

            assertTrue(
                "Could not find matching item for: " +
                        "idProduit=${expectedItem.idProduit}, " +
                        "idClient=${expectedItem.idClient}, " +
                        "idTypeTarification=${expectedItem.idTypeTarification}, " +
                        "prixCurrency=${expectedItem.prixCurrency}",
                matchingItem != null
            )

            if (expectedItem.vidTimestamp == initialTestData[0].vidTimestamp) {
                val currentTime = System.currentTimeMillis()
                val oneDayAgo = currentTime - 86400000

                assertTrue(
                    "First timestamp should be approximately 1 day ago",
                    abs((matchingItem?.vidTimestamp ?: 0) - oneDayAgo) < 10000
                )
            } else {
                assertEquals(
                    "Timestamp does not match for item: " +
                            "idProduit=${expectedItem.idProduit}, " +
                            "idClient=${expectedItem.idClient}",
                    expectedItem.vidTimestamp,
                    matchingItem?.vidTimestamp
                )
            }
        }
    }
}
