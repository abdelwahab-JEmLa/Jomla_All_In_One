package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Preview.Data.Test

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.C_CategorieProduitInfos

fun testCategories(): List<C_CategorieProduitInfos> {
    return listOf(
        C_CategorieProduitInfos(
            id = 1L,
            nom = "Snacks & Chips",
            position = 3
        ),
        C_CategorieProduitInfos(
            id = 2L,
            nom = "Confiseries",
            position = 2
        ),
        C_CategorieProduitInfos(
            id = 3L,
            nom = "Biscuits",
            position = 1
        ),
        C_CategorieProduitInfos(
            id = 4L,
            nom = "Boissons",
            position = 4
        ),
        C_CategorieProduitInfos(
            id = 5L,
            nom = "Produits Spéciaux",
            position = 5
        )
    )
}
