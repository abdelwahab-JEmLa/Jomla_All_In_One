package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.FilterState
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.MainFilter
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.SortOrder
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Preview.Data.Test.createTestProduct
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.AppBar.AppBar
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main.AfficheElements
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main.OptionsFragmentButtons
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST.EditeCategoriesMainList
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.EditeInfosMainList
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.REORDER_GRID.ReorderMultiCategories
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Utils.LoadingScreen
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.DisponibilityEtates
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.CategoriesTabelle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

enum class ModeAffichage {
    CATEGORIES_LIST, PRODUCTS_LIST, REORDER_GRID
}

@Preview
@Composable
private fun EditeBaseDonneMainScreenIdS9Prev() {
    EditeBaseDonneMainScreenIdS9()
}

@Composable
fun EditeBaseDonneMainScreenIdS9(
    modifier: Modifier = Modifier,
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val aCentraldatashandlerprotojuin9 = viewModel.a_CentralDatasHandlerProtoJuin9
    val progress = aCentraldatashandlerprotojuin9.loadingProgress
    val produitList = uiState.a_ProduitInfosList
    val categoriesCompoRepository = viewModel.categoriesCompoRepository
    val categoriesList = categoriesCompoRepository.datasValue

    var currentMode by remember { mutableStateOf(ModeAffichage.PRODUCTS_LIST) }
    var filterState by remember { mutableStateOf(FilterState()) }
    var maskedElements by remember { mutableStateOf(setOf<AfficheElements>()) }
    var selectedProducts by remember { mutableStateOf(setOf<ArticlesBasesStatsTable>()) }
    var showBulkMoveDialog by remember { mutableStateOf(false) }

    val selectedCategories = remember(categoriesList) {
        categoriesList.filter { it.cSelectionePourDeplace }.map { it.id }.toSet()
    }

    // Fixed: Corrected the filteredAndSortedProduitList derivedStateOf
    val filteredAndSortedProduitList by remember(produitList, filterState, categoriesList,
        aCentraldatashandlerprotojuin9.categoryGroupedSortedProducts) {
        derivedStateOf {
            var filtered = produitList

            filterState.searchText.takeIf { it.isNotEmpty() }?.let { searchQuery ->
                val query = searchQuery.lowercase()
                filtered = filtered.filter { product ->
                    product.nom.lowercase().contains(query) ||
                            product.nomArab.lowercase().contains(query) ||
                            (product.autreNomDarticle?.lowercase()?.contains(query) == true)
                }
            }

            if (filterState.hideNonDispo) {
                filtered = filtered.filter { it.disponibilityEtates != DisponibilityEtates.NON_DISPO }
            }
            if (filterState.hideDispoOnly) {
                filtered = filtered.filter { it.disponibilityEtates != DisponibilityEtates.DISPO }
            }
            if (filterState.hidePetiteProbability) {
                filtered = filtered.filter { it.disponibilityEtates != DisponibilityEtates.PETITE_PROBABILITY }
            }
            if (filterState.hidePrixAchatZero) {
                filtered = filtered.filter { it.prixAchat > 0.0 }
            }
            if (filterState.hidePrixAchatPositif) {
                filtered = filtered.filter { it.prixAchat <= 0.0 }
            }

            when {
                filterState.enableCategoryGrouping -> {
                    when (filterState.sortOrder) {
                        SortOrder.CATEGORY_GROUPED -> {
                            // Fixed: Explicitly specify the type for the lambda parameter
                            aCentraldatashandlerprotojuin9.categoryGroupedSortedProducts.filter { product: ArticlesBasesStatsTable ->
                                filtered.contains(product)
                            }
                        }
                        else -> applySortOrder(filtered, filterState.sortOrder)
                    }
                }
                else -> applySortOrder(filtered, filterState.sortOrder, categoriesList)
            }
        }
    }

    progress?.let { prog ->
        if (prog < 1.0f) {
            LoadingScreen(prog)
        } else {
            Box {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    if (!maskedElements.contains(AfficheElements.APP_BAR)) {
                        AppBar(
                            onCreateProductAndCapture = { createTestProduct() },
                            onProductCreated = { viewModel.addOrUpdateProduit(it) },
                            currentMode = currentMode,
                            onModeChanged = { currentMode = it },
                            filterState = filterState,
                            onFilterChanged = { filterState = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    MainFilter(
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
                                produitList = filteredAndSortedProduitList,
                                onPrixUpdate = { updatedProduct ->
                                    viewModel.addOrUpdateProduit(updatedProduct)
                                },
                                modifier = Modifier.fillMaxSize()
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

private fun Set<ArticlesBasesStatsTable>.toggleProduct(product: ArticlesBasesStatsTable): Set<ArticlesBasesStatsTable> {
    return if (contains(product)) this - product else this + product
}

private fun applySortOrder(
    products: List<ArticlesBasesStatsTable>,
    sortOrder: SortOrder,
    categoriesList: List<CategoriesTabelle> = emptyList()
): List<ArticlesBasesStatsTable> {
    return when (sortOrder) {
        SortOrder.ID_DESC -> products.sortedByDescending { it.id }
        SortOrder.ID_ASC -> products.sortedBy { it.id }
        SortOrder.NAME_ASC -> products.sortedBy { it.nom.lowercase() }
        SortOrder.NAME_DESC -> products.sortedByDescending { it.nom.lowercase() }
        SortOrder.CATEGORY_GROUPED -> {
            val categoryMap = categoriesList.associateBy { it.id }
            val catalogues = B4CatalogueCategoriesRepository().associateBy { it.id.toLong() }

            products.sortedWith(
                compareBy<ArticlesBasesStatsTable> { product ->
                    val categoryId = product.idParentCategorie ?: 0L
                    val category = categoryMap[categoryId]
                    val catalogueId = category?.catalogueParentId ?: 4L

                    if (catalogueId == 4L || category?.nom == "NONE" || category == null) {
                        Int.MAX_VALUE - 1000
                    } else {
                        catalogues[catalogueId]?.position ?: (Int.MAX_VALUE - 2000)
                    }
                }.thenBy { product ->
                    val categoryId = product.idParentCategorie ?: 0L
                    val category = categoryMap[categoryId]
                    if (category?.nom == "NONE" || category == null) {
                        Int.MAX_VALUE - 1000
                    } else {
                        category.position
                    }
                }.thenBy { it.positionDonSonCesFrereCategorieProduits }
                    .thenBy { it.nom.lowercase() }
            )
        }
    }
}
