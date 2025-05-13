package com.example.clientjetpack.Id1.PrixChangable.Test._ID1.Test

import com.example.clientjetpack.Id1.PrixChangable.Test.Function.createTimestamp
import com.example.clientjetpack.Id1.PrixChangable.Test.Models.InputEtInfosSqlModels
import com.example.clientjetpack.Id1.PrixChangable.Test.Models.NoSqlDataBases
import com.example.clientjetpack.Id1.PrixChangable.Test.Modules.logHErartchiDataBase
import com.example.clientjetpack.Id1.PrixChangable.Test.Modules.mockOutputNoSqlModel

fun testID1(
    noSqlDataBases: NoSqlDataBases
) {
    logHErartchiDataBase(
        mockOutputNoSqlModel(noSqlDataBases).produits.toMutableList(),
        "logHErartchiDataBase"
    )

    // Add new test entries to validate data updates
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

    // Verify updates with modified data
    logHErartchiDataBase(
        mockOutputNoSqlModel(noSqlDataBases).produits.toMutableList(),
        "logHErartchiDataBase testDataAfterAdd"
    )
}
