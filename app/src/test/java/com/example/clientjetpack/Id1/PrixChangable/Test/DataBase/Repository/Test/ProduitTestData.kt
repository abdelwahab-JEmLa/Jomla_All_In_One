package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Test

import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.AB_ReferentialSepareDataBases

/**
 * Test data for Produit repository
 */
object ProduitTestData {
    /**
     * Initial data for testing
     */
    val initialTestData = listOf(
        AB_ReferentialSepareDataBases.ProduitDataBase(
            id = 1L,  // Explicitly set ID to 1 to match references
            nom = "Caramels"
        ),
        AB_ReferentialSepareDataBases.ProduitDataBase(
            id = 2L,  // Explicitly set ID to 2 to match references
            nom = "Chocolats"
        )
    )
}
