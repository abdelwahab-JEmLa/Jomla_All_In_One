package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.W.Ui.AfficheIconVentMultiItems
import androidx.compose.foundation.layout.Row
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
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            state = rememberLazyListState(),
            modifier = Modifier.weight(1f)
        ) {
            items(listAchatCouleur) { achatCouleur ->
                VerticalDivider(
                    thickness = 9.dp,
                    color = Color.Red
                )

                View_AchatCouleur(
                    viewModel, achatCouleur
                )
            }
        }

        if (listAchatCouleur.size > 1) {
            AfficheIconVentMultiItems()
        }
    }
}

