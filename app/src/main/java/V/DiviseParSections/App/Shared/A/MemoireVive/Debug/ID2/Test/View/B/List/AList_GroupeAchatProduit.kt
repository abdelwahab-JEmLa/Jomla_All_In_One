package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.B.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel.GrossistAchatSec12FragID1_ViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Changed from: import androidx.wear.compose.material.Text

@Composable
fun List_GroupeAchatProduit(
    modifier: Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
) {
    val repo = viewModel.getter.repo11AchatOperation

    val items = repo.bProduitKeyIDToListKAchatCouleurOperation.entries.toList()
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Column {
            Elevated_Text(viewModel)

            LazyColumn {
                items(items) { groupeAchatProduit ->
                    View_AchatProduitOperation(
                        viewModel = viewModel,
                        groupeAchatProduit = groupeAchatProduit
                    )
                }
            }
        }
    }
}

@Composable
private fun Elevated_Text(viewModel: GrossistAchatSec12FragID1_ViewModel) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(petitePaddine),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Text(
            text = "List_GroupeAchatProduit",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
