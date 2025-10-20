package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.ViewModel.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import org.koin.compose.koinInject

@Composable
fun MainFilterT1(
    viewModel: ViewModelMainFastSearchProduitPourVent,
    products: List<ArticlesBasesStatsTable>,
    categories: List<CategoriesTabelle>,
    searchFilter: String,
    modifier: Modifier = Modifier,
    sourceLenceurDeCetteFragment: ActiveCentralValues.RoleDefinieParSourceACetteFragment?,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repo10OperationVentCouleur: Repo10OperationVentCouleur = aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur,
    searchFieldFocusRequester: FocusRequester? = null,  // ADD THIS
    on_Pour_FocuceAfficheClavieSearcherProduit: () -> Unit = {},
    isCartonEditMode: Boolean,
    on_PourEntre_EditeMode: (Boolean) -> Unit = {},
) {
    val currentApp_Est_ItsWorkChezGrossisst = focusedValuesGetter.currentApp_ItsWorkChezGrossisst
    val categoryMap = remember(categories) { categories.associateBy { it.id } }
    val catalogues = remember { B4CatalogueCategoriesRepository().associateBy { it.id } }

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
                    products.filter {
                        it.nom.contains(searchFilter, true) ||
                                it.nomMutable.contains(searchFilter, true) ||
                                it.nomArab.contains(searchFilter, true)
                    }
                }
            }
            null -> {
                // Default behavior - use search text filtering
                if (searchFilter.isBlank()) {
                    emptyList() // Return empty list when no search text
                } else {
                    products.filter {
                        it.nom.contains(searchFilter, true) ||
                                it.nomMutable.contains(searchFilter, true) ||
                                it.nomArab.contains(searchFilter, true)
                    }
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

    val sortedProducts = remember(filteredProducts, categories, repo10OperationVentCouleur_datasValue, currentApp_Est_ItsWorkChezGrossisst) {  //<--
    //TODO(1): fait si le produit dernie update est moin de  2 mn de ne pas secont  sorte order
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

            // Separate products into those with sales and those without
            val (productsWithSales, productsWithoutSales) = filteredProducts.partition { product ->
                productLastSaleMap.containsKey(product.keyID)
            }

            // Sort products with sales by most recent sale timestamp (descending)
            val sortedWithSales = productsWithSales.sortedByDescending { product ->
                productLastSaleMap[product.keyID] ?: 0L
            }

            // Sort products without sales by existing logic
            val (regular, orphan) = productsWithoutSales.partition { product ->
                val category = categoryMap[product.idParentCategorie ?: 0L]
                val catalogueId = category?.catalogueParentId ?: 4L
                category != null && catalogueId != 4L && !category.nom.equals("NONE", true)
            }

            val sortedRegular = regular.sortedWith(
                compareBy<ArticlesBasesStatsTable> {
                    val category = categoryMap[it.idParentCategorie ?: 0L]
                    catalogues[category?.catalogueParentId ?: 4L]?.position ?: Int.MAX_VALUE
                }.thenBy { categoryMap[it.idParentCategorie ?: 0L]?.position ?: Int.MAX_VALUE }
                    .thenBy { it.positionDonSonCesFrereCategorieProduits }
                    .thenBy { it.nom.lowercase() }
            )

            val sortedOrphan = orphan.sortedWith(
                compareBy<ArticlesBasesStatsTable> {
                    val category = categoryMap[it.idParentCategorie ?: 0L]
                    category?.nom?.takeIf { !it.equals("NONE", true) } ?: "ZZZZZ_NO_CATEGORY"
                }.thenBy { it.positionDonSonCesFrereCategorieProduits }
                    .thenBy { it.nom.lowercase() }
            )

            // Return products with recent sales first, then the rest
            sortedWithSales + sortedRegular + sortedOrphan
        }
    }


    MainListT1(
        modifier = modifier,
        searchFilter = searchFilter,
        sortedProducts = sortedProducts,
        searchFieldFocusRequester = searchFieldFocusRequester,  // ADD THIS
        on_Pour_FocuceAfficheClavieSearcherProduit = on_Pour_FocuceAfficheClavieSearcherProduit,
        isCartonEditMode = isCartonEditMode,
        on_PourEntre_EditeMode = on_PourEntre_EditeMode
    )
}
