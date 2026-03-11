package Application4.App.Fragment.ID1.Fragment.Filter

import Application4.App.Fragment.ID1.Fragment.ViewModel.UiState_NewProtoPatterns
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.get_ListM21CataloguesCategorie
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * SortTunnel - Compact sorting and grouping logic with Catalogue support
 * UPDATED: Now handles Catalogue -> Category -> Product -> Colors hierarchy
 */
@Composable
fun SortTunnel(
    filteredProducts: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>,
    sortOrder: SortOrder_Facade_Boutique,
    enableCategoryGrouping: Boolean,
    uiStateNewProtoPatterns: UiState_NewProtoPatterns,
): List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>> {

    return remember(filteredProducts, sortOrder, enableCategoryGrouping) {

        // If grouping disabled, create single "All products" catalogue and category
        if (!enableCategoryGrouping) {
            val allProducts = filteredProducts.flatMap { (_, categories) ->
                categories.flatMap { it.second }
            }

            val sorted = when (sortOrder) {
                SortOrder_Facade_Boutique.ID_ASC -> allProducts.sortedBy { it.first.id }
                SortOrder_Facade_Boutique.ID_DESC -> allProducts.sortedByDescending { it.first.id }
                SortOrder_Facade_Boutique.NAME_ASC -> allProducts.sortedBy { it.first.nom }
                SortOrder_Facade_Boutique.NAME_DESC -> allProducts.sortedByDescending { it.first.nom }
                SortOrder_Facade_Boutique.PRIX_ACHAT_TIME_ASC -> allProducts.sortedBy { it.first.prixAchatDernierTimeTempUpdate }
                SortOrder_Facade_Boutique.PRIX_ACHAT_TIME_DESC -> allProducts.sortedByDescending { it.first.prixAchatDernierTimeTempUpdate }
                else -> allProducts
            }

            val singleCategory = M16CategorieProduit(
                id = -1,
                nom = "Tous les produits",
                displayedHeader = false
            )

            val singleCatalogue = M21CataloguesCategorie(
                id = -1,
                nom = "Tous les catalogues",
                position = 0
            )

            return@remember listOf(
                singleCatalogue to listOf(singleCategory to sorted)
            )
        }

        // Apply sorting with category and catalogue grouping
        when (sortOrder) {
            SortOrder_Facade_Boutique.CATEGORY_GROUPED -> {
                filteredProducts
                    .sortedBy { (catalogue, _) -> catalogue.position }
                    .map { (catalogue, categories) ->
                        catalogue to categories.sortedBy { (category, _) -> category.positionDouble }
                    }
            }
            else -> {
                val allProducts = filteredProducts.flatMap { (_, categories) ->
                    categories.flatMap { it.second }
                }

                val sorted = when (sortOrder) {
                    SortOrder_Facade_Boutique.ID_ASC -> allProducts.sortedBy { it.first.id }
                    SortOrder_Facade_Boutique.ID_DESC -> allProducts.sortedByDescending { it.first.id }
                    SortOrder_Facade_Boutique.NAME_ASC -> allProducts.sortedBy { it.first.nom }
                    SortOrder_Facade_Boutique.NAME_DESC -> allProducts.sortedByDescending { it.first.nom }
                    SortOrder_Facade_Boutique.PRIX_ACHAT_TIME_ASC -> allProducts.sortedBy { it.first.prixAchatDernierTimeTempUpdate }
                    SortOrder_Facade_Boutique.PRIX_ACHAT_TIME_DESC -> allProducts.sortedByDescending { it.first.prixAchatDernierTimeTempUpdate }
                    else -> allProducts
                }

                // FIX: was `uiState.list_M16CategorieProduit` (unresolved) — data lives inside list_Datas
                val categories = uiStateNewProtoPatterns.list_Datas?.m16CategorieProduit ?: emptyList()

                val categoryProductPairs = sorted
                    .groupBy { (product, _) -> product.idParentCategorie }
                    .mapNotNull { (categoryId, pairs) ->
                        categories
                            .find { it.id == categoryId }
                            ?.let { it to pairs }
                    }
                    .sortedBy { (category, _) -> category.positionDouble }

                val allCatalogues = get_ListM21CataloguesCategorie()

                allCatalogues
                    .sortedBy { it.position }
                    .mapNotNull { catalogue ->
                        val categoriesInCatalogue = categoryProductPairs
                            .filter { (category, _) ->
                                category.catalogueParentId == catalogue.id
                            }

                        if (categoriesInCatalogue.isNotEmpty()) {
                            catalogue to categoriesInCatalogue
                        } else {
                            null
                        }
                    }
            }
        }
    }
}
