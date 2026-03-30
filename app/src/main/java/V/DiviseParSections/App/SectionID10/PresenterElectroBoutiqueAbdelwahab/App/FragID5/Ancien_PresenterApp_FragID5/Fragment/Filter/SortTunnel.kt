package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Filter

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.get_ListM21CataloguesCategorie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Filter.Model.SortOrder_Facade_Boutique
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import androidx.compose.runtime.Composable


fun SortTunnel(
    filteredProducts: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>,
    sortOrder: SortOrder_Facade_Boutique,
    enableCategoryGrouping: Boolean,
    repositorysMainGetter: RepositorysMainGetter,
    prioritiseProduitsEnVente: Boolean = false,
    onVentProduitKeyIDs: Set<String> = emptySet()
): List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>> {

    // ── helper: float en-vente products to the top of every category ──────────
    fun floatEnVente(
        result: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>
    ) = result.map { (catalogue, categories) ->
        catalogue to categories.map { (category, products) ->
            val (enVente, rest) = products.partition { (product, _) ->
                product.keyID in onVentProduitKeyIDs
            }
            category to (enVente + rest)
        }
    }

    val shouldFloat = prioritiseProduitsEnVente && onVentProduitKeyIDs.isNotEmpty()

    // ── no grouping: flatten → sort → optional float ───────────────────────────
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

        val flat = listOf(singleCatalogue to listOf(singleCategory to sorted))
        return if (shouldFloat) floatEnVente(flat) else flat
    }

    // ── grouping enabled: sort then optional float ─────────────────────────────
    val sortedResult = when (sortOrder) {
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

            val categoryProductPairs = sorted
                .groupBy { (product, _) -> product.idParentCategorie }
                .mapNotNull { (categoryId, pairs) ->
                    repositorysMainGetter.repoM16CategorieProduit.datasValue
                        .find { it.id == categoryId }
                        ?.let { it to pairs }
                }
                .sortedBy { (category, _) -> category.positionDouble }

            val allCatalogues = get_ListM21CataloguesCategorie()

            allCatalogues
                .sortedBy { it.position }
                .mapNotNull { catalogue ->
                    val categoriesInCatalogue = categoryProductPairs
                        .filter { (category, _) -> category.catalogueParentId == catalogue.id }
                    if (categoriesInCatalogue.isNotEmpty()) catalogue to categoriesInCatalogue
                    else null
                }
        }
    }

    return if (shouldFloat) floatEnVente(sortedResult) else sortedResult
}
