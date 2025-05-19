package com.example.clientjetpack.ID3.Test

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.InfosSqlDataBasesRepository
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.DataBasesInfosSql
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.Function.testDatasDataBasesInfosSql
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.B.NoSQL.Repository.ConvertiseurNoSqlToSqlRepositorys
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitsNoSqlDataBase
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.testDatasProduitNoSqlDataBase
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.clientjetpack.Modules.LogFilterRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class __ID3InstrumentalTest : KoinTest {
    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val combinedLogFilter = LogFilterRule.filter()
        .filterByTag("InstrumentalTest")
        .filterByTag("onOperationSuccess")
        .filterByTag("OperationTrackerImp")
        .build()

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val infosSqlDataBasesRepository: InfosSqlDataBasesRepository by inject()
    private val convertiseurNoSqlToSqlRepositorys: ConvertiseurNoSqlToSqlRepositorys by inject()

    @Before
    fun setup() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        stopKoin()

        val testDispatcher = StandardTestDispatcher(testScheduler)

        startKoin {
        }
        testScheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }


    private fun assertDataMatchesExpectedNoSql(
        expected: ProduitsNoSqlDataBase,
        actual: ProduitsNoSqlDataBase
    ) {
        // Compare the top-level list size
        assertEquals(
            "Products list size should match",
            expected.produits.size,
            actual.produits.size
        )

        // Compare each product
        expected.produits.forEach { expectedProduit ->
            val actualProduit = actual.produits.find { it.infosId == expectedProduit.infosId }
                ?: throw AssertionError("Product with ID ${expectedProduit.infosId} not found")

            // Compare client list size
            assertEquals(
                "Client list size should match for product ID ${expectedProduit.infosId}",
                expectedProduit.clientAchteurs.size,
                actualProduit.clientAchteurs.size
            )

            // Compare each client
            expectedProduit.clientAchteurs.forEach { expectedClient ->
                val actualClient =
                    actualProduit.clientAchteurs.find { it.infosId == expectedClient.infosId }
                        ?: throw AssertionError("Client with ID ${expectedClient.infosId} not found for product ID ${expectedProduit.infosId}")

                // Compare type tarification list size
                assertEquals(
                    "Type tarification list size should match for client ID ${expectedClient.infosId}",
                    expectedClient.typeTarification.size,
                    actualClient.typeTarification.size
                )

                // Compare each type tarification
                expectedClient.typeTarification.forEach { expectedType ->
                    val actualType =
                        actualClient.typeTarification.find { it.infosId == expectedType.infosId }
                            ?: throw AssertionError("Type tarification with ID ${expectedType.infosId} not found for client ID ${expectedClient.infosId}")

                    // Compare prices list size
                    assertEquals(
                        "Prices list size should match for type tarification ID ${expectedType.infosId}",
                        expectedType.PrixsCurrency.size,
                        actualType.PrixsCurrency.size
                    )

                    // Compare each price
                    expectedType.PrixsCurrency.forEach { expectedPrix ->
                        val actualPrix =
                            actualType.PrixsCurrency.find { it.vidTimestamp == expectedPrix.vidTimestamp }
                                ?: throw AssertionError("Price with timestamp ${expectedPrix.vidTimestamp} not found for type tarification ID ${expectedType.infosId}")

                        assertEquals(
                            "Price value should match for timestamp ${expectedPrix.vidTimestamp}",
                            expectedPrix.valeur,
                            actualPrix.valeur,
                            0.01
                        )
                    }
                }
            }
        }
    }


    @Test
    fun testFlowWorksAndAssertEqualsTestData() = runTest(testDispatcher) {
        // First, delete all existing data
        infosSqlDataBasesRepository.deleteAllRoom()
        // Advance time to process all coroutines related to deletion
        testScheduler.advanceUntilIdle()

        // Add test data
        infosSqlDataBasesRepository.upsert(testDatasDataBasesInfosSql())
        // Advance time to process all coroutines related to adding data
        testScheduler.advanceUntilIdle()

        // Now collect and verify data from the repository
        val actualDataList = infosSqlDataBasesRepository.modelListFlow.first()
        val actualData = actualDataList.firstOrNull()
            ?: throw AssertionError("Expected data not found")
        assertDataMatchesExpected(testDatasDataBasesInfosSql(), actualData)

        // Explicitly force the refresh of NoSQL data
        convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()
        testScheduler.advanceUntilIdle()

        // Collect and verify NoSQL data
        val actualDatanoSqlDataFlow = convertiseurNoSqlToSqlRepositorys.noSqlDataFlow.first()
        assertDataMatchesExpectedNoSql(testDatasProduitNoSqlDataBase(), actualDatanoSqlDataFlow)
    }

    private fun assertDataMatchesExpected(expected: DataBasesInfosSql, actual: DataBasesInfosSql) {
        Products(expected, actual)
        Clients(expected, actual)
        Tarifications(expected, actual)
        TypeTarifications(expected, actual)
    }
}
