package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel.ModeAffichage
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.A_MainFilter
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.Models.FilterState
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.Models.SortOrder
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.Models.TimeFilterUtils
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.AppBar.AppBar
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main.AfficheElements
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main.OptionsFragmentButtons
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST.EditeCategoriesMainList
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.EditeInfosMainList
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.REORDER_GRID.ReorderMultiCategories
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CataloguesCaegorie
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Utils.LoadingScreen
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable.EtateActuelleOnFusionAvecBaseDonne
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun EditeBaseDonneMainScreenIdS9Prev() {
    EditeBaseDonneMainScreenIdS9()
}

@Composable
fun EditeBaseDonneMainScreenIdS9(
    modifier: Modifier = Modifier,
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel = koinViewModel(),
    innerPadding: PaddingValues = PaddingValues(0.dp)
) {
    val uiState by viewModel.uiState.collectAsState()

    val a_CentralDatasHandlerProtoJuin9 = viewModel.a_CentralDatasHandlerProtoJuin9

    val groupeOrientationToRepositorysA_ProduitsToB_Categories =
        a_CentralDatasHandlerProtoJuin9
            .a_GroupeValuesA_ProduitsToB_Categories

    val progress = a_CentralDatasHandlerProtoJuin9.loadingProgress
    val aProduitdatabasecomposerepositorypj17 = viewModel.a_ProduitDataBaseComposeRepositoryPJ17

    val produitList = a_CentralDatasHandlerProtoJuin9.filteredA_ProduitsParCatalogueBsonId

    val categoriesCompoRepository = viewModel.categoriesCompoRepository
    val categoriesList = categoriesCompoRepository.datasValue

    val currentMode = uiState.currentMode
    var filterState by remember { mutableStateOf(FilterState()) }
    var maskedElements by remember { mutableStateOf(setOf<AfficheElements>()) }
    var selectedProducts by remember { mutableStateOf(setOf<ArticlesBasesStatsTable>()) }
    var showBulkMoveDialog by remember { mutableStateOf(false) }

    // FIXED: Proper collection operation for selectedCategories
    val selectedCategories = remember(categoriesList, uiState.selectionePourDeplacement_Categorie) {
        val selectedFromList = categoriesList.filter { it.cSelectionePourDeplace }.map { it.id }.toSet()
        val selectedFromState = uiState.selectionePourDeplacement_Categorie?.let { setOf(it.id) } ?: emptySet()
        selectedFromList + selectedFromState
    }

    val filteredAndSortedProduitList by remember(
        produitList, filterState, categoriesList,
        groupeOrientationToRepositorysA_ProduitsToB_Categories.categoryGroupedSortedProducts
    ) {
        derivedStateOf {
            var filtered = produitList

            // Search filter
            filterState.searchText.takeIf { it.isNotEmpty() }?.let { searchQuery ->
                val query = searchQuery.lowercase()
                filtered = filtered.filter { product ->
                    product.nom.lowercase().contains(query) ||
                            product.nomArab.lowercase().contains(query) ||
                            (product.autreNomDarticle?.lowercase()?.contains(query) == true)
                }
            }

            // State filters
            if (filterState.hideQuiNeSontPas_cUnNeveauArrivage) {
                filtered = filtered.filter {
                    it.etateActuelleOnFusionAvecBaseDonne ==
                            EtateActuelleOnFusionAvecBaseDonne.CaprtureSonImage
                }
            }
            if (filterState.hideNonDispo) {
                filtered =
                    filtered.filter { it.disponibilityEtates != DisponibilityEtates.NON_DISPO }
            }
            if (filterState.hideDispoOnly) {
                filtered = filtered.filter { it.disponibilityEtates != DisponibilityEtates.DISPO }
            }
            if (filterState.hidePetiteProbability) {
                filtered =
                    filtered.filter { it.disponibilityEtates != DisponibilityEtates.PETITE_PROBABILITY }
            }

            // Price filters
            if (filterState.hidePrixAchatZero) {
                filtered = filtered.filter { it.prixAchat > 0.0 }
            }
            if (filterState.hidePrixAchatPositif) {
                filtered = filtered.filter { it.prixAchat <= 0.0 }
            }

            // Time-based filter for prixAchatDernierTimeTempUpdate
            if (filterState.enablePrixAchatTimeFilter) {
                val days = TimeFilterUtils.parseDaysString(filterState.prixAchatTimeFilterDays)
                if (days > 0) {
                    filtered = filtered.filter { product ->
                        TimeFilterUtils.isOlderThanDays(
                            product.prixAchatDernierTimeTempUpdate,
                            days
                        )
                    }
                }
            }

            if (filterState.hidePrixVenteZero) {
                filtered = filtered.filter { it.prixVent > 0.0 }
            }
            if (filterState.hidePrixVentePositif) {
                filtered = filtered.filter { it.prixVent <= 0.0 }
            }

            // Priority filters
            if (filterState.hideHeldPrioriteDemandAuGrossist) {
                filtered = filtered.filter { !it.heldPrioriteDemandAuGrossist }
            }
            if (filterState.hideNonHeldPrioriteDemandAuGrossist) {
                filtered = filtered.filter { it.heldPrioriteDemandAuGrossist }
            }

            // Apply sorting with proper category-based product positioning
            when {
                filterState.enableCategoryGrouping -> {
                    when (filterState.sortOrder) {
                        SortOrder.CATEGORY_GROUPED -> {
                            // Use category grouped sorting with catalogue logic
                            applyCategoryGroupedSortingWithCatalogueLogic(
                                filtered,
                                categoriesList,
                                groupeOrientationToRepositorysA_ProduitsToB_Categories.categoryGroupedSortedProducts
                            )
                        }
                        else -> applySortOrderWithCategoryPosition(filtered, filterState.sortOrder)
                    }
                }
                else -> applySortOrderWithCategoryPosition(filtered, filterState.sortOrder)
            }
        }
    }

    progress?.let { prog ->
        if (prog < 1.0f) {
            LoadingScreen(prog)
        } else {
            Surface(
                modifier = modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    ) {
                        if (!maskedElements.contains(AfficheElements.APP_BAR)) {
                            AppBar(
                                currentMode = currentMode,
                                onModeChanged = { viewModel.new_currentMode(it) },
                                filterState = filterState,
                                onFilterChanged = { filterState = it },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        A_MainFilter(
                            filterState = filterState,
                            onFilterChanged = { filterState = it },
                            totalCount = produitList.size,
                            filteredCount = filteredAndSortedProduitList.size,
                            modifier = Modifier.fillMaxWidth()
                        )

                        when (currentMode) {
                            ModeAffichage.CATEGORIES_LIST -> {
                                EditeCategoriesMainList(
                                    modifier = Modifier.fillMaxSize(),
                                    viewModel = viewModel,
                                    produitList = filteredAndSortedProduitList,
                                    onProductCategoryChanged = { updatedProduct ->
                                        viewModel.addOrUpdateProduit(updatedProduct)
                                    },
                                    selectedProducts = selectedProducts,
                                    onProductSelectionToggle = { product ->
                                        selectedProducts = selectedProducts.toggleProduct(product)
                                    },
                                    showBulkMoveDialog = showBulkMoveDialog,
                                    onShowBulkMoveDialog = { show ->
                                        showBulkMoveDialog = show
                                        if (!show) selectedProducts = emptySet()
                                    }
                                )
                            }

                            ModeAffichage.PRODUCTS_LIST -> {
                                EditeInfosMainList(
                                    filterState = filterState,
                                    modifier = Modifier.fillMaxSize(),
                                    filteredAndSortedProduitList = filteredAndSortedProduitList,
                                    aProduitdatabasecomposerepositorypj17 = aProduitdatabasecomposerepositorypj17
                                )
                            }

                            ModeAffichage.REORDER_GRID -> {
                                ReorderMultiCategories(
                                    modifier = Modifier.fillMaxSize(),
                                    viewModel = viewModel,
                                    produitList = produitList
                                )
                            }
                        }
                    }

                    OptionsFragmentButtons(
                        viewModel = viewModel,
                        viewModelScope = viewModel.viewModelScope,
                        onToggleMasque = { maskedElements = it },
                        selectedProducts = selectedProducts,
                        onShowBulkMoveDialog = { showBulkMoveDialog = true },
                        selectedCategories = selectedCategories,
                        onCategoriesUpdated = { viewModel.addOrUpdateCategories(it) }
                    )
                }
            }
        }
    }
}

