package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.ClientDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.NoSqlDataBases
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.OutputNoSqlModel
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.Tarification
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.TypeTarificationDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.TypeTarificationEnum
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.createTimestamp
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
import androidx.compose.runtime.remember
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

class NoSqlDataBasesPreviewProvider : PreviewParameterProvider<NoSqlDataBases> {
    override val values = sequenceOf(
        NoSqlDataBases(
            produitInfos = mutableListOf(
                ProduitInfos(id = 1, nom = "Produit Optila"),
                ProduitInfos(id = 2, nom = "Produit Hnina"),
                ProduitInfos(id = 3, nom = "Produit kemya")
            ),
            clientDataBase = mutableListOf(
                ClientDataBase(id = 1, nom = "Client Abderrahman", idActiveTypeTarificationDataBase = 1),
                ClientDataBase(id = 2, nom = "Client Beta", idActiveTypeTarificationDataBase = 2),
                ClientDataBase(id = 3, nom = "Client Gamma", idActiveTypeTarificationDataBase = 3)
            ),

            tarificationEntries = mutableListOf(
                Tarification(
                    vidTimestamp = createTimestamp(day = 1, hour = 12, minute = 30),
                    idProduit = 1,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 20.99
                ),
                Tarification(
                    vidTimestamp = createTimestamp(day = 5, hour = 13, minute = 30),
                    idProduit = 1,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 25.50
                ),

                Tarification(
                    vidTimestamp = createTimestamp(day = 5, hour = 14, minute = 30),
                    idProduit = 1,
                    idClient = 2,
                    idTypeTarification = 2,
                    prixCurrency = 9.75
                ),

                Tarification(
                    vidTimestamp = createTimestamp(day = 6, hour = 3, minute = 30),
                    idProduit = 2,
                    idClient = 1,
                    idTypeTarification = 1,
                    prixCurrency = 15.25
                ),

                Tarification(
                    vidTimestamp = createTimestamp(day = 6, hour = 4, minute = 30),
                    idProduit = 3,
                    idClient = 1,
                    idTypeTarification = 3,
                    prixCurrency = 14.80
                )
            )
        )
    )
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
                val clientInfo = tarificationViewModel?.getSqlClient(client.id)
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
            val typeTarifInfo = tarificationViewModel?.getSqlTypeTarification(typeTarif.id)
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


@Preview(showBackground = true)
@Composable
fun PrixPrevDirect(
    @PreviewParameter(NoSqlToOutputModelPreviewProvider::class) outputModel: OutputNoSqlModel
) {
    val viewModel = remember {
        val noSqlDataProvider = NoSqlDataBasesPreviewProvider().values.first()
        TarificationViewModel(noSqlDataProvider)
    }

    MaterialTheme {
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
                            "Tarification Dashboard (Direct Model)",
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
                        items(outputModel.produits) { produit ->
                            val produitName =
                                viewModel.getSqlProduit(produit.id)?.nom ?: "Produit ${produit.id}"
                            ProduitCard(
                                produit = produit,
                                produitName = produitName,
                                tarificationViewModel = viewModel
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
}

class TarificationViewModel(dataProvider: NoSqlDataBases? = null) {
    private val clientMap: Map<Long, ClientDataBase> = dataProvider?.clientDataBase?.associateBy { it.id } !!
    private val produitMap: Map<Long, ProduitInfos> = dataProvider?.produitInfos?.associateBy { it.id } !!
    private val typeTarificationMap: Map<Long, TypeTarificationDataBase>

    init {
        val typeTarifEnumValues = TypeTarificationEnum.entries.toTypedArray()
        typeTarificationMap = (1..3).associateBy(
            keySelector = { it.toLong() },
            valueTransform = { id ->
                val enumValue = typeTarifEnumValues[(id - 1) % typeTarifEnumValues.size]
                TypeTarificationDataBase(id = id.toLong(), typeTarificationEnum = enumValue)
            }
        )
    }

    fun getSqlClient(id: Long) = clientMap[id]
    fun getSqlProduit(id: Long) = produitMap[id]
    fun getSqlTypeTarification(id: Long) = typeTarificationMap[id]
}

fun NoSqlDataBases.toOutputNoSqlModel(): OutputNoSqlModel {
    val groupedByProduct = tarificationEntries.groupBy { it.idProduit }

    val produits = groupedByProduct.map { (produitId, produitTarifications) ->
        val groupedByClient = produitTarifications.groupBy { it.idClient }

        val produitTimestamp =
            produitTarifications.maxOfOrNull { it.vidTimestamp } ?: System.currentTimeMillis()

        val clients = groupedByClient.map { (clientId, clientTarifications) ->
            val groupedByType = clientTarifications.groupBy { it.idTypeTarification }

            val clientTimestamp =
                clientTarifications.maxOfOrNull { it.vidTimestamp } ?: System.currentTimeMillis()

            val typeTarifications = groupedByType.map { (typeId, typeTarifications) ->
                val typeTimestamp =
                    typeTarifications.maxOfOrNull { it.vidTimestamp } ?: System.currentTimeMillis()

                val prices = typeTarifications.map { tarif ->
                    OutputNoSqlModel.Produit.Client.TypeTarification.Prix(
                        vidTimestamp = tarif.vidTimestamp,
                        valeur = tarif.prixCurrency
                    )
                }.sortedByDescending { it.vidTimestamp }

                OutputNoSqlModel.Produit.Client.TypeTarification(
                    id = typeId,
                    vidTimestamp = typeTimestamp,
                    PrixsCurrency = prices
                )
            }.sortedByDescending { it.vidTimestamp }

            OutputNoSqlModel.Produit.Client(
                id = clientId,
                vidTimestamp = clientTimestamp,
                typeTarification = typeTarifications
            )
        }.sortedByDescending { it.vidTimestamp }

        OutputNoSqlModel.Produit(
            id = produitId,
            vidTimestamp = produitTimestamp,
            clients = clients
        )
    }.sortedByDescending { it.vidTimestamp }

    return OutputNoSqlModel(produits = produits)
}

class NoSqlToOutputModelPreviewProvider : PreviewParameterProvider<OutputNoSqlModel> {
    private val noSqlProvider = NoSqlDataBasesPreviewProvider()

    override val values = sequence {
        noSqlProvider.values.forEach { noSqlData ->
            yield(noSqlData.toOutputNoSqlModel())
        }
    }
}

// Helper function to format timestamp to readable date and time
fun strDateEtTempFromVidTimestamp(timestamp: Long): Pair<String, String> {
    val date = Date(timestamp)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return Pair(dateFormat.format(date), timeFormat.format(date))
}
