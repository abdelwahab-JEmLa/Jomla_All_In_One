package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test

import V.DiviseParSections.App.Shared.Repository.A.Base.ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainFastSearchProduitPourVent(
    viewModel: ViewModelMainFastSearchProduitPourVent = koinViewModel(),
    modifier: Modifier = Modifier,
    tag: String = ""
) {
    val uiState by viewModel.uiState.collectAsState()
    val products = viewModel.getter.bProduitInfosRepository.datasValue
    val categories = viewModel.getter.b3CategoriesCompoRepository.datasValue

    val tagParent = "--${Z_AppCompt.keyModel}-${ParametresAppComptNonSaved().gerantComptKeyByParent}--${Z_AppCompt.keyModelValID7VentParent}-${ParametresAppComptNonSaved().activePeriodKeyByParent}"
    val keyCompose = SemanticsPropertyKey<String>(tagParent)
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    Surface(
        modifier = modifier.fillMaxSize().semantics { set(keyCompose, tagParent) }
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recherche Rapide Produits",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                FloatingActionButton(
                    onClick = viewModel::onAddNewProduct,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Add, "Ajouter produit")
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.searchText,
                onValueChange = viewModel::onSearchTextChange,
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                placeholder = { Text("Rechercher un produit...") },
                leadingIcon = { Icon(Icons.Default.Search, "Rechercher") },
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            MainList(products, categories, uiState.searchText, Modifier.fillMaxSize())
        }
    }
}

@Composable
fun MainList(
    products: List<ArticlesBasesStatsTable>,
    categories: List<CategoriesTabelle>,
    searchFilter: String,
    modifier: Modifier = Modifier
) {
    val categoryMap = remember(categories) { categories.associateBy { it.id } }
    val catalogues = remember { B4CatalogueCategoriesRepository().associateBy { it.id } }

    val filteredProducts = remember(products, searchFilter) {
        if (searchFilter.isBlank()) products
        else products.filter {
            it.nom.contains(searchFilter, true) ||
                    it.nomMutable.contains(searchFilter, true) ||
                    it.nomArab.contains(searchFilter, true)
        }
    }

    val sortedProducts = remember(filteredProducts, categories) {
        val (regular, orphan) = filteredProducts.partition { product ->
            val category = categoryMap[product.idParentCategorie ?: 0L]
            val catalogueId = category?.catalogueParentId ?: 4L
            category != null && catalogueId != 4L && !category.nom.equals("NONE", true)
        }

        val sortedRegular = regular.sortedWith(
            compareBy<ArticlesBasesStatsTable> {
                val category = categoryMap[it.idParentCategorie ?: 0L]
                catalogues[category?.catalogueParentId ?: 4L]?.position ?: Int.MAX_VALUE
            }.thenBy { categoryMap[it.idParentCategorie ?: 0L]?.position ?: Int.MAX_VALUE }
                .thenBy { it.positionDonSonCesFrereCategorieProduits }
                .thenBy { it.nom.lowercase() }
        )

        val sortedOrphan = orphan.sortedWith(
            compareBy<ArticlesBasesStatsTable> {
                val category = categoryMap[it.idParentCategorie ?: 0L]
                category?.nom?.takeIf { !it.equals("NONE", true) } ?: "ZZZZZ_NO_CATEGORY"
            }.thenBy { it.positionDonSonCesFrereCategorieProduits }
                .thenBy { it.nom.lowercase() }
        )

        sortedRegular + sortedOrphan
    }

    LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (searchFilter.isNotEmpty() && sortedProducts.isEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Box(
                        Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Aucun produit trouvé pour \"$searchFilter\"",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        } else {
            items(sortedProducts) { product ->
                ViewProduit(product, categoryMap[product.idParentCategorie], Modifier.fillMaxWidth())
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
    Card(modifier, elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                product.nom,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (product.nomArab.isNotEmpty()) {
                Text(
                    product.nomArab,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Catégorie: ${category?.nom ?: "Non définie"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Prix: ${product.prixVent} DA",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

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
                        product.disponibilityEtates.nomArabe,
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
