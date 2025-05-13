package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test.Function.formatTimestamp
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    @PreviewParameter(PreviewProvider::class) initDatas: List<TypeTarification>
) {
    var datas by remember { mutableStateOf(initDatas) }

    ClientJetPackTheme(darkTheme = true) {
        MainScreen(
            datas = datas,
            onAddData = {
                val newData = newData(datas)
                datas = datas + newData
            },
            onClickToFilter = {
                datas = datas.map { typeTarification ->
                    if (typeTarification.parent.produit.id == 2L) {
                        typeTarification.copy(
                            cesStatuesMutable = typeTarification.cesStatuesMutable.copy(
                                cActiveDonsSonListParent = true
                            )
                        )
                    } else {
                        typeTarification.copy(
                            cesStatuesMutable = typeTarification.cesStatuesMutable.copy(
                                cActiveDonsSonListParent = false
                            )
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun MainScreen(
    datas: List<TypeTarification>,
    modifier: Modifier = Modifier,
    onAddData: () -> Unit,
    onClickToFilter: () -> Unit
) {
    var showFirstCard by remember { mutableStateOf(true) }

    val filteredTariff = datas
        .filter { typeTarification ->
            typeTarification.cesStatuesMutable.cActiveDonsSonListParent
        }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (showFirstCard && filteredTariff.isNotEmpty()) {
                val firstTariff = filteredTariff.first()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Produit: ${firstTariff.parent.produit.infos.nom}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        val clientName = firstTariff.parent.produit.clients.firstOrNull()?.infos?.nom ?: "N/A"
                        Text(
                            text = "Client: $clientName",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            MainList(datas = datas)
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Filter FAB
            FloatingActionButton(
                onClick = onClickToFilter,
                modifier = Modifier
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter")
            }

            // Add FAB
            FloatingActionButton(
                onClick = onAddData,
                modifier = Modifier
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}

@Composable
fun MainList(datas: List<TypeTarification>, modifier: Modifier = Modifier) {
    var sortByNewest by remember { mutableStateOf(true) }

    val filteredTariff = datas
        .filter { typeTarification ->
            typeTarification.cesStatuesMutable.cActiveDonsSonListParent
        }
        .let { list ->
            if (sortByNewest) {
                list.sortedByDescending { it.timestamp }
            } else {
                list.sortedBy { it.timestamp }
            }
        }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        items(filteredTariff) { typeTarification ->
            TarificationTypeSection(typeTarification = typeTarification)
        }
    }
}

@Composable
fun TarificationItem(
    prix: TypeTarification.Prix,
    modifier: Modifier = Modifier
) {
    val (date, time) = formatTimestamp(prix.timestamp)
    var isEditing by remember { mutableStateOf(false) }

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
    typeTarification: TypeTarification,
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
                    val newPrice = TypeTarification.Prix(
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
