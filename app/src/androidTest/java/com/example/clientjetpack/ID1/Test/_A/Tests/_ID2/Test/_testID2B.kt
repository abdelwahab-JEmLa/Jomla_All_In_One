package com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test

import com.example.clientjetpack.ID1.Test.Packages.Function.createTimestamp
import com.example.clientjetpack.ID1.Test.Packages.Models.InputEtInfosSqlModels
import com.example.clientjetpack.ID1.Test.Packages.Modules.Log.logHErartchiDataBase
import com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.ViewModel.TarificationViewModel
import kotlinx.coroutines.flow.first

suspend fun testID_2_B(viewModel: TarificationViewModel) {
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
    viewModel.addNewProduitInfos(
        InputEtInfosSqlModels.ProduitInfos(
            id = 5L,
            nom = "Produit 5"
        )
    )

    logHErartchiDataBase(
        viewModel.outputNoSqlFlow.first().produits.toMutableList(),
        "testID_2_BaddNewTestDataTarificationEtClient"
    )
}
