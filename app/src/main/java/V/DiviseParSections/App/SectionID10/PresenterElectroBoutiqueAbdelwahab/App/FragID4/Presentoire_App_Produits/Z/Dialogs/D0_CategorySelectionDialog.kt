package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Z.Dialogs

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Filter.FilterState_Facad_Boutique
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CatalogHeaderCard
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.CataloguesCaegorie
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

private const val TAG = "CategorySelectionDialog"

/**
 * COMPLETE REFACTORED VERSION - No ViewModel dependencies
 * All data is passed as parameters, all actions through callbacks
 * FIXED: Added dropdown menu for produit_a_Une_Couleur_Ac_Image filter (TODO #1 completed)
 */
@Composable
fun CategorySelectionDialog_FragID4(
    product: ArticlesBasesStatsTable,
    allCategories: List<CategoriesTabelle>,
    allProducts: List<ArticlesBasesStatsTable>, // For calculating products per category
    onCategorySelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    onCreateNewCategory: (String) -> Unit = {},
    onUpdateCategoryName: (Long, String) -> Unit = { _, _ -> },
    isFastMoveMode: Boolean = false,
    // NEW: Parameter for image filter control
    currentImageFilter: FilterState_Facad_Boutique.WhatDo = FilterState_Facad_Boutique.WhatDo.Ignore,
    onImageFilterChanged: (FilterState_Facad_Boutique.WhatDo) -> Unit = {}
) {
    var showSearch by remember(isFastMoveMode) { mutableStateOf(isFastMoveMode) }
    var searchText by remember { mutableStateOf("") }
    var filterWithProducts by remember { mutableStateOf(false) }
    var showImageFilterMenu by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    // EXTENSIVE LOGGING FOR DEBUGGING
    Log.d(TAG, "========== CategorySelectionDialog COMPOSITION START ==========")
    Log.d(TAG, "CategorySelectionDialog opened")
    Log.d(TAG, "All categories: ${allCategories.size}")
    Log.d(TAG, "All products: ${allProducts.size}")
    Log.d(TAG, "Product: ${product.nom}, Current category: ${product.idParentCategorie}")
    Log.d(TAG, "currentImageFilter parameter: $currentImageFilter")
    Log.d(TAG, "showImageFilterMenu state: $showImageFilterMenu")
    Log.d(TAG, "isFastMoveMode: $isFastMoveMode")
    Log.d(TAG, "showSearch: $showSearch")
    Log.d(TAG, "filterWithProducts: $filterWithProducts")

    if (showSearch) {
        LaunchedEffect(Unit) {
            delay(100)
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }

    val catalogues = remember { B4CatalogueCategoriesRepository() }

    val sortedCategories = remember(allCategories) {
        allCategories.sortedBy { it.position }
    }

    // Calculate products per category for filtering and display
    val productsByCategory = remember(allProducts) {
        allProducts
            .mapNotNull { product -> product.idParentCategorie?.let { it to product } }
            .groupBy({ it.first }, { it.second })
    }

    val availableCategories = remember(productsByCategory) {
        productsByCategory.keys.toList()
    }

    Log.d(TAG, "Available categories (with products): ${availableCategories.size}")

    val sansCategorieCategory = remember {
        CategoriesTabelle(id = 0L, nom = "Sans Catégorie", position = 0)
    }

    // Products without category
    val productsWithoutCategory = remember(allProducts) {
        allProducts.filter { it.idParentCategorie == null }
    }

    val categoriesByCatalogue = remember(sortedCategories, catalogues) {
        val grouped = mutableMapOf<CataloguesCaegorie, List<CategoriesTabelle>>()

        catalogues.forEach { catalogue ->
            val categoriesInCatalogue = sortedCategories.filter {
                it.catalogueParentId == catalogue.id
            }
            if (categoriesInCatalogue.isNotEmpty()) {
                grouped[catalogue] = categoriesInCatalogue.sortedBy { it.position }
                Log.d(TAG, "Catalogue '${catalogue.nom}': ${categoriesInCatalogue.size} categories")
            }
        }

        val orphanedCategories = sortedCategories.filter {
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

    val filteredCategoriesByCatalogue by remember(
        categoriesByCatalogue,
        searchText,
        filterWithProducts,
        availableCategories
    ) {
        derivedStateOf {
            val result = categoriesByCatalogue.mapValues { (_, categories) ->
                var filtered = categories

                if (filterWithProducts) {
                    filtered = filtered.filter { availableCategories.contains(it.id) }
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
        !isFastMoveMode || searchText.isNotBlank()
    }

    fun createCategoryFromSearchText() {
        val trimmedText = searchText.trim()
        if (trimmedText.isNotEmpty()) {
            Log.d(TAG, "Creating new category: $trimmedText")
            onCreateNewCategory(trimmedText)
        }
    }

    // Helper function to get display text for image filter
    fun getImageFilterDisplayText(filter: FilterState_Facad_Boutique.WhatDo): String {
        return when (filter) {
            FilterState_Facad_Boutique.WhatDo.N_Affiche_Que_Lui -> "Avec images"
            FilterState_Facad_Boutique.WhatDo.Ne_Affiche_Aucune -> "Sans images"
            FilterState_Facad_Boutique.WhatDo.Ignore -> "Tous"
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
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Log.d(TAG, "========== RENDERING HEADER ROW ==========")

                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Log.d(TAG, "Rendering header title")
                    Text(
                        text = "Sélectionner Catégorie",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    // Close button at top right
                    TextButton(
                        onClick = {
                            Log.d(TAG, "Close button clicked")
                            onDismiss()
                        }
                    ) {
                        Text("Fermer")
                    }
                }

                // Action buttons row
                Log.d(TAG, "========== RENDERING BUTTON ROW ==========")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Log.d(TAG, "About to render Image Filter Button Card")
                    Log.d(TAG, "currentImageFilter value: $currentImageFilter")
                    Log.d(TAG, "Filter is active: ${currentImageFilter != FilterState_Facad_Boutique.WhatDo.Ignore}")

                    // NEW: Image Filter Dropdown Button - HIGHLY VISIBLE
                    Box {
                        Log.d(TAG, "Rendering Card for Image Filter Button")
                        Card(
                            modifier = Modifier.padding(end = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (currentImageFilter != FilterState_Facad_Boutique.WhatDo.Ignore) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Log.d(TAG, "Inside Card content - rendering Row with Icon and Text")
                            Log.d(TAG, "Display text: ${getImageFilterDisplayText(currentImageFilter)}")

                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Log.d(TAG, "Rendering Image icon")
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Filtre d'images",
                                    modifier = Modifier.size(24.dp),
                                    tint = if (currentImageFilter != FilterState_Facad_Boutique.WhatDo.Ignore) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                                Log.d(TAG, "Rendering filter text")
                                Text(
                                    text = getImageFilterDisplayText(currentImageFilter),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentImageFilter != FilterState_Facad_Boutique.WhatDo.Ignore) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                                Log.d(TAG, "Rendering dropdown arrow icon")
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = if (currentImageFilter != FilterState_Facad_Boutique.WhatDo.Ignore) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }

                        Log.d(TAG, "Rendering clickable overlay Box")
                        // Invisible clickable overlay to make entire card clickable
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable {
                                    Log.d(TAG, "IMAGE FILTER BUTTON CLICKED!")
                                    Log.d(TAG, "Previous showImageFilterMenu: $showImageFilterMenu")
                                    showImageFilterMenu = !showImageFilterMenu
                                    Log.d(TAG, "New showImageFilterMenu: $showImageFilterMenu")
                                }
                        )

                        Log.d(TAG, "About to render DropdownMenu")
                        Log.d(TAG, "showImageFilterMenu state: $showImageFilterMenu")

                        DropdownMenu(
                            expanded = showImageFilterMenu,
                            onDismissRequest = {
                                Log.d(TAG, "DropdownMenu dismissed")
                                showImageFilterMenu = false
                            }
                        ) {
                            Log.d(TAG, "Inside DropdownMenu - rendering menu items")
                            FilterState_Facad_Boutique.WhatDo.values().forEach { filterOption ->
                                Log.d(TAG, "Rendering menu item for: $filterOption")
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = getImageFilterDisplayText(filterOption),
                                                fontWeight = if (filterOption == currentImageFilter) {
                                                    FontWeight.Bold
                                                } else {
                                                    FontWeight.Normal
                                                }
                                            )
                                            if (filterOption == currentImageFilter) {
                                                Icon(
                                                    imageVector = Icons.Default.FilterList,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        Log.d(TAG, "Menu item clicked: $filterOption")
                                        Log.d(TAG, "Calling onImageFilterChanged callback")
                                        onImageFilterChanged(filterOption)
                                        showImageFilterMenu = false
                                        Log.d(TAG, "Image filter changed to: $filterOption")
                                    }
                                )
                            }
                        }
                    }

                    Log.d(TAG, "Rendering 'Filter by products' button")
                    // Filter by products button
                    IconButton(onClick = {
                        Log.d(TAG, "Filter by products button clicked")
                        filterWithProducts = !filterWithProducts
                        Log.d(TAG, "filterWithProducts now: $filterWithProducts")
                    }) {
                        Icon(
                            imageVector = if (filterWithProducts) Icons.Filled.FilterList
                            else Icons.Outlined.FilterList,
                            contentDescription = if (filterWithProducts) "Afficher tout"
                            else "Filtrer catégories avec produits",
                            tint = if (filterWithProducts) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Log.d(TAG, "Checking if search toggle button should render: isFastMoveMode=$isFastMoveMode")
                    // Search toggle button
                    if (!isFastMoveMode) {
                        Log.d(TAG, "Rendering search toggle button")
                        IconButton(onClick = {
                            Log.d(TAG, "Search toggle clicked")
                            showSearch = !showSearch
                            Log.d(TAG, "showSearch now: $showSearch")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = if (showSearch) "Masquer recherche"
                                else "Afficher recherche"
                            )
                        }
                    }

                    Log.d(TAG, "Checking if create category button should render")
                    Log.d(TAG, "searchText.isNotBlank(): ${searchText.isNotBlank()}")
                    // Create category button
                    if (searchText.isNotBlank() &&
                        !filteredCategoriesByCatalogue.values
                            .flatten()
                            .any { it.nom.equals(searchText.trim(), ignoreCase = true) }
                    ) {
                        Log.d(TAG, "Rendering create category button")
                        IconButton(onClick = {
                            Log.d(TAG, "Create category button clicked")
                            createCategoryFromSearchText()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Créer nouvelle catégorie",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Log.d(TAG, "========== FINISHED RENDERING HEADER ==========")

            // Visual separator
            androidx.compose.material3.HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Search field
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
                        Text(
                            if (isFastMoveMode) "Tapez pour voir les catégories..."
                            else "Rechercher"
                        )
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

            // Product name
            Text(
                text = "Produit: ${product.nom}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Content area
            if (isFastMoveMode && !shouldShowCategories) {
                // Fast move mode placeholder
                Box(
                    modifier = Modifier
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
                // Categories grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Sans Catégorie option
                    if (searchText.isBlank() || "Sans Catégorie".contains(searchText, true)) {
                        item(span = { GridItemSpan(4) }) {
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
                                categorie = sansCategorieCategory,
                                categoryId = null,
                                categoryName = "Sans Catégorie",
                                isSelected = product.idParentCategorie == null || product.idParentCategorie == 0L,
                                onClick = {
                                    Log.d(TAG, "Selected 'Sans Catégorie' for product: ${product.nom}")
                                    onCategorySelected(null)
                                },
                                onEditName = null,
                                productsInCategory = productsWithoutCategory
                            )
                        }
                    }

                    // All other categories grouped by catalogue
                    filteredCategoriesByCatalogue.forEach { (catalogue, categories) ->
                        item(span = { GridItemSpan(4) }) {
                            CatalogHeaderCard(
                                catalogue = catalogue,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(categories) { category ->
                            val productsInThisCategory = productsByCategory[category.id] ?: emptyList()

                            CategoryOptionGridCard(
                                categorie = category,
                                categoryId = category.id,
                                categoryName = category.nom,
                                isSelected = product.idParentCategorie == category.id,
                                onClick = {
                                    Log.d(
                                        TAG,
                                        "Selected category '${category.nom}' (ID: ${category.id}) for product: ${product.nom}"
                                    )
                                    onCategorySelected(category.id)
                                },
                                onEditName = { newName ->
                                    onUpdateCategoryName(category.id, newName)
                                },
                                productsInCategory = productsInThisCategory
                            )
                        }
                    }
                }
            }
        }
    }
}
