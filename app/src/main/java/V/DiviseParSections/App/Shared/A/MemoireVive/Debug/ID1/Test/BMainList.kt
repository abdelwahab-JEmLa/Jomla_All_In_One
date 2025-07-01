package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.CategoriesTabelle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                ViewProduit(
                    product,
                    categoryMap[product.idParentCategorie],
                    Modifier.fillMaxWidth()
                )
            }
        }
    }
}
