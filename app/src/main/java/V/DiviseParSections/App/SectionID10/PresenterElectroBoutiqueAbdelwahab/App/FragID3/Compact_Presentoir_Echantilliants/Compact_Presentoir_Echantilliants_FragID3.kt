package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Dialog_Fast_Affiche_Panie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.Item_Produit_FragID3
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST.Dialogs.CategorySelectionDialog
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.CataloguesCaegorie
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Jomla_Clients
import V.DiviseParSections.App._0.Navigation.Screen
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun Compact_Presentoir_Echantilliants_FragID3(
    modifier: Modifier = Modifier,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    FragmentNavigationHandler: FragmentNavigationHandler = koinInject(),
    categoryViewModel: EditeBaseDonneMainScreenIdS9ViewModel? = null,
    on_pour_send_data: (String, String) -> Unit = { _, _ -> }
) {
    // FIXED: Create ViewModel locally if not provided
    val viewModelToUse = categoryViewModel ?: koinInject<EditeBaseDonneMainScreenIdS9ViewModel>()

    // LOG: Initial state check
    android.util.Log.e("CategoryDialog", "=== Compact_Presentoir_Echantilliants_FragID3 COMPOSED ===")
    android.util.Log.e("CategoryDialog", "categoryViewModel provided: ${categoryViewModel != null}")
    android.util.Log.e("CategoryDialog", "viewModelToUse is available: ${viewModelToUse != null}")

    // State for category dialog management
    var selectedProductForCategoryChange by remember { mutableStateOf<ArticlesBasesStatsTable?>(null) }

    // LOG: Track state changes
    LaunchedEffect(selectedProductForCategoryChange) {
        android.util.Log.d("CategoryDialog", "selectedProductForCategoryChange changed: ${selectedProductForCategoryChange?.nom ?: "null"}")
        android.util.Log.d("CategoryDialog", "viewModelToUse is null: ${viewModelToUse == null}")
    }

    // Get all categories for the dialog
    val allCategories = remember(repositorysMainGetter.repoM16CategorieProduit.datasValue) {
        repositorysMainGetter.repoM16CategorieProduit.datasValue
    }

    val categoryMap = remember(allCategories) {
        allCategories.associateBy { it.id }
    }

    val catalogues = remember { B4CatalogueCategoriesRepository() }

    val lastBonVentAbdelwahab = remember(
        repositorysMainGetter.repo8BonVent.datasValue,
        repositorysMainGetter.repo2Client.datasValue
    ) {
        repositorysMainGetter.getLastBonVentForClient(
            clientKeyID = Jomla_Clients.ECHATILLANTS_KEY_ID,
            etateFilter = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
        )
    }

    val operationsFromLastBon = remember(
        lastBonVentAbdelwahab,
        repositorysMainGetter.repo10OperationVentCouleur.datasValue
    ) {
        lastBonVentAbdelwahab?.let { bonVent ->
            repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter { operation ->
                operation.parent_M8BonVent_KeyId == bonVent.keyID
            }
        } ?: emptyList()
    }

    val list_M3couleur = remember(
        repositorysMainGetter.repo03CouleurProduitInfos.datasValue,
        operationsFromLastBon
    ) {
        repositorysMainGetter.repo03CouleurProduitInfos.datasValue.filter { couleur ->
            val hasStock = couleur.count_Don_Depot > 0

            val produit = repositorysMainGetter.repoM1Produit.datasValue.find {
                it.keyID == couleur.parentBProduitInfosKeyID
            }

            val isInOperations = operationsFromLastBon.any { operation ->
                operation.parent_M3CouleurProduit_KeyID == couleur.keyID
            }

            val isAvailable = produit?.disponibilityEtates == DisponibilityEtates.DISPO

            hasStock && isInOperations && isAvailable
        }.sortedByDescending { couleur ->
            operationsFromLastBon.find { operation ->
                operation.parent_M3CouleurProduit_KeyID == couleur.keyID
            }?.creationTimestamps ?: 0L
        }
    }

    val groupe_Couleur_Par_Produit = remember(list_M3couleur) {
        list_M3couleur.groupBy { it.parentBProduitInfosKeyID }
            .mapNotNull { (productKeyID, colors) ->
                repositorysMainGetter.repoM1Produit.datasValue.find {
                    it.keyID == productKeyID
                }?.let { product -> product to colors }
            }
            .sortedBy { (product, _) -> product.nom }
    }

    val groupe_Par_Categorie = remember(groupe_Couleur_Par_Produit) {
        groupe_Couleur_Par_Produit.groupBy { (product, _) -> product.idParentCategorie }
            .mapNotNull { (categoryId, productColorPairs) ->
                repositorysMainGetter.repoM16CategorieProduit.datasValue.find {
                    it.id == categoryId
                }?.let { category -> category to productColorPairs }
            }
            .sortedBy { (category, _) -> category.positionDouble }
    }

    Etager_LazyColumn_FragID3(
        modifier = modifier,
        categoriesWithProducts = groupe_Par_Categorie,
        fragmentNavigationHandler = FragmentNavigationHandler,
        catalogues = catalogues,
        categoryMap = categoryMap,
        onProductCategoryClick = { product ->
            android.util.Log.d("CategoryDialog", "onProductCategoryClick called for: ${product.nom}")
            android.util.Log.d("CategoryDialog", "Product keyID: ${product.keyID}")
            android.util.Log.d("CategoryDialog", "Product current category: ${product.idParentCategorie}")
            selectedProductForCategoryChange = product
            android.util.Log.d("CategoryDialog", "State updated, selectedProductForCategoryChange is now: ${selectedProductForCategoryChange?.nom}")
        },
        on_pour_send_data = on_pour_send_data
    )

    focusedValuesGetter.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie.ifTrue {
        Dialog_Fast_Affiche_Panie()
    }

    // Category Selection Dialog - handles category changes from child items
    selectedProductForCategoryChange?.let { product ->
        android.util.Log.d("CategoryDialog", "Entering dialog block for product: ${product.nom}")
        android.util.Log.d("CategoryDialog", "viewModelToUse available: ${viewModelToUse != null}")

        android.util.Log.d("CategoryDialog", "Displaying CategorySelectionDialog")
        CategorySelectionDialog(
            viewModel = viewModelToUse,
            product = product,
            onCategorySelected = { newCategoryId ->
                android.util.Log.d("CategoryDialog", "onCategorySelected called with: $newCategoryId")
                // Update the product's category
                val updatedProduct = newCategoryId?.let {
                    product.copy(idParentCategorie = it)
                }
                updatedProduct?.let {
                    repositorysMainGetter.repo1ProduitInfos.upsert(it)
                }
                selectedProductForCategoryChange = null
            },
            onDismiss = {
                android.util.Log.d("CategoryDialog", "Dialog dismissed")
                selectedProductForCategoryChange = null
            },
            onUpdateCategory = { categoryId, newName ->
                android.util.Log.d("CategoryDialog", "onUpdateCategory called: $categoryId -> $newName")
                // Update category name if needed
                val categoryToUpdate = categoryMap[categoryId]
                categoryToUpdate?.let {
                    val updated = it.copy(nom = newName)
                    viewModelToUse.addOrUpdateCategorie(updated)
                }
            },
            categoriesMap = categoryMap,
            availableCategories = allCategories.map { it.id }
        )
    } ?: run {
        android.util.Log.d("CategoryDialog", "selectedProductForCategoryChange is null - no dialog shown")
    }
}

