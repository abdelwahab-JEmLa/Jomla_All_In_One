package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Test

import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.InputSqlModels

/**
 * Test data for TypeTarification repository
 */
object TypeTarificationTestData {
    /**
     * Initial data for testing
     */
    val initialTestData = listOf(
        InputSqlModels.TypeTarificationDataBase(
            id = 1L,  // Explicitly set ID
            typeTarificationEnum = InputSqlModels.TypeTarificationEnum.ParBenifice
        ),
        InputSqlModels.TypeTarificationDataBase(
            id = 2L,  // Explicitly set ID
            typeTarificationEnum = InputSqlModels.TypeTarificationEnum.Historique
        ),
        InputSqlModels.TypeTarificationDataBase(
            id = 3L,  // Explicitly set ID
            typeTarificationEnum = InputSqlModels.TypeTarificationEnum.LeMaxPrixArrive
        )
    )
}
