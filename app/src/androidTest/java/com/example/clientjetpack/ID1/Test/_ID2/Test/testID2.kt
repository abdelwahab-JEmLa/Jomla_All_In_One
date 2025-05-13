package com.example.clientjetpack.ID1.Test._ID2.Test

import com.example.clientjetpack.ID1.Test.Z.Fragment.A.ViewModel.TarificationViewModel
import com.example.clientjetpack.ID1.Test._ID1.Test.Models.InputEtInfosSqlModels
import com.example.clientjetpack.ID1.Test._ID1.Test.Models.NoSqlDataBases
import com.example.clientjetpack.ID1.Test._ID1.Test.Modules.Log.logHErartchiDataBase
import com.example.clientjetpack.ID1.Test._ID1.Test.Modules.mockOutputNoSqlModel
import com.example.clientjetpack.ID1.Test._ID1.Test.W.Init.initialClientsData
import com.example.clientjetpack.ID1.Test._ID1.Test.W.Init.initialProductsData
import com.example.clientjetpack.ID1.Test._ID1.Test.Z.Function.createTimestamp

fun testID2(viewModel: TarificationViewModel) {
    val inputSqlGroupeRepositorys = viewModel
        .inputSqlGroupeRepositorys

    val modelList = inputSqlGroupeRepositorys
        .TarificationRepository()
        .modelList
    val noSqlDataBases = NoSqlDataBases(
        modelList
            .toMutableList(),
        initialProductsData.toMutableList(),
        initialClientsData.toMutableList()
    )

    logHErartchiDataBase(
        mockOutputNoSqlModel(noSqlDataBases).produits.toMutableList(),
        "logHErartchiDataBase"
    )

    noSqlDataBases.apply {
        tarificationEntries.add(
            InputEtInfosSqlModels.Tarification(
                vidTimestamp = createTimestamp(day = 1, hour = 13, minute = 30),
                idProduit = 5L,
                idClient = 1L,
                idTypeTarification = 2L,
                prixCurrency = 20.99
            )
        )
        produitInfos.add(
            InputEtInfosSqlModels.ProduitInfos(
                id = 5L,
                nom = "Produit 5"
            )
        )
    }

    logHErartchiDataBase(
        mockOutputNoSqlModel(noSqlDataBases).produits.toMutableList(),
        "logHErartchiDataBase testDataAfterAdd"
    )
}
