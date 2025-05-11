package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Input.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.InputEtInfosSqlModels

/**
 * Test data for Client repository
 */
object ClientTestData {
    /**
     * Initial data for testing
     */
    val initialTestData = listOf(
        InputEtInfosSqlModels.ClientDataBase(
            id = 1L,
            nom = "Client A",
            idActiveTypeTarificationDataBase = 1L  // Set to match the tarification type
        ),
        InputEtInfosSqlModels.ClientDataBase(
            id = 2L,  // Explicitly set ID
            nom = "Client B",
        )
    )
}
