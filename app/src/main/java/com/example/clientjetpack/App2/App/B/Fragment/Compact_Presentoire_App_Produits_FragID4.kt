package com.example.clientjetpack.App2.App.B.Fragment

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.Filter.GroupTunnel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.UiStateSec9Frag1
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.WifiConexiontLuncher
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainGetter_app2
import com.example.clientjetpack.App2.App.B.Fragment.Filter.FilterSortGroupe_Tunnels_app2
import com.example.clientjetpack.App2.App.B.Fragment.Z.Components.Dialogs.CategorySelectionDialog_App2
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
fun Compact_Presentoire_App_Produits_App2(
    modifier: Modifier = Modifier,
    FocusedValuesGetter_app2: FocusedValuesGetter_app2 = koinInject(),
    RepositorysMainGetter_app2: RepositorysMainGetter_app2 = koinInject(),
    viewModelWifiConexiontLuncher: WifiConexiontLuncher,
    categoryViewModel: EditeBaseDonneMainScreenIdS9ViewModel? = null,
    on_pour_send_data: (String, String) -> Unit = { _, _ -> },
    onClickImageToShowControles: () -> Unit = { },
    isWifiClientConnected_1: Boolean
) {
    val viewModelToUse = categoryViewModel ?: koinInject<EditeBaseDonneMainScreenIdS9ViewModel>()
    val uiState by viewModelToUse.uiState.collectAsState()

    var selectedProductForCategoryChange by remember { mutableStateOf<ArticlesBasesStatsTable?>(null) }
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

    val allCategories = remember(RepositorysMainGetter_app2.repoM16CategorieProduit.datasValue) {
        RepositorysMainGetter_app2.repoM16CategorieProduit.datasValue
    }

    val allProducts = remember(RepositorysMainGetter_app2.repo1ProduitInfos.datasValue) {
        RepositorysMainGetter_app2.repo1ProduitInfos.datasValue
    }

    val allColors = remember(RepositorysMainGetter_app2.repo03CouleurProduitInfos.datasValue) {
        RepositorysMainGetter_app2.repo03CouleurProduitInfos.datasValue
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

    FilterSortGroupe_Tunnels_app2(
        isWifiClientConnected_1=isWifiClientConnected_1,
        modifier = modifier,
        FocusedValuesGetter_app2 = FocusedValuesGetter_app2,
        RepositorysMainGetter_app2 = RepositorysMainGetter_app2,
        groupe_Par_Catalogue = groupe_Par_Catalogue,
        viewModelWifiConexiontLuncher = viewModelWifiConexiontLuncher,
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
        CategorySelectionDialog_App2(
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

                RepositorysMainGetter_app2.repo1ProduitInfos.update(updatedProduct)
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
                    CategoriesTabelle(
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
                    RepositorysMainGetter_app2.repoM16CategorieProduit.addOrUpdateData(updatedCategory)
                }
            }
        )
    }
}
