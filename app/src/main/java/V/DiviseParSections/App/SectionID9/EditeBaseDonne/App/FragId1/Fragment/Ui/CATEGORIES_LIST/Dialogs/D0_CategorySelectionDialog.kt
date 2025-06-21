package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST.Dialogs

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.Repository.CategoriesTabelle
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.UiStateSec9Frag1
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CatalogHeaderCard
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CataloguesCaegorie
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

@Composable
fun CategorySelectionDialog(
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    product: ArticlesBasesStatsTable,
    onCategorySelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    onUpdateCategory: ((Long, String) -> Unit)? = null,
    categoriesMap: Map<Long, CategoriesTabelle> = emptyMap(),
    availableCategories: List<Long> = emptyList(),
) {
    // Get current UI state to check click mode
    val uiState by viewModel.uiState.collectAsState()
    val isFastMoveMode = uiState.clickItemMode == UiStateSec9Frag1.ClickItemMode.FastMove

    var showSearch by remember(isFastMoveMode) { mutableStateOf(isFastMoveMode) }
    var searchText by remember { mutableStateOf("") }
    var filterWithProducts by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    // FIXED: Auto-focus and show keyboard when in FastMove mode or when search is enabled
    if (showSearch) {
        LaunchedEffect(Unit) {
            delay(100)
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }

    val catalogues = remember { B4CatalogueCategoriesRepository() }
    val allCategories = remember(categoriesMap) { categoriesMap.values.sortedBy { it.position } }

    // Create a dummy category for "Sans Catégorie" option
    val sansCategorieCategory = remember {
        CategoriesTabelle(
            id = 0L,
            nom = "Sans Catégorie",
            position = 0
        )
    }

    // Group categories by catalogue (improved organization like EditeCategoriesMainList)
    val categoriesByCatalogue = remember(allCategories, catalogues) {
        val grouped = mutableMapOf<CataloguesCaegorie, List<CategoriesTabelle>>()

        // Group categories by their catalogue parent
        catalogues.forEach { catalogue ->
            val categoriesInCatalogue = allCategories.filter {
                it.catalogueParentId == catalogue.id
            }
            if (categoriesInCatalogue.isNotEmpty()) {
                grouped[catalogue] = categoriesInCatalogue.sortedBy { it.position }
            }
        }

        // Handle categories without a valid catalogue (orphaned categories)
        val orphanedCategories = allCategories.filter {
            it.catalogueParentId == 0L || !catalogues.any { c -> c.id == it.catalogueParentId }
        }
        if (orphanedCategories.isNotEmpty()) {
            grouped[CataloguesCaegorie(id = 0, nom = "Autres", premierCategorieId = 0)] = orphanedCategories.sortedBy { it.position }
        }

        grouped
    }

    val filteredCategoriesByCatalogue by remember(
        categoriesByCatalogue,
        searchText,
        filterWithProducts,
        availableCategories
    ) {
        derivedStateOf {
            categoriesByCatalogue.mapValues { (_, categories) ->
                var filtered = categories
                if (filterWithProducts) {
                    filtered = filtered.filter { availableCategories.contains(it.id) }
                }
                if (searchText.isNotBlank()) {
                    filtered = filtered.filter { it.nom.contains(searchText, true) }
                }
                filtered
            }.filterValues { it.isNotEmpty() }
        }
    }

    // FIXED: Determine if categories should be shown
    val shouldShowCategories = remember(isFastMoveMode, searchText) {
        if (isFastMoveMode) {
            // In FastMove mode, only show categories when there's search text
            searchText.isNotBlank()
        } else {
            // In Standard mode, always show categories
            true
        }
    }

    // Function to process text for category creation
    fun processText(input: String): String {
        return if (input.contains(".")) {
            // If input contains dots, replace .word with #WORD
            input.replace(Regex("\\.([a-zA-Z]+)")) { matchResult ->
                "#${matchResult.groupValues[1].uppercase()}"
            }
        } else {
            // If no dots, capitalize only the first letter of the entire string
            input.trim().replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase() else char.toString()
            }
        }
    }

    // Function to create category directly from search text
    val createCategoryFromSearchText = {
        if (searchText.trim().isNotEmpty()) {
            val processedName = processText(searchText.trim())
            val newCategory = CategoriesTabelle(
                nom = processedName,
                position = 0,
                catalogueParentId = 4,
            )
            viewModel.addOrUpdateCategorie(newCategory)
            // Clear search text after creating category
            searchText = ""
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
                // Header with title and action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isFastMoveMode) "DR" else "Changer Catégorie",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Search toggle button - auto-active in FastMove mode
                        IconButton(
                            onClick = {
                                showSearch = !showSearch
                                if (!showSearch) {
                                    searchText = ""
                                    keyboard?.hide()
                                }
                            }
                        ) {
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

                        // Filter toggle button
                        IconButton(onClick = { filterWithProducts = !filterWithProducts }) {
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
                                        imageVector = if (filterWithProducts) Icons.Filled.FilterList else Icons.Outlined.FilterList,
                                        contentDescription = "Filtrer",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        // Add category button - FIXED: Use search text directly
                        IconButton(onClick = {
                            // Instead of showing dialog, process search text and add category
                            createCategoryFromSearchText()
                        }) {
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

                // Search field - always shown in FastMove mode
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
                                // Option: Create category when pressing search
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

                // FIXED: Show help text in FastMove mode when no search text
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
                    // Main grid content - organized like EditeCategoriesMainList
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4), // Increased to 4 columns for better grid layout
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Add "Sans Catégorie" option at the top
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
                                    isSelected = product.idParentCategorie == null,
                                    onClick = { onCategorySelected(null) },
                                    onEditName = null
                                )
                            }
                        }

                        // Add catalogue sections with headers (improved organization)
                        filteredCategoriesByCatalogue.forEach { (catalogue, categories) ->
                            // Catalogue header spanning full width
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(4) }) {
                                CatalogHeaderCard(
                                    catalogue = catalogue,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            // Categories in this catalogue
                            items(categories) { category ->
                                CategoryOptionGridCard(
                                    viewModel = viewModel,
                                    categorie = category,
                                    categoryId = category.id,
                                    categoryName = category.nom,
                                    isSelected = product.idParentCategorie == category.id,
                                    onClick = { onCategorySelected(category.id) },
                                    onEditName = if (onUpdateCategory != null) { name ->
                                        onUpdateCategory(category.id, name)
                                    } else null
                                )
                            }
                        }
                    }
                }

                // Footer with close button
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
