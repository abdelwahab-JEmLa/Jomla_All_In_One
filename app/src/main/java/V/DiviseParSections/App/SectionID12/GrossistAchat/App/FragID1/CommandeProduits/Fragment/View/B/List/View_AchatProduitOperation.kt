// View_AchatProduitOperation.kt
package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.KAchatCouleurOperation
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.List_AchatCouleurOperation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun View_AchatProduitOperation(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    groupeAchatProduit: Map.Entry<String, List<KAchatCouleurOperation>>,
) {
    val produit = viewModel.getter.bProduitInfosRepository.datasValue.find { it.keyID == groupeAchatProduit.key }

    HorizontalDivider()
    Text(groupeAchatProduit.key)

    if (produit != null) {
        Card {
            HorizontalDivider(Modifier.height(20.dp), thickness = 5.dp, color = Color.Red)

            Column {
                Text(
                    produit.nom
                )

                List_AchatCouleurOperation(
                    viewModel = viewModel,
                    listAchatCouleur = groupeAchatProduit.value,
                )
            }
        }
    }
}
