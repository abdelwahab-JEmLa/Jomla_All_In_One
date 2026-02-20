package V.DiviseParSections.App.Shared.Repository.Repo21.Repository

import EntreApps.Shared.Models.M21CataloguesCategorie
import androidx.compose.ui.graphics.Color

// Static repository function that provides catalogues list
fun get_ListM21CataloguesCategorie(): List<M21CataloguesCategorie> {
    return listOf(
        M21CataloguesCategorie(
            keyID = "t4",
            id = 4,
            nom = "Sans Catalogue",
            premierCategorieId = 0,
            position = 0,
            couleur = Color(0xFF9C27B0) // Purple
        ),
        M21CataloguesCategorie(
            keyID = "t2",
            id = 2,
            nom = "Cosmétique",
            premierCategorieId = 1755942163531,
            position = 1,
            couleur = Color(0xFFE91E63) // Pink for cosmetics
        ),
        M21CataloguesCategorie(
            keyID = "t1",
            id = 1,
            nom = "Confiserie",
            premierCategorieId = 1755942577975,
            position = 2,
            couleur = Color(0xFFFF9800) // Orange for confectionery
        ),
        M21CataloguesCategorie(
            keyID = "t3",
            id = 3,
            nom = "TeBnage",
            premierCategorieId = 1755942590731,
            position = 3,
            couleur = Color(0xFF4CAF50) // Green for teenage category
        ),
    )
}
