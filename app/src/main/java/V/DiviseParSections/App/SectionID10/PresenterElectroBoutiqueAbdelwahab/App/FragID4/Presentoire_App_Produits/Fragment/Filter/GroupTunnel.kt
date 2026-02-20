package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Filter

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.get_ListM21CataloguesCategorie
import EntreApps.Shared.Models.M21CataloguesCategorie
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * GroupTunnel - Compact grouping logic with Catalogue hierarchy
 * Groups: Catalogue -> Category -> Product -> Colors
 * FIXED: Now returns proper catalogue hierarchy structure
 */
@Composable
fun GroupTunnel(
    allColors: List<M3CouleurProduitInfos>,
    allProducts: List<M01Produit>,
    allCategories: List<M16CategorieProduit>
): List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>> {

    return remember(allColors, allProducts, allCategories) {
        // Get all available catalogues
        val allCatalogues = get_ListM21CataloguesCategorie()

        // Step 1: Group colors by product
        val productColorPairs = allColors
            .groupBy { it.parentBProduitInfosKeyID }
            .mapNotNull { (productKeyID, colors) ->
                allProducts.find { it.keyID == productKeyID }?.let { it to colors }
            }
            .sortedBy { (product, _) -> product.nom }

        // Step 2: Group products by category
        val categoryProductPairs = productColorPairs
            .groupBy { (product, _) -> product.idParentCategorie }
            .mapNotNull { (categoryId, pairs) ->
                allCategories.find { it.id == categoryId }?.let { category ->
                    category to pairs
                }
            }
            .sortedBy { (category, _) -> category.positionDouble }

        // Step 3: Group categories by catalogue
        allCatalogues
            .sortedBy { it.position }
            .mapNotNull { catalogue ->
                // Find all categories that belong to this catalogue
                val categoriesInCatalogue = categoryProductPairs
                    .filter { (category, _) ->
                        category.catalogueParentId == catalogue.id
                    }
                    .sortedBy { (category, _) -> category.positionDouble }

                // Only include catalogues that have categories with products
                if (categoriesInCatalogue.isNotEmpty()) {
                    catalogue to categoriesInCatalogue
                } else {
                    null
                }
            }
    }
}
