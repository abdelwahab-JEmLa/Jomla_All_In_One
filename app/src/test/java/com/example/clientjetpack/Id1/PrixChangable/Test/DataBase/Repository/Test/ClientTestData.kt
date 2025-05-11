package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Test

import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.AB_ReferentialSepareDataBases

/**
 * Test data for Client repository
 */
object ClientTestData {
    /**
     * Initial data for testing
     */
    val initialTestData = listOf(
        AB_ReferentialSepareDataBases.ClientDataBase(
            id = 1L,
            nom = "Client A",
            idActiveTypeTarificationDataBase = 1L  // Set to match the tarification type
        ),
        AB_ReferentialSepareDataBases.ClientDataBase(
            id = 2L,  // Explicitly set ID
            nom = "Client B",
        )
    )
}
