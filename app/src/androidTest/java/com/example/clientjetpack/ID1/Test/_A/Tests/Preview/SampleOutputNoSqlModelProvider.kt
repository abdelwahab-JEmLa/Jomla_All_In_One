package com.example.clientjetpack.ID1.Test._A.Tests.Preview

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ID1.Test.Packages.Function.strDateEtTempFromVidTimestamp
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Packages.ViewModel.TarificationViewModel

@Preview(showBackground = true)
@Composable
fun MultipleItemsPrixPrev() {
    val multipleItemsData = OutputNoSqlModel(
        produits = listOf(
            OutputNoSqlModel.Produit(
                infosId = 1,
                vidTimestamp = System.currentTimeMillis(),
                clients = listOf(
                    OutputNoSqlModel.Produit.Client(
                        infosId = 1,
                        vidTimestamp = System.currentTimeMillis(),
                        typeTarification = listOf(
                            OutputNoSqlModel.Produit.Client.TypeTarification(
                                infosId = 1,
                                vidTimestamp = System.currentTimeMillis(),
                                PrixsCurrency = listOf(
                                    OutputNoSqlModel.Produit.Client.TypeTarification.Prix(
                                        valeur = 10.5,
                                        vidTimestamp = System.currentTimeMillis()
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            OutputNoSqlModel.Produit(
                infosId = 2,
                vidTimestamp = System.currentTimeMillis(),
                clients = listOf(
                    OutputNoSqlModel.Produit.Client(
                        infosId = 2,
                        vidTimestamp = System.currentTimeMillis(),
                        typeTarification = listOf(
                            OutputNoSqlModel.Produit.Client.TypeTarification(
                                infosId = 2,
                                vidTimestamp = System.currentTimeMillis(),
                                PrixsCurrency = listOf(
                                    OutputNoSqlModel.Produit.Client.TypeTarification.Prix(
                                        valeur = 15.75,
                                        vidTimestamp = System.currentTimeMillis()
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    )

    MaterialTheme {
        PreviewContentWithData(multipleItemsData)
    }
}

@Composable
fun PreviewContentWithData(data: OutputNoSqlModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = 0) {
            Tab(
                text = { Text("UI") },
                selected = true,
                onClick = { }
            )
            Tab(
                text = { Text("Logs") },
                selected = false,
                onClick = { }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
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

                    Button(onClick = { }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(data.produits) { produit ->
                        V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview._A.View.ProduitCard(
                            produit = produit,
                            produitName = "Preview Produit ${produit.infosId}",
                            tarificationViewModel = null
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            FloatingActionButton(
                onClick = { },
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
fun ProduitCard(
    produit: OutputNoSqlModel.Produit,
    produitName: String,
    tarificationViewModel: TarificationViewModel?,
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
                val clientInfo = tarificationViewModel?.getSqlClient(client.infosId)
                ClientSection(
                    client = client,
                    clientName = clientInfo?.nom ?: "Client ${client.infosId}",
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
    tarificationViewModel: TarificationViewModel?,
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
            val typeTarifInfo = tarificationViewModel?.getSqlTypeTarification(typeTarif.infosId)
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
