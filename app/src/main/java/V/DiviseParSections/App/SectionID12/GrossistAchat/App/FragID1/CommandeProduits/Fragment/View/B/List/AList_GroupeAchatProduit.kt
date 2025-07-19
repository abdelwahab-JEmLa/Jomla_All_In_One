package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.W_AchatProduitOperation.View.View_AchatProduitOperation
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.remember
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
    val items = remember(repo.filteredDatasValue) {
        repo.filteredDatasValue.mapNotNull { achat ->
            if (achat.parent_M3CouleurProduit_KeyID.isBlank() || achat.parent_M3CouleurProduit_KeyID == "null") return@mapNotNull null

            val sales = achat.get_list_v_Depuit_joinedStringKeys(repo.repo10OperationVentCouleur.datasValue)
            val produitId = sales.firstOrNull()?.parent_M1Produit_KeyId

            if (produitId.isNullOrBlank() || produitId == "null" || produitId.length <= 5) return@mapNotNull null

            produitId to achat
        }.groupBy({ it.first }, { it.second }).entries.toList()
    }

    Box(modifier = modifier.fillMaxSize().padding(4.dp)) {
        if (items.isEmpty()) {
            ElevatedCard(
                modifier = Modifier
                    .getSemanticsTag(repo.datasValue, "repo11AchatOperation_datasValue")
                    .getSemanticsTag(items, "grouped_items_list")
                    .fillMaxWidth()
                    .padding(petitePaddine),
                elevation = CardDefaults.elevatedCardElevation(2.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    text = when {
                        repo.datasValue.isEmpty() -> "Aucune opération d'achat disponible\nAjoutez des opérations d'achat pour commencer"
                        repo.filteredDatasValue.isEmpty() -> "Aucune opération d'achat ne correspond au filtre actuel\n(${repo.datasValue.size} opérations totales disponibles)\nModifiez ou supprimez le filtre pour voir plus d'opérations"
                        else -> "Aucune opération d'achat valide trouvée\n(${repo.filteredDatasValue.size} opérations après filtrage)\nVérifiez la validité des données ou contactez le support"
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn {
                items(items) { groupe ->
                    View_AchatProduitOperation(viewModel, groupe)
                }
            }
        }
    }
}
