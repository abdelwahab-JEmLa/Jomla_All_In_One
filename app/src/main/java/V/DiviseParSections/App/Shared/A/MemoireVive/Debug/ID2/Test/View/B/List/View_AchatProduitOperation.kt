package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.B.List

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.B.List.Z.List.List_AchatCouleurOperation
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.IDKeyModel11.Repository.M11AchatOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun View_AchatProduitOperation(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    groupeAchatProduit: Map.Entry<String, List<M11AchatOperation>>,
) {
    val produit = viewModel.getter.repoM1ProduitInfos.datasValue.find { it.keyID == groupeAchatProduit.key }

    if (produit != null) {
        Card {
            HorizontalDivider(Modifier.height(20.dp), thickness = 5.dp, color = Color.Red)

            Column {
                Header(produit)

                List_AchatCouleurOperation(
                    viewModel = viewModel,
                    listAchatCouleur = groupeAchatProduit.value,
                )
            }
        }
    }
}

@Composable
private fun Header(produit: ArticlesBasesStatsTable) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = produit.nom,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = { showDialog = true }
            ) {
                Text("Details")
            }
        }
    }

    // Dialog that appears when button is clicked
    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Product Details",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Name: ${produit.nom}",
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "ID: ${produit.keyID}",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = { showDialog = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}
