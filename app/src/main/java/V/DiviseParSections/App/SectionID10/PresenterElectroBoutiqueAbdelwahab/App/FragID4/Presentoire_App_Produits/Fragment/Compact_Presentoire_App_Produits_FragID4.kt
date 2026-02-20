package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Filter.FilterSortGroupe_Tunnels
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Filter.GroupTunnel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Z.Components.Dialogs.CategorySelectionDialog_FragID4
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.UiStateSec9Frag1
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

/**
 * Compact_Presentoire_App_Produits_FragID4
 *
 * Ultra-simplified version - delegates to specialized composables:
 * 1. Fetches raw data from repositories
 * 2. Delegates grouping to GroupTunnel (NOW WITH CATALOGUE HIERARCHY)
 * 3. Delegates filtering to FilterSortGroupe_Tunnels
 * 4. Delegates sorting to SortTunnel (handled within FilterSortGroupe_Tunnels)
 */
@Composable
fun Compact_Presentoire_App_Produits_FragID4(
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    viewModelHeadViewModel: HeadViewModel,
    categoryViewModel: EditeBaseDonneMainScreenIdS9ViewModel? = null,
    on_pour_send_data: (String, String) -> Unit = { _, _ -> },
    onClickImageToShowControles: () -> Unit,
    isWifiClientConnected_1: Boolean
) {
    val viewModelToUse = categoryViewModel ?: koinInject<EditeBaseDonneMainScreenIdS9ViewModel>()
    val uiState by viewModelToUse.uiState.collectAsState()

    var selectedProductForCategoryChange by remember { mutableStateOf<M01Produit?>(null) }
    var justMovedProductKeyID by remember { mutableStateOf<String?>(null) }

    // Auto-clear justMovedProductKeyID after animation
    LaunchedEffect(justMovedProductKeyID) {
        justMovedProductKeyID?.let {
            delay(1500)
            justMovedProductKeyID = null
        }
    }

    // ============================================
    // STEP 1: FETCH RAW DATA FROM REPOSITORIES
    // ============================================

    val allCategories = remember(repositorysMainGetter.repoM16CategorieProduit.datasValue) {
        repositorysMainGetter.repoM16CategorieProduit.datasValue
    }

    val allProducts = remember(repositorysMainGetter.repoM1Produit.datasValue) {
        repositorysMainGetter.repoM1Produit.datasValue
    }

    val allColors = remember(repositorysMainGetter.repo03CouleurProduitInfos.datasValue) {
        repositorysMainGetter.repo03CouleurProduitInfos.datasValue
            .sortedByDescending { it.creationTimestamp }
    }

    Log.d("Compact_Presentoire_FragID4", """
        |=== RAW DATA FETCHED ===
        |Categories: ${allCategories.size}
        |Products: ${allProducts.size}
        |Colors: ${allColors.size}
    """.trimMargin())

    // ============================================
    // STEP 2: GROUP DATA WITH CATALOGUE HIERARCHY (via GroupTunnel)
    // ============================================

    val groupe_Par_Catalogue = GroupTunnel(
        allColors = allColors,
        allProducts = allProducts,
        allCategories = allCategories
    )

    Log.d("Compact_Presentoire_FragID4", """
        |=== GROUPING COMPLETE (WITH CATALOGUE HIERARCHY) ===
        |Catalogues with categories: ${groupe_Par_Catalogue.size}
        |Total categories: ${groupe_Par_Catalogue.sumOf { it.second.size }}
        |Total products: ${groupe_Par_Catalogue.sumOf { (_, categories) ->
        categories.sumOf { it.second.size }
    }}
        |Delegating to FilterSortGroupe_Tunnels for filtering and sorting...
    """.trimMargin())

    // ============================================
    // STEP 3: FILTER & SORT (via FilterSortGroupe_Tunnels)
    // ============================================

    FilterSortGroupe_Tunnels(
        isWifiClientConnected_1=isWifiClientConnected_1,
        modifier = modifier,
        focusedValuesGetter = focusedValuesGetter,
        repositorysMainGetter = repositorysMainGetter,
        groupe_Par_Catalogue = groupe_Par_Catalogue,
        viewModelHeadViewModel = viewModelHeadViewModel,
        on_pour_send_data = on_pour_send_data,
        onClickImageToShowControles = onClickImageToShowControles,
        onProductCategoryClick = { product ->
            Log.d(
                "Compact_Presentoire_FragID4",
                "onProductCategoryClick called for: ${product.nom}"
            )
            selectedProductForCategoryChange = product
        },
        justMovedProductKeyID = justMovedProductKeyID
    )

    // ============================================
    // STEP 4: CATEGORY SELECTION DIALOG
    // ============================================

    selectedProductForCategoryChange?.let { product ->
        Log.d("Compact_Presentoire_FragID4", "Displaying CategorySelectionDialog for: ${product.nom}")
        CategorySelectionDialog_FragID4(
            product = product,
            allCategories = allCategories,
            allProducts = allProducts,
            isFastMoveMode = uiState.clickItemMode == UiStateSec9Frag1.ClickItemMode.FastMove,
            onCategorySelected = { newCategoryId ->
                Log.d("Compact_Presentoire_FragID4", "Category selected: $newCategoryId")
                val updatedProduct = newCategoryId?.let {
                    product.copy(
                        idParentCategorie = it,
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )
                } ?: product

                repositorysMainGetter.repoM1Produit.update(updatedProduct)
                justMovedProductKeyID = product.keyID

                selectedProductForCategoryChange = null
            },
            onDismiss = {
                Log.d("Compact_Presentoire_FragID4", "Dialog dismissed")
                selectedProductForCategoryChange = null
            },
            onCreateNewCategory = { categoryName ->
                Log.d("Compact_Presentoire_FragID4", "Creating new category: $categoryName")
                viewModelToUse.addOrUpdateCategorie(
                    M16CategorieProduit(
                        nom = categoryName,
                        position = 0,
                        catalogueParentId = 4
                    )
                )
            },
            onUpdateCategoryName = { categoryId, newName ->
                Log.d("Compact_Presentoire_FragID4", "Updating category $categoryId to: $newName")

                allCategories.find { it.id == categoryId }?.let { category ->
                    val updatedCategory = category.copy(nom = newName)
                    repositorysMainGetter.repoM16CategorieProduit.addOrUpdateData(updatedCategory)
                }
            }
        )
    }
}
