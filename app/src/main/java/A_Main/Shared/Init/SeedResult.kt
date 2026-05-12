package A_Main.Shared.Init

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos

data class SeedResult(
    val colors: List<M3CouleurProduitInfos> = emptyList(),
    val products: List<M01Produit> = emptyList(),
    val categories: List<M16CategorieProduit> = emptyList(),
)
