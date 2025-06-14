package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.FilterState
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.MainFilter
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Preview.Data.Test.createTestProduct
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.AppBar.AppBar
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main.AfficheElements
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main.OptionsFragmentButtons
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Utils.LoadingScreen
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.CATEGORIES_LIST.EditeCategoriesMainList
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.PRODUCTS_LIST.EditeInfosMainList
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.REORDER_GRID.ReorderMultiCategories
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.DisponibilityEtates
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    CATEGORIES_LIST,
    PRODUCTS_LIST,
    REORDER_GRID
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
    val progress = uiState.mainLoadingProgressPJuin3
    val produitList = uiState.a_ProduitInfosList
    val categoriesList = uiState.c_CategorieProduitInfosList

    var produitListLocal by remember(produitList) { mutableStateOf(produitList) }
    var categoriesListLocal by remember(categoriesList) { mutableStateOf(categoriesList) }

    var currentMode by remember { mutableStateOf(ModeAffichage.REORDER_GRID) }
    var filterState by remember { mutableStateOf(FilterState()) }

    var maskedElements by remember { mutableStateOf(setOf<AfficheElements>()) }

    // State for bulk product selection
    var selectedProducts by remember { mutableStateOf(setOf<ArticlesBasesStatsTable>()) }
    var showBulkMoveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(produitList, categoriesList) {
        produitListLocal = produitList
        categoriesListLocal = categoriesList
    }

    // Updated filter logic for A_Main.kt
    // Replace the filteredProduitList derivedStateOf block with this:

    val filteredProduitList by remember(produitListLocal, filterState) {
        derivedStateOf {
            var filtered = produitListLocal

            // Search filter by product name
            if (filterState.searchText.isNotEmpty()) {
                val searchQuery = filterState.searchText.lowercase()
                filtered = filtered.filter { product ->
                    product.nom.lowercase().contains(searchQuery) ||
                            product.nom.lowercase().contains(searchQuery) ||
                            product.nomArab.lowercase().contains(searchQuery) ||
                            (product.autreNomDarticle?.lowercase()?.contains(searchQuery) == true)
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
            filtered
        }
    }

    if (progress < 1.0f) {
        LoadingScreen(progress)
    } else {
        Box {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // AppBar - can be masked/hidden
                if (!maskedElements.contains(AfficheElements.APP_BAR)) {
                    AppBar(
                        onCreateProductAndCapture = {
                            createTestProduct()
                        },
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
                    totalCount = produitListLocal.size,
                    filteredCount = filteredProduitList.size,
                    modifier = Modifier.fillMaxWidth()
                )

                when (currentMode) {
                    ModeAffichage.CATEGORIES_LIST -> {
                        EditeCategoriesMainList(
                            categoriesList = categoriesListLocal,
                            produitList = filteredProduitList,
                            onProductCategoryChanged = { updatedProduct ->
                                produitListLocal = produitListLocal.map { product ->
                                    if (product.id == updatedProduct.id) {
                                        updatedProduct
                                    } else {
                                        product
                                    }
                                }
                                viewModel.addOrUpdateProduit(updatedProduct)
                            },
                            onCategoriesEdite = { updatedCategories ->
                                categoriesListLocal = updatedCategories
                                viewModel.addOrUpdateCategories(updatedCategories)
                            },
                            modifier = Modifier.fillMaxSize(),
                            selectedProducts = selectedProducts,
                            onProductSelectionToggle = { product ->
                                selectedProducts = if (selectedProducts.contains(product)) {
                                    selectedProducts - product
                                } else {
                                    selectedProducts + product
                                }
                            },
                            showBulkMoveDialog = showBulkMoveDialog,
                            onShowBulkMoveDialog = { show ->
                                showBulkMoveDialog = show
                                if (!show) {
                                    selectedProducts = emptySet()
                                }
                            }
                        )
                    }

                    ModeAffichage.PRODUCTS_LIST -> {
                        EditeInfosMainList(
                            produitList = filteredProduitList,
                            onPrixUpdate = { updatedProduct ->
                                produitListLocal = produitListLocal.map { product ->
                                    if (product.id == updatedProduct.id) {
                                        updatedProduct
                                    } else {
                                        product
                                    }
                                }
                                viewModel.addOrUpdateProduit(updatedProduct)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    ModeAffichage.REORDER_GRID -> {
                        ReorderMultiCategories(
                            categoriesList = categoriesListLocal,
                            onCategoriesReordered = { updatedCategories ->
                                categoriesListLocal = updatedCategories
                                viewModel.addOrUpdateCategories(updatedCategories)
                            },
                            modifier = Modifier.fillMaxSize(),
                            produitList = produitList
                        )
                    }
                }
            }

            OptionsFragmentButtons(
                viewModel=viewModel,
                viewModelScope = viewModel.viewModelScope,
                onToggleMasque = { newMaskedElements ->
                    maskedElements = newMaskedElements
                },
                selectedProducts = selectedProducts,
                onShowBulkMoveDialog = {
                    showBulkMoveDialog = true
                }
            )
        }
    }
}
