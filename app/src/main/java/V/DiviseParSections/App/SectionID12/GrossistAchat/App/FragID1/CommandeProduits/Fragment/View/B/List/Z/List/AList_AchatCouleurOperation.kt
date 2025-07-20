package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.W.Ui.AfficheIconVentMultiItems
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun List_AchatCouleurOperation(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    listAchatCouleur: List<M11AchatOperation>,
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow(
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listAchatCouleur) { achatCouleur ->
                VerticalDivider(
                    thickness = 9.dp,
                    color = Color.Red,
                    modifier = Modifier.width(9.dp)
                )

                View_AchatCouleur(
                    relative_M11AchatOperation = achatCouleur,
                    viewModel = viewModel,
                )
            }
        }

        if (listAchatCouleur.size > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            ) {
                AfficheIconVentMultiItems()
            }
        }
    }
}
