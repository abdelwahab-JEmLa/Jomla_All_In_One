package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainList(
    modifier: Modifier = Modifier,
    fVentCouleurOperationRepository: Repo10OperationVentCouleur,
    viewModel: ZViewModel_Sec1Frag3
) {
    val uiState by viewModel.uiState.collectAsState()

    val groupedVents by remember {
        derivedStateOf {
            val filteredData = if (uiState.filterNonTrouve) {
                fVentCouleurOperationRepository.onVentFilteredDatas
                    .filter { it.etateDelivery != FCouleurVentOperationInfos.EtateDelivery.NonTrouve }
            } else {
                fVentCouleurOperationRepository.onVentFilteredDatas
            }

            filteredData.groupBy { it.parentBProduitInfosKeyId }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(groupedVents.entries.toList()) { (productKeyId, achatGroup) ->
            ProductGroup(
                viewModel = viewModel,
                productKeyId = productKeyId,
                vents = achatGroup
            )
        }
    }
}
