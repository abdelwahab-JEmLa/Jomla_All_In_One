package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun ProduitCardPrev(
    @PreviewParameter(ProduitsPreviewProvider::class) initProduits: List<Produit>
) {
    var produits by remember { mutableStateOf(initProduits) }

    MainScreen(
        produits = produits,
        onAddProduct = {
            val newProduct = newProduit(produits)
            produits = produits + newProduct
        }
    )
}

@Composable
fun MainScreen(produits: List<Produit>, modifier: Modifier = Modifier, onAddProduct: () -> Unit) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            MainList(produits = produits)
        }

        FloatingActionButton(
            onClick = onAddProduct,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Composable
fun MainList(produits: List<Produit>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        items(produits) { produit ->
            ProduitCard(produit = produit)
        }
    }
}

@Composable
fun ProduitCard(
    produit: Produit,
    modifier: Modifier = Modifier
) {
    val (date, time) = formatTimestamp(produit.timestamp)

    var currentProduit by remember { mutableStateOf(produit) }

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentProduit.infos.nom,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$date $time",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    IconButton(onClick = {
                        currentProduit = currentProduit.copy(
                            infos = currentProduit.infos.copy(
                                nom = "${currentProduit.infos.nom} (Édité)"
                            )
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Éditer le produit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            currentProduit.clients.forEach { client ->
                ClientSection(client = client)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ClientSection(
    client: Produit.Client,
    modifier: Modifier = Modifier
) {
    val (date, time) = formatTimestamp(client.timestamp)

    var currentClient by remember { mutableStateOf(client) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentClient.infos.nom,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$date $time",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                // Add edit button to demonstrate using the state
                IconButton(onClick = {
                    // Update the client name as an example of using the state
                    currentClient = currentClient.copy(
                        infos = currentClient.infos.copy(
                            nom = "${currentClient.infos.nom} (Édité)"
                        )
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Éditer le client",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tabs for tarification types
        var selectedTabIndex by remember { mutableIntStateOf(0) }

        if (currentClient.typesTarification.isNotEmpty()) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                currentClient.typesTarification.forEachIndexed { index, type ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = type.infos.nom) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Show selected tarification type
            if (selectedTabIndex < currentClient.typesTarification.size) {
                TarificationTypeSection(
                    typeTarification = currentClient.typesTarification[selectedTabIndex]
                )
            }
        } else {
            Text(
                text = "Aucune tarification disponible",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
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
                text = currentTypeTarification.infos.nom,
                style = MaterialTheme.typography.titleSmall
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$date $time",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                IconButton(onClick = {
                    // Create a new price with the next ID
                    val newPriceId =
                        (currentTypeTarification.PrixsCurrency.maxOfOrNull { it.id } ?: 0) + 1
                    val newPrice = Produit.Client.TypeTarification.Prix(
                        id = newPriceId,
                        timestamp = System.currentTimeMillis(),
                        valeur = 0.0
                    )

                    // Update the tarification with the new price
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

        // Display all prices
        currentTypeTarification.PrixsCurrency.forEach { prix ->
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
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Prix: ${prix.valeur}€",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "$date $time",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
