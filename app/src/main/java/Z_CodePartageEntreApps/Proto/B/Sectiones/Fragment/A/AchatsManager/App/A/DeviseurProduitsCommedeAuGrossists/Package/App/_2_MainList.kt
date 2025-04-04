package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.A.DeviseurProduitsCommedeAuGrossists.Package.App

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainList(
    modifier: Modifier = Modifier,
    uiState: UiState
) {
    // Get the last period in the list
    val lastPeriod = uiState._1_4_PeriodeVentList.last()

    // Filter BonAchat list if filtering is active and we have a lastPeriod
    val filteredBonAchatList = if (uiState.isFilteringActive) {
        uiState._1_3_BonAchatList.filter { it.parent_1_4_PeriodeVent == lastPeriod.id }
    } else {
        uiState._1_3_BonAchatList
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        // Iterate through each BonAchat and its related operations
        filteredBonAchatList.forEach { bonAchat ->
            // Display the header for each BonAchat
            item(span = { GridItemSpan(2) }) {
                Header_FragID_7(
                    modifier = Modifier.fillMaxWidth(),
                    id = bonAchat.id,
                    startDateInString = lastPeriod.startDateInString ,
                    clientAchteurID = bonAchat.clientAchteurID
                )
            }

            // Get products for this bonAchat
            val relatedProducts = uiState._1_2_ProduitAcheteOperationList.filter {
                it.parent_1_3_BonAchat == bonAchat.id
            }

            // Add items for each product
            items(relatedProducts.size) { index ->
                val produit = relatedProducts[index]
                MainItem(
                    modifier = Modifier.fillMaxWidth(),
                    idproduit = produit.vid,
                    _1_1_CouleurAcheteOperation = uiState._1_1_CouleurAcheteOperationList
                        .filter {
                        it.parent_1_2_ProduitAcheteOperationID == produit.vid
                    }
                )
            }
        }
    }
}
