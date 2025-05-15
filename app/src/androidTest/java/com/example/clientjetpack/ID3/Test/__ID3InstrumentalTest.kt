package com.example.clientjetpack.ID3.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Home.FireBaseHandler
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Home.TestAppDatabase
import com.example.clientjetpack.ID3.Test.DataBase.SQL.InfosSqlDataBasesRepository
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.A_ProduitInfos
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.B_ClientInfos
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.D_TarificationInfos
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
import java.util.Calendar
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
                            testDispatcher ,
                        )
                    }
                    single { FireBaseHandler() }
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
    fun testFlowWorksAndAssertEqualsTestData() = runTest(testDispatcher) {
        suspendCoroutine { continuation ->
            infosSqlDataBasesRepository.deleteAll {
                infosSqlDataBasesRepository.add(testDatas) {
                    launch {
                        val actualDataList = infosSqlDataBasesRepository.modelListFlow.first()
                        val actualData = actualDataList.firstOrNull() ?: throw AssertionError("Expected data not found")
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

    private fun TypeTarifications(
        expected: DataBasesInfosSql,
        actual: DataBasesInfosSql
    ) {
        assertEquals(
            "Type Tarifications list size should match",
            expected.c_TypeTarificationInfos.size,
            actual.c_TypeTarificationInfos.size
        )

        expected.c_TypeTarificationInfos.forEach { expectedType ->
            val actualType = actual.c_TypeTarificationInfos.find { it.id == expectedType.id }
                ?: throw AssertionError("Type Tarification with ID ${expectedType.id} not found")

            assertEquals(
                "Type Tarification enum should match for ID ${expectedType.id}",
                expectedType.typeTarificationEnum,
                actualType.typeTarificationEnum
            )
        }
    }

    private fun Tarifications(
        expected: DataBasesInfosSql,
        actual: DataBasesInfosSql
    ) {
        // Tarifications
        assertEquals(
            "Tarifications list size should match",
            expected.d_TarificationInfos.size,
            actual.d_TarificationInfos.size
        )

        expected.d_TarificationInfos.forEach { expectedTarif ->
            val actualTarif =
                actual.d_TarificationInfos.find { it.vidTimestamp == expectedTarif.vidTimestamp }
                    ?: throw AssertionError("Tarification with timestamp ${expectedTarif.vidTimestamp} not found")

            assertEquals(
                "Tarification product ID should match for timestamp ${expectedTarif.vidTimestamp}",
                expectedTarif.idProduit,
                actualTarif.idProduit
            )
            assertEquals(
                "Tarification client ID should match for timestamp ${expectedTarif.vidTimestamp}",
                expectedTarif.idClient,
                actualTarif.idClient
            )
            assertEquals(
                "Tarification type ID should match for timestamp ${expectedTarif.vidTimestamp}",
                expectedTarif.idTypeTarification,
                actualTarif.idTypeTarification
            )
            assertEquals(
                "Tarification price should match for timestamp ${expectedTarif.vidTimestamp}",
                expectedTarif.prixCurrency,
                actualTarif.prixCurrency,
                0.01
            )
        }
    }

    private fun Clients(
        expected: DataBasesInfosSql,
        actual: DataBasesInfosSql
    ) {
        // Clients
        assertEquals(
            "Clients list size should match",
            expected.b_ClientInfos.size,
            actual.b_ClientInfos.size
        )

        expected.b_ClientInfos.forEach { expectedClient ->
            val actualClient = actual.b_ClientInfos.find { it.id == expectedClient.id }
                ?: throw AssertionError("Client with ID ${expectedClient.id} not found")

            assertEquals(
                "Client name should match for ID ${expectedClient.id}",
                expectedClient.nom,
                actualClient.nom
            )
            assertEquals(
                "Client active tarification ID should match for ID ${expectedClient.id}",
                expectedClient.idActiveTypeTarificationDataBase,
                actualClient.idActiveTypeTarificationDataBase
            )
        }
    }

    private fun Products(
        expected: DataBasesInfosSql,
        actual: DataBasesInfosSql
    ) {
        // Products
        assertEquals(
            "Products list size should match",
            expected.a_ProduitInfos.size,
            actual.a_ProduitInfos.size
        )

        expected.a_ProduitInfos.forEach { expectedProduct ->
            val actualProduct = actual.a_ProduitInfos.find { it.id == expectedProduct.id }
                ?: throw AssertionError("Product with ID ${expectedProduct.id} not found")

            assertEquals(
                "Product name should match for ID ${expectedProduct.id}",
                expectedProduct.nom,
                actualProduct.nom
            )
        }
    }

    private fun testDatas(): DataBasesInfosSql {
        return DataBasesInfosSql(
            a_ProduitInfos = mutableListOf(
                A_ProduitInfos(id = 1, nom = "Produit Optila"),
                A_ProduitInfos(id = 2, nom = "Produit Hnina"),
                A_ProduitInfos(id = 3, nom = "Produit kemya")
            ),
            b_ClientInfos = mutableListOf(
                B_ClientInfos(
                    id = 1,
                    nom = "ClientAchteur Abderrahman",
                    idActiveTypeTarificationDataBase = 1
                ),
                B_ClientInfos(
                    id = 2,
                    nom = "ClientAchteur Beta",
                    idActiveTypeTarificationDataBase = 2
                ),
                B_ClientInfos(
                    id = 3,
                    nom = "ClientAchteur Gamma",
                    idActiveTypeTarificationDataBase = 3
                )
            ),
            d_TarificationInfos = mutableListOf(
                D_TarificationInfos(
                    vidTimestamp = createTimestamp(day = 1, hour = 12, minute = 30),
                    idProduit = 1,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 20.99
                ),
                D_TarificationInfos(
                    vidTimestamp = createTimestamp(day = 5, hour = 13, minute = 30),
                    idProduit = 1,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 25.50
                ),
                D_TarificationInfos(
                    vidTimestamp = createTimestamp(day = 5, hour = 14, minute = 30),
                    idProduit = 1,
                    idClient = 2,
                    idTypeTarification = 2,
                    prixCurrency = 9.75
                ),
                D_TarificationInfos(
                    vidTimestamp = createTimestamp(day = 6, hour = 3, minute = 30),
                    idProduit = 2,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 15.25
                ),
                D_TarificationInfos(
                    vidTimestamp = createTimestamp(day = 6, hour = 4, minute = 30),
                    idProduit = 3,
                    idClient = 1,
                    idTypeTarification = 3,
                    prixCurrency = 14.80
                )
            )
        )
    }
    fun createTimestamp(year: Int = 2025, month: Int = 5, day: Int, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, hour, minute, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

}
