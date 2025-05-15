package com.example.clientjetpack.ID3.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Home.setupKoinTestInject
import com.example.clientjetpack.ID3.Test.DataBase.Repo.InfosSqlDataBasesRepository
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
import org.koin.core.context.stopKoin
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

    // Single scheduler that will be shared across all dispatchers
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    // Use the implementation class instead of the interface
    private val infosSqlDataBasesRepository: InfosSqlDataBasesRepository by inject()

    private val testDatas = testDatas()

    @Before
    fun setup() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        setupKoinTestInject()

        testScheduler.advanceUntilIdle()

        clearAndAddTestData()
    }

    private suspend fun clearAndAddTestData() {
        suspendCoroutine { continuation ->
            infosSqlDataBasesRepository.deleteAll {
                infosSqlDataBasesRepository.add(testDatas) { data ->
                    continuation.resume(data)
                }
            }
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
        testScheduler.advanceUntilIdle()

        val repositoryData = infosSqlDataBasesRepository.modelListFlow.first()

        println("Repository data size: ${repositoryData.size}")
        if (repositoryData.isNotEmpty()) {
            println("Products: ${repositoryData.first().a_ProduitInfos.size}")
            println("Clients: ${repositoryData.first().b_ClientInfos.size}")
            println("Tarifs: ${repositoryData.first().d_TarificationInfos.size}")
        }

        assert(repositoryData.isNotEmpty()) { "Repository data should not be empty" }

        val actualData = repositoryData.first()

        assertEquals(
            "Products list size should match",
            testDatas.a_ProduitInfos.size,
            actualData.a_ProduitInfos.size
        )

        for (i in testDatas.a_ProduitInfos.indices) {
            val expected = testDatas.a_ProduitInfos[i]
            val actual = actualData.a_ProduitInfos.find { it.id == expected.id }
            assertEquals("Product ID ${expected.id} should match", expected.id, actual?.id)
            assertEquals(
                "Product name should match for ID ${expected.id}",
                expected.nom,
                actual?.nom
            )
        }

        assertEquals(
            "Clients list size should match",
            testDatas.b_ClientInfos.size,
            actualData.b_ClientInfos.size
        )

        for (i in testDatas.b_ClientInfos.indices) {
            val expected = testDatas.b_ClientInfos[i]
            val actual = actualData.b_ClientInfos.find { it.id == expected.id }
            assertEquals("Client ID ${expected.id} should match", expected.id, actual?.id)
            assertEquals(
                "Client name should match for ID ${expected.id}",
                expected.nom,
                actual?.nom
            )
            assertEquals(
                "Client active tarification ID should match for ID ${expected.id}",
                expected.idActiveTypeTarificationDataBase, actual?.idActiveTypeTarificationDataBase
            )
        }

        assertEquals(
            "Tarifications list size should match",
            testDatas.d_TarificationInfos.size,
            actualData.d_TarificationInfos.size
        )

        for (i in testDatas.d_TarificationInfos.indices) {
            val expected = testDatas.d_TarificationInfos[i]
            val actual =
                actualData.d_TarificationInfos.find { it.vidTimestamp == expected.vidTimestamp }
            assertEquals(
                "Tarification timestamp ${expected.vidTimestamp} should match",
                expected.vidTimestamp, actual?.vidTimestamp
            )
            assertEquals(
                "Tarification product ID should match for timestamp ${expected.vidTimestamp}",
                expected.idProduit, actual?.idProduit
            )
            assertEquals(
                "Tarification client ID should match for timestamp ${expected.vidTimestamp}",
                expected.idClient, actual?.idClient
            )
            assertEquals(
                "Tarification type ID should match for timestamp ${expected.vidTimestamp}",
                expected.idTypeTarification, actual?.idTypeTarification
            )
            actual?.prixCurrency?.let {
                assertEquals(
                    "Tarification price should match for timestamp ${expected.vidTimestamp}",
                    expected.prixCurrency, it, 0.01
                )
            }
        }
    }
}
