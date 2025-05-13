package com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test

import com.example.clientjetpack.ID1.Test.Packages.Function.createTimestamp
import com.example.clientjetpack.ID1.Test.Packages.Models.InputEtInfosSqlModels
import com.example.clientjetpack.ID1.Test.Packages.Modules.Log.logHErartchiDataBase
import com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.Repository.Output.OutputNoSqlModelRepositoryImp
import com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.ViewModel.TarificationViewModel
import kotlinx.coroutines.flow.first
import kotlin.test.assertEquals

suspend fun testID_2_B(viewModel: TarificationViewModel) {

    viewModel.addNewProduitInfos(
        InputEtInfosSqlModels.ProduitInfos(
            id = 5L,
            nom = "Produit 5"
        )
    )

    kotlinx.coroutines.delay(100)

    val repoImpl = viewModel.outputNoSqlRepository as? OutputNoSqlModelRepositoryImp
    repoImpl?.refreshData()

    val newTarification =
        InputEtInfosSqlModels.Tarification(
            vidTimestamp = createTimestamp(day = 1, hour = 13, minute = 30),
            idProduit = 5L,
            idClient = 1L,
            idTypeTarification = 2L,
            prixCurrency = 20.99
        )

    viewModel.addNewTestDataTarificationEtClient(
        newTarification
    )

    repoImpl?.refreshData()


    val currentProductCount = viewModel.outputNoSqlFlow.first().produits.size

    assertEquals(
        5,
        currentProductCount,
        "Expected 5 products but found $currentProductCount"
    )

    logHErartchiDataBase(
        viewModel.outputNoSqlFlow.first().produits.toMutableList(),
        "testID_2_BaddNewTestDataTarificationEtClient"
    )
}
