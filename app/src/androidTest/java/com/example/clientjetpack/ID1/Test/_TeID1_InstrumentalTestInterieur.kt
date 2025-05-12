package com.example.clientjetpack.ID1.Test

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.clientjetpack.ID1.Test.Z.Fragment.A.ViewModel.TarificationViewModel
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Models.InputEtInfosSqlModels
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.Test.initialClientsData
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.Test.initialProductsData
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.Test.initialTestData
import com.example.clientjetpack.ID1.Test.Z.Fragment.Log.logProduits
import com.example.clientjetpack.ID1.Test.Z.Fragment.Passive.strDateEtTempFromVidTimestamp
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
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
    fun testFromSqlToNoSqlDB() = runTest {
        // Initialize the tarification entries with test data
        val tarificationEntries = MutableStateFlow(initialTestData)

        val initialProductsData1 = initialProductsData
        val modelListclientRepository = initialClientsData

        val produitsList = mutableListOf<OutputNoSqlModel.Produit>()

        for (produitDB in initialProductsData1) {
            val produitId = produitDB.id

            val produitClients = mutableListOf<OutputNoSqlModel.Produit.Client>()

            val uniqueClientIds = mutableSetOf<Long>()
            val entriesList = tarificationEntries.value  // Get the list from the Flow
            for (entry in entriesList) {  // Now iterating over a List, not a Flow
                if (entry.idProduit == produitId) {
                    uniqueClientIds.add(entry.idClient)
                }
            }

            for (clientId in uniqueClientIds) {
                assertTrue(modelListclientRepository.isNotEmpty())

                val clientDB = modelListclientRepository.find { it.id == clientId }
                if (clientDB != null) {
                    val clientEntries = entriesList.filter {
                        it.idProduit == produitId && it.idClient == clientId
                    }

                    val uniqueTypeIds = clientEntries.map { it.idTypeTarification }.toSet()

                    val typeTarifications =
                        mutableListOf<OutputNoSqlModel.Produit.Client.TypeTarification>()

                    for (typeId in uniqueTypeIds) {
                        val typeEntries = clientEntries.filter { it.idTypeTarification == typeId }
                            .sortedByDescending { it.vidTimestamp }

                        if (typeEntries.isNotEmpty()) {
                            val latestTimestamp = typeEntries.first().vidTimestamp

                            val priceList = typeEntries.map { entry ->
                                OutputNoSqlModel.Produit.Client.TypeTarification.Prix(
                                    vidTimestamp = entry.vidTimestamp,
                                    valeur = entry.prixCurrency
                                )
                            }

                            val typeTarification =
                                OutputNoSqlModel.Produit.Client.TypeTarification(
                                    vidTimestamp = latestTimestamp,
                                    id = typeId,
                                    PrixsCurrency = priceList
                                )

                            typeTarifications.add(typeTarification)
                        }
                    }

                    if (typeTarifications.isNotEmpty()) {
                        val clientLatestTimestamp = typeTarifications.maxOf { it.vidTimestamp }

                        val client = OutputNoSqlModel.Produit.Client(
                            vidTimestamp = clientLatestTimestamp,
                            id = clientId,
                            typeTarification = typeTarifications
                        )

                        produitClients.add(client)
                    }
                }
            }

            val produitLatestTimestamp = if (produitClients.isNotEmpty()) {
                produitClients.maxOf { it.vidTimestamp }
            } else {
                System.currentTimeMillis()
            }

            val produit = OutputNoSqlModel.Produit(
                vidTimestamp = produitLatestTimestamp,
                id = produitId,
                clients = produitClients
            )

            produitsList.add(produit)
        }

        // Verify that at least one product has non-empty clients list
        assertTrue(
            "At least one product should have clients",
            produitsList.any { it.clients.isNotEmpty() })

        // Verify that all products have at least one client (based on test data provided)
        for (produit in produitsList) {
            Assert.assertFalse(
                "Product ${produit.id} should have clients",
                produit.clients.isEmpty()
            )
        }

        SepareReferentialDataBasesNoVM(produitsList, "Frome testFromSqlToNoSqlDB")
    }

    private fun SepareReferentialDataBasesNoVM(
        produitsList: MutableList<OutputNoSqlModel.Produit>,
        name: String,
    ) =
        runTest {
            try {
                val currentStrTime =
                    strDateEtTempFromVidTimestamp(
                        System.currentTimeMillis()
                    )
                println(
                    "======== C Le Test Log Output Print Du Temp=${currentStrTime.first} " +
                            "${currentStrTime.second} du  $name  ========"
                )

                testDispatcher.scheduler.advanceUntilIdle()

                println("\n-- Hierarchical Structure --")

                // Create an OutputNoSqlModel from the produitsList
                val outputModel = OutputNoSqlModel(produits = produitsList)

                // Now pass the properly constructed model to logProduits
                logProduits(
                    outputModel,
                    viewModel
                )

                println("\n========TEST $name COMPLETED SUCCESSFULLY ========\n")

            } catch (e: Exception) {
                println("Erreur dans SepareReferentialDataBases: ${e.message}")
                throw e
            }
        }

    @Test
    fun A_logSepareReferentialDataBases(): Unit = runTest {
        assertEquals(
            1L,
            viewModel.getSqlClient(1)?.idActiveTypeTarificationDataBase
        )

        val currentValue = viewModel.outputNoSqlFlow.first()

        // Convert the produits to a mutable list before passing to the function
        val produitsMutableList = currentValue.produits.toMutableList()

        // Now pass the mutable list instead of the OutputNoSqlModel
        SepareReferentialDataBasesNoVM(produitsMutableList, "Frome viewModel.outputNoSqlFlow.first()")

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

        println("\n-- Hierarchical Structure --")

        logProduits(
            currentValue,
            viewModel
        )

        println("\n========TEST $name COMPLETED SUCCESSFULLY ========\n")
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
