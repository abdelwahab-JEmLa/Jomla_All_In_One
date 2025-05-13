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
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class Produit(
    val id: Long,
    val timestamp: Long,
    val infos: ProduitInfos,
    val clients: List<Client>,
) {
    data class ProduitInfos(
        val nom: String = ""
    )

    data class Client(
        val id: Long,
        val timestamp: Long,
        val infos: ClientInfos,
        val typesTarification: List<TypeTarification>,
    ) {
        data class ClientInfos(
            val nom: String = ""
        )

        data class TypeTarification(
            val id: Long,
            val timestamp: Long,
            val infos: Infos,
            val PrixsCurrency: List<Prix>,
        ) {
            data class Infos(
                val nom: String = ""
            )

            data class Prix(
                val id: Long,
                val timestamp: Long,
                val valeur: Double,
            )
        }
    }
}

// Function to convert timestamp to formatted date and time
fun formatTimestamp(timestamp: Long): Pair<String, String> {
    val date = Date(timestamp)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return Pair(dateFormat.format(date), timeFormat.format(date))
}

class ProduitsPreviewProvider : PreviewParameterProvider<List<Produit>> {
    override val values = sequenceOf(
        listOf(
            Produit(
                id = 1,
                timestamp = System.currentTimeMillis(),
                infos = Produit.ProduitInfos(nom = "Produit A"),
                clients = listOf(
                    Produit.Client(
                        id = 101,
                        timestamp = System.currentTimeMillis() - 86400000, // Yesterday
                        infos = Produit.Client.ClientInfos(nom = "Client Alpha"),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1001,
                                timestamp = System.currentTimeMillis(),
                                infos = Produit.Client.TypeTarification.Infos(nom = "Standard"),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10001,
                                        timestamp = System.currentTimeMillis(),
                                        valeur = 29.99
                                    ),
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10002,
                                        timestamp = System.currentTimeMillis() - 604800000, // Week ago
                                        valeur = 24.99
                                    )
                                )
                            ),
                            Produit.Client.TypeTarification(
                                id = 1002,
                                timestamp = System.currentTimeMillis(),
                                infos = Produit.Client.TypeTarification.Infos(nom = "Premium"),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10003,
                                        timestamp = System.currentTimeMillis(),
                                        valeur = 49.99
                                    )
                                )
                            )
                        )
                    ),
                    Produit.Client(
                        id = 102,
                        timestamp = System.currentTimeMillis(),
                        infos = Produit.Client.ClientInfos(nom = "Client Beta"),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1003,
                                timestamp = System.currentTimeMillis(),
                                infos = Produit.Client.TypeTarification.Infos(nom = "Basic"),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10004,
                                        timestamp = System.currentTimeMillis(),
                                        valeur = 19.99
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            Produit(
                id = 2,
                timestamp = System.currentTimeMillis() - 259200000, // 3 days ago
                infos = Produit.ProduitInfos(nom = "Produit B"),
                clients = listOf(
                    Produit.Client(
                        id = 103,
                        timestamp = System.currentTimeMillis(),
                        infos = Produit.Client.ClientInfos(nom = "Client Gamma"),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1004,
                                timestamp = System.currentTimeMillis(),
                                infos = Produit.Client.TypeTarification.Infos(nom = "Enterprise"),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10005,
                                        timestamp = System.currentTimeMillis(),
                                        valeur = 99.99
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ProduitCardPrev(
    @PreviewParameter(ProduitsPreviewProvider::class) initProduits: List<Produit>
) {
    var produits by remember { mutableStateOf(initProduits) }

    MainScreen(
        produits = produits,
        onAddProduct = {
            // Create a new product with a unique ID and add it to the list
            val newProduct = Produit(
                id = (produits.maxOfOrNull { it.id } ?: 0) + 1,
                timestamp = System.currentTimeMillis(),
                infos = Produit.ProduitInfos(nom = "Nouveau Produit"),
                clients = listOf(
                    Produit.Client(
                        id = 1000,
                        timestamp = System.currentTimeMillis(),
                        infos = Produit.Client.ClientInfos(nom = "Nouveau Client"),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 10000,
                                timestamp = System.currentTimeMillis(),
                                infos = Produit.Client.TypeTarification.Infos(nom = "Tarif Standard"),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 100000,
                                        timestamp = System.currentTimeMillis(),
                                        valeur = 500.0
                                    )
                                )
                            )
                        )
                    )
                )
            )
            produits = produits + newProduct
        }
    )
}

@Composable
fun MainScreen(produits: List<Produit>, modifier: Modifier = Modifier, onAddProduct: () -> Unit) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tabs for different views could go here
            MainList(produits = produits)
        }

        // Add a floating action button for adding new products
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = produit.infos.nom,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$date $time",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            produit.clients.forEach { client ->
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = client.infos.nom,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$date $time",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tabs for tarification types
        var selectedTabIndex by remember { mutableIntStateOf(0) }

        if (client.typesTarification.isNotEmpty()) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                client.typesTarification.forEachIndexed { index, type ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = type.infos.nom) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Show selected tarification type
            if (selectedTabIndex < client.typesTarification.size) {
                TarificationTypeSection(
                    typeTarification = client.typesTarification[selectedTabIndex]
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
                    val newPriceId = (currentTypeTarification.PrixsCurrency.maxOfOrNull { it.id } ?: 0) + 1
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