private fun Set<ArticlesBasesStatsTable>.toggleProduct(product: ArticlesBasesStatsTable): Set<ArticlesBasesStatsTable> {
    return if (contains(product)) this - product else this + product
}

/**
 * Applies sorting to products with consideration for category position.
 * This function handles different sort orders while maintaining category-aware positioning.
 */
private fun applySortOrderWithCategoryPosition(
    products: List<ArticlesBasesStatsTable>,
    sortOrder: SortOrder
): List<ArticlesBasesStatsTable> {
    return when (sortOrder) {
        SortOrder.ID_DESC -> products.sortedByDescending { it.id }
        SortOrder.ID_ASC -> products.sortedBy { it.id }
        SortOrder.NAME_ASC -> products.sortedBy { it.nom.lowercase() }
        SortOrder.NAME_DESC -> products.sortedByDescending { it.nom.lowercase() }
        SortOrder.PRIX_ACHAT_TIME_DESC -> products.sortedByDescending { it.prixAchatDernierTimeTempUpdate }
        SortOrder.PRIX_ACHAT_TIME_ASC -> products.sortedBy { it.prixAchatDernierTimeTempUpdate }
        SortOrder.CATEGORY_GROUPED -> {
            // This case should not happen as it's handled separately above
            applyCategoryGroupedSorting(products)
        }
    }
}

