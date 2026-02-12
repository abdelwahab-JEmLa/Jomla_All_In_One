package com.example.clientjetpack.App2.App.B.Fragment.Filter

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.CataloguesCaegorie
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainGetter_app2

/**
 * SortTunnel - Compact sorting and grouping logic with Catalogue support
 * UPDATED: Now handles Catalogue -> Category -> Product -> Colors hierarchy
 */
@Composable
fun SortTunnel(
    filteredProducts: List<Pair<CataloguesCaegorie, List<Pair<CategoriesTabelle, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>>>,
    sortOrder: SortOrder_Facade_Boutique,
    enableCategoryGrouping: Boolean,
    RepositorysMainGetter_app2: RepositorysMainGetter_app2
): List<Pair<CataloguesCaegorie, List<Pair<CategoriesTabelle, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>>> {

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

            val singleCategory = CategoriesTabelle(
                id = -1,
                nom = "Tous les produits",
                displayedHeader = false
            )

            val singleCatalogue = CataloguesCaegorie(
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
                // Keep catalogue and category grouping, sort by position
                filteredProducts
                    .sortedBy { (catalogue, _) -> catalogue.position }
                    .map { (catalogue, categories) ->
                        catalogue to categories.sortedBy { (category, _) -> category.positionDouble }
                    }
            }
            else -> {
                // Flatten all products, sort them, then regroup by catalogue and category
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

                // Regroup by category
                val categoryProductPairs = sorted
                    .groupBy { (product, _) -> product.idParentCategorie }
                    .mapNotNull { (categoryId, pairs) ->
                        RepositorysMainGetter_app2.repoM16CategorieProduit.datasValue
                            .find { it.id == categoryId }
                            ?.let { it to pairs }
                    }
                    .sortedBy { (category, _) -> category.positionDouble }

                // Regroup by catalogue
                val allCatalogues = B4CatalogueCategoriesRepository()

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
