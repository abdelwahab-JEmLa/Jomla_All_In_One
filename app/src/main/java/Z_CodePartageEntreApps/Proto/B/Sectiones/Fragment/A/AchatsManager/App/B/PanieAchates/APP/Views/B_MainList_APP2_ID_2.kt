package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.PanieAchates.APP.Views

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun B_MainList_APP2_ID_2(
    composeKeyVID: Long?,
    modifier: Modifier = Modifier,
    _0_HeadOfRepositorys_Repository_Model: _0_0_HeadOfRepositorys_Model,
    onQuantitySelected: (Int) -> Unit,
    onDoneupdatePrice: (SnapshotStateList<_1_1_CouleurAcheteOperation>) -> Unit,
) {
    val validColorOperations =
        _0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository
            .modelDatasSnapList
            .filter {
                it.parentProduitAchateOperationVID != null &&
                        it.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
            }

    val produitsBonAchatIDs by remember {
        mutableStateOf(
            produitsBonAchatIDs(
                _0_HeadOfRepositorys_Repository_Model,
                composeKeyVID,
                validColorOperations
            )
        )
    }

    // Fixed: properly implement the products mapping
    val produitsBonAchateDepuitproduitsBonAchatIDs by remember {
        mutableStateOf(
            produitsBonAchatIDs.map { produitItem ->
                _0_HeadOfRepositorys_Repository_Model._2_1_ProduitsDataBase_Repository
                    .modelDatasSnapList.find { it.vid == produitItem.produitAcheterID }
            }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(produitsBonAchatIDs) { produitItem ->
            C_MainItem_APP2_ID_2(
                composeKeyVID = produitItem.vid,
                _0_HeadOfRepositorys_Repository_Model = _0_HeadOfRepositorys_Repository_Model,
                onQuantitySelected = onQuantitySelected,
                onDoneupdatePrice = { newPrice ->
                    // Update the provisional price after notifying about price update
                    onDoneupdatePrice(_0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList)

                    // Now update the product with the new provisional price
                    val price = newPrice.toDoubleOrNull() ?: 0.0
                    if (price > 0) {
                        // Find the product and update its provisional price
                        val produit = _0_HeadOfRepositorys_Repository_Model
                            ._1_2_ProduitAcheteOperation_Repository
                            .modelDatasSnapList
                            .find { it.vid == produitItem.vid }

                        produit?.let { product ->
                            val updatedProduct = product.copy(
                                provisoireMonPrix = price
                            )
                            _0_HeadOfRepositorys_Repository_Model
                                ._1_2_ProduitAcheteOperation_Repository
                                .updateUnSeulData(updatedProduct)

                            // Update the corresponding product in our mapping list
                            val productIndex = produitsBonAchatIDs.indexOf(produitItem)
                            if (productIndex >= 0 && productIndex < produitsBonAchateDepuitproduitsBonAchatIDs.size) {
                                // This will trigger a recomposition with the updated price
                                produitsBonAchateDepuitproduitsBonAchatIDs[productIndex]?.let { dbProduct ->
                                    // We don't need to update the database product, but we need to refresh the list
                                    onQuantitySelected(0) // Trigger a refresh
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

private fun produitsBonAchatIDs(
    _0_HeadOfRepositorys_Repository_Model: _0_0_HeadOfRepositorys_Model,
    composeKeyVID: Long?,
    validColorOperations: List<_1_1_CouleurAcheteOperation>,
) = _0_HeadOfRepositorys_Repository_Model
    ._1_2_ProduitAcheteOperation_Repository
    .modelDatasSnapList
    .filter { produitOpe ->
        val condition1 = produitOpe.parent_1_3_BonAchat == composeKeyVID
        val condition2 = produitOpe.etateActuellementEst == _1_2_ProduitAcheteOperation
            .EtateActuellementEst
            .CONFIRME
        val condition3 = validColorOperations
            .any { it.parentProduitAchateOperationVID == produitOpe.vid }

        condition1 && condition2 && condition3
    }
