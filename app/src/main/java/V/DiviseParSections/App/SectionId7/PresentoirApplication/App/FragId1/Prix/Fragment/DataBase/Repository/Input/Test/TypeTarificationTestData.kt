package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Input.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.InputEtInfosSqlModels

/**
 * Test data for TypeTarification repository
 */
object TypeTarificationTestData {
    /**
     * Initial data for testing
     */
    val initialTestData = listOf(
        InputEtInfosSqlModels.TypeTarificationDataBase(
            id = 1L,  // Explicitly set ID
            typeTarificationEnum = InputEtInfosSqlModels.TypeTarificationEnum.ParBenifice
        ),
        InputEtInfosSqlModels.TypeTarificationDataBase(
            id = 2L,  // Explicitly set ID
            typeTarificationEnum = InputEtInfosSqlModels.TypeTarificationEnum.Historique
        ),
        InputEtInfosSqlModels.TypeTarificationDataBase(
            id = 3L,  // Explicitly set ID
            typeTarificationEnum = InputEtInfosSqlModels.TypeTarificationEnum.LeMaxPrixArrive
        )
    )
}
