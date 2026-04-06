package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Preview.Data.Test

import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit

fun testCategories(): List<M16CategorieProduit> {
    return listOf(
        M16CategorieProduit(
            id = 1L,
            nom = "Snacks & Chips",
            position = 3
        ),
        M16CategorieProduit(
            id = 2L,
            nom = "Confiseries",
            position = 2
        ),
        M16CategorieProduit(
            id = 3L,
            nom = "Biscuits",
            position = 1
        ),
        M16CategorieProduit(
            id = 4L,
            nom = "Boissons",
            position = 4
        ),
        M16CategorieProduit(
            id = 5L,
            nom = "Produits Spéciaux",
            position = 5
        )
    )
}
