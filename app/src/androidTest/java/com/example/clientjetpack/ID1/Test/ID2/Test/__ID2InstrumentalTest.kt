package com.example.clientjetpack.ID1.Test.ID2.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.FireBaseHandler
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo._InfosSqlDataBases_GroupeRepositorys
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo._InfosSqlDataBases_GroupeRepositorysImp
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.TestAppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
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
class __ID2InstrumentalTest : KoinTest {
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
    private val repositoriesImpl: _InfosSqlDataBases_GroupeRepositorysImp by inject()

    @Before
    fun setup() = runTest {
        Dispatchers.setMain(testDispatcher)
        stopKoin()
        startKoin {
            modules(
                module {
                    // Fixed: Use the companion object method directly
                    single {
                        TestAppDatabase.getTestDatabase(InstrumentationRegistry.getInstrumentation().targetContext)
                    }

                    single<_InfosSqlDataBases_GroupeRepositorys> {
                        _InfosSqlDataBases_GroupeRepositorysImp(
                            InstrumentationRegistry.getInstrumentation().targetContext,
                            get()
                        )
                    }

                    single {
                        _InfosSqlDataBases_GroupeRepositorysImp(
                            InstrumentationRegistry.getInstrumentation().targetContext,
                            get()
                        )
                    }

                    single { this@__ID2InstrumentalTest }
                    single { FireBaseHandler() }
                }
            )
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun idTest2Num1() = runTest {
        // First delete all existing data
        repositoriesImpl.produitRepository.deleteAll()
        repositoriesImpl.clientRepository.deleteAll()
        repositoriesImpl.typeTarificationRepository.deleteAll()
        repositoriesImpl.tarificationRepository.deleteAll()

        // Advance time to ensure deletions are processed
        testDispatcher.scheduler.advanceUntilIdle()

        // Get test data
        val testData = testDatas()

        // Step 1: Add products with tracking
        var expectedProduits = testData.a_ProduitInfos.size
        var completedProduits = 0

        suspendCoroutine<Unit> { continuation ->
            testData.a_ProduitInfos.forEach { produit ->
                repositoriesImpl.produitRepository.add(produit) {
                    completedProduits++
                    LogFilterRule.log("InstrumentalTest", "Added produit ${it.id}: ${it.nom}")

                    if (completedProduits == expectedProduits) {
                        continuation.resume(Unit)
                    }
                }
            }
        }

        // Step 2: Add clients with tracking
        var expectedClients = testData.b_ClientInfos.size
        var completedClients = 0

        suspendCoroutine<Unit> { continuation ->
            testData.b_ClientInfos.forEach { client ->
                // Extend B_ClientInfos_Repository interface and implementation to support callbacks
                // For now, using a workaround with update() which has a callback
                repositoriesImpl.clientRepository.add(client)
                repositoriesImpl.clientRepository.update(client) { updatedClient ->
                    completedClients++
                    LogFilterRule.log("InstrumentalTest", "Added/Updated client ${updatedClient.id}: ${updatedClient.nom}")

                    if (completedClients == expectedClients) {
                        continuation.resume(Unit)
                    }
                }
            }
        }

        // Step 3: Add tarifications with tracking
        var expectedTarifications = testData.d_TarificationInfos.size
        var completedTarifications = 0

        suspendCoroutine<Unit> { continuation ->
            testData.d_TarificationInfos.forEach { tarification ->
                repositoriesImpl.tarificationRepository.add(tarification) {
                    completedTarifications++
                    LogFilterRule.log("InstrumentalTest", "Added tarification with timestamp ${it.vidTimestamp}")

                    if (completedTarifications == expectedTarifications) {
                        continuation.resume(Unit)
                    }
                }
            }
        }

        // Give time for data to be inserted
        testDispatcher.scheduler.advanceUntilIdle()

        // Add explicit logging to help debug
        LogFilterRule.log("InstrumentalTest", "Before collecting flow data")

        // Wait for all Flow collections to complete by accessing the flow directly
        val produits = repositoriesImpl.produitRepository.modelListFlow.first()
        LogFilterRule.log("InstrumentalTest", "Collected ${produits.size} produits")

        val clients = repositoriesImpl.clientRepository.modelListFlow.first()
        LogFilterRule.log("InstrumentalTest", "Collected ${clients.size} clients")

        val tarifications = repositoriesImpl.tarificationRepository.modelListFlow.first()
        LogFilterRule.log("InstrumentalTest", "Collected ${tarifications.size} tarifications")

        // Log the current state
        produits.forEach {
            LogFilterRule.log("InstrumentalTest", "Produit in DB: ${it.id} - ${it.nom}")
        }

        clients.forEach {
            LogFilterRule.log("InstrumentalTest", "Client in DB: ${it.id} - ${it.nom}")
        }

        // Verify data was added correctly using the collected data
        assert(produits.size == testData.a_ProduitInfos.size) {
            "Expected ${testData.a_ProduitInfos.size} produits, but got ${produits.size}"
        }

        assert(clients.size == testData.b_ClientInfos.size) {
            "Expected ${testData.b_ClientInfos.size} clients, but got ${clients.size}"
        }

        assert(tarifications.size == testData.d_TarificationInfos.size) {
            "Expected ${testData.d_TarificationInfos.size} tarifications, but got ${tarifications.size}"
        }

        // Verify specific data content matches
        testData.a_ProduitInfos.forEach { produit ->
            val found = produits.find { it.id == produit.id }
            assert(found != null) { "Produit with id ${produit.id} not found" }
            assert(found?.nom == produit.nom) {
                "Produit name mismatch. Expected: ${produit.nom}, Found: ${found?.nom}"
            }
        }
    }
    //<--
    //TODO(1): cree un test pour trouve pk je ne trouve pas les data seted au firebase database 

    fun createTimestamp(year: Int = 2025, month: Int = 5, day: Int, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, hour, minute, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
