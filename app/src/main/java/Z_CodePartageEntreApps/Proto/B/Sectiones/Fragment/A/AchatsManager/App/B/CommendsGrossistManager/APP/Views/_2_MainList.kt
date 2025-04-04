package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views

import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.ViewModel.UiState_APP2_ID_2
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainList_APP2_ID_2(
    modifier: Modifier = Modifier,
    uiState: UiState_APP2_ID_2
) {
    val filteredBonAchatList =
        uiState._1_3_BonAchatList.filter { bonAchat ->
            bonAchat.parent_1_4_PeriodeVent == uiState._1_4_PeriodeVentList.lastOrNull()!!.vid
        }

    // Group products by product ID
    val groupedProducts = uiState._1_2_ProduitAcheteOperationList
        .filter { produit ->
            filteredBonAchatList.any { bonAchat -> bonAchat.vid == produit.parent_1_3_BonAchat }
        }
        .groupBy { it.produitAcheterID }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(groupedProducts.entries.toList()) { (productId, productsList) ->
            MainItem_APP2_ID_2(
                modifier = Modifier.fillMaxWidth(),
                idproduit = productId,

                uiState=uiState,
                opetaionsAcceptedListVID= productsList.map { it.vid }
            )
        }
    }
}
