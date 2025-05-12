package com.example.clientjetpack.ID1.Test.Fragment.DataBase.Repository.Input.Test

import com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.InputEtInfosSqlModels

/**
 * Test data for Produit repository
 */
object ProduitTestData {
    /**
     * Initial data for testing
     */
    val initialTestData = listOf(
        InputEtInfosSqlModels.ProduitInfos(
            id = 1L,  // Explicitly set ID to 1 to match references
            nom = "Caramels"
        ),
        InputEtInfosSqlModels.ProduitInfos(
            id = 2L,  // Explicitly set ID to 2 to match references
            nom = "Chocolats"
        )
    )
}
