package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Dialogs

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.UiStateSec9Frag1
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CatalogHeaderCard
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CataloguesCaegorie
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

private const val TAG = "CategorySelectionDialog"

@Composable
fun CategorySelectionDialog_FragID4(
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel = koinViewModel(),     //<--
    //TODO(1): enleve ca et fait que tout ce fait au composable
    product: ArticlesBasesStatsTable,
    onCategorySelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    onUpdateCategory: ((Long, String) -> Unit)? = null,
    categoriesMap: Map<Long, CategoriesTabelle> = emptyMap(),
    availableCategories: List<Long> = emptyList(),
) {
    // FIXED: Load categories from ViewModel if not provided
    val uiState by viewModel.uiState.collectAsState()

    // FIXED: Get categories from ViewModel's repository
    val categoriesFromViewModel = remember(viewModel.repositorysMainGetter.repoM16CategorieProduit.datasValue) {
        viewModel.repositorysMainGetter.repoM16CategorieProduit.datasValue
    }

    // FIXED: Create categories map from ViewModel data if not provided
    val effectiveCategoriesMap = remember(categoriesMap, categoriesFromViewModel) {
        if (categoriesMap.isEmpty()) {
            categoriesFromViewModel.associateBy { it.id }
        } else {
            categoriesMap
        }
    }

    // FIXED: Get products to calculate which categories have products
    val productsFromViewModel = remember(viewModel.repositorysMainGetter.repoM1Produit.datasValue) {
        viewModel.repositorysMainGetter.repoM1Produit.datasValue
    }

    // FIXED: Calculate available categories (those that have products)
    val effectiveAvailableCategories = remember(availableCategories, productsFromViewModel) {
        if (availableCategories.isEmpty()) {
            productsFromViewModel
                .mapNotNull { it.idParentCategorie }
                .distinct()
        } else {
            availableCategories
        }
    }

    // Log for debugging
    Log.d(TAG, "CategorySelectionDialog opened")
    Log.d(TAG, "Categories map size: ${effectiveCategoriesMap.size}")
    Log.d(TAG, "Available categories: ${effectiveAvailableCategories.size}")
    Log.d(TAG, "Product: ${product.nom}, Current category: ${product.idParentCategorie}")

    val isFastMoveMode = uiState.clickItemMode == UiStateSec9Frag1.ClickItemMode.FastMove
    var showSearch by remember(isFastMoveMode) { mutableStateOf(isFastMoveMode) }
    var searchText by remember { mutableStateOf("") }
    var filterWithProducts by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    if (showSearch) {
        LaunchedEffect(Unit) {
            delay(100)
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }

    val catalogues = remember { B4CatalogueCategoriesRepository() }

    // FIXED: Use effectiveCategoriesMap instead of empty categoriesMap
    val allCategories = remember(effectiveCategoriesMap) {
        effectiveCategoriesMap.values.sortedBy { it.position }
    }

    Log.d(TAG, "All categories loaded: ${allCategories.size}")
    allCategories.take(5).forEach { cat ->
        Log.d(TAG, "Category: ${cat.nom} (ID: ${cat.id}, Position: ${cat.position})")
    }

    val sansCategorieCategory =
        remember { CategoriesTabelle(id = 0L, nom = "Sans Catégorie", position = 0) }

    val categoriesByCatalogue = remember(allCategories, catalogues) {
        val grouped = mutableMapOf<CataloguesCaegorie, List<CategoriesTabelle>>()

        catalogues.forEach { catalogue ->
            val categoriesInCatalogue =
                allCategories.filter { it.catalogueParentId == catalogue.id }
            if (categoriesInCatalogue.isNotEmpty()) {
                grouped[catalogue] = categoriesInCatalogue.sortedBy { it.position }
                Log.d(TAG, "Catalogue '${catalogue.nom}': ${categoriesInCatalogue.size} categories")
            }
        }

        val orphanedCategories = allCategories.filter {
            it.catalogueParentId == 0L || !catalogues.any { c -> c.id == it.catalogueParentId }
        }

        if (orphanedCategories.isNotEmpty()) {
            grouped[CataloguesCaegorie(id = 0, nom = "Autres", premierCategorieId = 0)] =
                orphanedCategories.sortedBy { it.position }
            Log.d(TAG, "Orphaned categories: ${orphanedCategories.size}")
        }

        Log.d(TAG, "Total catalogue groups: ${grouped.size}")
        grouped
    }

    // FIXED: Use effectiveAvailableCategories
    val filteredCategoriesByCatalogue by remember(
        categoriesByCatalogue,
        searchText,
        filterWithProducts,
        effectiveAvailableCategories
    ) {
        derivedStateOf {
            val result = categoriesByCatalogue.mapValues { (catalogue, categories) ->
                var filtered = categories

                if (filterWithProducts) {
                    filtered = filtered.filter { effectiveAvailableCategories.contains(it.id) }
                }

                if (searchText.isNotBlank()) {
                    filtered = filtered.filter { it.nom.contains(searchText, true) }
                }

                filtered
            }.filterValues { it.isNotEmpty() }

            Log.d(TAG, "Filtered categories: ${result.values.sumOf { it.size }} total")
            result
        }
    }

    val shouldShowCategories = remember(isFastMoveMode, searchText) {
        if (isFastMoveMode) searchText.isNotBlank() else true
    }

    fun processText(input: String): String = if (input.contains(".")) {
        input.replace(Regex("\\.([a-zA-Z]+)")) { "#${it.groupValues[1].uppercase()}" }
    } else {
        input.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    val createCategoryFromSearchText = {
        if (searchText.trim().isNotEmpty()) {
            viewModel.addOrUpdateCategorie(
                CategoriesTabelle(
                    nom = processText(searchText.trim()),
                    position = 0,
                    catalogueParentId = 4
                )
            )
            Log.d(TAG, "Creating new category: ${processText(searchText.trim())}")
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isFastMoveMode) "Déplacement Rapide" else "Changer Catégorie",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = {
                            showSearch = !showSearch
                            if (!showSearch) {
                                searchText = ""
                                keyboard?.hide()
                            }
                        }) {
                            Card(
                                modifier = Modifier.size(48.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (showSearch) MaterialTheme.colorScheme.secondary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Rechercher",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                        IconButton(onClick = {
                            filterWithProducts = !filterWithProducts
                            Log.d(TAG, "Filter with products: $filterWithProducts")
                        }) {
                            Card(
                                modifier = Modifier.size(48.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (filterWithProducts) MaterialTheme.colorScheme.tertiary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (filterWithProducts) Icons.Filled.FilterList
                                        else Icons.Outlined.FilterList,
                                        contentDescription = "Filtrer",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                        IconButton(onClick = createCategoryFromSearchText) {
                            Card(
                                modifier = Modifier.size(48.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Ajouter",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (showSearch) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .focusRequester(focusRequester),
                        label = { Text("Rechercher") },
                        placeholder = {
                            Text(if (isFastMoveMode) "Tapez pour voir les catégories..." else "Rechercher")
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = { searchText = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = null)
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboard?.hide()
                                if (searchText.trim().isNotEmpty()) {
                                    createCategoryFromSearchText()
                                }
                            }
                        ),
                        singleLine = true
                    )
                }

                Text(
                    text = "Produit: ${product.nom}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (isFastMoveMode && !shouldShowCategories) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Mode Déplacement Rapide",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            Text(
                                text = "Commencez à taper pour voir les catégories disponibles",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } else if (shouldShowCategories) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Sans Catégorie option
                        if (searchText.isBlank() || "Sans Catégorie".contains(searchText, true)) {
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(4) }) {
                                CatalogHeaderCard(
                                    catalogue = CataloguesCaegorie(
                                        id = 0,
                                        nom = "Sans Catégorie",
                                        premierCategorieId = 0
                                    ),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            item {
                                CategoryOptionGridCard(
                                    viewModel = viewModel,
                                    categorie = sansCategorieCategory,
                                    categoryId = null,
                                    categoryName = "Sans Catégorie",
                                    isSelected = product.idParentCategorie == null || product.idParentCategorie == 0L,
                                    onClick = {
                                        Log.d(TAG, "Selected 'Sans Catégorie' for product: ${product.nom}")
                                        onCategorySelected(null)
                                    },
                                    onEditName = null
                                )
                            }
                        }

                        // All other categories grouped by catalogue
                        filteredCategoriesByCatalogue.forEach { (catalogue, categories) ->
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(4) }) {
                                CatalogHeaderCard(
                                    catalogue = catalogue,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                            items(categories) { category ->
                                CategoryOptionGridCard(
                                    viewModel = viewModel,
                                    categorie = category,
                                    categoryId = category.id,
                                    categoryName = category.nom,
                                    isSelected = product.idParentCategorie == category.id,
                                    onClick = {
                                        Log.d(TAG, "Selected category '${category.nom}' (ID: ${category.id}) for product: ${product.nom}")
                                        onCategorySelected(category.id)
                                    },
                                    onEditName = if (onUpdateCategory != null) { name ->
                                        onUpdateCategory(category.id, name)
                                    } else null
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Fermer")
                    }
                }
            }
        }
    }
}
