package com.example.clientjetpack.ID1.Test.ID2.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.LogFilterRule
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.A_ProduitInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.B_ClientInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.D_TarificationInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models._InfosSqlDataBases
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo._InfosSqlDataBases_GroupeRepositorys
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo._InfosSqlDataBases_GroupeRepositorysImp
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.TestAppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class __ID2InstrumentalTest: KoinTest {
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

        // Get test data
        val testData = testDatas()

        // Add test data to repositories
        testData.a_ProduitInfos.forEach { produit ->
            repositoriesImpl.produitRepository.add(produit)
        }

        testData.b_ClientInfos.forEach { client ->
            repositoriesImpl.clientRepository.add(client)
        }

        testData.d_TarificationInfos.forEach { tarification ->
            repositoriesImpl.tarificationRepository.add(tarification)
        }

        // Give time for data to be inserted and collected
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify data was added correctly
        assert(repositoriesImpl.produitRepository.modelList.size == testData.a_ProduitInfos.size)
        assert(repositoriesImpl.clientRepository.modelList.size == testData.b_ClientInfos.size)
        assert(repositoriesImpl.tarificationRepository.modelList.size == testData.d_TarificationInfos.size)

        // Verify specific data content matches
        testData.a_ProduitInfos.forEach { produit ->
            val found = repositoriesImpl.produitRepository.modelList.find { it.id == produit.id }
            assert(found != null)
            assert(found?.nom == produit.nom)
        }
    }

    private fun testDatas(): _InfosSqlDataBases {
        return _InfosSqlDataBases(
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
                B_ClientInfos(id = 2, nom = "ClientAchteur Beta", idActiveTypeTarificationDataBase = 2),
                B_ClientInfos(id = 3, nom = "ClientAchteur Gamma", idActiveTypeTarificationDataBase = 3)
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

    fun createTimestamp(year: Int = 2025, month: Int=5, day: Int, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, hour, minute, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
