package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Test

import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.AB_ReferentialSepareDataBases

/**
 * Test data for TypeTarification repository
 */
object TypeTarificationTestData {
    /**
     * Initial data for testing
     */
    val initialTestData = listOf(
        AB_ReferentialSepareDataBases.TypeTarificationDataBase(
            id = 1L,  // Explicitly set ID
            typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.ParBenifice
        ),
        AB_ReferentialSepareDataBases.TypeTarificationDataBase(
            id = 2L,  // Explicitly set ID
            typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.Historique
        ),
        AB_ReferentialSepareDataBases.TypeTarificationDataBase(
            id = 3L,  // Explicitly set ID
            typeTarificationEnum = AB_ReferentialSepareDataBases.TypeTarificationEnum.LeMaxPrixArrive
        )
    )
}
