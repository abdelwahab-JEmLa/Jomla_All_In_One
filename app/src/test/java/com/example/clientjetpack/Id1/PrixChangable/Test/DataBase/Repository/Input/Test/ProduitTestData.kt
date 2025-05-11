package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Input.Test

import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.InputEtInfosSqlModels

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
