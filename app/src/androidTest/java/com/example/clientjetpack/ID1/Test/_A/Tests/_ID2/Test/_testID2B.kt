package com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test

import com.example.clientjetpack.ID1.Test.Packages.Function.createTimestamp
import com.example.clientjetpack.ID1.Test.Packages.Models.InputEtInfosSqlModels
import com.example.clientjetpack.ID1.Test.Packages.Modules.Log.logHErartchiDataBase
import com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.Repository.Output.OutputNoSqlModelRepositoryImp
import com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.ViewModel.TarificationViewModel
import kotlinx.coroutines.flow.first
import kotlin.test.assertEquals

suspend fun testID_2_B(viewModel: TarificationViewModel) {
    // Log initial state
    val initialProductCount = viewModel.outputNoSqlFlow.first().produits.size
    println("Initial product count: $initialProductCount")

    // First add the product information to ensure it exists before adding tarification
    viewModel.addNewProduitInfos(
        InputEtInfosSqlModels.ProduitInfos(
            id = 5L,
            nom = "Produit 5"
        )
    )

    // Give some time for the product to be processed
    kotlinx.coroutines.delay(100)

    // Verify the product was added successfully
    val repoImpl = viewModel.outputNoSqlRepository as? OutputNoSqlModelRepositoryImp
    repoImpl?.refreshData()

    // Verify product count after adding the product
    val afterProductCount = viewModel.outputNoSqlFlow.first().produits.size
    println("Product count after adding product: $afterProductCount")

    // Then add the tarification information
    val newTarification =
        InputEtInfosSqlModels.Tarification(
            vidTimestamp = createTimestamp(day = 1, hour = 13, minute = 30),
            idProduit = 5L,
            idClient = 1L,
            idTypeTarification = 2L,
            prixCurrency = 20.99
        )

    // Add the tarification data
    viewModel.addNewTestDataTarificationEtClient(
        newTarification
    )

    // Give enough time for the data to be processed
    kotlinx.coroutines.delay(200)

    // Force a refresh of the output model data
    repoImpl?.refreshData()

    // Wait for the refresh to complete
    kotlinx.coroutines.delay(100)

    // Get the current product count
    val currentProductCount = viewModel.outputNoSqlFlow.first().produits.size
    println("Final product count: $currentProductCount")

    // Log the product list to debug
    val productList = viewModel.outputNoSqlFlow.first().produits.toMutableList()
    println("Product IDs: ${productList.map { it.id }}")

    // Assert that we now have 5 products
    assertEquals(
        5,
        currentProductCount,
        "Expected 5 products but found $currentProductCount"
    )

    // Log the current state
    logHErartchiDataBase(
        viewModel.outputNoSqlFlow.first().produits.toMutableList(),
        "testID_2_BaddNewTestDataTarificationEtClient"
    )
}
