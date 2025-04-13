package W.App.A.PanierFinaleDAchat.APP.View

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun B_MainList_FragID_2(
    modifier: Modifier = Modifier,
) {             /*
    val filteredBonAchatList =
        uiState._1_3_BonAchatList.filter { it.vid == uiState.bonAchetOnCourseMntID }

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
                    id = bonAchat.vid,
                    startDateInString = "" ,
                    clientAchteurID = bonAchat.clientAchteurID
                )
            }

            // Get products for this bonAchat
            val relatedProducts = uiState._1_2_ProduitAcheteOperationList.filter {
                it.parent_1_3_BonAchat == bonAchat.vid
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
 */
}
