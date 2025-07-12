package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.B.List.W_AchatProduitOperation.View

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.A.Main.Modules.Ui.Dialog_Choisire_Grossist_Modularized
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import java.text.DecimalFormat

@Composable
fun Header(
    produit: ArticlesBasesStatsTable,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    groupeAchatProduit: Map.Entry<String, List<M11AchatOperation>>
) {
    val uiState by viewModel.uiState.collectAsState()

    val firstAchatOperation = groupeAchatProduit.value.firstOrNull()

    val priceFormatter = DecimalFormat("#,##0.00")
    val formattedPrixAchat =
        priceFormatter.format(firstAchatOperation?.prix_Achat_De_Cette_Grossist)

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
            // Product info column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = produit.nom,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "Achat: $formattedPrixAchat DA",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            val grossist = firstAchatOperation?.let { achat ->
                viewModel.aCentralFacade.repositorysMainGetter.repo15Grossist.datasValue
                    .find { it.keyID == achat.parent_M15Grossist_KeyID }
            }

            Button(
                modifier = Modifier
                    .getSemanticsTag(grossist,"grossist"),
                onClick = { viewModel.update_dialog_Choisire_Grossist_Modularized_showDialog(true)  },
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

    val list_M11AchatOperation = groupeAchatProduit.value
    if (uiState.dialog_Choisire_Grossist_Modularized_showDialog) {
        Dialog_Choisire_Grossist_Modularized(
            viewModel = viewModel,
        ) { grossistSelected ->
            if (grossistSelected != null) {
                list_M11AchatOperation.map {
                    viewModel.aCentralFacade.repositorysMainSetter.repo11AchatOperation_update_If_Exist(
                        it.copy(
                            parent_M15Grossist_DebugInfos = grossistSelected.get_DebugInfos(),
                            parent_M15Grossist_KeyID = grossistSelected.keyID
                        )
                    )
                }
            }
            viewModel.update_dialog_Choisire_Grossist_Modularized_showDialog(false)
        }
    }
}
