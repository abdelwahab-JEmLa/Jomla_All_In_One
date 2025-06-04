package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.Shared.Module.Catalogue

fun startupeDatas(): List<CataloguesCaegorie> {
    return listOf(
        CataloguesCaegorie(
            id = 1,
            nom = "Confiserie",
            premierCategorieId = 20
        ),
        CataloguesCaegorie(
            id = 2,
            nom = "Cosmétique",
            premierCategorieId = 1
        ),
        CataloguesCaegorie(
            id = 3,
            nom = "Teenager",
            premierCategorieId = 100
        )
    )
}
