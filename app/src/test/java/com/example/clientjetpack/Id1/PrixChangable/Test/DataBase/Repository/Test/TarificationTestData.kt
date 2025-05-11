package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Test

import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.InputSqlModels
import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.createTimestamp

object TarificationTestData {
    val initialTestData = listOf(
        // Test data for Caramels (product id 1)
        InputSqlModels.A_TarificationDataBaseFacileEntre(
            vidTimestamp = System.currentTimeMillis() - 86400000, // 1 day ago
            idProduit = 1L,
            idClient = 1L, // Client 1
            idTypeTarification = 1L, // ParBenifice
            prixCurrency = 2.99
        ),
        InputSqlModels.A_TarificationDataBaseFacileEntre(
            vidTimestamp = createTimestamp(
                day = 10,
                hour = 14,
                minute = 30
            ),
            idProduit = 1L,
            idClient = 1L,
            idTypeTarification = 2L,
            prixCurrency = 5.99
        ),

        // Test data for Chocolats (product id 2)
        InputSqlModels.A_TarificationDataBaseFacileEntre(
            vidTimestamp = System.currentTimeMillis() - 172800000, // 2 days ago
            idProduit = 2L,
            idClient = 2L,
            idTypeTarification = 2L, // ParBenifice
            prixCurrency = 4.99
        )
    )
}
