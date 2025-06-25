package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Preview.Data.Test

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A2_Passive.CategoriesTabelle

fun testCategories(): List<CategoriesTabelle> {
    return listOf(
        CategoriesTabelle(
            id = 1L,
            nom = "Snacks & Chips",
            position = 3
        ),
        CategoriesTabelle(
            id = 2L,
            nom = "Confiseries",
            position = 2
        ),
        CategoriesTabelle(
            id = 3L,
            nom = "Biscuits",
            position = 1
        ),
        CategoriesTabelle(
            id = 4L,
            nom = "Boissons",
            position = 4
        ),
        CategoriesTabelle(
            id = 5L,
            nom = "Produits Spéciaux",
            position = 5
        )
    )
}
