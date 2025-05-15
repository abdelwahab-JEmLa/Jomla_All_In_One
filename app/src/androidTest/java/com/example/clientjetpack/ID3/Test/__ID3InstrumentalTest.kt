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
import kotlinx.coroutines.launch
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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    @Test
    fun id2Test() = runTest(testDispatcher) {
        suspendCoroutine { continuation ->
            launch {
                // First, ensure we have test data in the SQL repository
                infosSqlDataBasesRepository.deleteAll {
                    infosSqlDataBasesRepository.add(testDatas) {
                        launch {
                            val actualData = convertiseurNoSqlToSqlRepository.noSqlDataFlow.first()
                            assertDataMatchesExpectedDontconvertiseurNoSqlToSqlRepository(
                                testDatas,
                                actualData
                            )
                            continuation.resume(Unit)
                        }
                    }
                }
            }
        }
    }

    private fun assertDataMatchesExpectedDontconvertiseurNoSqlToSqlRepository(
        expected: DataBasesInfosSql,
        actual: ProduitNoSqlDataBase
    ) {
        // Check that all products are converted correctly
        assertEquals(
            "Products list size should match",
            expected.a_ProduitInfos.size,
            actual.produits.size
        )

        expected.a_ProduitInfos.forEach { expectedProduct ->
            val actualProduct = actual.produits.find { it.infosId == expectedProduct.id }
                ?: throw AssertionError("NoSQL Product with ID ${expectedProduct.id} not found")
        }

        // Check that all clients are converted correctly
        val expectedClients = expected.b_ClientInfos
        val actualClientIds =
            actual.produits.flatMap { it.clientAchteurs }.map { it.infosId }.distinct()

        assertEquals(
            "Client list size should match",
            expectedClients.size,
            actualClientIds.size
        )

        expectedClients.forEach { expectedClient ->
            val clientExists = actualClientIds.any { it == expectedClient.id }
            if (!clientExists) {
                throw AssertionError("NoSQL Client with ID ${expectedClient.id} not found")
            }
        }

        // Check tarifications
        val expectedTarifications = expected.d_TarificationInfos
        var foundTarificationCount = 0

        actual.produits.forEach { produit ->
            produit.clientAchteurs.forEach { client ->
                client.typeTarification.forEach { typeTarif ->
                    foundTarificationCount += typeTarif.PrixsCurrency.size

                    typeTarif.PrixsCurrency.forEach { prix ->
                        val expectedTarif =
                            expectedTarifications.find { it.vidTimestamp == prix.vidTimestamp }
                        if (expectedTarif != null) {
                            assertEquals(
                                "Price value should match for timestamp ${prix.vidTimestamp}",
                                expectedTarif.prixCurrency,
                                prix.valeur,
                                0.01
                            )
                        }
                    }
                }
            }
        }

        assertEquals(
            "Tarification count should match",
            expectedTarifications.size,
            foundTarificationCount
        )
    }

    @Test
    fun testFlowWorksAndAssertEqualsTestData() = runTest(testDispatcher) {
        suspendCoroutine { continuation ->
            infosSqlDataBasesRepository.deleteAll {
                infosSqlDataBasesRepository.add(testDatas) {
                    launch {
                        val actualDataList = infosSqlDataBasesRepository.modelListFlow.first()
                        val actualData = actualDataList.firstOrNull()
                            ?: throw AssertionError("Expected data not found")
                        assertDataMatchesExpected(testDatas, actualData)
                        continuation.resume(Unit)
                    }
                }
            }
        }
    }

    private fun assertDataMatchesExpected(expected: DataBasesInfosSql, actual: DataBasesInfosSql) {
        Products(expected, actual)
        Clients(expected, actual)
        Tarifications(expected, actual)
        TypeTarifications(expected, actual)
    }
}
