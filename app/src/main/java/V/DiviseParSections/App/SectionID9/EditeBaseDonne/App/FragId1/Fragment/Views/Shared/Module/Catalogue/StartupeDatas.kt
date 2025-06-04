package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.Shared.Module.Catalogue

fun startupeDatas(): List<CataloguesCaegorie> {
    return listOf(
        
        CataloguesCaegorie(
            id = 2,
            nom = "Cosmétique",
            premierCategorieId = 18,
            position = 1  // Added position for consistency
        ),
        CataloguesCaegorie(
            id = 1,
            nom = "Confiserie",
            premierCategorieId = 32,
            position = 2  // Fixed: Added missing position
        ),
        CataloguesCaegorie(
            id = 3,
            nom = "TeBnage",
            premierCategorieId = 136,
            position = 3  // Added position for consistency
        )
    )
}
