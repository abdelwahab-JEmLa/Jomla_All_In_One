package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Packages.ViewModel.TarificationViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clientjetpack.ID1.Test.Packages.Function.strDateEtTempFromVidTimestamp
import com.example.clientjetpack.ID1.Test.Packages.Models.InputEtInfosSqlModels
import com.example.clientjetpack.ID1.Test.Packages.Models.OutputNoSqlModel

@Preview
@Composable
fun PrixPrev() {
    Main()
}

@Composable
fun Main(
    tarificationViewModel: TarificationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val outputNoSqlData by tarificationViewModel.outputNoSqlFlow.collectAsState()
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("UI", "Logs")

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (tabIndex) {
                0 -> UiView(outputNoSqlData, tarificationViewModel)
                1 -> LogView(outputNoSqlData)
            }

            FloatingActionButton(
                onClick = { tarificationViewModel.addTest(tarificationViewModel) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Test Data")
            }
        }
    }
}

@Composable
fun UiView(
    data: OutputNoSqlModel,
    tarificationViewModel: TarificationViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Tarification Dashboard",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Button(onClick = { tarificationViewModel.refreshOutputData() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(data.produits) { produit ->
                val produitInfo = tarificationViewModel.getSqlProduitInfos(produit.id)

                ProduitCard(
                    produit = produit,
                    produitName = produitInfo?.nom ?: "Produit ${produit.id}",
                    tarificationViewModel = tarificationViewModel
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ProduitCard(
    produit: OutputNoSqlModel.Produit,
    produitName: String,
    tarificationViewModel: TarificationViewModel,
    modifier: Modifier = Modifier
) {
    val (date, time) = strDateEtTempFromVidTimestamp(produit.vidTimestamp)

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
                    text = produitName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$date $time",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${produit.clients.size} clients",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            produit.clients.forEach { client ->
                val clientInfo = tarificationViewModel.getSqlClient(client.id)
                ClientSection(
                    client = client,
                    clientName = clientInfo?.nom ?: "Client ${client.id}",
                    tarificationViewModel = tarificationViewModel
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ClientSection(
    client: OutputNoSqlModel.Produit.Client,
    clientName: String,
    tarificationViewModel: TarificationViewModel,
    modifier: Modifier = Modifier
) {
    val (date, time) = strDateEtTempFromVidTimestamp(client.vidTimestamp)

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
                text = clientName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$date $time",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${client.typeTarification.size} tarification types",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        client.typeTarification.forEach { typeTarif ->
            val typeTarifInfo = tarificationViewModel.getSqlTypeTarification(typeTarif.id)
            TarificationTypeSection(
                typeTarif = typeTarif,
                typeName = typeTarifInfo?.typeTarificationEnum?.name ?: "Type ${typeTarif.id}"
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun TarificationTypeSection(
    typeTarif: OutputNoSqlModel.Produit.Client.TypeTarification,
    typeName: String,
    modifier: Modifier = Modifier
) {
    val (date, time) = strDateEtTempFromVidTimestamp(typeTarif.vidTimestamp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = typeName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$date $time",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        typeTarif.PrixsCurrency.forEach { prix ->
            val (prixDate, prixTime) = strDateEtTempFromVidTimestamp(prix.vidTimestamp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${prix.valeur}€",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$prixDate $prixTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun LogView(data: OutputNoSqlModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            "Log Output",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .background(Color.Black)
        ) {
            item {
                Text(
                    text = "\n================= Hierarchical Database ===========================================" +
                            "\n================================================================" +
                            "\n======== C Le Test Log Output Print Du Temp=${strDateEtTempFromVidTimestamp(System.currentTimeMillis()).first} " +
                            "\n================================================================" +
                            " du   ========",
                    color = Color.Green,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Visible
                )

                Text(
                    text = "\n-- Hierarchical Structure --",
                    color = Color.Green,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            items(data.produits) { produit ->
                logProduitItem(produit)
            }

            item {
                Text(
                    text = "\n========TEST Hierarchical Database COMPLETED SUCCESSFULLY ========\n",
                    color = Color.Green,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun logProduitItem(produit: OutputNoSqlModel.Produit) {
    val isLastProduit = true // For simplicity, we'll treat each product as potentially the last one in the log view
    val (produitDate, produitTime) = strDateEtTempFromVidTimestamp(produit.vidTimestamp)

    Column {
        Text(
            text = "└─ Product : ${produit.id}, Date: $produitDate Time: $produitTime (${produit.clients.size} clients)",
            color = Color.Green,
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Visible
        )

        produit.clients.forEachIndexed { clientIndex, client ->
            val isLastClient = clientIndex == produit.clients.size - 1
            logClientItem(client, isLastProduit, isLastClient)
        }
    }
}

@Composable
fun logClientItem(
    client: OutputNoSqlModel.Produit.Client,
    isLastProduit: Boolean,
    isLastClient: Boolean
) {
    val clientPrefix = if (isLastProduit) "  └─" else "  ├─"
    val (clientDate, clientTime) = strDateEtTempFromVidTimestamp(client.vidTimestamp)

    Column {
        Text(
            text = "$clientPrefix Client ID: ${client.id}, Date: $clientDate Time: $clientTime (${client.typeTarification.size} tarification types)",
            color = Color.Green,
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Visible
        )

        client.typeTarification.forEachIndexed { typeIndex, type ->
            val isLastType = typeIndex == client.typeTarification.size - 1
            logTarificationTypeItem(type, isLastProduit, isLastClient, isLastType)
        }
    }
}

@Composable
fun logTarificationTypeItem(
    type: OutputNoSqlModel.Produit.Client.TypeTarification,
    isLastProduit: Boolean,
    isLastClient: Boolean,
    isLastType: Boolean
) {
    val typePrefix = when {
        isLastProduit && isLastClient -> "     └─"
        else -> "  │     ├─"
    }
    val (typeDate, typeTime) = strDateEtTempFromVidTimestamp(type.vidTimestamp)

    Column {
        Text(
            text = "$typePrefix Tarification Type : ${type.id} , Date: $typeDate Time: $typeTime (${type.PrixsCurrency.size} currencies)",
            color = Color.Green,
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Visible
        )

        type.PrixsCurrency.forEachIndexed { currencyIndex, currency ->
            val isLastCurrency = currencyIndex == type.PrixsCurrency.size - 1
            logPrixCurrencyItem(currency, isLastProduit, isLastClient, isLastType, isLastCurrency)
        }
    }
}

@Composable
fun logPrixCurrencyItem(
    currency: OutputNoSqlModel.Produit.Client.TypeTarification.Prix,
    isLastProduit: Boolean,
    isLastClient: Boolean,
    isLastType: Boolean,
    isLastCurrency: Boolean
) {
    val currencyPrefix = when {
        isLastProduit && isLastClient && isLastType -> "          └─"
        else -> "  │     │  ├─"
    }
    val (currencyDate, currencyTime) = strDateEtTempFromVidTimestamp(currency.vidTimestamp)

    Text(
        text = "$currencyPrefix Currency: ${currency.valeur}, Date: $currencyDate Time: $currencyTime",
        color = Color.Green,
        style = MaterialTheme.typography.bodySmall,
        overflow = TextOverflow.Visible
    )
}

@Composable
fun logProduitsList(modifier: Modifier = Modifier) {
    val tarificationViewModel: TarificationViewModel = viewModel()
    val outputNoSqlData by tarificationViewModel.outputNoSqlFlow.collectAsState()

    Column(modifier = modifier) {
        Text("Products List:", style = MaterialTheme.typography.titleMedium)
        outputNoSqlData.produits.forEach { produit ->
            val produitInfo = tarificationViewModel.getSqlProduitInfos(produit.id)
            Text("- ${produitInfo?.nom ?: "Product ${produit.id}"} (ID: ${produit.id})")
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun logProduitsListheader(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(
            "Produits List Header",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text("Details on all available products in the system")
    }
}

@Composable
fun logClientsList(modifier: Modifier = Modifier) {
    val tarificationViewModel: TarificationViewModel = viewModel()
    val clientsData = tarificationViewModel.inputSqlGroupeRepositorys.ClientDataBase_Repository().modelList

    Column(modifier = modifier) {
        Text("Clients List:", style = MaterialTheme.typography.titleMedium)
        clientsData.forEach { client ->
            Text("- ${client.nom} (ID: ${client.id}, Active Type: ${client.idActiveTypeTarificationDataBase})")
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun logClientsListtheader(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(
            "Clients List Header",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text("Details on all registered clients in the system")
    }
}

@Composable
fun logTarificationTypesList(modifier: Modifier = Modifier) {
    val tarificationViewModel: TarificationViewModel = viewModel()
    val typesData = tarificationViewModel.inputSqlGroupeRepositorys.TypeTarificationInfosRepository().modelList

    Column(modifier = modifier) {
        Text("Tarification Types List:", style = MaterialTheme.typography.titleMedium)
        typesData.forEach { type ->
            Text("- ${type.typeTarificationEnum.name} (ID: ${type.id})")
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun logTarificationTypesListheader(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(
            "Tarification Types List Header",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text("All tarification types supported by the system")
    }
}

@Composable
fun logTarificationTypesItem(modifier: Modifier = Modifier) {
    val types = InputEtInfosSqlModels.TypeTarificationEnum.values()

    Column(modifier = modifier.padding(8.dp)) {
        types.forEach { type ->
            Text(
                text = "- ${type.name}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}
