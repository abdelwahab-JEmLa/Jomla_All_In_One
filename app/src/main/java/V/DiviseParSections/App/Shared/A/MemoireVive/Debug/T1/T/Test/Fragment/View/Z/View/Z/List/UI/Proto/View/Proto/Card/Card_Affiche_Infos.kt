package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.View.Z.View.Z.List.UI.Proto.View.Proto.Card

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.T1.T.Test.Fragment.ViewModel.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.RepoM1ProduitInfos
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun Card_Affiche_InfosP(
    vm: ViewModelMainFastSearchProduitPourVent = koinViewModel(),
) {
    val repoProduit = vm.aCentralFacade.repositorysMainGetter.repoM1ProduitInfos
    val datas1 = repoProduit.datasValue
    val produit = datas1.firstOrNull {
        it.nom.contains("liya")
    }
    Card_Affiche_Infos(produit = produit)
}

@Composable
fun Card_Affiche_Infos(
    vm: ViewModelMainFastSearchProduitPourVent = koinViewModel(),
    produit: ArticlesBasesStatsTable?,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Change Infos Produit ${produit?.getDebugInfos()}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (produit != null) {
                    CartonDisplayer(
                        vm = vm,
                        produit = produit,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun CartonDisplayer(
    produit: ArticlesBasesStatsTable,
    vm: ViewModelMainFastSearchProduitPourVent,
    modifier: Modifier = Modifier,
) {
    val repoProduit = vm.aCentralFacade.repositorysMainGetter.repoM1ProduitInfos

    val quantite_Boit_Par_Carton = produit.quantite_Boit_Par_Carton
    val actuelle_Affiche_Est_Carton = produit.actuelle_Affiche_Est_Carton

    fun updateQyt(
        repoProduit: RepoM1ProduitInfos,
        produit: ArticlesBasesStatsTable,
        quantite_Boit_Par_Carton: Int
    ) {
        repoProduit.update(
            produit.copy(
                quantite_Boit_Par_Carton = quantite_Boit_Par_Carton
            )
        )
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.Blue.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Carton",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "$quantite_Boit_Par_Carton Boit/Par Carton",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Text(
                    text = "Quantité par carton",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val newQuantity =
                        if (quantite_Boit_Par_Carton > 0) quantite_Boit_Par_Carton - 1 else 0
                    updateQyt(repoProduit, produit, newQuantity)
                }) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Diminuer quantité carton",
                        tint = Color.Red
                    )
                }

                Text(
                    text = if (quantite_Boit_Par_Carton == 0) "0" else "$quantite_Boit_Par_Carton",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (quantite_Boit_Par_Carton == 0) Color.Red else Color.Black,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    val newQuantity = quantite_Boit_Par_Carton + 1
                    updateQyt(repoProduit, produit, newQuantity)
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Augmenter quantité carton",
                        tint = Color.Green
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = {
                    repoProduit.update(
                        produit.copy(
                            actuelle_Affiche_Est_Carton = !actuelle_Affiche_Est_Carton
                        )
                    )
                }) {
                    Icon(
                        imageVector = if (actuelle_Affiche_Est_Carton) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Toggle Actuelle Affiche",
                        tint = if (actuelle_Affiche_Est_Carton) Color.Yellow else Color.Gray
                    )
                }
            }
        }
    }
}
