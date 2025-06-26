package Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CataloguesCaegorie
import androidx.compose.ui.graphics.Color

// Static repository function that provides catalogues list
fun B4CatalogueCategoriesRepository(): List<CataloguesCaegorie> {
    return listOf(
        CataloguesCaegorie(
            key = "t4",
            id = 4,
            nom = "Sans Catalogue",
            premierCategorieId = 0,
            position = 0,
            couleur = Color(0xFF9C27B0) // Purple
        ),
        CataloguesCaegorie(
            key = "t2",
            id = 2,
            nom = "Cosmétique",
            premierCategorieId = 100,
            position = 1,
            couleur = Color(0xFFE91E63) // Pink for cosmetics
        ),
        CataloguesCaegorie(
            key = "t1",
            id = 1,
            nom = "Confiserie",
            premierCategorieId = 3,
            position = 2,
            couleur = Color(0xFFFF9800) // Orange for confectionery
        ),
        CataloguesCaegorie(
            key = "t3",
            id = 3,
            nom = "TeBnage",
            premierCategorieId = 92,
            position = 3,
            couleur = Color(0xFF4CAF50) // Green for teenage category
        ),
    )
}
