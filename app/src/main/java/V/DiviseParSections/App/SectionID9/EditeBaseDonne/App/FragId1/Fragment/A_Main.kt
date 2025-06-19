package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment

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
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Utils.LoadingScreen
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel.ModeAffichage
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable.EtateActuelleOnFusionAvecBaseDonne
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A_ProduitDataBase.Repository.DisponibilityEtates
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
    val produitList = aProduitdatabasecomposerepositorypj17.datasValue
    val categoriesCompoRepository = viewModel.categoriesCompoRepository
    val categoriesList = categoriesCompoRepository.datasValue

    val currentMode = uiState.currentMode
    var filterState by remember { mutableStateOf(FilterState()) }
    var maskedElements by remember { mutableStateOf(setOf<AfficheElements>()) }
    var selectedProducts by remember { mutableStateOf(setOf<ArticlesBasesStatsTable>()) }
    var showBulkMoveDialog by remember { mutableStateOf(false) }

    val selectedCategories = remember(categoriesList) {
        categoriesList.filter { it.cSelectionePourDeplace }.map { it.id }.toSet()
    }

    val filteredAndSortedProduitList by remember(
        produitList, filterState, categoriesList,
        groupeOrientationToRepositorysA_ProduitsToB_Categories.categoryGroupedSortedProducts
    ) {
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
            if (filterState.hidePrixAchatZero) {
                filtered = filtered.filter { it.prixAchat > 0.0 }
            }
            if (filterState.hidePrixAchatPositif) {
                filtered = filtered.filter { it.prixAchat <= 0.0 }
            }

            // NEW: Time-based filter for prixAchatDernierTimeTempUpdate
            if (filterState.enablePrixAchatTimeFilter) {
                val days = TimeFilterUtils.parseDaysString(filterState.prixAchatTimeFilterDays)
                if (days > 0) {
                    filtered = filtered.filter { product ->
                        // Assuming ArticlesBasesStatsTable has a field like prixAchatDernierTimeTempUpdate
                        // You may need to adjust this field name based on your actual data model
                        TimeFilterUtils.isOlderThanDays(product.prixAchatDernierTimeTempUpdate, days)
                    }
                }
            }

            if (filterState.hidePrixVenteZero) {
                filtered = filtered.filter { it.prixVent > 0.0 }
            }
            if (filterState.hidePrixVentePositif) {
                filtered = filtered.filter { it.prixVent <= 0.0 }
            }
            if (filterState.hideHeldPrioriteDemandAuGrossist) {
                filtered = filtered.filter { !it.heldPrioriteDemandAuGrossist }
            }
            if (filterState.hideNonHeldPrioriteDemandAuGrossist) {
                filtered = filtered.filter { it.heldPrioriteDemandAuGrossist }
            }

            when {
                filterState.enableCategoryGrouping -> {
                    when (filterState.sortOrder) {
                        SortOrder.CATEGORY_GROUPED -> {
                            groupeOrientationToRepositorysA_ProduitsToB_Categories.categoryGroupedSortedProducts.filter { product: ArticlesBasesStatsTable ->
                                filtered.contains(product)
                            }
                        }

                        else -> applySortOrder(filtered, filterState.sortOrder)
                    }
                }

                else -> applySortOrder(filtered, filterState.sortOrder)
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

private fun applySortOrder(
    products: List<ArticlesBasesStatsTable>,
    sortOrder: SortOrder,
    categoryGroupedSortedProducts: List<ArticlesBasesStatsTable> = emptyList()
): List<ArticlesBasesStatsTable> {
    return when (sortOrder) {
        SortOrder.ID_DESC -> products.sortedByDescending { it.id }
        SortOrder.ID_ASC -> products.sortedBy { it.id }
        SortOrder.NAME_ASC -> products.sortedBy { it.nom.lowercase() }
        SortOrder.NAME_DESC -> products.sortedByDescending { it.nom.lowercase() }
        SortOrder.CATEGORY_GROUPED -> {
            categoryGroupedSortedProducts.filter { categoryProduct ->
                products.any { it.id == categoryProduct.id }
            }
        }
    }
}
