package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test

import V.DiviseParSections.App.Shared.Repository.A.Base.ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Preview @Composable private fun ID1ScreenPrev() { MainFastSearchProduitPourVent() }

@Composable
fun MainFastSearchProduitPourVent(
    viewModel: ViewModelMainFastSearchProduitPourVent = koinViewModel(),
    modifier: Modifier = Modifier,
    tag: String = ""
) {
    val uiState by viewModel.uiState.collectAsState()
    val repo = viewModel.getter.bProduitInfosRepository.datasValue
    val categoriesRepo = viewModel.getter.b3CategoriesCompoRepository.datasValue

    val tagParent_ID7VentPeriod = "--${Z_AppCompt.keyModel}-${ParametresAppComptNonSaved().gerantComptKeyByParent}--${Z_AppCompt.keyModelValID7VentParent}-${ParametresAppComptNonSaved().activePeriodKeyByParent}"
    val keyCompose = SemanticsPropertyKey<String>(tagParent_ID7VentPeriod)

    // Focus requester for search field
    val focusRequester = remember { FocusRequester() }

    // Request focus when composable first loads
    LaunchedEffect(Unit) {
        delay(100) // Small delay to ensure UI is ready
        focusRequester.requestFocus()
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                set(keyCompose, tagParent_ID7VentPeriod)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with Add button
            HeaderSection(
                onAddClick = { viewModel.onAddNewProduct() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search field that focuses on start
            SearchField(
                searchText = uiState.searchText,
                onSearchTextChange = { viewModel.onSearchTextChange(it) },
                focusRequester = focusRequester,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Product list
            MainList(
                products = repo,
                categories = categoriesRepo,
                searchFilter = uiState.searchText,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun HeaderSection(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Recherche Rapide Produits",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Ajouter produit"
            )
        }
    }
}

@Composable
private fun SearchField(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        modifier = modifier.focusRequester(focusRequester),
        placeholder = { Text("Rechercher un produit...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Rechercher"
            )
        },
        singleLine = true
    )
}

@Composable
fun MainList(
    products: List<ArticlesBasesStatsTable>,
    categories: List<CategoriesTabelle>,
    searchFilter: String,
    modifier: Modifier = Modifier
) {
    // Create category map for lookup
    val categoryMap = remember(categories) {
        categories.associateBy { it.id }
    }

    // Create catalogue map for lookup
    val catalogues = remember {
        B4CatalogueCategoriesRepository().associateBy { it.id }
    }

    // Filter and sort products based on search and category grouping
    val categoryGroupedSortedProducts = remember(products, categories, searchFilter) {
        // First filter by search if not empty
        val filteredProducts = if (searchFilter.isBlank()) {
            products
        } else {
            products.filter { product ->
                product.nom.contains(searchFilter, ignoreCase = true) ||
                        product.nomMutable.contains(searchFilter, ignoreCase = true) ||
                        product.nomArab.contains(searchFilter, ignoreCase = true)
            }
        }

        // Separate regular products from orphan products
        val (regularProducts, orphanProducts) = filteredProducts.partition { product ->
            val categoryId = product.idParentCategorie ?: 0L
            val category = categoryMap[categoryId]
            val catalogueId = category?.catalogueParentId ?: 4L

            category != null &&
                    catalogueId != 4L &&
                    !category.nom.equals("NONE", ignoreCase = true)
        }

        // Sort regular products by catalogue, then category, then position, then name
        val sortedRegular = regularProducts.sortedWith(
            compareBy<ArticlesBasesStatsTable> { product ->
                val categoryId = product.idParentCategorie ?: 0L
                val category = categoryMap[categoryId]
                val catalogueId = category?.catalogueParentId ?: 4L
                catalogues[catalogueId]?.position ?: Int.MAX_VALUE
            }.thenBy { product ->
                val categoryId = product.idParentCategorie ?: 0L
                categoryMap[categoryId]?.position ?: Int.MAX_VALUE
            }.thenBy { it.positionDonSonCesFrereCategorieProduits }
                .thenBy { it.nom.lowercase() }
        )

        // Sort orphan products
        val sortedOrphan = orphanProducts.sortedWith(
            compareBy<ArticlesBasesStatsTable> { product ->
                val categoryId = product.idParentCategorie ?: 0L
                val category = categoryMap[categoryId]
                category?.nom?.takeIf { !it.equals("NONE", ignoreCase = true) }
                    ?: "ZZZZZ_NO_CATEGORY"
            }.thenBy { it.positionDonSonCesFrereCategorieProduits }
                .thenBy { it.nom.lowercase() }
        )

        sortedRegular + sortedOrphan
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (searchFilter.isNotEmpty() && categoryGroupedSortedProducts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucun produit trouvé pour \"$searchFilter\"",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        } else {
            items(categoryGroupedSortedProducts) { product ->
                ViewProduit(
                    product = product,
                    category = categoryMap[product.idParentCategorie],
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ViewProduit(
    product: ArticlesBasesStatsTable,
    category: CategoriesTabelle?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Product name
            Text(
                text = product.nom,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Arabic name if available
            if (product.nomArab.isNotEmpty()) {
                Text(
                    text = product.nomArab,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category and price info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Catégorie: ${category?.nom ?: "Non définie"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Prix: ${product.prixVent} DA",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Availability indicator
                Surface(
                    color = when (product.disponibilityEtates) {
                        V.DiviseParSections.App.Shared.Repository.DisponibilityEtates.DISPO ->
                            MaterialTheme.colorScheme.primary
                        V.DiviseParSections.App.Shared.Repository.DisponibilityEtates.NON_DISPO ->
                            MaterialTheme.colorScheme.error
                        V.DiviseParSections.App.Shared.Repository.DisponibilityEtates.PETITE_PROBABILITY ->
                            MaterialTheme.colorScheme.tertiary
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = product.disponibilityEtates.nomArabe,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
