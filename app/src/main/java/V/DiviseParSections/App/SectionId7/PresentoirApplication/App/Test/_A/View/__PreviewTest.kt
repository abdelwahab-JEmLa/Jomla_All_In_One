package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test._A.View

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test.Produit
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test._PreviewProvider
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test.addNewTransactionType
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ui.theme.ClientJetPackTheme

@Preview
@Composable
fun PreviewTest(
    @PreviewParameter(_PreviewProvider::class) initProduits: List<Produit>
) {
    var produits by remember { mutableStateOf(initProduits) }
    var showOnlyLatestPrices by remember { mutableStateOf(true) }
    var showDebugLogs by remember { mutableStateOf(false) }

    ClientJetPackTheme(darkTheme = true) {
        MainScreen(
            produits = produits,
            showOnlyLatestPrices = showOnlyLatestPrices,
            showDebugLogs = showDebugLogs,
            onAddProduct = {
                logDebug("onAddProduct called. Current product count: ${produits.size}")
                val newProduct = addNewTransactionType(produits)
                newProduct?.let {
                    // Only add if not already in the list (prevents duplication)
                    val exists = produits.any { p -> p.id == it.id }
                    if (!exists) {
                        logDebug("Adding new product with id: ${it.id}")
                        produits = produits + it
                    } else {
                        // If product exists, update it instead of adding a new one
                        logDebug("Updating existing product with id: ${it.id}")
                        produits = produits.map { p -> if (p.id == it.id) it else p }
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
