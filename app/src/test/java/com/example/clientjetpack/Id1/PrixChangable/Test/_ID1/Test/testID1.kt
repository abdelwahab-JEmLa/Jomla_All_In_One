package com.example.clientjetpack.Id1.PrixChangable.Test._ID1.Test

import com.example.clientjetpack.Id1.PrixChangable.Test.Function.createTimestamp
import com.example.clientjetpack.Id1.PrixChangable.Test.Main.Modules.logHErartchiDataBase
import com.example.clientjetpack.Id1.PrixChangable.Test.Main.Modules.mockOutputNoSqlModel
import com.example.clientjetpack.Id1.PrixChangable.Test.Models.InputEtInfosSqlModels
import com.example.clientjetpack.Id1.PrixChangable.Test.Models.NoSql

fun testID1(
    tarificationEntries: MutableList<InputEtInfosSqlModels.Tarification>,
    produitInfos: MutableList<InputEtInfosSqlModels.ProduitInfos>,
    clientDataBase: MutableList<InputEtInfosSqlModels.ClientDataBase>,
) {
    // Create NoSql model and generate initial test data
    val noSql = NoSql(tarificationEntries, produitInfos, clientDataBase)

    logHErartchiDataBase(
        mockOutputNoSqlModel(noSql).produits.toMutableList(),
        "logHErartchiDataBase"
    )

    // Add new test entries to validate data updates
    noSql.apply {
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
        mockOutputNoSqlModel(noSql).produits.toMutableList(),
        "logHErartchiDataBase testDataAfterAdd"
    )
}
