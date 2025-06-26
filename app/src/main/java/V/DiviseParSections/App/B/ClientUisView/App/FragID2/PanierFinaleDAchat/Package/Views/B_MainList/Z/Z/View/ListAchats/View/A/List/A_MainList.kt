package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.Z.View.ListAchats.View.A.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.FAchatOperationCouleurRepositoryComposable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.ZViewModel_Sec1Frag3
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
    mainRepo: FAchatOperationCouleurRepositoryComposable,
    viewModel: ZViewModel_Sec1Frag3
) {
    val groupedAchats = mainRepo.datasValue.groupBy { it.parentProduitId }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(groupedAchats.entries.toList()) { (productId, achatGroup) ->
            ProductGroup(
                bProduitDataBase_SubClassFunctionality= viewModel.uiStateCentralRepositorys.bProduitDataBase_SubClassFunctionality,
                viewModel = viewModel,
                productId = productId,
                achats = achatGroup
            )
        }
    }
}
