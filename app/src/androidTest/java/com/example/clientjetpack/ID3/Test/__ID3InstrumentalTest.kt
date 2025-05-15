package com.example.clientjetpack.ID3.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.clientjetpack.ID3.Test.DataBase.FireBase.ConvertiseurNoSqlToSqlRepository
import com.example.clientjetpack.ID3.Test.DataBase.FireBase.Model.ProduitNoSqlDataBase
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Home.FireBaseHandler
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Home.TestAppDatabase
import com.example.clientjetpack.ID3.Test.DataBase.SQL.InfosSqlDataBasesRepository
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.DataBasesInfosSql
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
import org.koin.dsl.module
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
    private val convertiseurNoSqlToSqlRepository: ConvertiseurNoSqlToSqlRepository by inject()
    private val testDatas = testDatas()

    @Before
    fun setup() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        stopKoin()

        val testDispatcher = StandardTestDispatcher(testScheduler)

        startKoin {
            modules(
                module {
                    single {
                        TestAppDatabase.getTestDatabase(InstrumentationRegistry.getInstrumentation().targetContext)
                    }
                    single {
                        InfosSqlDataBasesRepository(
                            get(),
                            get(),
                            testDispatcher,
                        )
                    }
                    single { FireBaseHandler() }
                    single {
                        ConvertiseurNoSqlToSqlRepository(
                            get(),
                            testDispatcher,
                        )
                    }
                })
        }
        testScheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }


    private fun assertDataMatchesExpectedNoSql(
        expected: ProduitNoSqlDataBase,
        actual: ProduitNoSqlDataBase
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

    fun testDatasProduitNoSqlDataBase(): ProduitNoSqlDataBase {
        // This should match the structure expected after conversion from testDatas()
        return ProduitNoSqlDataBase(
            produits = listOf(
                // Produit 1: "Produit Optila"
                ProduitNoSqlDataBase.Produit(
                    vidTimestamp = System.currentTimeMillis(),  // Exact timestamp doesn't matter in test
                    infosId = 1,
                    clientAchteurs = listOf(
                        // Client 1: "ClientAchteur Abderrahman"
                        ProduitNoSqlDataBase.Produit.ClientAchteur(
                            vidTimestamp = System.currentTimeMillis(),
                            infosId = 1,
                            typeTarification = listOf(
                                // Type Tarification 1: "ParBenifice"
                                ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                    vidTimestamp = System.currentTimeMillis(),
                                    infosId = 1,
                                    PrixsCurrency = listOf(
                                        // Two prices for this combination
                                        ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                                            vidTimestamp = createTimestamp(
                                                day = 1,
                                                hour = 12,
                                                minute = 30
                                            ),
                                            valeur = 20.99
                                        ),
                                        ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                                            vidTimestamp = createTimestamp(
                                                day = 5,
                                                hour = 13,
                                                minute = 30
                                            ),
                                            valeur = 25.50
                                        )
                                    )
                                )
                            )
                        ),
                        // Client 2: "ClientAchteur Beta"
                        ProduitNoSqlDataBase.Produit.ClientAchteur(
                            vidTimestamp = System.currentTimeMillis(),
                            infosId = 2,
                            typeTarification = listOf(
                                // Type Tarification 2: "Historique"
                                ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                    vidTimestamp = System.currentTimeMillis(),
                                    infosId = 2,
                                    PrixsCurrency = listOf(
                                        ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                                            vidTimestamp = createTimestamp(
                                                day = 5,
                                                hour = 14,
                                                minute = 30
                                            ),
                                            valeur = 9.75
                                        )
                                    )
                                )
                            )
                        )
                    )
                ),
                // Produit 2: "Produit Hnina"
                ProduitNoSqlDataBase.Produit(
                    vidTimestamp = System.currentTimeMillis(),
                    infosId = 2,
                    clientAchteurs = listOf(
                        // Client 1: "ClientAchteur Abderrahman"
                        ProduitNoSqlDataBase.Produit.ClientAchteur(
                            vidTimestamp = System.currentTimeMillis(),
                            infosId = 1,
                            typeTarification = listOf(
                                // Type Tarification 1: "ParBenifice"
                                ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                    vidTimestamp = System.currentTimeMillis(),
                                    infosId = 1,
                                    PrixsCurrency = listOf(
                                        ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                                            vidTimestamp = createTimestamp(
                                                day = 6,
                                                hour = 3,
                                                minute = 30
                                            ),
                                            valeur = 15.25
                                        )
                                    )
                                )
                            )
                        )
                    )
                ),
                // Produit 3: "Produit kemya"
                ProduitNoSqlDataBase.Produit(
                    vidTimestamp = System.currentTimeMillis(),
                    infosId = 3,
                    clientAchteurs = listOf(
                        // Client 1: "ClientAchteur Abderrahman"
                        ProduitNoSqlDataBase.Produit.ClientAchteur(
                            vidTimestamp = System.currentTimeMillis(),
                            infosId = 1,
                            typeTarification = listOf(
                                // Type Tarification 3: "LeMaxPrixArrive"
                                ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                    vidTimestamp = System.currentTimeMillis(),
                                    infosId = 3,
                                    PrixsCurrency = listOf(
                                        ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                                            vidTimestamp = createTimestamp(
                                                day = 6,
                                                hour = 4,
                                                minute = 30
                                            ),
                                            valeur = 14.80
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun testFlowWorksAndAssertEqualsTestData() = runTest(testDispatcher) {
        // First, delete all existing data
        infosSqlDataBasesRepository.deleteAll()
        // Advance time to process all coroutines related to deletion
        testScheduler.advanceUntilIdle()

        // Add test data
        infosSqlDataBasesRepository.add(testDatas)
        // Advance time to process all coroutines related to adding data
        testScheduler.advanceUntilIdle()

        // Now collect and verify data from the repository
        val actualDataList = infosSqlDataBasesRepository.modelListFlow.first()
        val actualData = actualDataList.firstOrNull()
            ?: throw AssertionError("Expected data not found")
        assertDataMatchesExpected(testDatas, actualData)

        // Explicitly force the refresh of NoSQL data
        convertiseurNoSqlToSqlRepository.refreshNoSqlData()
        testScheduler.advanceUntilIdle()

        // Collect and verify NoSQL data
        val actualDatanoSqlDataFlow = convertiseurNoSqlToSqlRepository.noSqlDataFlow.first()
        assertDataMatchesExpectedNoSql(testDatasProduitNoSqlDataBase(), actualDatanoSqlDataFlow)
    }

    private fun assertDataMatchesExpected(expected: DataBasesInfosSql, actual: DataBasesInfosSql) {
        Products(expected, actual)
        Clients(expected, actual)
        Tarifications(expected, actual)
        TypeTarifications(expected, actual)
    }
}
