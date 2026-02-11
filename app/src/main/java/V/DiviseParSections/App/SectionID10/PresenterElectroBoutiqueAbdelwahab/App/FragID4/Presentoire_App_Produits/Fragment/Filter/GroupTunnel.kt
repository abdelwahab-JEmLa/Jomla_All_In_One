package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Filter

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.CataloguesCaegorie
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
    allProducts: List<ArticlesBasesStatsTable>,
    allCategories: List<CategoriesTabelle>
): List<Pair<CataloguesCaegorie, List<Pair<CategoriesTabelle, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>>> {

    return remember(allColors, allProducts, allCategories) {
        // Get all available catalogues
        val allCatalogues = B4CatalogueCategoriesRepository()

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