/**
 * Applies category-grouped sorting with proper position handling.
 * This function groups products by category and sorts them within each category by their position.
 *
 * @param products List of products to sort
 * @return List of products sorted by category and position within category
 */
private fun applyCategoryGroupedSorting(products: List<ArticlesBasesStatsTable>): List<ArticlesBasesStatsTable> {
    return products.groupBy { it.idParentCategorie }
        .toSortedMap(nullsFirst())
        .flatMap { (_, categoryProducts) ->
            categoryProducts.sortedWith(
                compareBy<ArticlesBasesStatsTable> { it.positionDonSonCesFrereCategorieProduits }
                    .thenByDescending { it.dernierTimeTampsSynchronisationAvecFireBase }
                    .thenBy { it.id }
            )
        }
}

/**
 * Applies category grouped sorting with position handling using pre-sorted category grouped products.
 * This function efficiently applies filtering to pre-sorted products and handles any remaining products.
 *
 * @param filteredProducts Products that have passed through filters
 * @param categoryGroupedProducts Pre-sorted products grouped by category
 * @return List of products maintaining category grouping and proper positioning
 */
private fun applyCategoryGroupedSortingWithPosition(
    filteredProducts: List<ArticlesBasesStatsTable>,
    categoryGroupedProducts: List<ArticlesBasesStatsTable>
): List<ArticlesBasesStatsTable> {
    // Create a map for faster lookup
    val filteredProductsSet = filteredProducts.toSet()

    // First, get products that are in the pre-sorted category grouped list and apply filtering
    val sortedFilteredFromGrouped = categoryGroupedProducts.filter { product ->
        filteredProductsSet.contains(product)
    }

    // Then, handle any products that might not be in the grouped list but are in filtered
    // Sort these by category and position as well
    val remainingProducts = filteredProducts.filterNot { product ->
        categoryGroupedProducts.contains(product)
    }

    // Apply category grouped sorting to remaining products
    val sortedRemainingProducts = if (remainingProducts.isNotEmpty()) {
        applyCategoryGroupedSorting(remainingProducts)
    } else {
        emptyList()
    }

    return sortedFilteredFromGrouped + sortedRemainingProducts
}

/**
 * Applies category grouped sorting using the catalogue-based logic from MainList.
 * This function groups products by their catalogue hierarchy first, then by category,
 * maintaining proper sorting within each category.
 *
 * @param filteredProducts Products that have passed through filters
 * @param currentCategories List of all available categories
 * @param categoryGroupedProducts Pre-sorted products grouped by category (fallback)
 * @return List of products sorted by catalogue -> category -> position
 */
