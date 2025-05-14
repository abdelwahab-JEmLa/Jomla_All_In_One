package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.TarificationViewModel
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.function.strDateEtTempFromVidTimestamp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun ProduitCardPreview() {
    // Create a new view model to get test data
    val viewModel = TarificationViewModel()

    // Get the output model containing all data
    val outputModel = viewModel.getOutputModel()

    // Select the first product for preview (if available)
    if (outputModel.produits.isNotEmpty()) {
        val firstProduit = outputModel.produits.first()
        val produitName = viewModel.getSqlProduit(firstProduit.infosId)?.nom ?: "Produit ${firstProduit.infosId}"

        // Display the product card with the data
        ProduitCard(
            produit = firstProduit,
            produitName = produitName,
            tarificationViewModel = viewModel
        )
    }
}
@Composable
fun ProduitCard(
    produit: TarificationViewModel.ProduitNoSqlDataBase.Produit,
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
                text = "${produit.clientAchteurs.size} clientAchteurs",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            produit.clientAchteurs.forEach { client ->
                val clientInfo = tarificationViewModel?.getSqlClient(client.infosId)
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
    clientAchteur: TarificationViewModel.ProduitNoSqlDataBase.Produit.ClientAchteur,
    clientName: String,
    tarificationViewModel: TarificationViewModel?,
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
    typeTarif: TarificationViewModel.ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification,
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
