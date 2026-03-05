package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.ViewModel.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import EntreApps.Shared.Models.Home.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.get_ListM21CataloguesCategorie
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import EntreApps.Shared.Models.M16CategorieProduit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import org.koin.compose.koinInject

@Composable
fun MainFilterT1(
    viewModel: ViewModelMainFastSearchProduitPourVent,
    products: List<M01Produit>,
    categories: List<M16CategorieProduit>,
    searchFilter: String,
    modifier: Modifier = Modifier,
    sourceLenceurDeCetteFragment: ActiveCentralValues.RoleDefinieParSourceACetteFragment?,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repo10OperationVentCouleur: Repo10OperationVentCouleur = aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur,
    searchFieldFocusRequester: FocusRequester? = null,
    on_Pour_FocuceAfficheClavieSearcherProduit: () -> Unit = {},
    cartonEditModeProductId: String? = null,
    boitEditModeProductId: String? = null,  // NEW PARAMETER
    on_PourEntre_CartonEditeMode: (String?) -> Unit = {},
    on_PourEntre_BoitEditeMode: (String?) -> Unit = {},  // NEW CALLBACK
) {
    val currentApp_Est_ItsWorkChezGrossisst = focusedValuesGetter.currentApp_ItsWorkChezGrossisst
    val categoryMap = remember(categories) { categories.associateBy { it.id } }
    val catalogues = remember { get_ListM21CataloguesCategorie().associateBy { it.id } }


// Replace the filteredProducts remember block with this updated version:

    val filteredProducts = remember(products, searchFilter, sourceLenceurDeCetteFragment, currentApp_Est_ItsWorkChezGrossisst) {
        // First apply source-based filtering
        val sourceFilteredProducts = when (sourceLenceurDeCetteFragment) {
            is ActiveCentralValues.RoleDefinieParSourceACetteFragment.SearchProduit -> {
                // Filter by specific product instead of search text
                products.filter { it.id == sourceLenceurDeCetteFragment.produit.id }
            }
            is ActiveCentralValues.RoleDefinieParSourceACetteFragment.AfficheSearchAllProduits -> {
                // Use search text filtering for general search
                if (searchFilter.isBlank()) {
                    emptyList() // Return empty list when no search text
                } else {
                    // Separate primary matches from category name matches
                    val primaryMatches = products.filter {
                        it.nom.contains(searchFilter, true) ||
                                it.nomArab.contains(searchFilter, true)
                    }

                    val categoryMatches = products.filter {
                        !primaryMatches.contains(it) && // Exclude products already in primary matches
                                it.nomMutable.contains(searchFilter, true)
                    }

                    // Return primary matches first, then category matches
                    primaryMatches + categoryMatches
                }
            }
            null -> {
                // Default behavior - use search text filtering
                if (searchFilter.isBlank()) {
                    emptyList() // Return empty list when no search text
                } else {
                    // Separate primary matches from category name matches
                    val primaryMatches = products.filter {
                        it.nom.contains(searchFilter, true) ||
                                it.nomArab.contains(searchFilter, true)
                    }

                    val categoryMatches = products.filter {
                        !primaryMatches.contains(it) && // Exclude products already in primary matches
                                it.nomMutable.contains(searchFilter, true)
                    }

                    // Return primary matches first, then category matches
                    primaryMatches + categoryMatches
                }
            }
        }

        if (currentApp_Est_ItsWorkChezGrossisst) {
            sourceFilteredProducts.filter { product ->
                val category = categoryMap[product.idParentCategorie ?: 0L]
                val catalogueId = category?.catalogueParentId ?: 4L
                catalogueId == 1L // Filter only products from Confiserie catalogue (id = 1)
            }
        } else {
            sourceFilteredProducts
        }
    }

    val repo10OperationVentCouleur_datasValue = repo10OperationVentCouleur.datasValue
    val currentTimeMillis = remember { System.currentTimeMillis() }
    val twoMinutesInMillis = 2 * 60 * 1000L  // 2 minutes

    val sortedProducts = remember(
        filteredProducts,
        categories,
        repo10OperationVentCouleur_datasValue,
        currentApp_Est_ItsWorkChezGrossisst,
        currentTimeMillis
    ) {
        if (filteredProducts.isEmpty()) {
            emptyList()
        } else {
            val relevantOperations = if (currentApp_Est_ItsWorkChezGrossisst) {
                repo10OperationVentCouleur_datasValue.filter { it.its_created_in_working_for_wholesaler }
            } else {
                repo10OperationVentCouleur_datasValue.filter { !it.its_created_in_working_for_wholesaler }
            }

            val productLastSaleMap = relevantOperations
                .groupBy { it.parent_M1Produit_KeyId }
                .mapValues { (_, operations) ->
                    operations.maxOfOrNull { it.dernierTimeTampsSynchronisationAvecFireBase } ?: 0L
                }

            val recentlyUpdatedProductIds = productLastSaleMap
                .filter { (_, timestamp) ->
                    (currentTimeMillis - timestamp) < twoMinutesInMillis
                }
                .keys

            // Separate products: recently updated, with sales, and without sales
            val (recentlyUpdated, otherProducts) = filteredProducts.partition { product ->
                recentlyUpdatedProductIds.contains(product.keyID)
            }

            // Keep recently updated products in their current order (don't re-sort)
            val sortedRecentlyUpdated = recentlyUpdated

            // Sort other products as before
            val (productsWithSales, productsWithoutSales) = otherProducts.partition { product ->
                productLastSaleMap.containsKey(product.keyID)
            }

            val sortedWithSales = productsWithSales.sortedByDescending { product ->
                productLastSaleMap[product.keyID] ?: 0L
            }

            val (regular, orphan) = productsWithoutSales.partition { product ->
                val category = categoryMap[product.idParentCategorie ?: 0L]
                val catalogueId = category?.catalogueParentId ?: 4L
                category != null && catalogueId != 4L && !category.nom.equals("NONE", true)
            }

            val sortedRegular = regular.sortedWith(
                compareBy<M01Produit> {
                    val category = categoryMap[it.idParentCategorie ?: 0L]
                    catalogues[category?.catalogueParentId ?: 4L]?.position ?: Int.MAX_VALUE
                }.thenBy { categoryMap[it.idParentCategorie ?: 0L]?.position ?: Int.MAX_VALUE }
                    .thenBy { it.positionDonSonCesFrereCategorieProduits }
                    .thenBy { it.nom.lowercase() }
            )

            val sortedOrphan = orphan.sortedWith(
                compareBy<M01Produit> {
                    val category = categoryMap[it.idParentCategorie ?: 0L]
                    category?.nom?.takeIf { !it.equals("NONE", true) } ?: "ZZZZZ_NO_CATEGORY"
                }.thenBy { it.positionDonSonCesFrereCategorieProduits }
                    .thenBy { it.nom.lowercase() }
            )

            // Return: recently updated first (maintain order), then sorted by sales, then rest
            sortedRecentlyUpdated + sortedWithSales + sortedRegular + sortedOrphan
        }
    }

    MainListT1(
        modifier = modifier,
        searchFilter = searchFilter,
        sortedProducts = sortedProducts,
        searchFieldFocusRequester = searchFieldFocusRequester,
        on_Pour_FocuceAfficheClavieSearcherProduit = on_Pour_FocuceAfficheClavieSearcherProduit,
        cartonEditModeProductId = cartonEditModeProductId,
        boitEditModeProductId = boitEditModeProductId,  // NEW
        on_PourEntre_CartonEditeMode = on_PourEntre_CartonEditeMode,
        on_PourEntre_BoitEditeMode = on_PourEntre_BoitEditeMode  // NEW
    )
}
