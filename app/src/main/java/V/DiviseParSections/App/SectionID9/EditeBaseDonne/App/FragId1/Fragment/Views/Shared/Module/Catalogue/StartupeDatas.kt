package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.Shared.Module.Catalogue

fun startupeDatas(): List<CataloguesCaegorie> {
    return listOf(
        CataloguesCaegorie(
            id = 1,
            nom = "Confiserie",
            premierCategorieId = 20,
            position = 1  // Fixed: Added missing position
        ),
        CataloguesCaegorie(
            id = 2,
            nom = "Cosmétique",
            premierCategorieId = 1,
            position = 2  // Added position for consistency
        ),
        CataloguesCaegorie(
            id = 3,
            nom = "Teenager",
            premierCategorieId = 100,
            position = 3  // Added position for consistency
        )
    )
}
