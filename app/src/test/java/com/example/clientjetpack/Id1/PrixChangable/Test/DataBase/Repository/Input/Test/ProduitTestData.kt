package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Input.Test

import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.InputSqlModels

/**
 * Test data for Produit repository
 */
object ProduitTestData {
    /**
     * Initial data for testing
     */
    val initialTestData = listOf(
        InputSqlModels.ProduitDataBase(
            id = 1L,  // Explicitly set ID to 1 to match references
            nom = "Caramels"
        ),
        InputSqlModels.ProduitDataBase(
            id = 2L,  // Explicitly set ID to 2 to match references
            nom = "Chocolats"
        )
    )
}
