package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.ViewModel.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
) {
    val currentApp_Est_ItsWorkChezGrossisst = focusedValuesGetter.currentApp_Est_ItsWorkChezGrossisst
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

    val sortedProducts = remember(filteredProducts, categories) {
        if (filteredProducts.isEmpty()) {
            emptyList()
        } else {
            val (regular, orphan) = filteredProducts.partition { product ->
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

            sortedRegular + sortedOrphan
        }
    }

    MainListT1(modifier=modifier, searchFilter=searchFilter, sortedProducts=sortedProducts)
}
