package com.example.clientjetpack.Id1.PrixChangable.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.clientjetpack.Id1.PrixChangable.Test.Log.logProduits
import com.example.clientjetpack.Id1.PrixChangable.Test.Models.OutputNoSqlModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class _TestsDisplayerLogDataBase {
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testFullWorkflow() = runTest {
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
                Assert.assertTrue(modelListclientRepository.isNotEmpty())

                val clientDB = modelListclientRepository.find { it.id == clientId }
                if (clientDB != null) {
                    val clientEntries = entriesList.filter {
                        it.idProduit == produitId && it.idClient == clientId
                    }

                    val uniqueTypeIds = clientEntries.map { it.idTypeTarification }.toSet()

                    val typeTarifications = mutableListOf<OutputNoSqlModel.Produit.Client.TypeTarification>()

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
        Assert.assertTrue("At least one product should have clients", produitsList.any { it.clients.isNotEmpty() })

        // Verify that all products have at least one client (based on test data provided)
        for (produit in produitsList) {
            Assert.assertFalse("Product ${produit.id} should have clients", produit.clients.isEmpty())
        }

        SepareReferentialDataBases(produitsList)
    }

    private fun SepareReferentialDataBases(produitsList: MutableList<OutputNoSqlModel.Produit>) = runTest {
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

            println("\n-- Hierarchical Structure --")

            // Create an OutputNoSqlModel from the produitsList
            val outputModel = OutputNoSqlModel(produits = produitsList)

            // Now pass the properly constructed model to logProduits
            logProduits(outputModel)

            println("\n========TEST $name COMPLETED SUCCESSFULLY ========\n")

        } catch (e: Exception) {
            println("Erreur dans SepareReferentialDataBases: ${e.message}")
            throw e
        }
    }
}
