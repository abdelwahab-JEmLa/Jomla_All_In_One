package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ui.theme.ClientJetPackTheme

@Preview
@Composable
fun PreviewTest(
    @PreviewParameter(_PreviewProvider::class) initProduits: List<Produit>
) {
    var produits by remember { mutableStateOf(initProduits) }
    var isFiltered by remember { mutableStateOf(false) }

    ClientJetPackTheme(darkTheme = true) {
        MainScreen(
            produits = produits,
            onAddProduct = {
                val newProduct = newProduit(produits)
                produits = produits + newProduct
            },
            onFilter = {
                isFiltered = !isFiltered
                if (isFiltered) {
                    val filteredProducts = produits.map { product ->
                        if (product.id == 3L) {
                            product.copy(
                                clients = product.clients.filter { client ->
                                    client.id == 105L
                                }
                            )
                        } else {
                            product.copy(clients = emptyList())
                        }
                    }.filter { product ->
                        product.clients.isNotEmpty()
                    }
                    produits = filteredProducts
                } else {
                    // Reset to original products
                    produits = initProduits
                }
            }
        )
    }
}

@Composable
fun MainScreen(
    produits: List<Produit>,
    modifier: Modifier = Modifier,
    onAddProduct: () -> Unit,
    onFilter: () -> Unit
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

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Using the extracted ProductClientInfoCard composable
            if (filtredTariff.isNotEmpty()) {
                val (produit, client, _) = filtredTariff.first()
                ProductClientInfoCard(produit = produit, client = client)
            }

            // Rest of the original code
            MainList(filtredTariff = filtredTariff.map { it.third })
        }

        // Added two FABs - one for adding product and one for filtering
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Filter FAB - Will filter to show only product 3, client 105
            FloatingActionButton(
                onClick = onFilter,
                modifier = Modifier
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter")
            }

            // Add Product FAB
            FloatingActionButton(
                onClick = onAddProduct,
                modifier = Modifier
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}

@Composable
fun ProductClientInfoCard(produit: Produit, client: Produit.Client, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Produit: ${produit.infos.nom}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Client: ${client.infos.nom}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val (date, time) = formatTimestamp(produit.timestamp)
            Text(
                text = "Date: $date $time",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun MainList(filtredTariff: List<Produit.Client.TypeTarification>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {

        items(filtredTariff) { typeTarification ->
            TarificationTypeSection(typeTarification = typeTarification)
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

@Composable
fun TarificationTypeSection(
    typeTarification: Produit.Client.TypeTarification,
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
                    val newPriceId =
                        (currentTypeTarification.PrixsCurrency.maxOfOrNull { it.id } ?: 0) + 1
                    val newPrice = Produit.Client.TypeTarification.Prix(
                        id = newPriceId,
                        timestamp = System.currentTimeMillis(),
                        valeur = 0.0
                    )

                    currentTypeTarification = currentTypeTarification.copy(
                        PrixsCurrency = currentTypeTarification.PrixsCurrency + newPrice
                    )
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

        currentTypeTarification.PrixsCurrency.forEach { prix ->
            TarificationItem(prix = prix)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