@Composable
fun Etager_LazyColumn_FragID3(
    modifier: Modifier = Modifier,
    categoriesWithProducts: List<Pair<CategoriesTabelle, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    fragmentNavigationHandler: FragmentNavigationHandler = koinInject(),
    catalogues: List<CataloguesCaegorie>,
    categoryMap: Map<Long, CategoriesTabelle>,
    onProductCategoryClick: (ArticlesBasesStatsTable) -> Unit,
    on_pour_send_data: (String, String) -> Unit
) {
    val gridState = rememberLazyStaggeredGridState()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(4),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF0F5)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        categoriesWithProducts.forEach { (category, productColorPairs) ->
            item(
                key = "header_${category.id}",
                span = StaggeredGridItemSpan.FullLine
            ) {
                CategoryStickyHeader(category = category)
            }

            productColorPairs.forEach { (product, colors) ->
                val isExpanded = focusedValuesGetter.active_Central_Values
                    .expanded_M1Produit?.keyID == product.keyID

                item(
                    key = "product_${product.keyID}",
                    span = if (isExpanded) {
                        StaggeredGridItemSpan.FullLine
                    } else {
                        StaggeredGridItemSpan.SingleLane
                    }
                ) {
                    ProductItemWithCategory(
                        product = product,
                        colors = colors,
                        categoryMap = categoryMap,
                        catalogues = catalogues,
                        onProductCategoryClick = onProductCategoryClick,
                        on_pour_send_data = on_pour_send_data
                    )
                }
            }
        }

        // Navigation button at the end of the list
        item(
            key = "navigation_button",
            span = StaggeredGridItemSpan.FullLine
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        fragmentNavigationHandler.navigateTo(
                            Screen.Compact_Presentoire_App_Produits_FragID4,
                            FragmentNavigationHandler.DEFAULT_CONFIG
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "View All Products",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Extracted component to properly use remember in @Composable context
 * Displays product item with category badge
 */
@Composable
private fun ProductItemWithCategory(
    product: ArticlesBasesStatsTable,
    colors: List<M3CouleurProduitInfos>,
    categoryMap: Map<Long, CategoriesTabelle>,
    catalogues: List<CataloguesCaegorie>,
    onProductCategoryClick: (ArticlesBasesStatsTable) -> Unit,
    on_pour_send_data: (String, String) -> Unit
) {
    val currentCategory = remember(product.idParentCategorie, categoryMap) {
        product.idParentCategorie?.let { categoryMap[it] }
    }

    val currentCatalogue = remember(currentCategory, catalogues) {
        currentCategory?.catalogueParentId?.let { catalogueId ->
            catalogues.find { it.id.toLong() == catalogueId }
        }
    }

    android.util.Log.d("CategoryDialog", "ProductItemWithCategory rendering for: ${product.nom}")

    // FIXED: Remove duplicate CategoryBadge - only show it in Item_Produit_FragID3
    LazyStigerList_Produits_FragID3(
        product = product,
        colors = colors,
        on_pour_send_data = on_pour_send_data,
        onCategoryClick = {
            android.util.Log.d("CategoryDialog", "ProductItemWithCategory - onCategoryClick called for: ${product.nom}")
            onProductCategoryClick(product)
        }
    )
}

@Composable
fun CategoryStickyHeader(
    category: CategoriesTabelle,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = category.nom,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun LazyStigerList_Produits_FragID3(
    modifier: Modifier = Modifier,
    product: ArticlesBasesStatsTable,
    colors: List<M3CouleurProduitInfos>,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    on_pour_send_data: (String, String) -> Unit,
    onCategoryClick: (() -> Unit)? = null // Callback to notify parent about category click
) {
    val isExpanded = focusedValuesGetter.active_Central_Values
        .expanded_M1Produit?.keyID == product.keyID

    android.util.Log.d("CategoryDialog", "LazyStigerList_Produits_FragID3 - onCategoryClick null: ${onCategoryClick == null}")

    Item_Produit_FragID3(
        relative_M1produit = product,
        on_pour_send_data = on_pour_send_data,
        onCategoryClick = onCategoryClick, // FIXED: Pass the callback to child
        modifier = modifier
    )
}
