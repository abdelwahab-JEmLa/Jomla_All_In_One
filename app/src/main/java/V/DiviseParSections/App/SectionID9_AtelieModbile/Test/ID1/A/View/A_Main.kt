package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A.View

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.CameraHandler.B_1_CameraFAB
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.Glide.A_GlideDisplayImageByKeyId_Proto_5
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.ViewModel.ViewModel_TestID2
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.createTestProduct
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview
@Composable
private fun Id2Prev() {
    FragmentMain()
}

@Composable
fun FragmentMain(
    modifier: Modifier = Modifier,
    viewModel: ViewModel_TestID2 = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var produitListLocal by remember { mutableStateOf(uiState.produitInfosList.toList()) }
    var showCategoryView by remember { mutableStateOf(true) }

    LaunchedEffect(uiState.produitInfosList.size, uiState.produitInfosList.hashCode()) {
        val newList = uiState.produitInfosList.toList()
        produitListLocal = newList
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AppBar(
            viewModel = viewModel,
            onCreateProductAndCapture = {
                createTestProduct()
            },
            onProductCreated = { newProduct ->
                viewModel.addNewProduct(newProduct)
            },
            showCategoryView = showCategoryView,
            onToggleView = { showCategoryView = !showCategoryView },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (showCategoryView) "Vue par Catégories (${produitListLocal.size})" else "Liste des Produits (${produitListLocal.size})",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (showCategoryView) {
            EditeCategoriesMainList(
                produitList = produitListLocal,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            EditeInfosMainList(
                produitList = produitListLocal,
                onPrixUpdate = { updatedProduct ->
                    produitListLocal = produitListLocal.map { product ->
                        if (product.id == updatedProduct.id) {
                            updatedProduct
                        } else {
                            product
                        }
                    }
                    viewModel.updateProduct(updatedProduct)
                    viewModel.updateActualisationImage(updatedProduct.id)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun AppBar(
    viewModel: ViewModel_TestID2,
    onCreateProductAndCapture: () -> A_ProduitInfosTest,
    onProductCreated: (A_ProduitInfosTest) -> Unit,
    showCategoryView: Boolean,
    onToggleView: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Gestion Produits",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Toggle view button
            IconButton(
                onClick = onToggleView
            ) {
                Icon(
                    imageVector = if (showCategoryView) Icons.Default.List else Icons.Default.ViewModule,
                    contentDescription = if (showCategoryView) "Voir liste" else "Voir catégories",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            B_1_CameraFAB(
                onCreateProductAndCapture = onCreateProductAndCapture,
                onProductCreated = onProductCreated,
                viewModel = viewModel,
                webPQuality = 85
            )
        }
    }
}

@Composable
fun EditeCategoriesMainList(
    produitList: List<A_ProduitInfosTest>,
    modifier: Modifier = Modifier
) {
    // Group products by idParentCategorie
    val groupedProducts = remember(produitList) {
        produitList.groupBy { it.idParentCategorie ?: 0L }
            .toSortedMap() // Sort by category ID
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        groupedProducts.forEach { (categoryId, products) ->
            item(key = "header_$categoryId") {
                // Sticky header for category
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = if (categoryId == 0L) "Sans Catégorie" else "Catégorie $categoryId",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            item(key = "products_$categoryId") {
                // Horizontal scrolling row of products
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(
                        items = products,
                        key = { "product_${it.id}" }
                    ) { produit ->
                        MainItemEditeCategories(
                            produit = produit,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainItemEditeCategories(
    produit: A_ProduitInfosTest,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            A_GlideDisplayImageByKeyId_Proto_5(
                product = produit,
                produitVID = produit.id,
                refreshImage = produit.actualiseSonImageTest2,
                size = 80.dp,
                modifier = Modifier.weight(1f)
            )

            // Optional: Add product name below image
            Text(
                text = produit.nom,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
