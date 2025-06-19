package Views.P1._ArticlesStartFacade.B.View.B.List.Repository

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CataloguesCaegorie

// Static repository function that provides catalogues list
fun B4CatalogueCategoriesRepository(): List<CataloguesCaegorie> {
    return listOf(
        CataloguesCaegorie(
            bsonObjectId = "t4",
            id = 4,
            nom = "Sans Catalogue",
            premierCategorieId = 0,
            position = 0
        ),
        CataloguesCaegorie(
            bsonObjectId = "t2",
            id = 2,
            nom = "Cosmétique",
            premierCategorieId = 100,
            position = 1
        ),
        CataloguesCaegorie(
            bsonObjectId = "t1",
            id = 1,
            nom = "Confiserie",
            premierCategorieId = 3,
            position = 2
        ),
        CataloguesCaegorie(
            bsonObjectId = "t3",
            id = 3,
            nom = "TeBnage",
            premierCategorieId = 92,
            position = 3
        ),
    )
}
