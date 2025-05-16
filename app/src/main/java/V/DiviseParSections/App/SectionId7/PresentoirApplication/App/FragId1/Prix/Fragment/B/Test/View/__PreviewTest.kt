package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.View

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.A.Test.formatTimestamp
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.B.NoSQL.Model.ProduitNoSqlDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.TarificationViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ui.theme.ClientJetPackTheme
import org.koin.androidx.compose.koinViewModel


@Preview
@Composable
fun PreviewTest() {
    Fragment()
}

@Composable
private fun Fragment(
    viewModel: TarificationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState

    var noSqlData by remember { mutableStateOf(uiState.outputModel) }
    var showOnlyLatestPrices by remember { mutableStateOf(false) }
    var showDebugLogs by remember { mutableStateOf(false) }

    // Selection state for active product and client
    var selectedProductId by remember { mutableStateOf(1L) }
    var selectedClientId by remember { mutableStateOf(1L) }

    ClientJetPackTheme(darkTheme = true) {
        MainScreen(
            noSqlData = noSqlData,
            selectedProductId = selectedProductId,
            selectedClientId = selectedClientId,
            showOnlyLatestPrices = showOnlyLatestPrices,
            showDebugLogs = showDebugLogs,
            onAddPrice = { produitId, clientId, typeTarificationId ->
                // Find the product
                val produitIndex = noSqlData.produits.indexOfFirst { it.infosId == produitId }
                if (produitIndex >= 0) {
                    val produits = noSqlData.produits.toMutableList()
                    val produit = produits[produitIndex]

                    // Find the client
                    val clientIndex = produit.clientAchteurs.indexOfFirst { it.infosId == clientId }
                    if (clientIndex >= 0) {
                        val clientAchteurs = produit.clientAchteurs.toMutableList()
                        val client = clientAchteurs[clientIndex]

                        // Find the type tarification
                        val typeIndex = client.typeTarification.indexOfFirst { it.infosId == typeTarificationId }
                        if (typeIndex >= 0) {
                            val typeTarifications = client.typeTarification.toMutableList()
                            val typeTarification = typeTarifications[typeIndex]

                            // Create a new price
                            val newPrice = ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                                vidTimestamp = System.currentTimeMillis(),
                                valeur = 0.0
                            )

                            // Add the new price
                            val updatedTypeTarification = typeTarification.copy(
                                PrixsCurrency = typeTarification.PrixsCurrency + newPrice
                            )
                            typeTarifications[typeIndex] = updatedTypeTarification

                            // Update the state
                            val updatedClient = client.copy(typeTarification = typeTarifications)
                            clientAchteurs[clientIndex] = updatedClient
                            val updatedProduit = produit.copy(clientAchteurs = clientAchteurs)
                            produits[produitIndex] = updatedProduit
                            noSqlData = noSqlData.copy(produits = produits)

                        }
                    }
                }
            },
            onToggleLatestPrices = {
                showOnlyLatestPrices = !showOnlyLatestPrices
            },
            onToggleDebugLogs = {
                showDebugLogs = !showDebugLogs
            },
            onSelectProductAndClient = { produitId, clientId ->
                selectedProductId = produitId
                selectedClientId = clientId
            }
        )
    }
}

@Composable
fun MainScreen(
    noSqlData: ProduitNoSqlDataBase,
    selectedProductId: Long,
    selectedClientId: Long,
    showOnlyLatestPrices: Boolean,
    showDebugLogs: Boolean = false,
    modifier: Modifier = Modifier,
    onAddPrice: (Long, Long, Long) -> Unit,
    onToggleLatestPrices: () -> Unit,
    onToggleDebugLogs: () -> Unit = {},
    onSelectProductAndClient: (Long, Long) -> Unit = { _, _ -> }
) {
    // Find the selected product and client
    val selectedProduct = noSqlData.produits.find { it.infosId == selectedProductId }
    val selectedClient = selectedProduct?.clientAchteurs?.find { it.infosId == selectedClientId }

    // Extract all type tarifications for the selected product and client
    val typeTarificationsList = selectedClient?.typeTarification ?: emptyList()

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (selectedProduct != null && selectedClient != null) {
                NoSqlProductClientInfoCard(
                    produit = selectedProduct,
                    client = selectedClient
                )
            }

                MainList(
                    typeTarificationsList = typeTarificationsList,
                    selectedProductId = selectedProductId,
                    selectedClientId = selectedClientId,
                    showOnlyLatestPrices = showOnlyLatestPrices,
                    onAddPrice = onAddPrice,
                    modifier = Modifier.weight(1f)
                )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Debug Log toggle button
            FloatingActionButton(
                onClick = onToggleDebugLogs,
                modifier = Modifier
            ) {
                Icon(
                    Icons.Default.List,
                    contentDescription = "Toggle Debug Logs"
                )
            }

            FloatingActionButton(
                onClick = onToggleLatestPrices,
                modifier = Modifier
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = "Toggle Latest Prices"
                )
            }
        }
    }
}

@Composable
fun MainList(
    typeTarificationsList: List<ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification>,
    selectedProductId: Long,
    selectedClientId: Long,
    showOnlyLatestPrices: Boolean,
    onAddPrice: (Long, Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        items(typeTarificationsList) { typeTarification ->
            NoSqlTarificationTypeSection(
                typeTarification = typeTarification,
                selectedProductId = selectedProductId,
                selectedClientId = selectedClientId,
                showOnlyLatestPrices = showOnlyLatestPrices,
                onAddPrice = onAddPrice
            )
        }
    }
}

@Composable
fun NoSqlProductClientInfoCard(
    produit: ProduitNoSqlDataBase.Produit,
    client: ProduitNoSqlDataBase.Produit.ClientAchteur,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = "Produit ID: ${produit.infosId}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Client ID: ${client.infosId}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val (date, time) = formatTimestamp(produit.vidTimestamp)
        Text(
            text = "Date: $date $time",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun NoSqlTarificationTypeSection(
    typeTarification: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification,
    selectedProductId: Long,
    selectedClientId: Long,
    showOnlyLatestPrices: Boolean = false,
    onAddPrice: (Long, Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val (date, time) = formatTimestamp(typeTarification.vidTimestamp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Type ID: ${typeTarification.infosId}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$date $time",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                IconButton(onClick = {
                    onAddPrice(selectedProductId, selectedClientId, typeTarification.infosId)
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ajouter un prix",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val pricesToShow = if (showOnlyLatestPrices) {
            // Get only the most recent price by timestamp
            typeTarification.PrixsCurrency
                .maxByOrNull { it.vidTimestamp }
                ?.let { listOf(it) } ?: emptyList()
        } else {
            // Show all prices sorted by timestamp
            typeTarification.PrixsCurrency
                .sortedByDescending { it.vidTimestamp }
        }

        pricesToShow.forEach { prix ->
            NoSqlTarificationItem(prix = prix)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun NoSqlTarificationItem(
    prix: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix,
    modifier: Modifier = Modifier
) {
    val (date, time) = formatTimestamp(prix.vidTimestamp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Prix: ${prix.valeur}€",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$date $time",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
