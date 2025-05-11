package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Input.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.InputEtInfosSqlModels

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
