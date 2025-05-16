package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.View.Archive

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.B.NoSQL.Model.ProduitNoSqlDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.TarificationViewModel
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
import androidx.compose.ui.unit.dp

@Composable
fun ProduitCard(
    produit: ProduitNoSqlDataBase.Produit,
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
                ClientSection(
                    clientAchteur = client,
                    tarificationViewModel = tarificationViewModel
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ClientSection(
    clientAchteur: ProduitNoSqlDataBase.Produit.ClientAchteur,
    modifier: Modifier = Modifier,
    tarificationViewModel: TarificationViewModel?
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
            TarificationTypeSection(
                typeTarif = typeTarif,
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun TarificationTypeSection(
    typeTarif: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification,
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