private fun applyCategoryGroupedSortingWithCatalogueLogic(
    filteredProducts: List<ArticlesBasesStatsTable>,
    currentCategories: List<CategoriesTabelle>,
    categoryGroupedProducts: List<ArticlesBasesStatsTable>
): List<ArticlesBasesStatsTable> {
    val catalogues = B4CatalogueCategoriesRepository()

    // Group categories by catalogue using the same logic as MainList
    val categoriesByCatalogue = groupCategoriesByCatalogue(currentCategories, catalogues)

    // Create a map of products by category for quick lookup
    val productsByCategory = filteredProducts.groupBy { it.idParentCategorie ?: 0L }

    val sortedProducts = mutableListOf<ArticlesBasesStatsTable>()

    // Process each catalogue in order
    categoriesByCatalogue.forEach { (catalogue, categories) ->
        val shouldShowCatalogue = if (M18CentralParametresOfAllApps.get_Default().itsDevMode) {
            catalogue.id != 4L
        } else {
            true
        }

        if (shouldShowCatalogue) {
            // Process categories within this catalogue
            categories.forEach { category ->
                val productsInCategory = productsByCategory[category.id] ?: emptyList()

                // Sort products within category by position
                val sortedProductsInCategory = productsInCategory.sortedWith(
                    compareBy<ArticlesBasesStatsTable> { it.positionDonSonCesFrereCategorieProduits }
                        .thenByDescending { it.dernierTimeTampsSynchronisationAvecFireBase }
                        .thenBy { it.id }
                )

                sortedProducts.addAll(sortedProductsInCategory)
            }
        }
    }

    // Handle any remaining products that weren't processed (fallback)
    val processedProductIds = sortedProducts.map { it.id }.toSet()
    val remainingProducts = filteredProducts.filterNot { processedProductIds.contains(it.id) }

    if (remainingProducts.isNotEmpty()) {
        // Use fallback sorting for remaining products
        val fallbackSorted = applyCategoryGroupedSortingWithPosition(remainingProducts, categoryGroupedProducts)
        sortedProducts.addAll(fallbackSorted)
    }

    return sortedProducts
}

/**
 * Groups categories by their parent catalogue.
 * This function handles the logic for organizing categories under their respective catalogues,
 * including proper sorting and handling of orphan categories.
 *
 * Extracted from MainList.kt to maintain consistency across the application.
 */
private fun groupCategoriesByCatalogue(
    currentCategories: List<CategoriesTabelle>,
    catalogues: List<CataloguesCaegorie>
): LinkedHashMap<CataloguesCaegorie, List<CategoriesTabelle>> {
    val grouped = linkedMapOf<CataloguesCaegorie, List<CategoriesTabelle>>()

    // Process catalogues in order
    catalogues.forEach { catalogue ->
        val categoriesForCatalogue = currentCategories
            .filter { it.catalogueParentId == catalogue.id }
            .sortedWith(
                compareBy<CategoriesTabelle> { it.positionDouble }
                    .thenByDescending { it.dernierTimeTampsSynchronisationAvecFireBase }
            )

        if (categoriesForCatalogue.isNotEmpty()) {
            grouped[catalogue] = categoriesForCatalogue
        }
    }

    // Handle orphan categories (categories without a valid catalogue parent)
    val orphanCategories = currentCategories
        .filter { category ->
            category.catalogueParentId == 0L ||
                    !catalogues.any { catalogue -> catalogue.id == category.catalogueParentId }
        }
        .sortedWith(
            compareBy<CategoriesTabelle> { it.positionDouble }
                .thenByDescending { it.dernierTimeTampsSynchronisationAvecFireBase }
        )

    if (orphanCategories.isNotEmpty()) {
        val othersCatalogue = CataloguesCaegorie(
            id = 0,
            nom = "Autres",
            premierCategorieId = 0
        )
        grouped[othersCatalogue] = orphanCategories
    }

    return grouped
}
