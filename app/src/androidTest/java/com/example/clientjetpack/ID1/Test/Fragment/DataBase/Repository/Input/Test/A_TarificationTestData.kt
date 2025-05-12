package com.example.clientjetpack.ID1.Test.Fragment.DataBase.Repository.Input.Test

import com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.InputEtInfosSqlModels
import com.example.clientjetpack.ID1.Test.Fragment.Passive.createTimestamp

object A_TarificationTestData {
    val initialTestData = listOf(
        InputEtInfosSqlModels.Tarification(
            vidTimestamp = createTimestamp(
                day = 10,
                hour = 14,
                minute = 30
            ),
            idProduit = 1L,
            idClient = 1L,
            idTypeTarification = 1L,
            prixCurrency = 2.99
        ),
        InputEtInfosSqlModels.Tarification(
            vidTimestamp = createTimestamp(
                day = 12,
                hour = 14,
                minute = 30
            ),
            idProduit = 1L,
            idClient = 1L,
            idTypeTarification = 2L,
            prixCurrency = 5.99
        ),

        // Test data for Chocolats (product id 2)
        InputEtInfosSqlModels.Tarification(
            vidTimestamp = createTimestamp(
                day = 13,
                hour = 14,
                minute = 30
            ),
            idProduit = 2L,
            idClient = 2L,
            idTypeTarification = 2L, // ParBenifice
            prixCurrency = 4.99
        )
    )
}
