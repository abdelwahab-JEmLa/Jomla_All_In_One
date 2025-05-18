package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.View

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Function.formatTimestamp
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitNoSqlDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.TarificationViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ProductClientInfoCard(
    viewModel: TarificationViewModel,
    produit: ProduitNoSqlDataBase.Produit,
    client: ProduitNoSqlDataBase.Produit.ClientAchteur,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "produit.name > (Id=${produit.infosId})",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Client ID: ${client.infosId}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            val date = formatTimestamp(System.currentTimeMillis()).first
            val time = formatTimestamp(System.currentTimeMillis()).second

            Text(
                text = "Date: $date $time",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun TarificationTypeSection(
    modifier: Modifier = Modifier,
    viewModel: TarificationViewModel,
    typeTarification: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification,
    showOnlyLatestPrices: Boolean = false
) {
    val composeDataMutable by remember { mutableStateOf(typeTarification) }
    val sqlRelatedInfos= viewModel.get_C_TypeTarificationInfos(composeDataMutable)

    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Type ${sqlRelatedInfos?.entityCorrespond?.name ?: "Unknown"} " +
                            "ID:(${composeDataMutable.infosId})",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatTimestamp(composeDataMutable.vidTimestamp).first
                                +" "+
                                formatTimestamp(composeDataMutable.vidTimestamp).second,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val pricesToShow = if (showOnlyLatestPrices) {
                typeTarification.PrixsCurrency
                    .maxByOrNull { it.vidTimestamp }
                    ?.let { listOf(it) } ?: emptyList()
            } else {
                typeTarification.PrixsCurrency
                    .sortedByDescending { it.vidTimestamp }
            }

            if (pricesToShow.isEmpty()) {
                Text(
                    text = "No prices available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                pricesToShow.forEach { prix ->
                    TarificationItem(prix = prix)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun TarificationItem(
    prix: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix,
    modifier: Modifier = Modifier
) {
    val (date, time) = formatTimestamp(prix.vidTimestamp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
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
