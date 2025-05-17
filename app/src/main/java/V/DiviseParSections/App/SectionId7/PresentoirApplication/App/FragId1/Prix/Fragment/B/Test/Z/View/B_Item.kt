package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.Z.View

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.A.Test.formatTimestamp
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.B.NoSQL.Model.ProduitNoSqlDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.TarificationViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TarificationTypeSection(
    viewModel: TarificationViewModel,
    typeTarification: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification,
    showOnlyLatestPrices: Boolean = false,
    modifier: Modifier = Modifier
) {
    val composeDataMutable by remember { mutableStateOf(typeTarification) }
    val sqlRelatedInfos= viewModel.getSql_TypeTarification(composeDataMutable)

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
                text = "Type ${sqlRelatedInfos?.typeTarificationEnum?.name} " +
                        "ID:(${composeDataMutable.infosId})",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatTimestamp(composeDataMutable.vidTimestamp).first
                            +" "+
                            formatTimestamp(composeDataMutable.vidTimestamp).second,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val pricesToShow = if (showOnlyLatestPrices) {
            // Get only the most recent price by timestamp
            typeTarification.PrixsCurrency
                .maxByOrNull { it.vidTimestamp }
                ?.let { listOf(it) } ?: emptyList()
        } else {
            // Show all prices sorted by timestamp
            typeTarification.PrixsCurrency
                .sortedByDescending { it.vidTimestamp }
        }

        pricesToShow.forEach { prix ->
            TarificationItem(prix = prix)
            Spacer(modifier = Modifier.height(4.dp))
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
