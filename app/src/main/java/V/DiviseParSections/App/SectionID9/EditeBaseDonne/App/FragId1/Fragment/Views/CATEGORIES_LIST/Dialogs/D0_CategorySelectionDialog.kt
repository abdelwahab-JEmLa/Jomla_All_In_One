package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.CATEGORIES_LIST.Dialogs

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.Shared.Module.Catalogue.CatalogHeaderCard
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.Shared.Module.Catalogue.CataloguesCaegorie
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.Shared.Module.Catalogue.startupeDatas
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.CategoriesTabelle
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
    product: ArticlesBasesStatsTable,
    onCategorySelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    onAddCategory: ((String) -> Unit)? = null,
    onUpdateCategory: ((Long, String) -> Unit)? = null,
    categoriesMap: Map<Long, CategoriesTabelle> = emptyMap(),
    availableCategories: List<Long> = emptyList(),
    allProducts: List<ArticlesBasesStatsTable> = emptyList()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
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

    val catalogues = remember { startupeDatas() }
    val allCategories = remember(categoriesMap) { categoriesMap.values.sortedBy { it.position } }

    // Group categories by catalogue
    val categoriesByCatalogue = remember(allCategories, catalogues) {
        val grouped = mutableMapOf<CataloguesCaegorie, List<CategoriesTabelle>>()

        catalogues.forEach { catalogue ->
            val catalogueCategories = allCategories.filter { category ->
                category.id >= catalogue.premierCategorieId &&
                        (catalogues.find { it.premierCategorieId > catalogue.premierCategorieId }?.let { nextCatalogue ->
                            category.id < nextCatalogue.premierCategorieId
                        } ?: true)
            }
            if (catalogueCategories.isNotEmpty()) {
                grouped[catalogue] = catalogueCategories
            }
        }

        // Add categories that don't belong to any catalogue
        val uncategorizedCategories = allCategories.filter { category ->
            !catalogues.any { catalogue ->
                category.id >= catalogue.premierCategorieId &&
                        (catalogues.find { it.premierCategorieId > catalogue.premierCategorieId }?.let { nextCatalogue ->
                            category.id < nextCatalogue.premierCategorieId
                        } ?: true)
            }
        }

        if (uncategorizedCategories.isNotEmpty()) {
            grouped[CataloguesCaegorie(0, "Autres", 0)] = uncategorizedCategories
        }

        grouped
    }

    val filteredCategoriesByCatalogue by remember(categoriesByCatalogue, searchText, filterWithProducts, availableCategories) {
        derivedStateOf {
            categoriesByCatalogue.mapValues { (_, categories) ->
                var filtered = categories
                if (filterWithProducts) filtered = filtered.filter { availableCategories.contains(it.id) }
                if (searchText.isNotBlank()) filtered = filtered.filter { it.nom.contains(searchText, true) }
                filtered
            }.filterValues { it.isNotEmpty() }
        }
    }

    // Group products by category for image display
    val productsByCategory = remember(allProducts) {
        allProducts.groupBy { it.idParentCategorie ?: 0L }
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
                        text = "Changer Catégorie",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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
                        if (onAddCategory != null) {
                            IconButton(onClick = { showAddDialog = true }) {
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
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = { searchText = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = null)
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
                        singleLine = true
                    )
                }

                Text(
                    text = "Produit: ${product.nom}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
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
                                catalogue = CataloguesCaegorie(0, "Sans Catégorie", 0),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        item {
                            CategoryOptionGridCard(
                                categoryId = null,
                                categoryName = "Sans Catégorie",
                                isSelected = product.idParentCategorie == null,
                                onClick = { onCategorySelected(null) },
                                onEditName = null,
                                categoryProducts = emptyList()
                            )
                        }
                    }

                    // Add catalogue sections with sticky headers
                    filteredCategoriesByCatalogue.forEach { (catalogue, categories) ->
                        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(4) }) {
                            CatalogHeaderCard(
                                catalogue = catalogue,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(categories) { cat ->
                            CategoryOptionGridCard(
                                categoryId = cat.id,
                                categoryName = cat.nom,
                                isSelected = product.idParentCategorie == cat.id,
                                onClick = { onCategorySelected(cat.id) },
                                onEditName = if (onUpdateCategory != null) { name -> onUpdateCategory(cat.id, name) } else null,
                                categoryProducts = productsByCategory[cat.id] ?: emptyList()
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Fermer") }
                }
            }
        }
    }

    if (showAddDialog && onAddCategory != null) {
        AddCategoryDialog(
            onCategoryAdded = { name ->
                onAddCategory(name)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}
