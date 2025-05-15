package com.example.clientjetpack.ID3.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Home.setupKoinTestInject
import com.example.clientjetpack.ID3.Test.DataBase.Repo.InfosSqlDataBasesRepository
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.DataBasesInfosSql
import com.example.clientjetpack.Modules.LogFilterRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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

    private val testDispatcher = StandardTestDispatcher()

    // Use the implementation class instead of the interface
    private val infosSqlDataBasesRepository: InfosSqlDataBasesRepository by inject()

    private val testDatas = testDatas()

    @Before
    fun setup() = runTest {
        Dispatchers.setMain(testDispatcher)
        setupKoinTestInject()
        clearAddTestDataAuFireBase(testDatas)
    }

    @Test
    fun clearAddTestDataAuFireBase(testDatas: DataBasesInfosSql) = runTest {
        // Clear the database first
        infosSqlDataBasesRepository.deleteAll {
            // Add test data to repository and wait for it to complete
            infosSqlDataBasesRepository.add(testDatas) { _ ->
                idTest2Num1_FreeLuncheFlowWorkEtAssertEqualesTestData()
            }
        }

        // Advance the test dispatcher to ensure all operations complete
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    private fun idTest2Num1_FreeLuncheFlowWorkEtAssertEqualesTestData() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val repositoryData = infosSqlDataBasesRepository.modelListFlow.first()

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

        // Compare clients
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

        // Compare tarifications
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
