package com.example.clientjetpack.Id1.PrixChangable.Test._ID3.Test

import com.example.clientjetpack.Id1.PrixChangable.Test.Function.createTimestamp
import com.example.clientjetpack.Id1.PrixChangable.Test.Main.Modules.logHErartchiDataBase
import com.example.clientjetpack.Id1.PrixChangable.Test.Main.Modules.mockOutputNoSqlModel
import com.example.clientjetpack.Id1.PrixChangable.Test.Models.InputEtInfosSqlModels

fun testID2_Add(
    tarificationEntries: MutableList<InputEtInfosSqlModels.Tarification>,
    produitInfos: MutableList<InputEtInfosSqlModels.ProduitInfos>,
    clientDataBase: MutableList<InputEtInfosSqlModels.ClientDataBase>
): Unit {
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
            ),
        )

        val testData = mockOutputNoSqlModel(
            tarificationEntries, produitInfos, clientDataBase
        )

        logHErartchiDataBase(
            testData.produits.toMutableList(),
            "testID2_Add",
        )
    }
