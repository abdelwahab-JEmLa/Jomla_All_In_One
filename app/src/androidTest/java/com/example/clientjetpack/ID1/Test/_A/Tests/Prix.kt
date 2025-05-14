package com.example.clientjetpack.ID1.Test._A.Tests

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test.Models.OutputNoSqlModel
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clientjetpack.ID1.Test.Packages.Function.strDateEtTempFromVidTimestamp
import com.example.clientjetpack.ID1.Test.Packages.ViewModel.TarificationViewModel

@Preview
@Composable
fun PrixPrev() {        //<--
//TODO(1): pk le preview ne se lancepas quen c dont le test dossie 
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
                "D_TarificationInfos Dashboard",
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
                val produitInfo = tarificationViewModel.getSqlProduitInfos(produit.infosId)

                ProduitCard(
                    produit = produit,
                    produitName = produitInfo?.nom ?: "Produit ${produit.infosId}",
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
                text = "${produit.clientAchteurs.size} clientAchteurs",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            produit.clientAchteurs.forEach { client ->
                val clientInfo = tarificationViewModel.getSqlClient(client.infosId)
                ClientSection(
                    clientAchteur = client,
                    clientName = clientInfo?.nom ?: "ClientAchteur ${client.infosId}",
                    tarificationViewModel = tarificationViewModel
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ClientSection(
    clientAchteur: OutputNoSqlModel.Produit.ClientAchteur,
    clientName: String,
    tarificationViewModel: TarificationViewModel,
    modifier: Modifier = Modifier
) {
    val (date, time) = strDateEtTempFromVidTimestamp(clientAchteur.vidTimestamp)

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
            text = "${clientAchteur.typeTarification.size} tarification types",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        clientAchteur.typeTarification.forEach { typeTarif ->
            val typeTarifInfo = tarificationViewModel.getSqlTypeTarification(typeTarif.infosId)
            TarificationTypeSection(
                typeTarif = typeTarif,
                typeName = typeTarifInfo?.typeTarificationEnum?.name ?: "Type ${typeTarif.infosId}"
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun TarificationTypeSection(
    typeTarif: OutputNoSqlModel.Produit.ClientAchteur.TypeTarification,
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
