package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test._A.View

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test.Produit
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test._PreviewProvider
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test.addNewTransactionType
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test.formatTimestamp
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ui.theme.ClientJetPackTheme

@Preview
@Composable
fun PreviewTest(
    @PreviewParameter(_PreviewProvider::class) initProduits: List<Produit>
) {
    Fragment(initProduits)
}

@Composable
private fun Fragment(initProduits: List<Produit>) {
    var produits by remember { mutableStateOf(initProduits) }
    var showOnlyLatestPrices by remember { mutableStateOf(false) }
    var showDebugLogs by remember { mutableStateOf(false) }
    val newPrixValeur by remember { mutableStateOf(0.0) }

    val produitEtClientAActiveLeur by remember { mutableStateOf(Pair(1L, 2L)) }

    LaunchedEffect(produits, produitEtClientAActiveLeur) {
        // Find the product and client by their IDs
        val (produitId, clientId) = produitEtClientAActiveLeur
        val produitIndex = produits.indexOfFirst { it.id == produitId }

        if (produitIndex >= 0) {
            val updatedProduits = produits.toMutableList()
            // Activate the selected product
            updatedProduits[produitIndex] = updatedProduits[produitIndex].copy(
                cesStatuesMutable = updatedProduits[produitIndex].cesStatuesMutable.copy(
                    cActiveDonsSonListParent = true
                )
            )

            // Find and activate the selected client
            val clientIndex =
                updatedProduits[produitIndex].clients.indexOfFirst { it.id == clientId }
            if (clientIndex >= 0) {
                val updatedClients = updatedProduits[produitIndex].clients.toMutableList()
                updatedClients[clientIndex] = updatedClients[clientIndex].copy(
                    cesStatuesMutable = updatedClients[clientIndex].cesStatuesMutable.copy(
                        cActiveDonsSonListParent = true
                    )
                )
                updatedProduits[produitIndex] = updatedProduits[produitIndex].copy(
                    clients = updatedClients
                )
            }

            // Update the products list with our changes
            produits = updatedProduits

            // Log debug information
            logDebug("Updated active product $produitId and client $clientId")
        }
    }

    ClientJetPackTheme(darkTheme = true) {
        MainScreen(
            produits = produits,
            showOnlyLatestPrices = showOnlyLatestPrices,
            showDebugLogs = showDebugLogs,
            onAddProduct = {
                val newProduct = addNewTransactionType(produits, newPrixValeur, 3L)
                newProduct?.let {
                    // Only add if not already in the list (prevents duplication)
                    val exists = produits.any { p -> p.id == it.id }
                    produits = if (!exists) {
                        produits + it
                    } else {
                        // If product exists, update it instead of adding a new one
                        produits.map { p -> if (p.id == it.id) it else p }
                    }
                }
            },
            onToggleLatestPrices = {
                showOnlyLatestPrices = !showOnlyLatestPrices
            },
            onToggleDebugLogs = {
                showDebugLogs = !showDebugLogs
            }
        )
    }
}

@Composable
fun MainScreen(
    produits: List<Produit>,
    showOnlyLatestPrices: Boolean,
    showDebugLogs: Boolean = false,
    modifier: Modifier = Modifier,
    onAddProduct: () -> Unit,
    onToggleLatestPrices: () -> Unit,
    onToggleDebugLogs: () -> Unit = {}
) {
    val filtredTariff = produits
        .filter { produit ->
            produit.cesStatuesMutable.cActiveDonsSonListParent
        }
        .flatMap { produit ->
            produit.clients.filter { client ->
                client.cesStatuesMutable.cActiveDonsSonListParent
            }.flatMap { client ->
                client.typesTarification.map { typeTarification ->
                    Triple(produit, client, typeTarification)
                }
            }
        }
        .sortedWith(compareBy(
            { it.third.id },
            { it.third.PrixsCurrency.maxByOrNull { prix -> prix.valeur }?.valeur ?: 0.0 }
        ))

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (filtredTariff.isNotEmpty()) {
                val (produit, client, _) = filtredTariff.first()
                ProductClientInfoCard(produit = produit, client = client)
            }

            if (showDebugLogs) {
                DebugLogView(logs = debugLogs, modifier = Modifier.weight(1f))
            } else {
                MainList(
                    filtredTariff = filtredTariff.map { it.third },
                    showOnlyLatestPrices = showOnlyLatestPrices,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // New Debug Log toggle button
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

            FloatingActionButton(
                onClick = onAddProduct,
                modifier = Modifier
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    }
}

@Composable
fun MainList(
    filtredTariff: List<Produit.Client.TypeTarification>,
    showOnlyLatestPrices: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        items(filtredTariff) { typeTarification ->
            TarificationTypeSection(
                typeTarification = typeTarification,
                showOnlyLatestPrices = showOnlyLatestPrices
            )
        }
    }
}

@Composable
fun TarificationTypeSection(
    typeTarification: Produit.Client.TypeTarification,
    showOnlyLatestPrices: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (date, time) = formatTimestamp(typeTarification.timestamp)

    var currentTypeTarification by remember { mutableStateOf(typeTarification) }

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
                text = "Type: ${currentTypeTarification.infos.type.name}",
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
                    // Find the maximum price ID to prevent duplicates
                    val maxPriceId = currentTypeTarification.PrixsCurrency.maxOfOrNull { it.id } ?: 0
                    val newPriceId = maxPriceId + 1

                    val timestamp = System.currentTimeMillis()
                    val newPrice = Produit.Client.TypeTarification.Prix(
                        id = newPriceId,
                        timestamp = timestamp,
                        valeur = 0.0
                    )

                    // Ensure we're not adding duplicate price IDs
                    if (currentTypeTarification.PrixsCurrency.none { it.id == newPriceId }) {
                        currentTypeTarification = currentTypeTarification.copy(
                            PrixsCurrency = currentTypeTarification.PrixsCurrency + newPrice
                        )
                    }
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
            currentTypeTarification.PrixsCurrency
                .maxByOrNull { it.timestamp }
                ?.let { listOf(it) } ?: emptyList()
        } else {
            // Show all prices sorted by id and value
            currentTypeTarification.PrixsCurrency
                .sortedWith(compareBy({ it.id }, { it.valeur }))
        }

        pricesToShow.forEach { prix ->
            TarificationItem(prix = prix)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun TarificationItem(
    prix: Produit.Client.TypeTarification.Prix,
    modifier: Modifier = Modifier
) {
    val (date, time) = formatTimestamp(prix.timestamp)

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
