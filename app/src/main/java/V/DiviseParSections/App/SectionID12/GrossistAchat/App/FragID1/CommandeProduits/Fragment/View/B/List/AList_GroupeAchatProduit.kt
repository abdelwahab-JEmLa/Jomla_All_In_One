package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.W_AchatProduitOperation.View.View_AchatProduitOperation
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun List_GroupeAchatProduit(
    modifier: Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
) {
    val repo = viewModel.getter.repo11AchatOperation
    val items = repo.bProduitKeyID_To_List_KAchatCouleurOperation.entries.toList()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Column {
            if (items.isEmpty()) {
                ElevatedCard(
                    modifier = Modifier
                        .getSemanticsTag(aCentralFacade.repositorysMainGetter.repo11AchatOperation
                            .datasValue,"repo11AchatOperation")
                        .getSemanticsTag(items,"items")
                        .fillMaxWidth()
                        .padding(petitePaddine),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = "No purchase operations available",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
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
}
