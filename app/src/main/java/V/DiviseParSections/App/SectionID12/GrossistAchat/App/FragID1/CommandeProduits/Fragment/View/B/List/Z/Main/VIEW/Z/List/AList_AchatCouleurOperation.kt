package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.Main.VIEW.Z.List

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.KAchatCouleurOperation
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.Z.Archive.AfficheIconVentMultiItems
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
    listAchatCouleur: List<KAchatCouleurOperation>,
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


                BView_AchatCouleur(
                    viewModel, achatCouleur
                )
            }
        }

        if (true) {
            AfficheIconVentMultiItems()
        }
    }
}

