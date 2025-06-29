package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.FVentCouleurOperationRepository
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainList(
    modifier: Modifier = Modifier,
    fVentCouleurOperationRepository: FVentCouleurOperationRepository,
    viewModel: ZViewModel_Sec1Frag3
) {
    val groupedAchats = fVentCouleurOperationRepository
        .onVentFilteredDatas.groupBy { it.parentBProduitInfosKeyId }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(groupedAchats.entries.toList()) { (productKeyId, achatGroup) ->
            ProductGroup(
                bProduitDataBase_SubClassFunctionality= viewModel.uiStateCentralRepositorys.bProduitInfosRepository,
                viewModel = viewModel,
                productKeyId = productKeyId,
                achats = achatGroup
            )
        }
    }
}
