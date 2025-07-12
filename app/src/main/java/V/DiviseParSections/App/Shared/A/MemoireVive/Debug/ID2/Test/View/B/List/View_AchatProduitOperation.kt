package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.B.List

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.B.List.Z.List.List_AchatCouleurOperation
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.core.graphics.toColorInt

@Composable
fun View_AchatProduitOperation(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    groupeAchatProduit: Map.Entry<String, List<M11AchatOperation>>,
) {
    val produit =
        viewModel.getter.repoM1ProduitInfos.datasValue.find { it.keyID == groupeAchatProduit.key }

    if (produit != null) {
        Card(Modifier.getSemanticsTag(produit, "produit")) {
            HorizontalDivider(Modifier.height(20.dp), thickness = 5.dp, color = Color.Red)

            Column {
                Header(
                    produit,
                    viewModel = viewModel,
                    groupeAchatProduit=groupeAchatProduit,
                    )

                List_AchatCouleurOperation(
                    viewModel = viewModel,
                    listAchatCouleur = groupeAchatProduit.value,
                )
            }
        }
    }
}
@Composable
private fun Header(
    produit: ArticlesBasesStatsTable,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    groupeAchatProduit: Map.Entry<String, List<M11AchatOperation>>
) {
    var showDialog by remember { mutableStateOf(false) }

    val firstAchatOperation = groupeAchatProduit.value.lastOrNull()
    val grossist = firstAchatOperation?.let { achat ->
        viewModel.aCentralFacade.repositorysMainGetter.repo15Grossist.datasValue
            .find { it.keyID == achat.parent_M15Grossist_KeyID }
    }

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
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (grossist != null) {
                        try {
                            Color(grossist.couleur_In_Str.toColorInt())
                        } catch (e: Exception) {
                            MaterialTheme.colorScheme.primary
                        }
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            ) {
                Text(
                    text = grossist?.nom ?: "Choisir Grossiste",
                    color = Color.White
                )
            }
        }
    }

    if (showDialog) {
        Dialog_Choisire_Grossist(
            viewModel = viewModel,
            groupeAchatProduit = groupeAchatProduit,
        ) {
            showDialog = false
        }
    }
}

