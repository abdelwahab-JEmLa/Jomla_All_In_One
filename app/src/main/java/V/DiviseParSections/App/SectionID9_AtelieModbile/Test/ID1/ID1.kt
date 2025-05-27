package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

    // Update local state when ViewModel state changes
    LaunchedEffect(uiState.produitInfosList.size) {
        produitListLocal = uiState.produitInfosList.toList()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AppBar(
            onAddTestProduct = {
                val newTestProduct = createTestProduct()
                viewModel.addProduct(newTestProduct)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Liste des Produits",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        MainList(
            produitList = produitListLocal,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AppBar(
    onAddTestProduct: () -> Unit,
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

        Button(
            onClick = onAddTestProduct
        ) {
            Text(text = "Ajouter Test")
        }
    }
}

@Composable
fun MainList(
    modifier: Modifier = Modifier,
    produitList: List<A_ProduitInfosTest> = emptyList()
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(produitList) { produit ->
            ProductItem(produit = produit)
        }
    }
}

@Composable
private fun ProductItem(
    produit: A_ProduitInfosTest,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = produit.nom,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ID: ${produit.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${produit.prixVent} DA",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (produit.monPrixAchat > 0) {
                        Text(
                            text = "Achat: ${produit.monPrixAchat} DA",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val benefice = produit.prixVent - produit.monPrixAchat
                        Text(
                            text = "Bénéfice: $benefice DA",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (benefice > 0) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

        }
    }
}

// Helper function to create a test product
private fun createTestProduct(): A_ProduitInfosTest {
    val randomId = (1000..9999).random().toLong()
    return A_ProduitInfosTest(
        id = randomId,
        nom = "Test Product $randomId",
        nomArab = "منتج تجريبي $randomId",
        prixVent = (100..1000).random().toDouble(),
        monPrixAchat = (50..800).random().toDouble(),
        cartonState = "Test",
        nomCategorie = "Test Category",
        couleur1 = "🔴 Rouge 🔴",
        nmbrUnite = (10..200).random(),
        nmbrCaron = 1,
        clienPrixVentUnite = (5..20).random().toDouble(),
        commmentSeVent = "U",
        diponibilityState = "",
        articleHaveUniteImages = false,
        timestamps = System.currentTimeMillis(),
        needUpdate = true
    )
}
