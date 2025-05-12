package com.example.clientjetpack.ID1.Test

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.clientjetpack.ID1.Test.Z.Fragment.A.ViewModel.TarificationViewModel
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Models.InputEtInfosSqlModels
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.Test.initialTestData
import com.example.clientjetpack.ID1.Test.Z.Fragment.Log.logProduits
import com.example.clientjetpack.ID1.Test.Z.Fragment.Passive.strDateEtTempFromVidTimestamp
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class _TeID1_InstrumentalTestInterieur : KoinTest {
    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val combinedLogFilter = LogFilterRule.filter()
        .filterByTag("InstrumentalTest")
        .filterByTag("onOperationSuccess")
        .filterByTag("OperationTrackerImp")
        .build()

    private val testDispatcher = StandardTestDispatcher()

    // Initializer pour les données de test
    private val testDataInitializer = TestDataInitializer()

    // Use Koin's inject to properly initialize the ViewModel
    private val viewModel: TarificationViewModel by inject()

    private val fireBaseHandler by lazy { viewModel.inputSqlGroupeRepositorys.fireBaseHandler }

    private val parentDbRef: DatabaseReference =
        _0_0_HeadOfRepositorys_Model.getHeadSqlDataBaseRef()
            .child("C_InputEtInfosSql")

    private val sonDataBaseRef: DatabaseReference = parentDbRef.child("A_Tarification")

    @Before
    fun setup() = runTest {
        Dispatchers.setMain(testDispatcher)
        stopKoin()
        startKoin {
            modules(
                module {
                    single { this@_TeID1_InstrumentalTestInterieur }
                    viewModel { TarificationViewModel() }
                }
            )
        }

        // Initialisation des données de test complètes
        testDataInitializer.initializeAllTestData()

        // Avance le temps pour s'assurer que les données sont chargées
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun A_logSepareReferentialDataBases(): Unit = runTest {
        // Vérifie l'état initial du client 1
        assertEquals(
            1L,
            viewModel.getSqlClient(1)?.idActiveTypeTarificationDataBase
        )

        // Exécute le test qui a échoué précédemment
        SepareReferentialDataBases()

        // Vérifie que tous les produits ont au moins un client
        val currentValue = viewModel.outputNoSqlFlow.first()
        currentValue.produits.forEach { produit ->
            assertTrue(
                "Le produit ${produit.id} doit avoir au moins un client",
                produit.clients.isNotEmpty()
            )
        }
    }

    @Test
    fun B_logUpdateReferentialDataBases(): Unit = runTest {
        // Ajoute une nouvelle tarification
        viewModel.addNewTestDataTarificationEtClient()

        // Vérifie que la mise à jour a été effectuée
        assertEquals(
            2L, // Maintenant 2L au lieu de 1L après l'appel à addNewTestDataTarificationEtClient
            viewModel.getSqlClient(1)?.idActiveTypeTarificationDataBase
        )

        val name = "A_DataBasesSepareReferential_AfterUpdate"
        val currentStrTime = strDateEtTempFromVidTimestamp(System.currentTimeMillis())

        println("\n========Après Update========\n")
        println(
            "======== C Le Test Log Output Print Du Temp=${currentStrTime.first} " +
                    "${currentStrTime.second} du  $name  ========"
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val currentValue = viewModel.outputNoSqlFlow.first()
        assertTrue(currentValue.produits.isNotEmpty())

        // Vérifie que tous les produits ont au moins un client
        currentValue.produits.forEach { produit ->
            assertTrue(
                "Le produit ${produit.id} doit avoir au moins un client",
                produit.clients.isNotEmpty()
            )
        }

        mainLog(currentValue)

        println("\n========TEST $name COMPLETED SUCCESSFULLY ========\n")
    }


    private fun SepareReferentialDataBases() = runTest {
        try {
            val name = "A_DataBasesSepareReferential"
            val currentStrTime =
                strDateEtTempFromVidTimestamp(
                    System.currentTimeMillis()
                )
            println(
                "======== C Le Test Log Output Print Du Temp=${currentStrTime.first} " +
                        "${currentStrTime.second} du  $name  ========"
            )

            testDispatcher.scheduler.advanceUntilIdle()
            val currentValue = viewModel.outputNoSqlFlow.first()

            mainLog(currentValue)

            println("\n========TEST $name COMPLETED SUCCESSFULLY ========\n")

        } catch (e: Exception) {
            println("Erreur dans SepareReferentialDataBases: ${e.message}")
            throw e
        }
    }

    private fun mainLog(value: OutputNoSqlModel) {
        println("\n-- Hierarchical Structure --")

        logProduits(
            value,
            viewModel)
    }

    @Test
    fun testFullWorkflow() = runTest {
        fireBaseHandler.clearDatabaseAsync(sonDataBaseRef)
        fireBaseHandler.addAllToFireBaseAsync(initialTestData, sonDataBaseRef)

        val result = fireBaseHandler.loadDatasAsync(
            sonDataBaseRef,
            InputEtInfosSqlModels.Tarification::class.java
        )

        assertEquals(
            "La base de données doit contenir le bon nombre d'éléments",
            initialTestData.size,
            result.size
        )

        assertEquals(
            result.first(),
            initialTestData.first()
        )
    }
}
