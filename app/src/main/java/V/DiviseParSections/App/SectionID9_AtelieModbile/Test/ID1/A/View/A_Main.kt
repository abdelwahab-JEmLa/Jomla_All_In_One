package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A.View

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.CameraHandler.B_1_CameraFAB
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.ViewModel.ViewModel_TestID2
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.createTestProduct
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    val TAG = "FragmentMain"
    val uiState by viewModel.uiState.collectAsState()
    var produitListLocal by remember { mutableStateOf(uiState.produitInfosList.toList()) }

    // Update local state when ViewModel state changes
    LaunchedEffect(uiState.produitInfosList.size, uiState.produitInfosList.hashCode()) {
        val newList = uiState.produitInfosList.toList()
        Log.d(TAG, "ViewModel state changed - updating local list (${newList.size} items)")
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
                Log.d(TAG, "Adding new product to ViewModel: ${newProduct.nom} (refresh: ${newProduct.actualiseSonImageTest2})")
                viewModel.addNewProduct(newProduct)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Liste des Produits (${produitListLocal.size})",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        MainList(
            produitList = produitListLocal,
            onPrixUpdate = { updatedProduct ->
                // Update local state
                produitListLocal = produitListLocal.map { product ->
                    if (product.id == updatedProduct.id) {
                        updatedProduct
                    } else {
                        product
                    }
                }
                // Update ViewModel
                viewModel.updateProduct(updatedProduct)
                viewModel.updateActualisationImage(updatedProduct.id)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AppBar(
    viewModel: ViewModel_TestID2,
    onCreateProductAndCapture: () -> A_ProduitInfosTest,
    onProductCreated: (A_ProduitInfosTest) -> Unit,
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

        B_1_CameraFAB(
            onCreateProductAndCapture = onCreateProductAndCapture,
            onProductCreated = onProductCreated,
            viewModel = viewModel,
            webPQuality = 85   // Qualité WebP
        )
    }
}

@Composable
fun MainList(
    modifier: Modifier = Modifier,
    produitList: List<A_ProduitInfosTest> = emptyList(),
    onPrixUpdate: (A_ProduitInfosTest) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = produitList,
            key = { it.id } // Use stable key for better performance
        ) { produit ->
            ProductItem(
                produitInit = produit,
                onPrixUpdate = onPrixUpdate
            )
        }
    }
}
