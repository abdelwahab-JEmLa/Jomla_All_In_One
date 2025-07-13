package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.W_AchatProduitOperation.View

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.List_AchatCouleurOperation
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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

